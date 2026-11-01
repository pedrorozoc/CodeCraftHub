# Modelo de datos: Estadísticas de cursos

## Curso existente

La entidad `Curso` no cambia. Para las estadísticas aporta únicamente su estado actual, que debe ser exactamente una de estas categorías:

| Estado visible | Significado |
|---|---|
| `Not Started` | El curso aún no se ha iniciado. |
| `In Progress` | El curso está en progreso. |
| `Completed` | El curso se ha completado. |

Cada curso pertenece a exactamente una categoría. La funcionalidad no define transiciones nuevas.

## Estadísticas de cursos

Representación calculada, de solo lectura y no persistida.

| Campo | Tipo lógico | Regla |
|---|---|---|
| `total_courses` | entero no negativo | Número de cursos de la colección actual. |
| `courses_by_status` | mapa de conteos | Contiene exactamente las tres categorías de estado visibles. |
| `courses_by_status.Not Started` | entero no negativo | Cursos cuyo estado es `Not Started`. |
| `courses_by_status.In Progress` | entero no negativo | Cursos cuyo estado es `In Progress`. |
| `courses_by_status.Completed` | entero no negativo | Cursos cuyo estado es `Completed`. |

## Reglas de consistencia

- Los tres conteos están presentes incluso si su valor es cero.
- `total_courses` es igual a la suma de los tres conteos.
- La obtención de estadísticas no modifica la colección ni el archivo de cursos.
- Si el catálogo no puede leerse de manera válida, no se produce una representación de estadísticas.
