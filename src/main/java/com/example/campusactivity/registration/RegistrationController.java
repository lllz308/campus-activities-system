package com.example.campusactivity.registration;

import java.util.List;

import com.example.campusactivity.common.ApiResponse;
import com.example.campusactivity.registration.dto.RegistrationResponse;
import com.example.campusactivity.security.AppPrincipal;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/student")
public class RegistrationController {

    private final RegistrationService registrationService;

    public RegistrationController(RegistrationService registrationService) {
        this.registrationService = registrationService;
    }

    @PostMapping("/activities/{activityId}/registrations")
    public ApiResponse<RegistrationResponse> register(@PathVariable Long activityId,
                                                      @AuthenticationPrincipal AppPrincipal principal) {
        return ApiResponse.success(registrationService.register(activityId, principal));
    }

    @DeleteMapping("/activities/{activityId}/registrations")
    public ApiResponse<Void> cancel(@PathVariable Long activityId,
                                    @AuthenticationPrincipal AppPrincipal principal) {
        registrationService.cancel(activityId, principal);
        return ApiResponse.success();
    }

    @GetMapping("/registrations")
    public ApiResponse<List<RegistrationResponse>> listMine(@AuthenticationPrincipal AppPrincipal principal) {
        return ApiResponse.success(registrationService.listMine(principal));
    }
}

