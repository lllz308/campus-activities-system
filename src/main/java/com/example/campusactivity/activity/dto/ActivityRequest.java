package com.example.campusactivity.activity.dto;

import java.time.LocalDateTime;

import com.example.campusactivity.activity.ActivityStatus;
import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record ActivityRequest(
        @NotBlank @Size(max = 100) String title,
        @NotBlank @Size(max = 2000) String description,
        @NotBlank @Size(max = 120) String location,
        @NotNull @Future LocalDateTime startTime,
        @NotNull @Future LocalDateTime endTime,
        @NotNull @Future LocalDateTime registrationDeadline,
        @NotNull @Min(1) Integer capacity,
        @NotNull ActivityStatus status) {
}

