package com.example.campusactivity.activity.dto;

import java.time.LocalDateTime;

import com.example.campusactivity.activity.ActivityStatus;

public record ActivityResponse(
        Long id,
        String title,
        String description,
        String location,
        LocalDateTime startTime,
        LocalDateTime endTime,
        LocalDateTime registrationDeadline,
        Integer capacity,
        long registeredCount,
        ActivityStatus status,
        Long organizerId,
        String organizerName) {
}

