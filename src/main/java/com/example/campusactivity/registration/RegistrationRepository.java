package com.example.campusactivity.registration;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface RegistrationRepository extends JpaRepository<Registration, Long> {
    long countByActivityId(Long activityId);
    boolean existsByActivityIdAndStudentId(Long activityId, Long studentId);
    void deleteByActivityIdAndStudentId(Long activityId, Long studentId);
    List<Registration> findByStudentIdOrderByRegisteredAtDesc(Long studentId);

    @Query("select r from Registration r where r.student.id = :studentId and r.activity.endTime < :now order by r.activity.startTime desc")
    List<Registration> findHistoryByStudentId(@Param("studentId") Long studentId, @Param("now") LocalDateTime now);
}

