package com.ignacio.gradecalc.strategy;

import com.ignacio.gradecalc.entity.GradingScale;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class StandardStrategy implements GradingStrategy {

    @Override
    public String getGradeCode(BigDecimal percentage, List<GradingScale> scales) {
        GradingScale scale = findScaleForPercentage(percentage, scales);
        return scale != null ? scale.getGradeCode() : "N/A";
    }

    @Override
    public String getGradeName(BigDecimal percentage, List<GradingScale> scales) {
        GradingScale scale = findScaleForPercentage(percentage, scales);
        return scale != null ? scale.getGradeName() : "No Grade";
    }

    @Override
    public BigDecimal getGpaPoints(BigDecimal percentage, List<GradingScale> scales) {
        GradingScale scale = findScaleForPercentage(percentage, scales);
        return scale != null ? scale.getGpaPoints() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getMinimumPercentageForGrade(String targetGradeCode, List<GradingScale> scales) {
        String normalised = normaliseGradeCode(targetGradeCode);

        return scales.stream()
                .filter(scale -> normaliseGradeCode(scale.getGradeCode()).equalsIgnoreCase(normalised))
                .map(GradingScale::getMinPercentage)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getUniversityCode() {
        return "STANDARD";
    }

    private GradingScale findScaleForPercentage(BigDecimal percentage, List<GradingScale> scales) {
        if (percentage == null) {
            return null;
        }

        return scales.stream()
                .filter(scale -> percentage.compareTo(scale.getMinPercentage()) >= 0
                        && percentage.compareTo(scale.getMaxPercentage()) <= 0)
                .findFirst()
                .orElse(null);
    }

    /**
     * Normalise grade code input to handle common variations.
     * Users might type "first", "1st", "1", or "A" for the same grade.
     */
    private String normaliseGradeCode(String gradeCode) {
        if (gradeCode == null) {
            return "";
        }

        String normalised = gradeCode.toUpperCase().trim();

        // Convert descriptive names to letter grades
        normalised = normalised
                .replace("FIRST", "A")
                .replace("1ST", "A")
                .replace("SECOND", "B")
                .replace("2ND", "B")
                .replace("THIRD", "C")
                .replace("3RD", "C")
                .replace("PASS", "D")
                .replace("FAIL", "F");

        // Handle numeric inputs
        if (normalised.equals("1")) {
            normalised = "A";
        } else if (normalised.equals("2")) {
            normalised = "B";
        } else if (normalised.equals("3")) {
            normalised = "C";
        } else if (normalised.equals("4")) {
            normalised = "D";
        }

        return normalised;
    }
}
