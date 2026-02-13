package com.ignacio.gradecalc.controller;

import com.ignacio.gradecalc.entity.User;
import com.ignacio.gradecalc.repository.UserRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @GetMapping("/login")
    public String loginPage() {
        return "auth/login";
    }

    @GetMapping("/register")
    public String registerPage() {
        return "auth/register";
    }

    @PostMapping("/register")
    public String registerUser(
            @RequestParam String username,
            @RequestParam String password,
            @RequestParam String universityPreset,
            Model model) {

        // Check if username already exists
        if (userRepository.existsByUsername(username)) {
            model.addAttribute("error", "Username already taken");
            return "auth/register";
        }

        // Hash password and create user
        String hashedPassword = passwordEncoder.encode(password);
        User user = new User(username, hashedPassword, universityPreset);
        userRepository.save(user);

        return "redirect:/login?registered";
    }
}
