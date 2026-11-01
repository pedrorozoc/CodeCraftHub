---

description: "Tareas de implementación para la API CRUD de cursos"
---

# Tareas: Gestión CRUD de cursos

**Entrada**: Documentos de diseño de `specs/001-course-crud-api/`

**Prerrequisitos**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md),
[data-model.md](data-model.md) y [contrato HTTP](contracts/course-api.md).

**Pruebas**: Obligatorias por la constitución del proyecto: se incluyen pruebas unitarias de
validación/persistencia y pruebas HTTP con MockMvc antes de cada implementación de historia.

**Organización**: Las tareas se agrupan por historia para que cada incremento pueda validarse por
separado tras completar la base común.

## Formato: `[ID] [P?] [Historia] Descripción`

- **[P]**: Puede ejecutarse en paralelo con otras tareas sin dependencias incompletas.
- **[USn]**: Historia de usuario asociada; solo aparece en fases de historia.
- Cada tarea incluye la ruta exacta del archivo que crea o modifica.

## Convenciones de rutas

- Código: `src/main/java/com/codecrafthub/`
- Recursos: `src/main/resources/`
- Pruebas: `src/test/java/com/codecrafthub/`

## Fase 1: Configuración

**Propósito**: Inicializar el único proyecto Maven y la estructura de código prevista.

- [X] T001 Crear `pom.xml` con Java 21, Spring Boot 3.4.5, `spring-boot-starter-web`, `spring-boot-starter-validation` y `spring-boot-starter-test`.
- [X] T002 Crear `src/main/java/com/codecrafthub/CodeCraftHubApplication.java` y `src/main/resources/application.properties` con la aplicación Spring Boot y la ruta configurable de `courses.json`.

---

## Fase 2: Base común bloqueante

**Propósito**: Construir el modelo, persistencia y formato de errores que necesitan todas las
historias.

**⚠️ CRÍTICO**: Ninguna historia puede empezar hasta completar esta fase.

- [X] T003 [P] Crear `src/main/java/com/codecrafthub/model/CourseStatus.java` con identificadores Java en inglés y mapeo Jackson exacto para `Not Started`, `In Progress` y `Completed`.
- [X] T004 Crear `src/main/java/com/codecrafthub/model/Course.java` con `id`, `name`, `description`, `targetDate`, `status` y `createdAt`; aplicar anotaciones Jackson para `target_date` y `created_at`, y validar literalmente: «nombre y descripción no pueden estar vacíos ni contener solo espacios», «fecha de calendario en formato `YYYY-MM-DD`» y estado «exactamente `Not Started`, `In Progress` o `Completed`».
- [X] T005 Crear `src/main/java/com/codecrafthub/exception/CourseNotFoundException.java`, `src/main/java/com/codecrafthub/exception/StorageException.java` y `src/main/java/com/codecrafthub/exception/ApiExceptionHandler.java` para respuestas JSON snake_case con `timestamp`, `status`, `error_code`, `message` y `path`.
- [X] T006 Crear `src/main/java/com/codecrafthub/service/CourseService.java` con carga inicial que cree `courses.json` como `{"next_id":1,"courses":[]}`, lectura Jackson, escritura temporal y movimiento de reemplazo, y secuencia `next_id` persistente.
- [X] T007 Crear `src/main/java/com/codecrafthub/controller/CourseController.java` con mapeo base `/api/courses`, negociación `application/json` y dependencia de `CourseService`.

**Punto de control**: La aplicación compila, inicia con un archivo de cursos válido y dispone de un
formato único para errores.

---

## Fase 3: Historia de usuario 1 - Registrar y consultar cursos (Prioridad: P1) 🎯 MVP

**Objetivo**: Permitir crear un curso válido y recuperar la colección completa con identificador y
marca de tiempo asignados.

**Prueba independiente**: Crear un curso mediante `POST /api/courses`, obtener `201 Created`, y
comprobar con `GET /api/courses` que aparece con `id` 1 y `created_at`.

