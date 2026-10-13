package com.rikkei.microservice.course.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.rikkei.microservice.course.dto.CourseRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class CourseControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    @DisplayName("GET /api/courses without role header should return 403 Forbidden")
    void getCoursesWithoutHeader_shouldReturn403() throws Exception {
        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("GET /api/courses with STUDENT role header should return 200 OK")
    void getCoursesWithStudentRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .header("X-User-Role", "STUDENT")
                        .header("X-User-Name", "student1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("GET /api/courses with INSTRUCTOR role header should return 200 OK")
    void getCoursesWithInstructorRole_shouldReturn200() throws Exception {
        mockMvc.perform(get("/api/courses")
                        .header("X-User-Role", "INSTRUCTOR")
                        .header("X-User-Name", "instructor1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(greaterThanOrEqualTo(2))));
    }

    @Test
    @DisplayName("POST /api/courses with STUDENT role header should return 403 Forbidden")
    void createCourseWithStudentRole_shouldReturn403() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .title("New Java Course")
                .description("Intro to Java")
                .instructor("Instructor")
                .build();

        mockMvc.perform(post("/api/courses")
                        .header("X-User-Role", "STUDENT")
                        .header("X-User-Name", "student1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isForbidden());
    }

    @Test
    @DisplayName("POST /api/courses with INSTRUCTOR role header should return 201 Created")
    void createCourseWithInstructorRole_shouldReturn201() throws Exception {
        CourseRequest request = CourseRequest.builder()
                .title("Advanced Microservices")
                .description("Distributed systems with Spring Cloud")
                .instructor("Instructor Alpha")
                .build();

        mockMvc.perform(post("/api/courses")
                        .header("X-User-Role", "INSTRUCTOR")
                        .header("X-User-Name", "instructor1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id", notNullValue()))
                .andExpect(jsonPath("$.title", is("Advanced Microservices")));
    }
}
