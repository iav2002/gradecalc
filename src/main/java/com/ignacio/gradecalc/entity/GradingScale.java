package com.ignacio.gradecalc.entity;

import jakarta.persistence.*;
import java.math.BigDecimal;

@Entity
@Table(name = "grading_scales")
public class GradingScale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 20)
    private String university;

    @Column(nullable = false, length = 10)
    private String gradeCode;

    @Column(nullable = false, length = 50)
    private String gradeName;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal minPercentage;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal maxPercentage;

    @Column(precision = 3, scale = 2)
    private BigDecimal gpaPoints;

    // Default constructor (required by JPA)
    public GradingScale() {}

    // Constructor for seeding data
    public GradingScale(String university, String gradeCode, String gradeName,
                        BigDecimal minPercentage, BigDecimal maxPercentage, BigDecimal gpaPoints) {
        this.university = university;
        this.gradeCode = gradeCode;
        this.gradeName = gradeName;
        this.minPercentage = minPercentage;
        this.maxPercentage = maxPercentage;
        this.gpaPoints = gpaPoints;
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUniversity() {
        return university;
    }

    public void setUniversity(String university) {
        this.university = university;
    }

    public String getGradeCode() {
        return gradeCode;
    }

    public void setGradeCode(String gradeCode) {
        this.gradeCode = gradeCode;
    }

    public String getGradeName() {
        return gradeName;
    }

    public void setGradeName(String gradeName) {
        this.gradeName = gradeName;
    }

    public BigDecimal getMinPercentage() {
        return minPercentage;
    }

    public void setMinPercentage(BigDecimal minPercentage) {
        this.minPercentage = minPercentage;
    }

    public BigDecimal getMaxPercentage() {
        return maxPercentage;
    }

    public void setMaxPercentage(BigDecimal maxPercentage) {
        this.maxPercentage = maxPercentage;
    }

    public BigDecimal getGpaPoints() {
        return gpaPoints;
    }

    public void setGpaPoints(BigDecimal gpaPoints) {
        this.gpaPoints = gpaPoints;
    }
}