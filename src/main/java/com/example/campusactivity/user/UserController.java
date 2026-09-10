package com.example.campusactivity.user;

import com.example.campusactivity.common.ApiResponse;
import com.example.campusactivity.security.AppPrincipal;
import com.example.campusactivity.user.dto.ChangePasswordRequest;
import com.example.campusactivity.user.dto.UpdateProfileRequest;
import com.example.campusactivity.user.dto.UserProfileResponse;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/user")
public class UserController {

    private final UserService userService;

    public UserController(UserService userService) {
        this.userService = userService;
    }

    @GetMapping("/me")
    public ApiResponse<UserProfileResponse> me(@AuthenticationPrincipal AppPrincipal principal) {
        return ApiResponse.success(userService.getProfile(principal));
    }

    @PutMapping("/me")
    public ApiResponse<UserProfileResponse> updateMe(@Valid @RequestBody UpdateProfileRequest request,
                                                     @AuthenticationPrincipal AppPrincipal principal) {
        return ApiResponse.success(userService.updateProfile(principal, request));
    }

    @PostMapping("/change-password")
    public ApiResponse<Void> changePassword(@Valid @RequestBody ChangePasswordRequest request,
                                            @AuthenticationPrincipal AppPrincipal principal) {
        userService.changePassword(principal, request);
        return ApiResponse.success();
    }
}
