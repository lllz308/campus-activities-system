package com.example.campusactivity.activity;

import java.util.List;

import com.example.campusactivity.activity.dto.ActivityRequest;
import com.example.campusactivity.activity.dto.ActivityResponse;
import com.example.campusactivity.common.ApiResponse;
import com.example.campusactivity.security.AppPrincipal;
import jakarta.validation.Valid;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ActivityController {

    private final ActivityService activityService;

    public ActivityController(ActivityService activityService) {
        this.activityService = activityService;
    }

    @GetMapping("/activities")
    public ApiResponse<List<ActivityResponse>> list() {
        return ApiResponse.success(activityService.listPublished());
    }

    @GetMapping("/activities/{id}")
    public ApiResponse<ActivityResponse> get(@PathVariable Long id) {
        return ApiResponse.success(activityService.getPublished(id));
    }

    @GetMapping("/teacher/activities")
    public ApiResponse<List<ActivityResponse>> listMine(@AuthenticationPrincipal AppPrincipal principal) {
        return ApiResponse.success(activityService.listMine(principal));
    }

    @PostMapping("/teacher/activities")
    public ApiResponse<ActivityResponse> create(@Valid @RequestBody ActivityRequest request,
                                                @AuthenticationPrincipal AppPrincipal principal) {
        return ApiResponse.success(activityService.create(request, principal));
    }

    @PutMapping("/teacher/activities/{id}")
    public ApiResponse<ActivityResponse> update(@PathVariable Long id,
                                                @Valid @RequestBody ActivityRequest request,
                                                @AuthenticationPrincipal AppPrincipal principal) {
        return ApiResponse.success(activityService.update(id, request, principal));
    }

    @DeleteMapping("/teacher/activities/{id}")
    public ApiResponse<Void> delete(@PathVariable Long id,
                                    @AuthenticationPrincipal AppPrincipal principal) {
        activityService.delete(id, principal);
        return ApiResponse.success();
    }
}

