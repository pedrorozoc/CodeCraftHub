package com.codecrafthub.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.nio.file.Files;
import java.nio.file.Path;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = "app.courses-file=target/test-courses.json")
@AutoConfigureMockMvc
class CourseControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @AfterEach
    void cleanStorage() throws Exception {
        Files.deleteIfExists(Path.of("target/test-courses.json"));
    }

    @Test
    void createsAndListsCoursesWithSnakeCaseFields() throws Exception {
        String body = "{\"name\":\"Curso REST\",\"description\":\"Aprender API\","
                + "\"target_date\":\"2026-12-15\",\"status\":\"Not Started\"}";

        mockMvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andExpect(header().string("Location", org.hamcrest.Matchers.endsWith("/api/courses/1")))
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.target_date").value("2026-12-15"))
                .andExpect(jsonPath("$.created_at").exists())
                .andExpect(jsonPath("$.status").value("Not Started"));

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("Curso REST"));
    }

    @Test
    void rejectsInvalidRequests() throws Exception {
        String body = "{\"name\":\" \",\"description\":\"x\","
                + "\"target_date\":\"2026-13-45\",\"status\":\"started\"}";

        mockMvc.perform(post("/api/courses").contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error_code").value("validation_error"));
    }

    @Test
    void getsUpdatesAndDeletesCourses() throws Exception {
        long id = createCourse();
        String update = "{\"name\":\"Actualizado\",\"description\":\"Aprender API\","
                + "\"target_date\":\"2026-12-20\",\"status\":\"In Progress\"}";

        mockMvc.perform(get("/api/courses/{id}", id))
                .andExpect(status().isOk());
        mockMvc.perform(put("/api/courses/{id}", id).contentType(MediaType.APPLICATION_JSON).content(update))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("In Progress"));
        mockMvc.perform(delete("/api/courses/{id}", id))
                .andExpect(status().isNoContent());
        mockMvc.perform(get("/api/courses/{id}", id))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error_code").value("course_not_found"));
    }

    @Test
    void reportsStorageErrorsWithoutInternalDetails() throws Exception {
        Path storagePath = Path.of("target/test-courses.json");
        Files.deleteIfExists(storagePath);
        Files.createDirectory(storagePath);

        mockMvc.perform(get("/api/courses"))
                .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.error_code").value("storage_error"));
    }

    private long createCourse() throws Exception {
        String body = "{\"name\":\"Curso REST\",\"description\":\"Aprender API\","
                + "\"target_date\":\"2026-12-15\",\"status\":\"Not Started\"}";
        String response = mockMvc.perform(post("/api/courses")
                        .contentType(MediaType.APPLICATION_JSON).content(body))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        return objectMapper.readTree(response).get("id").asLong();
    }
}
