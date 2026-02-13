package com.ignacio.gradecalc.strategy;

import com.ignacio.gradecalc.entity.GradingScale;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;

@Component
public class TudStrategy implements GradingStrategy {

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
        return scales.stream()
                .filter(scale -> scale.getGradeCode().equalsIgnoreCase(targetGradeCode))
                .map(GradingScale::getMinPercentage)
                .findFirst()
                .orElse(null);
    }

    @Override
    public String getUniversityCode() {
        return "TUD";
    }

    /**
     * Find the grading scale that contains the given percentage.
     */
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
}
