package com.example.campusactivity.activity;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;

import jakarta.persistence.LockModeType;

public interface ActivityRepository extends JpaRepository<Activity, Long> {
    List<Activity> findByStatusAndEndTimeAfterOrderByStartTimeAsc(ActivityStatus status, LocalDateTime now);
    List<Activity> findByOrganizerIdOrderByCreatedAtDesc(Long organizerId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    java.util.Optional<Activity> findLockedById(Long id);
}

