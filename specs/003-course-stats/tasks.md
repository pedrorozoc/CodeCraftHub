---

description: "Lista de tareas para implementar estadísticas de cursos"
---

# Tareas: Estadísticas de cursos

**Entrada**: Documentos de diseño de `/specs/003-course-stats/`

**Prerrequisitos**: [plan.md](plan.md), [spec.md](spec.md), [research.md](research.md), [data-model.md](data-model.md), [contrato HTTP](contracts/course-stats-api.md) y [guía de validación](quickstart.md).

**Pruebas**: Obligatorias por la constitución del proyecto y por los escenarios de aceptación de la especificación.

**Organización**: Las tareas se agrupan por historia de usuario para que el resumen de progreso pueda implementarse y verificarse de forma independiente.

## Formato: `[ID] [P?] [Historia] Descripción`

- **[P]**: Puede ejecutarse en paralelo porque toca un archivo diferente y no depende de una tarea incompleta.
- **[US1]**: Traza la tarea a la historia de usuario P1.

## Fase 1: Preparación

**Propósito**: Confirmar el punto de partida y las convenciones que reutilizará la funcionalidad.

- [ ] T001 Verificar la lectura actual de `courses.json`, los tres valores de `CourseStatus` y la línea base de pruebas en `src/main/java/com/codecrafthub/service/CourseService.java`, `src/main/java/com/codecrafthub/model/CourseStatus.java` y `src/test/java/com/codecrafthub/`.

---

## Fase 2: Fundamentos

**Propósito**: No se requiere infraestructura nueva: el almacenamiento, la serialización JSON y el manejo de `StorageException` existentes son los prerrequisitos compartidos.

**Punto de control**: Confirmar que `CourseService.findAll()` sigue siendo la única lectura del catálogo que usará la agregación y que `ApiExceptionHandler` conserva la respuesta `storage_error`.

---

## Fase 3: Historia de usuario 1 - Consultar resumen de progreso (Prioridad: P1) 🎯 MVP

**Objetivo**: Permitir consultar el total de cursos y un conteo explícito para `Not Started`, `In Progress` y `Completed` sin alterar el catálogo.

**Prueba independiente**: Con una colección que contenga cursos en los tres estados, solicitar `GET /api/courses/stats` y comprobar `200 OK`, el total, los tres conteos y que la colección no cambió. Repetir con una colección vacía y esperar ceros.

### Pruebas para la historia de usuario 1

- [ ] T002 [P] [US1] Añadir pruebas de agregación con los tres estados, estados ausentes y colección vacía en `src/test/java/com/codecrafthub/service/CourseServiceTest.java`; verificar que los tres conteos siempre existen, cada curso cuenta una vez y el total es la suma de las categorías.
- [ ] T003 [P] [US1] Añadir pruebas MockMvc de `GET /api/courses/stats` en `src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java` para la respuesta `200` conforme a `specs/003-course-stats/contracts/course-stats-api.md`, la colección vacía, la ausencia de mutación y el error `storage_error`.

### Implementación para la historia de usuario 1

- [ ] T004 [US1] Implementar en `src/main/java/com/codecrafthub/service/CourseService.java` una operación de solo lectura que agregue el catálogo actual, inicialice los conteos de `NOT_STARTED`, `IN_PROGRESS` y `COMPLETED` en cero, y garantice que el total sea la suma de los tres conteos sin escribir `courses.json`.
- [ ] T005 [US1] Añadir `GET /api/courses/stats` a `src/main/java/com/codecrafthub/controller/CourseController.java`; delegar en la operación de servicio y serializar `total_courses` y `courses_by_status` con las etiquetas `Not Started`, `In Progress` y `Completed` establecidas en el contrato.

**Punto de control**: La historia queda completa cuando las pruebas de T002 y T003 pasan y la respuesta satisface todos los ejemplos de [course-stats-api.md](contracts/course-stats-api.md).

---

## Fase 4: Pulido y validación transversal

**Propósito**: Confirmar la calidad del cambio y su guía de uso de extremo a extremo.

- [ ] T006 Ejecutar `mvn test` desde `pom.xml` y resolver cualquier regresión en `src/test/java/com/codecrafthub/`.
- [ ] T007 Ejecutar los escenarios de [quickstart.md](quickstart.md) contra `src/main/java/com/codecrafthub/controller/CourseController.java` y confirmar que la consulta no modifica los cursos.

---

## Dependencias y orden de ejecución

### Dependencias por fase

- **Preparación (Fase 1)**: Comienza de inmediato.
- **Fundamentos (Fase 2)**: Depende de T001; no añade código, pero confirma los mecanismos que bloquearían el trabajo de la historia.
- **Historia de usuario 1 (Fase 3)**: T002 y T003 pueden comenzar tras T001 y en paralelo. T004 depende de los casos de prueba definidos en T002. T005 depende de T004 y del contrato validado por T003.
- **Pulido (Fase 4)**: T006 depende de T002–T005; T007 depende de T006.

### Dependencias de historias de usuario

- **US1 (P1)**: No depende de otras historias. Es el MVP completo de esta funcionalidad.

### Oportunidades de paralelismo

- T002 y T003 pueden realizarse en paralelo porque modifican los archivos de pruebas de servicio y controlador, respectivamente.
- Tras completar T005, T006 y la preparación del entorno para T007 pueden coordinarse, pero la confirmación final de quickstart debe usar una compilación con pruebas aprobadas.

## Ejemplo de ejecución paralela: Historia de usuario 1

```text
Tarea: "T002: Añadir pruebas de agregación en src/test/java/com/codecrafthub/service/CourseServiceTest.java"
Tarea: "T003: Añadir pruebas MockMvc en src/test/java/com/codecrafthub/controller/CourseControllerIntegrationTest.java"
```

## Estrategia de implementación

### MVP primero

1. Completar T001 y confirmar los fundamentos existentes.
2. Definir T002 y T003.
3. Implementar T004 y T005.
4. Ejecutar T006 y validar US1 de forma independiente.

### Entrega incremental

La única historia entrega todo el valor: un resumen actual, completo y no mutante. T007 solo confirma manualmente el contrato ya probado antes de la entrega.

## Validación de formato

Las siete tareas usan el formato de lista requerido: casilla, identificador secuencial, marcador `[P]` solo en tareas paralelizables, etiqueta `[US1]` para las tareas de historia y rutas de archivos específicas.
