package com.ignacio.gradecalc.repository;

import com.ignacio.gradecalc.entity.Assessment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AssessmentRepository extends JpaRepository<Assessment, Long> {

    List<Assessment> findByModuleId(Long moduleId);

    List<Assessment> findByModuleIdAndObtainedMarkIsNull(Long moduleId);

    List<Assessment> findByModuleIdAndObtainedMarkIsNotNull(Long moduleId);
}