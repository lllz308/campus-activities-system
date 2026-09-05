package com.example.campusactivity.auth;

import java.time.LocalDateTime;
import java.util.UUID;

import com.example.campusactivity.auth.dto.AuthResponse;
import com.example.campusactivity.auth.dto.LoginRequest;
import com.example.campusactivity.auth.dto.RegisterRequest;
import com.example.campusactivity.common.BusinessException;
import com.example.campusactivity.security.AuthToken;
import com.example.campusactivity.security.AuthTokenRepository;
import com.example.campusactivity.security.TokenHasher;
import com.example.campusactivity.user.User;
import com.example.campusactivity.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final AuthTokenRepository tokenRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthService(UserRepository userRepository, AuthTokenRepository tokenRepository,
                       PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            throw new BusinessException(HttpStatus.CONFLICT, "用户名已存在");
        }
        User user = userRepository.save(new User(request.username(), passwordEncoder.encode(request.password()),
                request.displayName(), request.role()));
        return issueToken(user);
    }

    @Transactional
    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误"));
        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException(HttpStatus.UNAUTHORIZED, "用户名或密码错误");
        }
        return issueToken(user);
    }

    @Transactional
    public void logout(String authorization) {
        if (authorization != null && authorization.startsWith("Bearer ")) {
            tokenRepository.deleteByTokenHash(TokenHasher.hash(authorization.substring(7).trim()));
        }
    }

    private AuthResponse issueToken(User user) {
        String rawToken = UUID.randomUUID().toString().replace("-", "")
                + UUID.randomUUID().toString().replace("-", "");
        tokenRepository.save(new AuthToken(TokenHasher.hash(rawToken), user, LocalDateTime.now().plusHours(24)));
        return new AuthResponse(rawToken, user.getId(), user.getUsername(), user.getDisplayName(), user.getRole());
    }
}

