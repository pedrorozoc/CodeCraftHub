package com.codecrafthub.service;

import com.codecrafthub.exception.CourseNotFoundException;
import com.codecrafthub.exception.StorageException;
import com.codecrafthub.model.Course;
import com.codecrafthub.model.CourseStatus;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class CourseServiceTest {

    @TempDir
    Path temporaryDirectory;

    @Test
    void createsListsAndPersistsCourses() {
        Path file = temporaryDirectory.resolve("courses.json");
        CourseService service = serviceFor(file);

        Course created = service.create(course("Curso REST", CourseStatus.NOT_STARTED));

        assertEquals(1L, created.getId());
        assertEquals(1, service.findAll().size());
        assertEquals(2L, new CourseService(mapper(), file.toString()).create(
                course("Segundo curso", CourseStatus.IN_PROGRESS)).getId());
    }

    @Test
    void updatesAndKeepsGeneratedFields() {
        CourseService service = serviceFor(temporaryDirectory.resolve("courses.json"));
        Course created = service.create(course("Original", CourseStatus.NOT_STARTED));

        Course updated = service.update(created.getId(), course("Actualizado", CourseStatus.COMPLETED));

        assertEquals(created.getId(), updated.getId());
        assertEquals(created.getCreatedAt(), updated.getCreatedAt());
        assertEquals("Actualizado", updated.getName());
    }

    @Test
    void deletesAndReportsMissingCourses() {
        CourseService service = serviceFor(temporaryDirectory.resolve("courses.json"));
        long id = service.create(course("Eliminar", CourseStatus.NOT_STARTED)).getId();

        service.delete(id);

        assertThrows(CourseNotFoundException.class, () -> service.findById(id));
        assertThrows(CourseNotFoundException.class, () -> service.delete(id));
    }

    @Test
    void reportsMalformedStorage() throws Exception {
        Path file = temporaryDirectory.resolve("courses.json");
        Files.writeString(file, "not-json");
        CourseService service = new CourseService(mapper(), file.toString());

        assertThrows(StorageException.class, service::findAll);
    }

    @Test
    void reportsWriteFailureWhenParentPathIsAFile() throws Exception {
        Path parentFile = temporaryDirectory.resolve("not-a-directory");
        Files.writeString(parentFile, "occupied");

        assertThrows(StorageException.class,
                () -> new CourseService(mapper(), parentFile.resolve("courses.json").toString()));
    }

    @Test
    void calculatesStatisticsForEveryStatusAndIncludesEmptyStatuses() {
        CourseService service = serviceFor(temporaryDirectory.resolve("courses.json"));
        service.create(course("Pendiente", CourseStatus.NOT_STARTED));
        service.create(course("En curso", CourseStatus.IN_PROGRESS));
        service.create(course("Finalizado", CourseStatus.COMPLETED));

        Map<String, Object> statistics = service.getStatistics();
        @SuppressWarnings("unchecked")
        Map<String, Long> coursesByStatus = (Map<String, Long>) statistics.get("courses_by_status");

        assertEquals(3L, statistics.get("total_courses"));
        assertEquals(1L, coursesByStatus.get("Not Started"));
        assertEquals(1L, coursesByStatus.get("In Progress"));
        assertEquals(1L, coursesByStatus.get("Completed"));
        assertEquals(3, service.findAll().size());
    }

    @Test
    void calculatesZeroStatisticsForAnEmptyCatalog() {
        CourseService service = serviceFor(temporaryDirectory.resolve("courses.json"));

        Map<String, Object> statistics = service.getStatistics();
        @SuppressWarnings("unchecked")
        Map<String, Long> coursesByStatus = (Map<String, Long>) statistics.get("courses_by_status");

        assertEquals(0L, statistics.get("total_courses"));
        assertEquals(0L, coursesByStatus.get("Not Started"));
        assertEquals(0L, coursesByStatus.get("In Progress"));
        assertEquals(0L, coursesByStatus.get("Completed"));
    }

    private CourseService serviceFor(Path file) {
        return new CourseService(mapper(), file.toString());
    }

    private ObjectMapper mapper() {
        return new ObjectMapper().registerModule(new JavaTimeModule());
    }

    private Course course(String name, CourseStatus status) {
        Course course = new Course();
        course.setName(name);
        course.setDescription("Descripción de prueba");
        course.setTargetDate(LocalDate.of(2026, 12, 15));
        course.setStatus(status);
        return course;
    }
}
