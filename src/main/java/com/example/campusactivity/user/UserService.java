package com.example.campusactivity.user;

import com.example.campusactivity.common.BusinessException;
import com.example.campusactivity.security.AppPrincipal;
import com.example.campusactivity.user.dto.ChangePasswordRequest;
import com.example.campusactivity.user.dto.UpdateProfileRequest;
import com.example.campusactivity.user.dto.UserProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional(readOnly = true)
    public UserProfileResponse getProfile(AppPrincipal principal) {
        return toResponse(loadUser(principal));
    }

    @Transactional
    public UserProfileResponse updateProfile(AppPrincipal principal, UpdateProfileRequest request) {
        User user = loadUser(principal);
        user.setDisplayName(request.displayName().trim());
        user.setStudentNo(trimToNull(request.studentNo()));
        user.setMajor(trimToNull(request.major()));
        user.setEmail(trimToNull(request.email()));
        userRepository.save(user);
        return toResponse(user);
    }

    @Transactional
    public void changePassword(AppPrincipal principal, ChangePasswordRequest request) {
        User user = loadUser(principal);
        if (!passwordEncoder.matches(request.oldPassword(), user.getPasswordHash())) {
            // 旧密码错误属于业务校验失败，返回 400 而非 401，避免前端将其误判为“会话失效”而强制登出
            throw new BusinessException(HttpStatus.BAD_REQUEST, "旧密码错误");
        }
        user.setPasswordHash(passwordEncoder.encode(request.newPassword()));
        userRepository.save(user);
    }

    private User loadUser(AppPrincipal principal) {
        return userRepository.findById(principal.id())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));
    }

    private String trimToNull(String value) {
        if (value == null) {
            return null;
        }
        String trimmed = value.trim();
        return trimmed.isEmpty() ? null : trimmed;
    }

    private UserProfileResponse toResponse(User user) {
        return new UserProfileResponse(user.getId(), user.getUsername(), user.getDisplayName(),
                user.getRole(), user.getStudentNo(), user.getMajor(), user.getEmail());
    }
}
