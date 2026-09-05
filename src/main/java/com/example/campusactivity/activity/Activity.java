package com.example.campusactivity.activity;

import java.time.LocalDateTime;

import com.example.campusactivity.user.User;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Version;

@Entity
public class Activity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 100)
    private String title;

    @Column(nullable = false, length = 2000)
    private String description;

    @Column(nullable = false, length = 120)
    private String location;

    @Column(nullable = false)
    private LocalDateTime startTime;

    @Column(nullable = false)
    private LocalDateTime endTime;

    @Column(nullable = false)
    private LocalDateTime registrationDeadline;

    @Column(nullable = false)
    private Integer capacity;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 16)
    private ActivityStatus status;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "organizer_id", nullable = false)
    private User organizer;

    @Column(nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @Column(nullable = false)
    private LocalDateTime updatedAt;

    @Version
    private Long version;

    protected Activity() {
    }

    public Activity(String title, String description, String location, LocalDateTime startTime,
                    LocalDateTime endTime, LocalDateTime registrationDeadline, Integer capacity,
                    ActivityStatus status, User organizer) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.registrationDeadline = registrationDeadline;
        this.capacity = capacity;
        this.status = status;
        this.organizer = organizer;
    }

    @PrePersist
    void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = createdAt;
    }

    @PreUpdate
    void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void update(String title, String description, String location, LocalDateTime startTime,
                       LocalDateTime endTime, LocalDateTime registrationDeadline, Integer capacity,
                       ActivityStatus status) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startTime = startTime;
        this.endTime = endTime;
        this.registrationDeadline = registrationDeadline;
        this.capacity = capacity;
        this.status = status;
    }

    public Long getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getLocation() { return location; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public LocalDateTime getRegistrationDeadline() { return registrationDeadline; }
    public Integer getCapacity() { return capacity; }
    public ActivityStatus getStatus() { return status; }
    public User getOrganizer() { return organizer; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }
}

