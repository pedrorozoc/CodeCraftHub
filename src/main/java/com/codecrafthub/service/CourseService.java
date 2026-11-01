package com.codecrafthub.service;

import com.codecrafthub.exception.CourseNotFoundException;
import com.codecrafthub.exception.StorageException;
import com.codecrafthub.model.Course;
import com.codecrafthub.model.CourseStatus;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.AtomicMoveNotSupportedException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.LinkedHashMap;
import java.util.Map;

/** Guarda todos los cursos en un único archivo JSON para mantener el ejemplo simple. */
@Service
public class CourseService {

    private final ObjectMapper objectMapper;
    private final Path coursesFile;

    public CourseService(ObjectMapper objectMapper,
                         @Value("${app.courses-file:courses.json}") String coursesFile) {
        this.objectMapper = objectMapper;
        this.coursesFile = Path.of(coursesFile);
        initializeStorage();
    }

    public synchronized List<Course> findAll() {
        return new ArrayList<>(readCatalog().getCourses());
    }

    /** Calcula un resumen actual sin modificar el catálogo persistido. */
    public synchronized Map<String, Object> getStatistics() {
        Map<String, Long> coursesByStatus = new LinkedHashMap<>();
        for (CourseStatus status : CourseStatus.values()) {
            coursesByStatus.put(status.getJsonValue(), 0L);
        }

        long totalCourses = 0;
        for (Course course : readCatalog().getCourses()) {
            coursesByStatus.computeIfPresent(course.getStatus().getJsonValue(),
                    (status, count) -> count + 1);
            totalCourses++;
        }

        Map<String, Object> statistics = new LinkedHashMap<>();
        statistics.put("total_courses", totalCourses);
        statistics.put("courses_by_status", coursesByStatus);
        return statistics;
    }

    public synchronized Course findById(long id) {
        return readCatalog().getCourses().stream()
                .filter(course -> course.getId() == id)
                .findFirst()
                .orElseThrow(() -> new CourseNotFoundException(id));
    }

    public synchronized Course create(Course course) {
        normalize(course);
        CourseCatalog catalog = readCatalog();
        course.setId(catalog.getNextId());
        course.setCreatedAt(Instant.now());
        catalog.setNextId(catalog.getNextId() + 1);
        catalog.getCourses().add(course);
        writeCatalog(catalog);
        return course;
    }

    public synchronized Course update(long id, Course course) {
        normalize(course);
        CourseCatalog catalog = readCatalog();
        for (int index = 0; index < catalog.getCourses().size(); index++) {
            Course existing = catalog.getCourses().get(index);
            if (existing.getId() == id) {
                course.setId(existing.getId());
                course.setCreatedAt(existing.getCreatedAt());
                catalog.getCourses().set(index, course);
                writeCatalog(catalog);
                return course;
            }
        }
        throw new CourseNotFoundException(id);
    }

    public synchronized void delete(long id) {
        CourseCatalog catalog = readCatalog();
        boolean removed = catalog.getCourses().removeIf(course -> course.getId() == id);
        if (!removed) {
            throw new CourseNotFoundException(id);
        }
        writeCatalog(catalog);
    }

    private void normalize(Course course) {
        if (course.getName() != null) {
            course.setName(course.getName().trim());
        }
        if (course.getDescription() != null) {
            course.setDescription(course.getDescription().trim());
        }
    }

    private void initializeStorage() {
        if (!Files.exists(coursesFile)) {
            writeCatalog(new CourseCatalog(1L, new ArrayList<>()));
        }
    }

    private CourseCatalog readCatalog() {
        try {
            if (!Files.exists(coursesFile)) {
                initializeStorage();
            }
            CourseCatalog catalog = objectMapper.readValue(coursesFile.toFile(), new TypeReference<>() { });
            if (catalog == null || catalog.getCourses() == null || catalog.getNextId() == null) {
                throw new IOException("El archivo no contiene el catálogo esperado.");
            }
            return catalog;
        } catch (IOException exception) {
            throw new StorageException("No se pudo leer courses.json.", exception);
        }
    }

    private void writeCatalog(CourseCatalog catalog) {
        Path parent = coursesFile.toAbsolutePath().getParent();
        try {
            Files.createDirectories(parent);
            Path temporaryFile = Files.createTempFile(parent, "courses-", ".tmp");
            objectMapper.writeValue(temporaryFile.toFile(), catalog);
            try {
                Files.move(temporaryFile, coursesFile, StandardCopyOption.REPLACE_EXISTING,
                        StandardCopyOption.ATOMIC_MOVE);
            } catch (AtomicMoveNotSupportedException exception) {
                Files.move(temporaryFile, coursesFile, StandardCopyOption.REPLACE_EXISTING);
            }
        } catch (IOException exception) {
            throw new StorageException("No se pudo guardar courses.json.", exception);
        }
    }

    /** Documento raíz para conservar la secuencia de identificadores entre reinicios. */
    public static class CourseCatalog {
        @JsonProperty("next_id")
        private Long nextId;
        private List<Course> courses;

        public CourseCatalog() {
        }

        public CourseCatalog(Long nextId, List<Course> courses) {
            this.nextId = nextId;
            this.courses = courses;
        }

        public Long getNextId() {
            return nextId;
        }

        public void setNextId(Long nextId) {
            this.nextId = nextId;
        }

        public List<Course> getCourses() {
            return courses;
        }

        public void setCourses(List<Course> courses) {
            this.courses = courses;
        }
    }
}
