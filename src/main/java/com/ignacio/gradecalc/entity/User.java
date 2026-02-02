package com.ignacio.gradecalc.entity;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 50)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 20)
    private String universityPreset;

    private LocalDateTime createdAT;

    // Default constructor (required by JPA)
    public User(){
    }

    // Constructor for creating new users
    public User(String username, String passwordHash, String universityPreset){
        this.username = username;
        this.passwordHash = passwordHash;
        this.universityPreset = universityPreset;
        this.createdAT = LocalDateTime.now();
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public void setPasswordHash(String passwordHash) {
        this.passwordHash = passwordHash;
    }

    public String getUniversityPreset() {
        return universityPreset;
    }

    public void setUniversityPreset(String universityPreset) {
        this.universityPreset = universityPreset;
    }

    public LocalDateTime getCreatedAT() {
        return createdAT;
    }

    public void setCreatedAT(LocalDateTime createdAT) {
        this.createdAT = createdAT;
    }
}

