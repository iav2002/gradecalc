package com.ignacio.gradecalc.strategy;

import com.ignacio.gradecalc.entity.GradingScale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TudStrategyTest {

    private TudStrategy strategy;
    private List<GradingScale> scales;

    @BeforeEach
    void setUp() {
        strategy = new TudStrategy();

        scales = Arrays.asList(
                createScale("A1", "First Class Honours", 80.00, 100.00, 4.00),
                createScale("A2", "First Class Honours", 75.00, 79.99, 3.80),
                createScale("A3", "First Class Honours", 70.00, 74.99, 3.60),
                createScale("B1", "Second Class Honours I", 65.00, 69.99, 3.20),
                createScale("B2", "Second Class Honours I", 60.00, 64.99, 3.00),
                createScale("B3", "Second Class Honours II", 55.00, 59.99, 2.80),
                createScale("C1", "Second Class Honours II", 50.00, 54.99, 2.60),
                createScale("C2", "Pass", 45.00, 49.99, 2.40),
                createScale("C3", "Pass", 40.00, 44.99, 2.00),
                createScale("D1", "Compensating Fail", 35.00, 39.99, 1.60),
                createScale("F", "Fail", 0.00, 34.99, 0.00)
        );
    }

    @Test
    @DisplayName("Should return correct grade code for First Class Honours")
    void getGradeCode_FirstClassHonours() {
        assertEquals("A1", strategy.getGradeCode(new BigDecimal("85"), scales));
        assertEquals("A2", strategy.getGradeCode(new BigDecimal("77"), scales));
        assertEquals("A3", strategy.getGradeCode(new BigDecimal("72"), scales));
    }

    @Test
    @DisplayName("Should return correct grade code for boundary values")
    void getGradeCode_BoundaryValues() {
        assertEquals("A3", strategy.getGradeCode(new BigDecimal("70.00"), scales));
        assertEquals("B1", strategy.getGradeCode(new BigDecimal("69.99"), scales));
        assertEquals("C3", strategy.getGradeCode(new BigDecimal("40.00"), scales));
        assertEquals("D1", strategy.getGradeCode(new BigDecimal("39.99"), scales));
    }

    @Test
    @DisplayName("Should return correct grade code for failing grades")
    void getGradeCode_FailingGrades() {
        assertEquals("D1", strategy.getGradeCode(new BigDecimal("37"), scales));
        assertEquals("F", strategy.getGradeCode(new BigDecimal("30"), scales));
        assertEquals("F", strategy.getGradeCode(new BigDecimal("0"), scales));
    }

    @Test
    @DisplayName("Should return N/A for null percentage")
    void getGradeCode_NullPercentage() {
        assertEquals("N/A", strategy.getGradeCode(null, scales));
    }

    @Test
    @DisplayName("Should return correct GPA points")
    void getGpaPoints_ReturnsCorrectValues() {
        assertBigDecimalEquals(new BigDecimal("4.00"), strategy.getGpaPoints(new BigDecimal("85"), scales));
        assertBigDecimalEquals(new BigDecimal("3.60"), strategy.getGpaPoints(new BigDecimal("72"), scales));
        assertBigDecimalEquals(new BigDecimal("3.00"), strategy.getGpaPoints(new BigDecimal("62"), scales));
        assertBigDecimalEquals(new BigDecimal("0.00"), strategy.getGpaPoints(new BigDecimal("20"), scales));
    }

    @Test
    @DisplayName("Should return minimum percentage for target grade")
    void getMinimumPercentageForGrade_ReturnsCorrectValues() {
        assertBigDecimalEquals(new BigDecimal("70.00"), strategy.getMinimumPercentageForGrade("A3", scales));
        assertBigDecimalEquals(new BigDecimal("60.00"), strategy.getMinimumPercentageForGrade("B2", scales));
        assertBigDecimalEquals(new BigDecimal("40.00"), strategy.getMinimumPercentageForGrade("C3", scales));
    }

    @Test
    @DisplayName("Should return null for invalid target grade")
    void getMinimumPercentageForGrade_InvalidGrade() {
        assertNull(strategy.getMinimumPercentageForGrade("X", scales));
        assertNull(strategy.getMinimumPercentageForGrade("", scales));
    }

    @Test
    @DisplayName("Should return correct university code")
    void getUniversityCode_ReturnsTUD() {
        assertEquals("TUD", strategy.getUniversityCode());
    }

    // Helper method for BigDecimal comparison (ignores scale)
    private void assertBigDecimalEquals(BigDecimal expected, BigDecimal actual) {
        assertTrue(expected.compareTo(actual) == 0,
                "Expected " + expected + " but got " + actual);
    }

    private GradingScale createScale(String code, String name, double min, double max, double gpa) {
        GradingScale scale = new GradingScale();
        scale.setGradeCode(code);
        scale.setGradeName(name);
        scale.setMinPercentage(new BigDecimal(String.valueOf(min)));
        scale.setMaxPercentage(new BigDecimal(String.valueOf(max)));
        scale.setGpaPoints(new BigDecimal(String.valueOf(gpa)));
        return scale;
    }
}