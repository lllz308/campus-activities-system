package com.example.campusactivity.auth.dto;

import com.example.campusactivity.user.Role;

public record AuthResponse(String token, Long userId, String username, String displayName, Role role) {
}

