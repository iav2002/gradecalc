package com.ignacio.gradecalc.service;

import com.ignacio.gradecalc.entity.Assessment;
import com.ignacio.gradecalc.entity.GradingScale;
import com.ignacio.gradecalc.entity.Module;
import com.ignacio.gradecalc.entity.User;
import com.ignacio.gradecalc.enums.UniversityPreset;
import com.ignacio.gradecalc.repository.GradingScaleRepository;
import com.ignacio.gradecalc.strategy.GradingStrategy;
import com.ignacio.gradecalc.strategy.GradingStrategyFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
public class GradeService {

    private final GradingStrategyFactory strategyFactory;
    private final GradingScaleRepository gradingScaleRepository;

    public GradeService(GradingStrategyFactory strategyFactory,
                        GradingScaleRepository gradingScaleRepository) {
        this.strategyFactory = strategyFactory;
        this.gradingScaleRepository = gradingScaleRepository;
    }

    /**
     * Calculate the current weighted percentage for a module based on completed assessments.
     * Only includes assessments that have been graded (obtainedMark is not null).
     */
    public BigDecimal calculateCurrentPercentage(Module module) {
        List<Assessment> assessments = module.getAssessments();

        if (assessments == null || assessments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalWeightedScore = BigDecimal.ZERO;
        BigDecimal totalWeight = BigDecimal.ZERO;

        for (Assessment assessment : assessments) {
            if (assessment.isCompleted()) {
                BigDecimal percentageScore = assessment.getPercentageScore();
                BigDecimal weight = assessment.getWeightPercentage();

                // Weighted contribution = (percentage score) * (weight / 100)
                BigDecimal contribution = percentageScore
                        .multiply(weight)
                        .divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

                totalWeightedScore = totalWeightedScore.add(contribution);
                totalWeight = totalWeight.add(weight);
            }
        }

        return totalWeightedScore;
    }

    /**
     * Calculate what percentage of the module has been completed (by weight).
     */
    public BigDecimal calculateCompletedWeight(Module module) {
        List<Assessment> assessments = module.getAssessments();

        if (assessments == null || assessments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return assessments.stream()
                .filter(Assessment::isCompleted)
                .map(Assessment::getWeightPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Calculate what percentage of the module is still pending.
     */
    public BigDecimal calculatePendingWeight(Module module) {
        List<Assessment> assessments = module.getAssessments();

        if (assessments == null || assessments.isEmpty()) {
            return BigDecimal.ZERO;
        }

        return assessments.stream()
                .filter(assessment -> !assessment.isCompleted())
                .map(Assessment::getWeightPercentage)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    /**
     * Get the current grade code for a module based on completed work.
     */
    public String getCurrentGradeCode(Module module, User user) {
        BigDecimal currentPercentage = calculateCurrentPercentage(module);
        BigDecimal completedWeight = calculateCompletedWeight(module);

        // If nothing completed yet, no grade to show
        if (completedWeight.compareTo(BigDecimal.ZERO) == 0) {
            return "N/A";
        }

        // Project current performance to full module
        BigDecimal projectedPercentage = projectToFullModule(currentPercentage, completedWeight);

        GradingStrategy strategy = strategyFactory.getStrategy(user.getUniversityPreset());
        List<GradingScale> scales = getScalesForUser(user);

        return strategy.getGradeCode(projectedPercentage, scales);
    }

    /**
     * Get the current GPA points for a module based on completed work.
     */
    public BigDecimal getCurrentGpaPoints(Module module, User user) {
        BigDecimal currentPercentage = calculateCurrentPercentage(module);
        BigDecimal completedWeight = calculateCompletedWeight(module);

        if (completedWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        BigDecimal projectedPercentage = projectToFullModule(currentPercentage, completedWeight);

        GradingStrategy strategy = strategyFactory.getStrategy(user.getUniversityPreset());
        List<GradingScale> scales = getScalesForUser(user);

        return strategy.getGpaPoints(projectedPercentage, scales);
    }

    /**
     * Calculate the minimum average percentage needed on remaining assessments
     * to achieve a target grade.
     *
     * @return The required percentage, or null if the target is impossible
     */
    public BigDecimal calculateRequiredPercentage(Module module, User user, String targetGradeCode) {
        GradingStrategy strategy = strategyFactory.getStrategy(user.getUniversityPreset());
        List<GradingScale> scales = getScalesForUser(user);

        // Get the minimum percentage needed for the target grade
        BigDecimal targetMinPercentage = strategy.getMinimumPercentageForGrade(targetGradeCode, scales);

        if (targetMinPercentage == null) {
            return null; // Invalid target grade
        }

        BigDecimal currentScore = calculateCurrentPercentage(module);
        BigDecimal pendingWeight = calculatePendingWeight(module);

        // If no pending assessments, can't improve
        if (pendingWeight.compareTo(BigDecimal.ZERO) == 0) {
            return null;
        }

        // Formula: requiredAverage = (targetTotal - currentScore) / (pendingWeight / 100)
        // Where targetTotal is the minimum overall percentage needed
        BigDecimal pointsNeeded = targetMinPercentage.subtract(currentScore);
        BigDecimal pendingWeightFraction = pendingWeight.divide(new BigDecimal("100"), 4, RoundingMode.HALF_UP);

        BigDecimal requiredPercentage = pointsNeeded.divide(pendingWeightFraction, 2, RoundingMode.HALF_UP);

        // If required percentage is over 100, target is impossible
        if (requiredPercentage.compareTo(new BigDecimal("100")) > 0) {
            return null;
        }

        // If required percentage is negative, target is already achieved
        if (requiredPercentage.compareTo(BigDecimal.ZERO) < 0) {
            return BigDecimal.ZERO;
        }

        return requiredPercentage;
    }

    /**
     * Check if a target grade is still achievable.
     */
    public boolean isTargetAchievable(Module module, User user, String targetGradeCode) {
        BigDecimal required = calculateRequiredPercentage(module, user, targetGradeCode);
        return required != null;
    }

    /**
     * Check if a target grade has already been secured (even with 0 on remaining work).
     */
    public boolean isTargetSecured(Module module, User user, String targetGradeCode) {
        GradingStrategy strategy = strategyFactory.getStrategy(user.getUniversityPreset());
        List<GradingScale> scales = getScalesForUser(user);

        BigDecimal targetMinPercentage = strategy.getMinimumPercentageForGrade(targetGradeCode, scales);

        if (targetMinPercentage == null) {
            return false;
        }

        // Current score with 0 on all remaining work
        BigDecimal currentScore = calculateCurrentPercentage(module);

        return currentScore.compareTo(targetMinPercentage) >= 0;
    }

    /**
     * Calculate weighted GPA across multiple modules.
     */
    public BigDecimal calculateOverallGpa(List<Module> modules, User user) {
        if (modules == null || modules.isEmpty()) {
            return BigDecimal.ZERO;
        }

        BigDecimal totalWeightedGpa = BigDecimal.ZERO;
        int totalCredits = 0;

        for (Module module : modules) {
            BigDecimal completedWeight = calculateCompletedWeight(module);

            // Only include modules with some completed work
            if (completedWeight.compareTo(BigDecimal.ZERO) > 0) {
                BigDecimal gpaPoints = getCurrentGpaPoints(module, user);
                int credits = module.getCredits();

                totalWeightedGpa = totalWeightedGpa.add(gpaPoints.multiply(new BigDecimal(credits)));
                totalCredits += credits;
            }
        }

        if (totalCredits == 0) {
            return BigDecimal.ZERO;
        }

        return totalWeightedGpa.divide(new BigDecimal(totalCredits), 2, RoundingMode.HALF_UP);
    }

    /**
     * Project current performance to what the grade would be if the same
     * performance continues for the rest of the module.
     */
    private BigDecimal projectToFullModule(BigDecimal currentScore, BigDecimal completedWeight) {
        if (completedWeight.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }

        // Current score is already the weighted contribution (e.g., 35% out of 50% completed)
        // To project: (currentScore / completedWeight) * 100
        return currentScore
                .multiply(new BigDecimal("100"))
                .divide(completedWeight, 2, RoundingMode.HALF_UP);
    }

    /**
     * Get the grading scales for a user's university.
     */
    private List<GradingScale> getScalesForUser(User user) {
        UniversityPreset preset = user.getUniversityPreset();
        String universityCode = preset.name();

        List<GradingScale> scales = gradingScaleRepository.findByUniversity(universityCode);

        // Fall back to STANDARD if no scales found for this university
        if (scales.isEmpty()) {
            scales = gradingScaleRepository.findByUniversity("STANDARD");
        }

        return scales;
    }
}