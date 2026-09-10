package com.example.campusactivity.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public record UpdateProfileRequest(
        @NotBlank @Size(max = 40) String displayName,
        @Size(max = 32) String studentNo,
        @Size(max = 64) String major,
        @Email @Size(max = 128) String email) {
}
