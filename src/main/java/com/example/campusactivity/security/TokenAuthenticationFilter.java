package com.example.campusactivity.security;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.List;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

@Component
public class TokenAuthenticationFilter extends OncePerRequestFilter {

    private final AuthTokenRepository tokenRepository;

    public TokenAuthenticationFilter(AuthTokenRepository tokenRepository) {
        this.tokenRepository = tokenRepository;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        String authorization = request.getHeader("Authorization");
        if (authorization != null && authorization.startsWith("Bearer ")) {
            String rawToken = authorization.substring(7).trim();
            tokenRepository.findByTokenHashAndExpiresAtAfter(TokenHasher.hash(rawToken), LocalDateTime.now())
                    .ifPresent(token -> {
                        var user = token.getUser();
                        var principal = new AppPrincipal(user.getId(), user.getUsername(),
                                user.getDisplayName(), user.getRole());
                        var authentication = new UsernamePasswordAuthenticationToken(
                                principal, null, List.of(new SimpleGrantedAuthority("ROLE_" + user.getRole().name())));
                        SecurityContextHolder.getContext().setAuthentication(authentication);
                    });
        }
        filterChain.doFilter(request, response);
    }
}

