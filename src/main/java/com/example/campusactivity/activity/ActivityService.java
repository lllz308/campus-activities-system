package com.example.campusactivity.activity;

import java.time.LocalDateTime;
import java.util.List;

import com.example.campusactivity.activity.dto.ActivityRequest;
import com.example.campusactivity.activity.dto.ActivityResponse;
import com.example.campusactivity.common.BusinessException;
import com.example.campusactivity.registration.RegistrationRepository;
import com.example.campusactivity.security.AppPrincipal;
import com.example.campusactivity.user.User;
import com.example.campusactivity.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ActivityService {

    private final ActivityRepository activityRepository;
    private final RegistrationRepository registrationRepository;
    private final UserRepository userRepository;

    public ActivityService(ActivityRepository activityRepository, RegistrationRepository registrationRepository,
                           UserRepository userRepository) {
        this.activityRepository = activityRepository;
        this.registrationRepository = registrationRepository;
        this.userRepository = userRepository;
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> listPublished() {
        return activityRepository
                .findByStatusAndEndTimeAfterOrderByStartTimeAsc(ActivityStatus.PUBLISHED, LocalDateTime.now())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public ActivityResponse getPublished(Long id) {
        Activity activity = find(id);
        if (activity.getStatus() != ActivityStatus.PUBLISHED) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "活动不存在");
        }
        return toResponse(activity);
    }

    @Transactional(readOnly = true)
    public List<ActivityResponse> listMine(AppPrincipal principal) {
        return activityRepository.findByOrganizerIdOrderByCreatedAtDesc(principal.id())
                .stream().map(this::toResponse).toList();
    }

    @Transactional
    public ActivityResponse create(ActivityRequest request, AppPrincipal principal) {
        validateTimes(request);
        User organizer = userRepository.findById(principal.id())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        Activity activity = new Activity(request.title(), request.description(), request.location(),
                request.startTime(), request.endTime(), request.registrationDeadline(), request.capacity(),
                request.status(), organizer);
        return toResponse(activityRepository.save(activity));
    }

    @Transactional
    public ActivityResponse update(Long id, ActivityRequest request, AppPrincipal principal) {
        validateTimes(request);
        Activity activity = findOwned(id, principal);
        long registeredCount = registrationRepository.countByActivityId(id);
        if (request.capacity() < registeredCount) {
            throw new BusinessException(HttpStatus.CONFLICT, "活动容量不能小于当前报名人数");
        }
        activity.update(request.title(), request.description(), request.location(), request.startTime(),
                request.endTime(), request.registrationDeadline(), request.capacity(), request.status());
        return toResponse(activity);
    }

    @Transactional
    public void delete(Long id, AppPrincipal principal) {
        Activity activity = findOwned(id, principal);
        if (registrationRepository.countByActivityId(id) > 0) {
            throw new BusinessException(HttpStatus.CONFLICT, "活动已有学生报名，不能直接删除，请改为取消活动");
        }
        activityRepository.delete(activity);
    }

    private void validateTimes(ActivityRequest request) {
        if (!request.registrationDeadline().isBefore(request.startTime())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "报名截止时间必须早于活动开始时间");
        }
        if (!request.startTime().isBefore(request.endTime())) {
            throw new BusinessException(HttpStatus.BAD_REQUEST, "活动结束时间必须晚于开始时间");
        }
    }

    private Activity findOwned(Long id, AppPrincipal principal) {
        Activity activity = find(id);
        if (!activity.getOrganizer().getId().equals(principal.id())) {
            throw new BusinessException(HttpStatus.FORBIDDEN, "只能管理自己发布的活动");
        }
        return activity;
    }

    private Activity find(Long id) {
        return activityRepository.findById(id)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "活动不存在"));
    }

    private ActivityResponse toResponse(Activity activity) {
        return new ActivityResponse(activity.getId(), activity.getTitle(), activity.getDescription(),
                activity.getLocation(), activity.getStartTime(), activity.getEndTime(),
                activity.getRegistrationDeadline(), activity.getCapacity(),
                registrationRepository.countByActivityId(activity.getId()), activity.getStatus(),
                activity.getOrganizer().getId(), activity.getOrganizer().getDisplayName());
    }
}

