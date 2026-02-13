package com.ignacio.gradecalc.strategy;

import com.ignacio.gradecalc.entity.GradingScale;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TcdStrategy implements GradingStrategy {

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
        // TCD uses proxy GPA values for cross-system comparison
        return scale != null ? scale.getGpaPoints() : BigDecimal.ZERO;
    }

    @Override
    public BigDecimal getMinimumPercentageForGrade(String targetGradeCode, List<GradingScale> scales) {
        // Handle common variations in how users might type Roman numerals
        String normalised = normaliseGradeCode(targetGradeCode);

        return scales.stream()
                .filter(scale -> normaliseGradeCode(scale.getGradeCode()).equalsIgnoreCase(normalised))
                .map(GradingScale::getMinPercentage)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getUniversityCode() {
        return "TCD";
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
     * Users might type "2.1", "2:1", "II1", "II.1", or "II-1" for the same grade.
     */
    private String normaliseGradeCode(String gradeCode) {
        if (gradeCode == null) {
            return "";
        }

        String normalised = gradeCode.toUpperCase().trim();

        // Convert common numeric representations to Roman numerals
        normalised = normalised
                .replace("2.1", "II.1")
                .replace("2:1", "II.1")
                .replace("2-1", "II.1")
                .replace("21", "II.1")
                .replace("2.2", "II.2")
                .replace("2:2", "II.2")
                .replace("2-2", "II.2")
                .replace("22", "II.2")
                .replace("1ST", "I")
                .replace("3RD", "III");

        // Normalise separators within Roman numerals
        normalised = normalised
                .replace("II1", "II.1")
                .replace("II-1", "II.1")
                .replace("II:1", "II.1")
                .replace("II2", "II.2")
                .replace("II-2", "II.2")
                .replace("II:2", "II.2");

        return normalised;
    }
}