### Pruebas de la historia de usuario 1

- [X] T008 [P] [US1] Crear `src/test/java/com/codecrafthub/service/CourseServiceTest.java` con pruebas de creación, listado, `next_id` y persistencia en un directorio temporal.
- [X] T009 [P] [US1] Crear `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` con contratos de `POST /api/courses` y `GET /api/courses`, `201/200`, cuerpos snake_case y estados JSON exactos.
- [X] T010 [P] [US1] Añadir a `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` casos de campos obligatorios ausentes, texto en blanco, `target_date` inválido y estados distintos de `Not Started`, `In Progress` o `Completed`, esperando `400` y `error_code` `validation_error`.

### Implementación de la historia de usuario 1

- [X] T011 [US1] Implementar en `src/main/java/com/codecrafthub/service/CourseService.java` la creación y listado, asignando identificador positivo consecutivo desde 1 y `createdAt` UTC, y persistiendo toda mutación.
- [X] T012 [US1] Implementar en `src/main/java/com/codecrafthub/controller/CourseController.java` `POST /api/courses` y `GET /api/courses`, incluyendo `Location` en la creación y validación de solicitudes.
- [ ] T013 [US1] Ejecutar y ajustar `src/test/java/com/codecrafthub/service/CourseServiceTest.java` y `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` hasta que prueben creación, lista y errores de validación.

**Punto de control**: Se puede registrar y listar un curso sin depender de las historias P2 o P3.

---

## Fase 4: Historia de usuario 2 - Consultar y modificar un curso (Prioridad: P2)

**Objetivo**: Recuperar un curso por identificador y reemplazar sus cuatro campos editables sin
alterar su identidad ni fecha de creación.

**Prueba independiente**: Con un curso existente, `GET /api/courses/{id}` devuelve el recurso y
`PUT /api/courses/{id}` actualiza nombre, descripción, fecha y estado conservando `id` y
`created_at`.

### Pruebas de la historia de usuario 2

- [X] T014 [P] [US2] Añadir a `src/test/java/com/codecrafthub/service/CourseServiceTest.java` pruebas de obtención, reemplazo y conservación de `id` y `createdAt`.
- [X] T015 [P] [US2] Añadir a `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` contratos de `GET /api/courses/{id}` y `PUT /api/courses/{id}`, con `200`, JSON snake_case y `404` con `course_not_found`.

### Implementación de la historia de usuario 2

- [X] T016 [US2] Implementar en `src/main/java/com/codecrafthub/service/CourseService.java` la búsqueda y reemplazo completo por identificador, preservando `id` y `createdAt` y lanzando `CourseNotFoundException` si no existe.
- [X] T017 [US2] Implementar en `src/main/java/com/codecrafthub/controller/CourseController.java` `GET /api/courses/{id}` y `PUT /api/courses/{id}`.
- [ ] T018 [US2] Ejecutar y ajustar `src/test/java/com/codecrafthub/service/CourseServiceTest.java` y `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` hasta validar obtención, actualización y recurso inexistente.

**Punto de control**: Las operaciones de P1 y P2 funcionan y siguen siendo verificables de forma
independiente.

---

## Fase 5: Historia de usuario 3 - Eliminar un curso y entender errores (Prioridad: P3)

**Objetivo**: Eliminar cursos y comunicar con claridad los fallos de recurso y persistencia.

**Prueba independiente**: Eliminar un curso existente devuelve `204`; solicitarlo después devuelve
`404`; una lectura o escritura fallida devuelve `500` con `storage_error` sin revelar detalles
internos.

### Pruebas de la historia de usuario 3

- [X] T019 [P] [US3] Añadir a `src/test/java/com/codecrafthub/service/CourseServiceTest.java` pruebas de eliminación, identificador inexistente, JSON persistido inválido y fallo de escritura usando un directorio temporal.
- [X] T020 [P] [US3] Añadir a `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` contratos de `DELETE /api/courses/{id}` para `204/404` y respuestas `500` con `error_code` `storage_error`.

