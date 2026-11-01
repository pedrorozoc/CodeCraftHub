# Modelo de datos: Gestión CRUD de cursos

## Curso

| Campo JSON | Campo Java | Tipo | Origen | Reglas |
|---|---|---|---|---|
| `id` | `id` | entero largo | Servidor | Positivo, autogenerado, comienza en 1, no editable. |
| `name` | `name` | texto | Solicitud | Obligatorio; tras recortar espacios no puede ser vacío. |
| `description` | `description` | texto | Solicitud | Obligatorio; tras recortar espacios no puede ser vacío. |
| `target_date` | `targetDate` | fecha local | Solicitud | Obligatorio; fecha de calendario en formato `YYYY-MM-DD`. |
| `status` | `status` | enumeración | Solicitud | Exactamente `Not Started`, `In Progress` o `Completed`. |
| `created_at` | `createdAt` | instante | Servidor | Autogenerado en UTC, no editable. |

Los nombres de Java se mantienen en camelCase. `targetDate` y `createdAt` llevan anotaciones
Jackson explícitas para exponerse como `target_date` y `created_at`. Los demás campos conservan el
mismo nombre en ambos formatos.

## Estado de curso

| Identificador Java | Valor JSON | Transiciones permitidas |
|---|---|---|
| `NOT_STARTED` | `Not Started` | A `IN_PROGRESS` o `COMPLETED`. |
| `IN_PROGRESS` | `In Progress` | A `NOT_STARTED` o `COMPLETED`. |
| `COMPLETED` | `Completed` | A `NOT_STARTED` o `IN_PROGRESS` si se corrige el progreso. |

La actualización completa permite cualquiera de estas transiciones porque el contrato no limita
correcciones de progreso.

## Documento persistido

`courses.json` contiene un documento UTF-8 con la forma siguiente:

```json
{
  "next_id": 2,
  "courses": [
    {
      "id": 1,
      "name": "REST Fundamentals",
      "description": "Learn resource-oriented APIs",
      "target_date": "2026-12-15",
      "status": "Not Started",
      "created_at": "2026-11-01T12:00:00Z"
    }
  ]
}
```

Al crear el archivo, su contenido es `{"next_id":1,"courses":[]}`. `next_id` es metadato interno
de persistencia y no aparece en respuestas HTTP. Las operaciones de escritura guardan el documento
entero; el archivo original permanece sin cambios si la serialización o el movimiento falla.

## Errores de la API

| Campo | Tipo | Descripción |
|---|---|---|
| `timestamp` | fecha y hora | Momento UTC en que se produjo el error. |
| `status` | entero | Código HTTP de la respuesta. |
| `error_code` | texto | Código estable, por ejemplo `validation_error`. |
| `message` | texto | Explicación en español para quien consume la API. |
| `path` | texto | Ruta solicitada. |
