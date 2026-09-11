package com.example.campusactivity.registration.dto;

import java.time.LocalDateTime;

public record HistoryRecordResponse(Long registrationId, Long activityId, String activityTitle,
                                    String location, LocalDateTime startTime, LocalDateTime endTime,
                                    LocalDateTime registeredAt) {
}
