package com.example.campusactivity.registration;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    long countByActivityId(Long activityId);
    boolean existsByActivityIdAndStudentId(Long activityId, Long studentId);
    void deleteByActivityIdAndStudentId(Long activityId, Long studentId);
    List<Registration> findByStudentIdOrderByRegisteredAtDesc(Long studentId);
}

