package com.ignacio.gradecalc.service;

import com.ignacio.gradecalc.entity.Assessment;
import com.ignacio.gradecalc.entity.Module;
import com.ignacio.gradecalc.enums.AssessmentType;
import com.ignacio.gradecalc.repository.AssessmentRepository;
import com.ignacio.gradecalc.repository.ModuleRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class AssessmentService {

    private final AssessmentRepository assessmentRepository;
    private final ModuleRepository moduleRepository;

    public AssessmentService(AssessmentRepository assessmentRepository, ModuleRepository moduleRepository) {
        this.assessmentRepository = assessmentRepository;
        this.moduleRepository = moduleRepository;
    }

    public Assessment createAssessment(Long moduleId, String name, AssessmentType type,
                                       BigDecimal weightPercentage, BigDecimal totalMarks,
                                       LocalDate dueDate) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        Assessment assessment = new Assessment(module, name, type, weightPercentage, totalMarks);
        assessment.setDueDate(dueDate);

        return assessmentRepository.save(assessment);
    }

    public Optional<Assessment> findById(Long assessmentId) {
        return assessmentRepository.findById(assessmentId);
    }

    public List<Assessment> findByModuleId(Long moduleId) {
        return assessmentRepository.findByModuleId(moduleId);
    }

    public List<Assessment> findPendingByModuleId(Long moduleId) {
        return assessmentRepository.findByModuleIdAndObtainedMarkIsNull(moduleId);
    }

    public List<Assessment> findCompletedByModuleId(Long moduleId) {
        return assessmentRepository.findByModuleIdAndObtainedMarkIsNotNull(moduleId);
    }

    public Assessment recordMark(Long assessmentId, BigDecimal obtainedMark) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + assessmentId));

        assessment.setObtainedMark(obtainedMark);
        return assessmentRepository.save(assessment);
    }

    public Assessment updateAssessment(Long assessmentId, String name, AssessmentType type,
                                       BigDecimal weightPercentage, BigDecimal totalMarks,
                                       LocalDate dueDate) {
        Assessment assessment = assessmentRepository.findById(assessmentId)
                .orElseThrow(() -> new IllegalArgumentException("Assessment not found: " + assessmentId));

        assessment.setName(name);
        assessment.setType(type);
        assessment.setWeightPercentage(weightPercentage);
        assessment.setTotalMarks(totalMarks);
        assessment.setDueDate(dueDate);

        return assessmentRepository.save(assessment);
    }

    public void deleteAssessment(Long assessmentId) {
        assessmentRepository.deleteById(assessmentId);
    }
}