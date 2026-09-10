package com.example.campusactivity.user.dto;

import com.example.campusactivity.user.Role;

public record UserProfileResponse(Long id, String username, String displayName, Role role,
                                 String studentNo, String major, String email) {
}
