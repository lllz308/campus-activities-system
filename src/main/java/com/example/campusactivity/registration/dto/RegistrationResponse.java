package com.example.campusactivity.registration.dto;

import java.time.LocalDateTime;

public record RegistrationResponse(Long registrationId, Long activityId, String activityTitle,
                                   LocalDateTime startTime, LocalDateTime registeredAt) {
}

