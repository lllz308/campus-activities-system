package com.example.campusactivity.user;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(name = "app_user", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 40)
    private String username;

    @Column(nullable = false)
    private String passwordHash;

    @Column(nullable = false, length = 40)
    private String displayName;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private Role role;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    protected User() {
    }

    public User(String username, String passwordHash, String displayName, Role role) {
        this.username = username;
        this.passwordHash = passwordHash;
        this.displayName = displayName;
        this.role = role;
        this.createdAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public String getUsername() { return username; }
    public String getPasswordHash() { return passwordHash; }
    public String getDisplayName() { return displayName; }
    public Role getRole() { return role; }
    public LocalDateTime getCreatedAt() { return createdAt; }
}

