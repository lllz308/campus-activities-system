package com.example.campusactivity.security;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

public interface AuthTokenRepository extends JpaRepository<AuthToken, Long> {
    Optional<AuthToken> findByTokenHashAndExpiresAtAfter(String tokenHash, LocalDateTime now);
    void deleteByTokenHash(String tokenHash);
}

