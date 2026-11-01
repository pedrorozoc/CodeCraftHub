# Investigación: Estadísticas de cursos

## Decisión 1: Calcular el resumen al atender cada consulta

**Decisión**: Leer la colección de cursos mediante el servicio existente y calcular el total y los conteos de estado para esa colección en cada solicitud.

**Justificación**: El catálogo se guarda en un único archivo JSON, la escala prevista es pequeña y la estadística debe reflejar el estado actual. El cálculo evita datos derivados desactualizados y no requiere modificar el flujo de crear, actualizar o eliminar.

**Alternativas consideradas**:

- Persistir conteos junto al catálogo: descartado porque duplica datos y podría desincronizarse.
- Mantener conteos en memoria: descartado porque se perderían al reiniciar y no reflejarían cambios externos del archivo.

## Decisión 2: Usar los tres estados de dominio existentes como categorías exhaustivas

**Decisión**: Inicializar un conteo para cada uno de los estados `Not Started`, `In Progress` y `Completed`, y aumentar exactamente uno por cada curso válido del catálogo.

**Justificación**: Son los únicos valores válidos del dominio. Inicializarlos garantiza ceros explícitos cuando un estado no tenga cursos y permite comprobar que la suma de categorías coincide con el total.

**Alternativas consideradas**:

- Omitir estados con cero cursos: descartado porque obliga a quien consume a inferir ceros.
- Aceptar categorías arbitrarias: descartado porque contradice la integridad del modelo de curso.

## Decisión 3: Reutilizar el manejo de errores de almacenamiento

**Decisión**: Dejar que los fallos al leer el catálogo sigan el mecanismo de error de almacenamiento actual; no construir respuestas parciales.

**Justificación**: Una estadística incompleta puede inducir decisiones erróneas. El producto ya tiene una representación JSON de error consistente para problemas de lectura.

**Alternativas consideradas**:

- Devolver ceros tras un error de lectura: descartado porque confundiría un fallo con una colección vacía.
- Devolver solo categorías que puedan leerse: descartado porque el archivo es la unidad de lectura y no admite resultados parciales confiables.
