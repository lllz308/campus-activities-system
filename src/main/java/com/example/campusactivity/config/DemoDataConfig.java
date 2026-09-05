package com.example.campusactivity.config;

import java.time.LocalDateTime;

import com.example.campusactivity.activity.Activity;
import com.example.campusactivity.activity.ActivityRepository;
import com.example.campusactivity.activity.ActivityStatus;
import com.example.campusactivity.user.Role;
import com.example.campusactivity.user.User;
import com.example.campusactivity.user.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

@Configuration
public class DemoDataConfig {

    @Bean
    CommandLineRunner demoData(UserRepository userRepository, ActivityRepository activityRepository,
                               PasswordEncoder passwordEncoder) {
        return args -> {
            User teacher = userRepository.save(new User("teacher01", passwordEncoder.encode("teacher123"),
                    "张老师", Role.TEACHER));
            userRepository.save(new User("student01", passwordEncoder.encode("student123"),
                    "李同学", Role.STUDENT));
            LocalDateTime now = LocalDateTime.now();
            activityRepository.save(new Activity("校园环保志愿活动", "清理校园公共区域并完成垃圾分类宣传。",
                    "大学生活动中心门口", now.plusDays(7), now.plusDays(7).plusHours(3),
                    now.plusDays(5), 30, ActivityStatus.PUBLISHED, teacher));
        };
    }
}

