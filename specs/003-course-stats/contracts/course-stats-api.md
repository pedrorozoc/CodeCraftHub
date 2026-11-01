# Contrato HTTP: Estadísticas de cursos

**Ruta**: `GET /api/courses/stats`

**Contenido de éxito**: `application/json`

## Finalidad

Devuelve un resumen de la colección de cursos disponible en el momento de la consulta. Es una operación de solo lectura y no requiere cuerpo de solicitud.

## Respuesta correcta

**Estado HTTP**: `200 OK`

```json
{
  "total_courses": 6,
  "courses_by_status": {
    "Not Started": 2,
    "In Progress": 3,
    "Completed": 1
  }
}
```

`total_courses` es la suma de los tres valores de `courses_by_status`. Las claves de ese objeto son las etiquetas de estado establecidas por el contrato de cursos; siempre aparecen las tres, incluso cuando su conteo es cero. Los campos fijos de la respuesta usan snake_case.

## Colección vacía

**Estado HTTP**: `200 OK`

```json
{
  "total_courses": 0,
  "courses_by_status": {
    "Not Started": 0,
    "In Progress": 0,
    "Completed": 0
  }
}
```

## Error de almacenamiento

Si el catálogo no se puede leer o no tiene una estructura válida, la ruta usa el error JSON común de la API.

**Estado HTTP**: `500 Internal Server Error`

```json
{
  "timestamp": "2026-11-01T12:00:00Z",
  "status": 500,
  "error_code": "storage_error",
  "message": "No se pudo leer courses.json.",
  "path": "/api/courses/stats"
}
```
