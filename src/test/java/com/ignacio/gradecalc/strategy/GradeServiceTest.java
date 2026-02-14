package com.ignacio.gradecalc.service;

import com.ignacio.gradecalc.entity.Assessment;
import com.ignacio.gradecalc.entity.GradingScale;
import com.ignacio.gradecalc.entity.Module;
import com.ignacio.gradecalc.entity.User;
import com.ignacio.gradecalc.enums.AssessmentType;
import com.ignacio.gradecalc.enums.UniversityPreset;
import com.ignacio.gradecalc.repository.GradingScaleRepository;
import com.ignacio.gradecalc.strategy.GradingStrategyFactory;
import com.ignacio.gradecalc.strategy.StandardStrategy;
import com.ignacio.gradecalc.strategy.TudStrategy;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class GradeServiceTest {

    @Mock
    private GradingScaleRepository gradingScaleRepository;

    private GradeService gradeService;
    private GradingStrategyFactory strategyFactory;
    private List<GradingScale> tudScales;
    private User testUser;

    @BeforeEach
    void setUp() {
        // Create real strategy factory with TUD and STANDARD strategies
        List strategies = Arrays.asList(new TudStrategy(), new StandardStrategy());
        strategyFactory = new GradingStrategyFactory(strategies);

        gradeService = new GradeService(strategyFactory, gradingScaleRepository);

        // Set up TUD scales
        tudScales = Arrays.asList(
                createScale("A1", 80.00, 100.00, 4.00),
                createScale("A2", 75.00, 79.99, 3.80),
                createScale("A3", 70.00, 74.99, 3.60),
                createScale("B1", 65.00, 69.99, 3.20),
                createScale("B2", 60.00, 64.99, 3.00),
                createScale("B3", 55.00, 59.99, 2.80),
                createScale("C1", 50.00, 54.99, 2.60),
                createScale("C2", 45.00, 49.99, 2.40),
                createScale("C3", 40.00, 44.99, 2.00),
                createScale("D1", 35.00, 39.99, 1.60),
                createScale("F", 0.00, 34.99, 0.00)
        );

        // Set up test user
        testUser = new User();
        testUser.setId(1L);
        testUser.setUniversityPreset(UniversityPreset.TUD);

        // Mock repository to return TUD scales
        when(gradingScaleRepository.findByUniversity("TUD")).thenReturn(tudScales);
    }

    @Test
    @DisplayName("Should calculate current percentage from completed assessments")
    void calculateCurrentPercentage_WithCompletedAssessments() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),      // 80% on 50% weight = 40 points
                createAssessment("Assignment", 30, 70, 100) // 70% on 30% weight = 21 points
                // Total: 61 points from 80% of module
        );

        BigDecimal result = gradeService.calculateCurrentPercentage(module);

        assertBigDecimalEquals(new BigDecimal("61"), result);
    }

    @Test
    @DisplayName("Should ignore pending assessments in current percentage")
    void calculateCurrentPercentage_IgnoresPendingAssessments() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),       // Completed: 40 points
                createPendingAssessment("Project", 50)       // Pending: ignored
        );

        BigDecimal result = gradeService.calculateCurrentPercentage(module);

        assertBigDecimalEquals(new BigDecimal("40"), result);
    }

    @Test
    @DisplayName("Should return zero for module with no assessments")
    void calculateCurrentPercentage_NoAssessments() {
        Module module = new Module();
        module.setAssessments(new ArrayList<>());

        BigDecimal result = gradeService.calculateCurrentPercentage(module);

        assertBigDecimalEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should calculate completed weight correctly")
    void calculateCompletedWeight_ReturnsCorrectSum() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),
                createAssessment("Assignment", 30, 70, 100),
                createPendingAssessment("Project", 20)
        );

        BigDecimal result = gradeService.calculateCompletedWeight(module);

        assertBigDecimalEquals(new BigDecimal("80"), result);
    }

    @Test
    @DisplayName("Should calculate pending weight correctly")
    void calculatePendingWeight_ReturnsCorrectSum() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),
                createPendingAssessment("Assignment", 30),
                createPendingAssessment("Project", 20)
        );

        BigDecimal result = gradeService.calculatePendingWeight(module);

        assertBigDecimalEquals(new BigDecimal("50"), result);
    }

    @Test
    @DisplayName("Should calculate required percentage for target grade")
    void calculateRequiredPercentage_AchievableTarget() {
        // Current: 40 points from 50% of module
        // Target: A3 (70% minimum)
        // Need: (70 - 40) / 0.50 = 60% on remaining work
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),  // 40 points
                createPendingAssessment("Project", 50)
        );

        BigDecimal result = gradeService.calculateRequiredPercentage(module, testUser, "A3");

        assertBigDecimalEquals(new BigDecimal("60"), result);
    }

    @Test
    @DisplayName("Should return null when target is impossible")
    void calculateRequiredPercentage_ImpossibleTarget() {
        // Current: 15 points from 50% of module
        // Target A1 (80% min) would need (80 - 15) / 0.50 = 130% - impossible
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 30, 100),  // 15 points
                createPendingAssessment("Project", 50)
        );

        BigDecimal result = gradeService.calculateRequiredPercentage(module, testUser, "A1");

        assertNull(result);
    }

    @Test
    @DisplayName("Should return zero when target already achieved")
    void calculateRequiredPercentage_AlreadyAchieved() {
        // Current: 60 points from 80% of module
        // Target: C1 (50% minimum)
        // Already have more than needed
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),           // 40 points
                createAssessment("Assignment", 30, 66.67, 100),  // ~20 points
                createPendingAssessment("Project", 20)
        );

        BigDecimal result = gradeService.calculateRequiredPercentage(module, testUser, "C1");

        assertBigDecimalEquals(BigDecimal.ZERO, result);
    }

    @Test
    @DisplayName("Should correctly identify achievable target")
    void isTargetAchievable_ReturnsTrue() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),
                createPendingAssessment("Project", 50)
        );

        assertTrue(gradeService.isTargetAchievable(module, testUser, "A3"));
    }

    @Test
    @DisplayName("Should correctly identify impossible target")
    void isTargetAchievable_ReturnsFalse() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 20, 100),  // Only 10 points
                createPendingAssessment("Project", 50)
        );

        // A1 needs 80%, have 10, need 70 from 50% = 140% - impossible
        assertFalse(gradeService.isTargetAchievable(module, testUser, "A1"));
    }

    @Test
    @DisplayName("Should correctly identify secured target")
    void isTargetSecured_ReturnsTrue() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 60, 90, 100),      // 54 points
                createAssessment("Assignment", 30, 80, 100), // 24 points
                createPendingAssessment("Project", 10)       // Even 0 here = 78 points
        );

        // C3 needs only 40%, we have 78 points already secured
        assertTrue(gradeService.isTargetSecured(module, testUser, "C3"));
    }

    @Test
    @DisplayName("Should return current grade code based on projected percentage")
    void getCurrentGradeCode_ReturnsCorrectGrade() {
        Module module = createModuleWithAssessments(
                createAssessment("Exam", 50, 80, 100),      // 40 points from 50%
                createPendingAssessment("Project", 50)
        );

        // Projected: 40 / 50 * 100 = 80% → A1
        String result = gradeService.getCurrentGradeCode(module, testUser);

        assertEquals("A1", result);
    }

    @Test
    @DisplayName("Should calculate overall GPA across modules")
    void calculateOverallGpa_WeightedByCredits() {
        Module module1 = createModuleWithAssessments(
                createAssessment("Exam", 100, 80, 100) // 80% → 4.0 GPA
        );
        module1.setCredits(5);

        Module module2 = createModuleWithAssessments(
                createAssessment("Exam", 100, 60, 100) // 60% → 3.0 GPA
        );
        module2.setCredits(5);

        List<Module> modules = Arrays.asList(module1, module2);

        // Expected: (4.0 * 5 + 3.0 * 5) / 10 = 3.5
        BigDecimal result = gradeService.calculateOverallGpa(modules, testUser);

        assertBigDecimalEquals(new BigDecimal("3.50"), result);
    }

    // Helper method for BigDecimal comparison (ignores scale)
    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertTrue(expected.compareTo(actual) == 0,
                "Expected " + expected + " but got " + actual);
    }

    // Helper methods for creating test data

    private Module createModuleWithAssessments(Assessment... assessments) {
        Module module = new Module();
        module.setCredits(5);
        module.setAssessments(new ArrayList<>(Arrays.asList(assessments)));
        return module;
    }

    private Assessment createAssessment(String name, double weight, double obtained, double total) {
        Assessment assessment = new Assessment();
        assessment.setName(name);
        assessment.setType(AssessmentType.EXAM);
        assessment.setWeightPercentage(new BigDecimal(String.valueOf(weight)));
        assessment.setObtainedMark(new BigDecimal(String.valueOf(obtained)));
        assessment.setTotalMarks(new BigDecimal(String.valueOf(total)));
        return assessment;
    }

    private Assessment createPendingAssessment(String name, double weight) {
        Assessment assessment = new Assessment();
        assessment.setName(name);
        assessment.setType(AssessmentType.ASSIGNMENT);
        assessment.setWeightPercentage(new BigDecimal(String.valueOf(weight)));
        assessment.setTotalMarks(new BigDecimal("100"));
        // obtainedMark is null = pending
        return assessment;
    }

    private GradingScale createScale(String code, double min, double max, double gpa) {
        GradingScale scale = new GradingScale();
        scale.setGradeCode(code);
        scale.setMinPercentage(new BigDecimal(String.valueOf(min)));
        scale.setMaxPercentage(new BigDecimal(String.valueOf(max)));
        scale.setGpaPoints(new BigDecimal(String.valueOf(gpa)));
        return scale;
    }
}