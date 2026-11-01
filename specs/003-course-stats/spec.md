# Especificación de funcionalidad: Estadísticas de cursos

**Rama de funcionalidad**: `003-course-stats`

**Creada**: 2026-11-01

**Estado**: Borrador

**Entrada**: Descripción del usuario: "Añadir `GET /api/courses/stats` para consultar el total de cursos y su distribución por estado."

## Escenarios de usuario y pruebas *(obligatorio)*

### Historia de usuario 1 - Consultar resumen de progreso (Prioridad: P1)

Como persona que sigue sus cursos de aprendizaje, quiero consultar un resumen de cuántos cursos tengo y cómo se distribuyen por avance, para conocer mi progreso sin revisar cada curso individualmente.

**Por qué esta prioridad**: Entrega el valor completo de la funcionalidad: una vista agregada e inmediata del progreso de aprendizaje.

**Prueba independiente**: Con una colección conocida que incluya cursos en los tres estados, se solicita el resumen y se comprueba que el total y cada conteo coinciden con esa colección.

**Escenarios de aceptación**:

1. **Dado** que hay cursos registrados en los estados `Not Started`, `In Progress` y `Completed`, **cuando** se consulta `GET /api/courses/stats`, **entonces** se recibe el número total de cursos y el número de cursos de cada uno de esos estados.
2. **Dado** que hay cursos registrados solo en uno o dos de los estados permitidos, **cuando** se consulta el resumen, **entonces** cada estado sin cursos se informa con conteo cero y el total coincide con la suma de los tres conteos.
3. **Dado** que no hay cursos registrados, **cuando** se consulta el resumen, **entonces** se recibe un total de cero y un conteo de cero para cada estado.

### Casos límite

- La colección de cursos está vacía.
- Todos los cursos están en el mismo estado.
- Uno o más estados permitidos no tienen cursos.
- La información de cursos no se puede leer; se comunica un error claro sin devolver estadísticas parciales o engañosas.

## Requisitos *(obligatorio)*

### Requisitos funcionales

- **RF-001**: El sistema DEBE proporcionar `GET /api/courses/stats` para consultar estadísticas agregadas de la colección actual de cursos.
- **RF-002**: La respuesta DEBE incluir el número total de cursos registrados.
- **RF-003**: La respuesta DEBE incluir, de forma identificable, el número de cursos en cada estado permitido: `Not Started`, `In Progress` y `Completed`.
- **RF-004**: Los conteos para los tres estados DEBEN estar presentes incluso cuando su valor sea cero.
- **RF-005**: El total DEBE ser igual a la suma de los conteos de `Not Started`, `In Progress` y `Completed` para la misma colección de cursos.
- **RF-006**: La consulta de estadísticas NO DEBE crear, modificar ni eliminar cursos ni alterar sus estados.
- **RF-007**: Si no se puede obtener la colección de cursos, el sistema DEBE informar un error HTTP apropiado con un cuerpo JSON legible y no DEBE presentar un resumen incompleto como válido.

### Entidades clave

- **Estadísticas de cursos**: Resumen calculado de la colección de cursos, compuesto por un total y un conteo para cada estado de avance permitido.
- **Curso**: Registro de aprendizaje existente cuyo estado contribuye a exactamente uno de los conteos del resumen.

## Criterios de éxito *(obligatorio)*

### Resultados medibles

- **CE-001**: El 100 % de las consultas realizadas sobre colecciones de prueba vacías, de un solo estado y de los tres estados devuelve un total y tres conteos que coinciden exactamente con los cursos de la colección.
- **CE-002**: En el 100 % de las consultas sobre una colección vacía, el total y los tres conteos son cero.
- **CE-003**: En el 100 % de las respuestas correctas, la suma de los tres conteos por estado es igual al total informado.
- **CE-004**: El 100 % de las consultas de estadísticas deja sin cambios la cantidad, los datos y los estados de los cursos existentes.

## Suposiciones

- Los únicos estados posibles de un curso son `Not Started`, `In Progress` y `Completed`, según la funcionalidad de gestión de cursos existente.
- La estadística se calcula sobre la colección disponible en el momento de la consulta y no requiere almacenar resultados históricos.
- La ruta solicitada es parte del contrato público de consulta de cursos; no se añaden autenticación, cuentas ni administración de usuarios.
- La respuesta seguirá las convenciones JSON existentes del producto y sus mensajes de error estarán en español.
