# Plan de implementación: Gestión CRUD de cursos

**Rama**: `001-course-crud-api` | **Fecha**: 2026-11-01 | **Especificación**:
[spec.md](spec.md)

**Entrada**: Especificación de funcionalidad de `specs/001-course-crud-api/spec.md`.

## Resumen

Crear una API REST educativa para crear, listar, obtener, actualizar y eliminar cursos. El servicio
se implementará como una única aplicación Java 21 con Spring Boot 3.4.5. Persistirá una colección y
el siguiente identificador en `courses.json`, usando Jackson para serializar JSON y escritura a un
archivo temporal seguida de movimiento al destino. El contrato HTTP conserva nombres snake_case y
estados visibles exactamente `Not Started`, `In Progress` y `Completed`.

## Contexto técnico

**Lenguaje/versión**: Java 21 (LTS).

**Dependencias principales**: Spring Boot 3.4.5; `spring-boot-starter-web`;
`spring-boot-starter-validation`; Jackson, gestionado por Spring Boot; y
`spring-boot-starter-test`.

**Almacenamiento**: Archivo local UTF-8 `courses.json`, sin base de datos ni ORM.

**Pruebas**: JUnit 5, Spring Boot Test y MockMvc; pruebas unitarias del servicio de archivos y
pruebas de contrato HTTP.

**Plataforma objetivo**: JVM con Java 21 y Maven 3.6.3 o superior; ejecución local como servicio
HTTP embebido.

**Tipo de proyecto**: Servicio web único, sin frontend.

**Objetivos de rendimiento**: Las operaciones sobre una colección local de hasta 1.000 cursos se
completan en menos de 1 segundo en desarrollo local, excluyendo fallos de E/S.

**Restricciones**: Una sola instancia y una escritura a la vez; sin autenticación, base de datos,
ORM, migraciones ni servicios externos. Todas las respuestas JSON usan campos snake_case.

**Escala/alcance**: Una persona, un archivo local y cinco operaciones CRUD; no se contempla acceso
concurrente entre procesos.

## Verificación de constitución

*Puerta: aprobada antes de la investigación y verificada de nuevo tras el diseño.*

| Regla | Evidencia de cumplimiento | Estado |
|---|---|---|
| Simplicidad para principiantes | Un módulo Maven, capas Controller/Service/Model y una única frontera de archivo. | Aprobado |
| Java, Spring Boot y REST | Java 21, Spring Boot y cinco rutas REST documentadas. | Aprobado |
| JSON como fuente de datos | `courses.json` es la única persistencia; no hay base de datos ni ORM. | Aprobado |
| Integridad de curso | Se validan nombre, descripción, fecha y estado; el estado de transporte usa las tres etiquetas exactas de la especificación. | Aprobado |
| Sin usuarios ni autenticación | Ninguna dependencia, ruta ni entidad de identidad está incluida. | Aprobado |
| Código inglés / documentación español | Paquetes, clases y métodos se nombrarán en inglés; comentarios, mensajes y documentos se redactarán en español. | Aprobado |

La traducción de estados acordada por la especificación se trata como contrato JSON para personas;
la implementación conservará un valor de enumeración inequívoco y nombres de código en inglés.

## Project Structure

### Documentación de esta funcionalidad

```text
specs/001-course-crud-api/
├── plan.md              # This file ($speckit-plan command output)
├── research.md          # Phase 0 output ($speckit-plan command)
├── data-model.md        # Phase 1 output ($speckit-plan command)
├── quickstart.md        # Phase 1 output ($speckit-plan command)
├── contracts/           # Phase 1 output ($speckit-plan command)
└── tasks.md             # Phase 2 output ($speckit-tasks command - NOT created by $speckit-plan)
```

### Código fuente (raíz del repositorio)

```text
pom.xml
src/
├── main/
│   ├── java/com/codecrafthub/
│   │   ├── CodeCraftHubApplication.java
│   │   ├── controller/CourseController.java
│   │   ├── exception/ApiExceptionHandler.java
│   │   ├── exception/CourseNotFoundException.java
│   │   ├── exception/StorageException.java
│   │   ├── model/Course.java
│   │   ├── model/CourseStatus.java
│   │   └── service/CourseService.java
│   └── resources/application.properties
└── test/java/com/codecrafthub/
    ├── controller/CourseControllerIntegrationTest.java
    └── service/CourseServiceTest.java
```

**Decisión de estructura**: Un único proyecto Maven. `CourseController` se limita a HTTP,
`CourseService` concentra validación de flujo y persistencia; el modelo contiene las anotaciones
Jackson que preservan el contrato snake_case. Las excepciones separan errores de recurso, validación
y almacenamiento para producir respuestas JSON consistentes.

## Seguimiento de complejidad

No hay violaciones de la constitución ni complejidad que requiera justificación.
