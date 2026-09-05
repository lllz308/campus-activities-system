package com.example.campusactivity.auth.dto;

import com.example.campusactivity.user.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;

public record RegisterRequest(
        @NotBlank @Pattern(regexp = "[A-Za-z0-9_]{4,20}") String username,
        @NotBlank @Size(min = 8, max = 72) String password,
        @NotBlank @Size(max = 40) String displayName,
        @NotNull Role role) {
}

