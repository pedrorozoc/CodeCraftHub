# Plan de implementación: Estadísticas de cursos

**Rama**: `003-course-stats` | **Fecha**: 2026-11-01 | **Especificación**: [spec.md](spec.md)

**Entrada**: Especificación de funcionalidad de `specs/003-course-stats/spec.md`.

## Resumen

Incorporar una consulta de solo lectura en `GET /api/courses/stats` que informe el total de cursos y los conteos de los tres estados admitidos. La solución reutilizará la lectura segura de `courses.json` del servicio actual, calculará el resumen para la colección leída y devolverá una representación JSON consistente desde el controlador de cursos. No se modificará el catálogo ni se añadirá persistencia.

## Contexto técnico

**Lenguaje/versión**: Java 21 (LTS).

**Dependencias principales**: Spring Boot 3.4.5, `spring-boot-starter-web`, Jackson gestionado por Spring Boot y `spring-boot-starter-test`.

**Almacenamiento**: Archivo local UTF-8 `courses.json`; la estadística se calcula en memoria a partir del catálogo existente y no se persiste.

**Pruebas**: JUnit 5, Spring Boot Test y MockMvc; pruebas unitarias para la agregación y pruebas de integración del contrato HTTP.

**Plataforma objetivo**: JVM con Java 21 y Maven 3.6.3 o superior, como servicio HTTP local.

**Tipo de proyecto**: Servicio web único, sin frontend.

**Objetivos de rendimiento**: Para una colección local de hasta 1.000 cursos, la consulta de estadísticas completa en menos de un segundo en desarrollo local, excluyendo fallos de E/S.

**Restricciones**: Una sola instancia; sin autenticación, base de datos, ORM, migraciones ni servicios externos. La consulta no puede modificar cursos. Los nombres de campos JSON son snake_case y los estados visibles mantienen las etiquetas existentes.

**Escala/alcance**: Una persona, un archivo local y una ruta adicional de resumen; no hay concurrencia entre procesos ni historial de estadísticas.

## Verificación de constitución

*Puerta: aprobada antes de la investigación y verificada de nuevo tras el diseño.*

| Regla | Evidencia de cumplimiento | Estado |
|---|---|---|
| Simplicidad para principiantes | Se añade una única operación de consulta y una agregación directa de la colección existente, sin capas ni dependencias nuevas. | Aprobado |
| Java, Spring Boot y REST | La capacidad se expone como una ruta HTTP de solo lectura con respuesta JSON y errores existentes. | Aprobado |
| JSON como fuente de datos | Solo se reutiliza la lectura del archivo `courses.json`; no se añade otra fuente de datos. | Aprobado |
| Integridad del modelo de curso | Cada curso contribuye a exactamente un conteo según el `CourseStatus` existente; no se cambian cursos ni estados. | Aprobado |
| Sin usuarios ni autenticación | No se añaden identidades, permisos ni rutas de usuarios. | Aprobado |
| Código inglés / documentación español | Los identificadores nuevos se nombrarán en inglés; estos documentos, comentarios y mensajes permanecen en español. | Aprobado |

La revisión posterior al diseño confirma los mismos seis puntos: el contrato, modelo, guía de validación y estrategia de pruebas no introducen violaciones ni complejidad que requiera excepción.

## Estructura del proyecto

### Documentación de esta funcionalidad

```text
specs/003-course-stats/
├── plan.md
├── research.md
├── data-model.md
├── quickstart.md
├── contracts/course-stats-api.md
└── tasks.md             # Creado posteriormente por $speckit-tasks
```

### Código fuente (raíz del repositorio)

```text
pom.xml
src/
├── main/java/com/codecrafthub/
│   ├── controller/CourseController.java       # Nueva operación GET de estadísticas
│   ├── model/Course.java
│   ├── model/CourseStatus.java
│   └── service/CourseService.java             # Agregación sobre el catálogo leído
└── test/java/com/codecrafthub/
    ├── controller/CourseControllerIntegrationTest.java
    └── service/CourseServiceTest.java
```

**Decisión de estructura**: Se conserva el único proyecto Maven y sus límites actuales. El controlador expone HTTP, el servicio lee y agrega el catálogo, y el modelo de estado existente define las tres categorías. No se requiere otra capa, base de datos ni un recurso persistente para el resumen.

## Seguimiento de complejidad

No hay violaciones de la constitución ni complejidad que requiera justificación.
