package com.ignacio.gradecalc.repository;

import com.ignacio.gradecalc.entity.Module;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModuleRepository extends JpaRepository<Module, Long> {

    List<Module> findByUserId(Long userId);

    List<Module> findByUserIdAndSemester(Long userId, String semester);
}