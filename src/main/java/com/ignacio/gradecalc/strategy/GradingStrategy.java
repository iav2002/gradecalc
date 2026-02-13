package com.ignacio.gradecalc.strategy;

import com.ignacio.gradecalc.entity.GradingScale;

import java.math.BigDecimal;
import java.util.List;

public interface GradingStrategy {

    /**
     * Convert a percentage score to the corresponding grade label.
     * @param percentage The score as a percentage (0-100)
     * @param scales The grading scales for this university
     * @return The grade code (e.g., "A+", "II.1", "B2")
     */
    String getGradeCode(BigDecimal percentage, List<GradingScale> scales);

    /**
     * Get the full grade name for display.
     * @param percentage The score as a percentage (0-100)
     * @param scales The grading scales for this university
     * @return The grade name (e.g., "First Class Honours", "Upper Second")
     */
    String getGradeName(BigDecimal percentage, List<GradingScale> scales);

    /**
     * Get the GPA points for a percentage score.
     * @param percentage The score as a percentage (0-100)
     * @param scales The grading scales for this university
     * @return The GPA points (e.g., 4.2, 3.6, null if not applicable)
     */
    BigDecimal getGpaPoints(BigDecimal percentage, List<GradingScale> scales);

    /**
     * Calculate the minimum percentage needed to achieve a target grade.
     * @param targetGradeCode The grade code the student is aiming for (e.g., "A-", "II.1")
     * @param scales The grading scales for this university
     * @return The minimum percentage required
     */
    BigDecimal getMinimumPercentageForGrade(String targetGradeCode, List<GradingScale> scales);

    /**
     * Get the university code this strategy handles.
     * @return The university preset code (e.g., "UCD", "TCD", "TUD")
     */
    String getUniversityCode();
}