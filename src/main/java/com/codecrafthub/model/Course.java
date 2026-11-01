package com.codecrafthub.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.time.Instant;
import java.time.LocalDate;

/** Representa un curso y define explícitamente los nombres del contrato JSON. */
public class Course {

    @JsonProperty
    private Long id;

    @NotBlank(message = "El campo name es obligatorio y no puede estar vacío.")
    private String name;

    @NotBlank(message = "El campo description es obligatorio y no puede estar vacío.")
    private String description;

    @NotNull(message = "El campo target_date es obligatorio.")
    @JsonProperty("target_date")
    private LocalDate targetDate;

    @NotNull(message = "El campo status es obligatorio.")
    private CourseStatus status;

    @JsonProperty("created_at")
    private Instant createdAt;

    public Course() {
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public LocalDate getTargetDate() {
        return targetDate;
    }

    public void setTargetDate(LocalDate targetDate) {
        this.targetDate = targetDate;
    }

    public CourseStatus getStatus() {
        return status;
    }

    public void setStatus(CourseStatus status) {
        this.status = status;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }
}
