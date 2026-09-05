package com.example.campusactivity.security;

import com.example.campusactivity.user.Role;

public record AppPrincipal(Long id, String username, String displayName, Role role) {
}