### Implementación de la historia de usuario 3

- [X] T021 [US3] Implementar en `src/main/java/com/codecrafthub/service/CourseService.java` la eliminación persistente y la conversión de errores de lectura/escritura/JSON en `StorageException`.
- [X] T022 [US3] Completar en `src/main/java/com/codecrafthub/exception/ApiExceptionHandler.java` el mapeo de `StorageException` a `500` sin detalles internos, y actualizar `src/main/java/com/codecrafthub/controller/CourseController.java` con `DELETE /api/courses/{id}` que devuelva `204 No Content`.
- [ ] T023 [US3] Ejecutar y ajustar `src/test/java/com/codecrafthub/service/CourseServiceTest.java` y `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` hasta validar eliminación y todos los errores de almacenamiento.

**Punto de control**: Las cinco operaciones del contrato están completas y los errores requeridos se
presentan en JSON consistente.

---

## Fase 6: Pulido y aspectos transversales

**Propósito**: Verificar el comportamiento completo, claridad didáctica y reglas de la constitución.

- [X] T024 [P] Añadir comentarios didácticos en español y verificar que los identificadores del código estén en inglés en `src/main/java/com/codecrafthub/**/*.java`.
- [X] T025 [P] Revisar `README.md` o crear `README.md` con instrucciones de ejecución en español y ejemplos que remitan a `specs/001-course-crud-api/quickstart.md`.
- [ ] T026 Ejecutar todos los escenarios de `specs/001-course-crud-api/quickstart.md` y corregir en `src/main/java/com/codecrafthub/` o `src/test/java/com/codecrafthub/` las diferencias encontradas.
- [ ] T027 Ejecutar `mvn test` desde `pom.xml` y verificar manualmente que el reinicio preserva cursos y que `courses.json` no queda malformado tras una mutación.

---

## Dependencias y orden de ejecución

### Dependencias por fase

- **Fase 1** no tiene dependencias.
- **Fase 2** depende de T001-T002 y bloquea todas las historias.
- **US1** depende de T003-T007.
- **US2** depende de la base común y necesita un curso creado por US1 para su prueba de integración.
- **US3** depende de la base común y necesita un curso creado por US1 para comprobar su eliminación.
- **Fase 6** depende de las tres historias completadas.

### Grafo de historias

```text
Configuración → Base común → US1 (P1, MVP) → US2 (P2)
                                      └──────→ US3 (P3)
US2 + US3 → Pulido
```

### Oportunidades de paralelo

- T003 puede comenzar junto a la preparación de fuentes tras T001-T002.
- T008-T010 pueden escribirse en paralelo porque cubren comportamientos distintos de US1.
- T014 y T015 pueden escribirse en paralelo; lo mismo aplica a T019 y T020.
- T024 y T025 pueden realizarse en paralelo una vez que las historias estén completas.

## Ejemplos de ejecución paralela

### Historia de usuario 1

```text
T008: Pruebas unitarias del servicio en src/test/java/com/codecrafthub/service/CourseServiceTest.java
T009: Contratos HTTP correctos en src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java
T010: Contratos HTTP de validación en src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java
```

### Historia de usuario 2

```text
T014: Pruebas unitarias de consulta y reemplazo en src/test/java/com/codecrafthub/service/CourseServiceTest.java
T015: Contratos HTTP de consulta y reemplazo en src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java
```

### Historia de usuario 3

```text
T019: Pruebas unitarias de borrado y almacenamiento en src/test/java/com/codecrafthub/service/CourseServiceTest.java
T020: Contratos HTTP de borrado y errores 500 en src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java
```

## Estrategia de implementación

1. Completar configuración y base común.
2. Entregar el MVP al terminar US1: crear y listar cursos con validación y JSON persistido.
3. Añadir US2 para consulta y reemplazo conservando identidad y fecha de creación.
4. Añadir US3 para borrado y errores de almacenamiento.
5. Ejecutar las pruebas y la guía rápida antes de considerar la funcionalidad terminada.
