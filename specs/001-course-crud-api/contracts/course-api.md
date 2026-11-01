# Contrato HTTP: API de cursos

**Base**: `/api/courses`

**Contenido**: `application/json`
**Convención**: Todos los campos JSON usan snake_case. Las fechas objetivo usan `YYYY-MM-DD` y las
marcas de tiempo usan ISO 8601 UTC.

## Recurso curso

```json
{
  "id": 1,
  "name": "REST Fundamentals",
  "description": "Learn resource-oriented APIs",
  "target_date": "2026-12-15",
  "status": "Not Started",
  "created_at": "2026-11-01T12:00:00Z"
}
```

En solicitudes de creación o actualización se envían solo `name`, `description`, `target_date` y
`status`. `id` y `created_at` los asigna y conserva el servidor.

## Operaciones

| Operación | Método y ruta | Éxito | Errores esperados |
|---|---|---|---|
| Crear | `POST /api/courses` | `201 Created`, recurso y cabecera `Location` | `400`, `500` |
| Listar | `GET /api/courses` | `200 OK`, arreglo de cursos | `500` |
| Obtener | `GET /api/courses/{id}` | `200 OK`, recurso | `404`, `500` |
| Reemplazar | `PUT /api/courses/{id}` | `200 OK`, recurso actualizado | `400`, `404`, `500` |
| Eliminar | `DELETE /api/courses/{id}` | `204 No Content` | `404`, `500` |

## Crear curso

`POST /api/courses`

```json
{
  "name": "REST Fundamentals",
  "description": "Learn resource-oriented APIs",
  "target_date": "2026-12-15",
  "status": "Not Started"
}
```

El servidor devuelve el recurso completo, incluido `id` y `created_at`.

## Reemplazar curso

`PUT /api/courses/1`

El cuerpo tiene la misma forma que una creación. La operación reemplaza los cuatro campos editables
y conserva `id` y `created_at`.

## Error

```json
{
  "timestamp": "2026-11-01T12:00:00Z",
  "status": 400,
  "error_code": "validation_error",
  "message": "El campo status debe ser Not Started, In Progress o Completed.",
  "path": "/api/courses"
}
```

| Caso | Estado HTTP | `error_code` |
|---|---|---|
| Campo faltante, vacío, fecha o estado inválido | `400 Bad Request` | `validation_error` |
| Curso inexistente | `404 Not Found` | `course_not_found` |
| Error de lectura, JSON persistido inválido o escritura | `500 Internal Server Error` | `storage_error` |
