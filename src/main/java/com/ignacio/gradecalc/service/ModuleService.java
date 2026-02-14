package com.ignacio.gradecalc.service;

import com.ignacio.gradecalc.entity.Module;
import com.ignacio.gradecalc.entity.User;
import com.ignacio.gradecalc.repository.ModuleRepository;
import com.ignacio.gradecalc.repository.UserRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ModuleService {

    private final ModuleRepository moduleRepository;
    private final UserRepository userRepository;

    public ModuleService(ModuleRepository moduleRepository, UserRepository userRepository) {
        this.moduleRepository = moduleRepository;
        this.userRepository = userRepository;
    }

    public Module createModule(Long userId, String name, String code, Integer credits,
                               String targetGrade, String semester) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found: " + userId));

        Module module = new Module(user, name, code, credits, targetGrade, semester);
        return moduleRepository.save(module);
    }

    public Optional<Module> findById(Long moduleId) {
        return moduleRepository.findById(moduleId);
    }

    public List<Module> findByUserId(Long userId) {
        return moduleRepository.findByUserId(userId);
    }

    public List<Module> findByUserIdAndSemester(Long userId, String semester) {
        return moduleRepository.findByUserIdAndSemester(userId, semester);
    }

    public Module updateModule(Long moduleId, String name, String code, Integer credits,
                               String targetGrade, String semester) {
        Module module = moduleRepository.findById(moduleId)
                .orElseThrow(() -> new IllegalArgumentException("Module not found: " + moduleId));

        module.setName(name);
        module.setCode(code);
        module.setCredits(credits);
        module.setTargetGrade(targetGrade);
        module.setSemester(semester);

        return moduleRepository.save(module);
    }

    public void deleteModule(Long moduleId) {
        moduleRepository.deleteById(moduleId);
    }
}