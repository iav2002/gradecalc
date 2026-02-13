package com.ignacio.gradecalc.entity;

import com.ignacio.gradecalc.enums.AssessmentType;
import jakarta.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "assessments")
public class Assessment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "module_id", nullable = false)
    private Module module;

    @Column(nullable = false, length = 100)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 50)
    private AssessmentType type;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal weightPercentage;

    @Column(precision = 5, scale = 2)
    private BigDecimal obtainedMark;

    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal totalMarks = new BigDecimal("100.00");

    private LocalDate dueDate;

    @Column(nullable = false)
    private LocalDateTime createdAt;

    @PrePersist
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    // Default constructor (required by JPA)
    public Assessment() {}

    // Constructor for creating new assessments
    public Assessment(Module module, String name, AssessmentType type,
                      BigDecimal weightPercentage, BigDecimal totalMarks) {
        this.module = module;
        this.name = name;
        this.type = type;
        this.weightPercentage = weightPercentage;
        this.totalMarks = totalMarks;
        this.createdAt = LocalDateTime.now();
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public AssessmentType getType() {
        return type;
    }

    public void setType(AssessmentType type) {
        this.type = type;
    }

    public BigDecimal getWeightPercentage() {
        return weightPercentage;
    }

    public void setWeightPercentage(BigDecimal weightPercentage) {
        this.weightPercentage = weightPercentage;
    }

    public BigDecimal getObtainedMark() {
        return obtainedMark;
    }

    public void setObtainedMark(BigDecimal obtainedMark) {
        this.obtainedMark = obtainedMark;
    }

    public BigDecimal getTotalMarks() {
        return totalMarks;
    }

    public void setTotalMarks(BigDecimal totalMarks) {
        this.totalMarks = totalMarks;
    }

    public LocalDate getDueDate() {
        return dueDate;
    }

    public void setDueDate(LocalDate dueDate) {
        this.dueDate = dueDate;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    // Utility method: check if assessment is completed
    public boolean isCompleted() {
        return obtainedMark != null;
    }

    // Utility method: get percentage score (if completed)
    public BigDecimal getPercentageScore() {
        if (obtainedMark == null || totalMarks == null) {
            return null;
        }
        return obtainedMark
                .divide(totalMarks, 4, java.math.RoundingMode.HALF_UP)
                .multiply(new BigDecimal("100"));
    }
}