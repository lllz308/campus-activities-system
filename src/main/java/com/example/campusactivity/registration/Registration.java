package com.example.campusactivity.registration;

import java.time.LocalDateTime;

import com.example.campusactivity.activity.Activity;
import com.example.campusactivity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;

@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = {"activity_id", "student_id"}))
public class Registration {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "activity_id", nullable = false)
    private Activity activity;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "student_id", nullable = false)
    private User student;

    @Column(nullable = false, updatable = false)
    private LocalDateTime registeredAt;

    protected Registration() {
    }

    public Registration(Activity activity, User student) {
        this.activity = activity;
        this.student = student;
        this.registeredAt = LocalDateTime.now();
    }

    public Long getId() { return id; }
    public Activity getActivity() { return activity; }
    public User getStudent() { return student; }
    public LocalDateTime getRegisteredAt() { return registeredAt; }
}

