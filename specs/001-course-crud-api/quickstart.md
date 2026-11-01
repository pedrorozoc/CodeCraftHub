# Guía rápida de validación

Esta guía valida el comportamiento completo descrito en [el contrato HTTP](contracts/course-api.md)
y [el modelo de datos](data-model.md), sin incluir código de implementación.

## Requisitos previos

- Java 21 instalado y disponible como `java`.
- Maven 3.6.3 o superior disponible como `mvn`.
- Un directorio con permiso de escritura para crear `courses.json`.

## Compilar y ejecutar

Desde la raíz del repositorio, después de implementar las tareas:

```bash
mvn test
mvn spring-boot:run
```

La aplicación se ejecuta por defecto en `http://localhost:8080`. El primer inicio crea
`courses.json` con una colección vacía si no existe.

## Escenario 1: crear y listar

```bash
curl -i -X POST http://localhost:8080/api/courses \
  -H 'Content-Type: application/json' \
  -d '{"name":"REST Fundamentals","description":"Learn resource-oriented APIs","target_date":"2026-12-15","status":"Not Started"}'

curl -i http://localhost:8080/api/courses
```

Espere `201 Created` en la creación y `200 OK` al listar. La respuesta de creación contiene un
`id` positivo y `created_at`; la lista contiene ese mismo curso.

## Escenario 2: obtener y actualizar

```bash
curl -i http://localhost:8080/api/courses/1

curl -i -X PUT http://localhost:8080/api/courses/1 \
  -H 'Content-Type: application/json' \
  -d '{"name":"REST Fundamentals","description":"Learn resource-oriented APIs","target_date":"2026-12-20","status":"In Progress"}'
```

Espere `200 OK` en ambos casos. La actualización conserva `id` y `created_at`, actualiza
`target_date` y devuelve `status` exactamente como `In Progress`.

## Escenario 3: validar errores y borrar

```bash
curl -i -X POST http://localhost:8080/api/courses \
  -H 'Content-Type: application/json' \
  -d '{"name":"","description":"x","target_date":"2026-13-45","status":"started"}'

curl -i -X DELETE http://localhost:8080/api/courses/1
curl -i http://localhost:8080/api/courses/1
```

Espere `400 Bad Request` con un cuerpo de error snake_case para la solicitud inválida, `204 No
Content` al borrar y `404 Not Found` al buscar el curso borrado.

## Escenario 4: verificar persistencia

1. Cree un curso válido y detenga la aplicación.
2. Revise que `courses.json` sea JSON válido y tenga `next_id` y `courses`.
3. Inicie la aplicación de nuevo y ejecute `GET /api/courses`.

Espere que el curso creado siga disponible y que `next_id` permita crear un nuevo curso sin reutilizar
identificadores eliminados.
