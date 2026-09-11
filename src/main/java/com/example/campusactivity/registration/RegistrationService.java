package com.example.campusactivity.registration;

import java.time.LocalDateTime;
import java.util.List;

import com.example.campusactivity.activity.Activity;
import com.example.campusactivity.activity.ActivityRepository;
import com.example.campusactivity.activity.ActivityStatus;
import com.example.campusactivity.common.BusinessException;
import com.example.campusactivity.registration.dto.HistoryRecordResponse;
import com.example.campusactivity.registration.dto.RegistrationResponse;
import com.example.campusactivity.security.AppPrincipal;
import com.example.campusactivity.user.User;
import com.example.campusactivity.user.UserRepository;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RegistrationService {

    private final RegistrationRepository registrationRepository;
    private final ActivityRepository activityRepository;
    private final UserRepository userRepository;

    public RegistrationService(RegistrationRepository registrationRepository,
                               ActivityRepository activityRepository, UserRepository userRepository) {
        this.registrationRepository = registrationRepository;
        this.activityRepository = activityRepository;
        this.userRepository = userRepository;
    }

    @Transactional
    public RegistrationResponse register(Long activityId, AppPrincipal principal) {
        Activity activity = activityRepository.findLockedById(activityId)
                .orElseThrow(() -> new BusinessException(HttpStatus.NOT_FOUND, "活动不存在"));
        LocalDateTime now = LocalDateTime.now();
        if (activity.getStatus() != ActivityStatus.PUBLISHED) {
            throw new BusinessException(HttpStatus.CONFLICT, "活动尚未发布或已取消");
        }
        if (!now.isBefore(activity.getRegistrationDeadline())) {
            throw new BusinessException(HttpStatus.CONFLICT, "活动报名已截止");
        }
        if (registrationRepository.existsByActivityIdAndStudentId(activityId, principal.id())) {
            throw new BusinessException(HttpStatus.CONFLICT, "请勿重复报名");
        }
        if (registrationRepository.countByActivityId(activityId) >= activity.getCapacity()) {
            throw new BusinessException(HttpStatus.CONFLICT, "活动名额已满");
        }
        User student = userRepository.findById(principal.id())
                .orElseThrow(() -> new BusinessException(HttpStatus.UNAUTHORIZED, "用户不存在"));
        return toResponse(registrationRepository.save(new Registration(activity, student)));
    }

    @Transactional
    public void cancel(Long activityId, AppPrincipal principal) {
        if (!registrationRepository.existsByActivityIdAndStudentId(activityId, principal.id())) {
            throw new BusinessException(HttpStatus.NOT_FOUND, "未找到报名记录");
        }
        registrationRepository.deleteByActivityIdAndStudentId(activityId, principal.id());
    }

    @Transactional(readOnly = true)
    public List<RegistrationResponse> listMine(AppPrincipal principal) {
        return registrationRepository.findByStudentIdOrderByRegisteredAtDesc(principal.id())
                .stream().map(this::toResponse).toList();
    }

    @Transactional(readOnly = true)
    public List<HistoryRecordResponse> listHistory(AppPrincipal principal) {
        return registrationRepository.findHistoryByStudentId(principal.id(), LocalDateTime.now())
                .stream().map(this::toHistoryResponse).toList();
    }

    private RegistrationResponse toResponse(Registration registration) {
        Activity activity = registration.getActivity();
        return new RegistrationResponse(registration.getId(), activity.getId(), activity.getTitle(),
                activity.getStartTime(), registration.getRegisteredAt());
    }

    private HistoryRecordResponse toHistoryResponse(Registration registration) {
        Activity activity = registration.getActivity();
        return new HistoryRecordResponse(registration.getId(), activity.getId(), activity.getTitle(),
                activity.getLocation(), activity.getStartTime(), activity.getEndTime(),
                registration.getRegisteredAt());
    }
}

