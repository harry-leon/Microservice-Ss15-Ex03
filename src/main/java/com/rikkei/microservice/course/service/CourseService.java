package com.rikkei.microservice.course.service;

import com.rikkei.microservice.course.dto.CourseRequest;
import com.rikkei.microservice.course.dto.CourseResponse;
import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class CourseService {

    private final List<CourseResponse> courses = Collections.synchronizedList(new ArrayList<>());
    private final AtomicLong idGenerator = new AtomicLong(1);

    @PostConstruct
    public void init() {
        courses.add(CourseResponse.builder()
                .id(idGenerator.getAndIncrement())
                .title("Microservices Architecture with Spring Cloud")
                .description("Comprehensive guide to building microservices with Spring Cloud and Spring Boot.")
                .instructor("Instructor Alpha")
                .build());

        courses.add(CourseResponse.builder()
                .id(idGenerator.getAndIncrement())
                .title("Role-Based Access Control (RBAC) in Microservices")
                .description("Learn how to implement header-based RBAC without redundant JWT validations.")
                .instructor("Instructor Beta")
                .build());
    }

    public List<CourseResponse> getAllCourses() {
        return new ArrayList<>(courses);
    }

    public CourseResponse createCourse(CourseRequest request) {
        CourseResponse newCourse = CourseResponse.builder()
                .id(idGenerator.getAndIncrement())
                .title(request.getTitle())
                .description(request.getDescription())
                .instructor(request.getInstructor())
                .build();
        courses.add(newCourse);
        return newCourse;
    }
}
