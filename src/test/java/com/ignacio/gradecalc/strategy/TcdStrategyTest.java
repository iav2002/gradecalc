package com.ignacio.gradecalc.strategy;

import com.ignacio.gradecalc.entity.GradingScale;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class TcdStrategyTest {

    private TcdStrategy strategy;
    private List<GradingScale> scales;

    @BeforeEach
    void setUp() {
        strategy = new TcdStrategy();

        scales = Arrays.asList(
                createScale("I", "First Class Honours", 70.00, 100.00, 4.00),
                createScale("II.1", "Second Class Honours I", 60.00, 69.99, 3.00),
                createScale("II.2", "Second Class Honours II", 50.00, 59.99, 2.00),
                createScale("III", "Third Class Honours", 40.00, 49.99, 1.00),
                createScale("F1", "Fail", 30.00, 39.99, 0.00),
                createScale("F2", "Fail", 0.00, 29.99, 0.00)
        );
    }

    @Test
    @DisplayName("Should return Roman numeral grade codes")
    void getGradeCode_RomanNumerals() {
        assertEquals("I", strategy.getGradeCode(new BigDecimal("75"), scales));
        assertEquals("II.1", strategy.getGradeCode(new BigDecimal("65"), scales));
        assertEquals("II.2", strategy.getGradeCode(new BigDecimal("55"), scales));
        assertEquals("III", strategy.getGradeCode(new BigDecimal("45"), scales));
    }

    @Test
    @DisplayName("Should normalise various input formats for II.1")
    void getMinimumPercentageForGrade_NormalisesInput() {
        BigDecimal expected = new BigDecimal("60.00");

        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("II.1", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("ii.1", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("2.1", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("2:1", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("2-1", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("II-1", scales));
    }

    @Test
    @DisplayName("Should normalise various input formats for II.2")
    void getMinimumPercentageForGrade_NormalisesII2() {
        BigDecimal expected = new BigDecimal("50.00");

        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("II.2", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("2.2", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("2:2", scales));
    }

    @Test
    @DisplayName("Should normalise first class variations")
    void getMinimumPercentageForGrade_NormalisesFirst() {
        BigDecimal expected = new BigDecimal("70.00");

        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("I", scales));
        assertBigDecimalEquals(expected, strategy.getMinimumPercentageForGrade("1st", scales));
    }

    @Test
    @DisplayName("Should return correct university code")
    void getUniversityCode_ReturnsTCD() {
        assertEquals("TCD", strategy.getUniversityCode());
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