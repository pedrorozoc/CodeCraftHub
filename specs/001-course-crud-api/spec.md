# Especificación de funcionalidad: Gestión CRUD de cursos

**Rama de funcionalidad**: `001-course-crud-api`

**Creada**: 2026-11-01

**Estado**: Borrador

**Entrada**: Descripción del usuario: "API para crear, consultar, actualizar y eliminar cursos de aprendizaje con persistencia local."

## Aclaraciones

### Sesión 2026-11-01

- P: ¿Qué convención de nombres y valores debe usar el contrato JSON de cursos? → R: Todas las
  solicitudes y respuestas usan nombres snake_case, incluidos `target_date` y `created_at`; el
  modelo Java puede usar `targetDate` y `createdAt` si los mapea con anotaciones Jackson; los únicos
  estados válidos son exactamente `Not Started`, `In Progress` y `Completed`.

## Escenarios de usuario y pruebas *(obligatorio)*

### Historia de usuario 1 - Registrar y consultar cursos (Prioridad: P1)

Como desarrollador que aprende, quiero registrar un curso y obtener la lista de mis cursos para
mantener visible mi plan de aprendizaje.

**Por qué esta prioridad**: Crear y consultar cursos entrega el valor mínimo: iniciar y revisar el
seguimiento de aprendizaje.

**Prueba independiente**: Se puede crear un curso válido y consultar la colección para comprobar
que aparece con su identificador y fecha de creación generados.

**Escenarios de aceptación**:

1. **Dado** que no hay cursos registrados, **cuando** se crea un curso con nombre, descripción,
   fecha objetivo y estado válidos, **entonces** se registra con identificador 1 y fecha de
   creación generada.
2. **Dado** que existen cursos registrados, **cuando** se consulta la colección, **entonces** se
   recibe la lista completa con todos los atributos de cada curso.
3. **Dado** que una solicitud de creación omite cualquier campo obligatorio, **cuando** se intenta
   registrar el curso, **entonces** se rechaza sin alterar los cursos existentes y se informa qué
   campos faltan.

---

### Historia de usuario 2 - Consultar y modificar un curso (Prioridad: P2)

Como desarrollador que aprende, quiero consultar un curso concreto y actualizar sus datos para
reflejar mi progreso y mis metas revisadas.

**Por qué esta prioridad**: El seguimiento pierde valor si no se puede recuperar ni corregir la
información de un curso ya registrado.

**Prueba independiente**: Con un curso existente, se puede consultarlo por identificador,
actualizar todos sus campos editables y volver a consultarlo para verificar los cambios.

**Escenarios de aceptación**:

1. **Dado** un curso existente, **cuando** se solicita por su identificador, **entonces** se
   reciben sus atributos completos.
2. **Dado** un curso existente, **cuando** se reemplazan sus datos por valores válidos, **entonces**
   se conservan su identificador y fecha de creación, y se muestran los nuevos valores.
3. **Dado** un identificador inexistente, **cuando** se consulta o actualiza, **entonces** se
   informa claramente que el curso no fue encontrado.

---

### Historia de usuario 3 - Eliminar un curso y entender errores (Prioridad: P3)

Como desarrollador que aprende, quiero eliminar un curso que ya no seguiré y recibir mensajes
claros ante entradas inválidas o problemas de almacenamiento.

**Por qué esta prioridad**: Permite mantener la lista relevante y demuestra las respuestas de error
esenciales de una API REST.

**Prueba independiente**: Se elimina un curso existente, se consulta la lista para confirmar que no
aparece y se prueban solicitudes inválidas o almacenamiento no disponible.

**Escenarios de aceptación**:

1. **Dado** un curso existente, **cuando** se solicita su eliminación, **entonces** deja de estar
   disponible en consultas posteriores.
2. **Dado** un identificador inexistente, **cuando** se solicita su eliminación, **entonces** se
   informa claramente que el curso no fue encontrado.
3. **Dado** un estado o una fecha objetivo inválidos, **cuando** se crea o actualiza un curso,
   **entonces** se rechaza la solicitud, se explica el valor inválido y no se guardan cambios.

### Casos límite

- La colección está vacía y se solicita la lista o un curso concreto.
- El primer curso recibe el identificador 1 y los identificadores posteriores no se reutilizan tras
  una eliminación durante la ejecución normal.
- El nombre o la descripción contienen solo espacios en blanco.
- La fecha objetivo no usa el formato `YYYY-MM-DD` o no representa una fecha de calendario válida.
- El estado difiere de `Not Started`, `In Progress` o `Completed`, incluso por mayúsculas o
  espacios adicionales.
- El archivo de datos no existe, está vacío, tiene contenido JSON inválido o no se puede leer o
  escribir.

## Requisitos *(obligatorio)*

### Requisitos funcionales

- **RF-001**: El sistema DEBE permitir crear un curso mediante `POST /api/courses`.
- **RF-002**: El sistema DEBE permitir obtener todos los cursos mediante `GET /api/courses`.
- **RF-003**: El sistema DEBE permitir obtener un curso por identificador mediante
  `GET /api/courses/{id}`.
- **RF-004**: El sistema DEBE permitir reemplazar un curso existente mediante
  `PUT /api/courses/{id}`.
- **RF-005**: El sistema DEBE permitir eliminar un curso existente mediante
  `DELETE /api/courses/{id}`.
- **RF-006**: Cada curso DEBE exponer exclusivamente nombres de campos snake_case en sus
  representaciones JSON de solicitud y respuesta: `id`, `name`, `description`, `target_date`,
  `status` y `created_at`.
- **RF-007**: Al crear un curso, el sistema DEBE asignar automáticamente un identificador positivo
  y consecutivo, comenzando en 1, y una marca de tiempo de creación.
- **RF-008**: El nombre, la descripción, la fecha objetivo y el estado DEBEN ser obligatorios al
  crear o actualizar un curso; nombre y descripción no pueden estar vacíos ni contener solo
  espacios.
- **RF-009**: La fecha objetivo DEBE recibirse como `target_date` en formato `YYYY-MM-DD` y debe
  representar una fecha válida.
- **RF-010**: El estado DEBE ser exactamente uno de los siguientes valores: `Not Started`,
  `In Progress` o `Completed`.
- **RF-011**: El sistema DEBE conservar los cursos en un único archivo local llamado `courses.json`
  y crear un archivo vacío válido cuando aún no exista.
- **RF-012**: Los datos guardados DEBEN permanecer disponibles al reiniciar el servicio y las
  modificaciones exitosas DEBEN reflejarse en la siguiente consulta.
- **RF-013**: Ante campos obligatorios faltantes, identificador inexistente, fecha o estado
  inválido, o errores de lectura/escritura, el sistema DEBE responder con un código HTTP apropiado
  y un cuerpo JSON que explique el error sin exponer detalles internos.
- **RF-014**: La interfaz JSON DEBE usar `target_date` y `created_at`; el modelo Java puede usar
  los nombres camelCase `targetDate` y `createdAt` únicamente cuando el mapeo con anotaciones
  Jackson conserve exactamente los nombres snake_case del contrato JSON.
- **RF-015**: El producto NO DEBE incluir autenticación, cuentas ni administración de usuarios.

### Entidades clave

- **Curso**: Unidad de aprendizaje que contiene un identificador, nombre, descripción, fecha
  objetivo, estado de avance y fecha de creación.
- **Colección de cursos**: Conjunto persistente de cursos que permite buscar, listar, modificar y
  eliminar por identificador.
- **Error de la API**: Representación JSON de un fallo de validación, ausencia de recurso o
  problema de persistencia, con un mensaje legible para quien consume la API.

## Criterios de éxito *(obligatorio)*

### Resultados medibles

- **CE-001**: Una persona puede crear un curso válido y recuperarlo por su identificador en menos
  de 2 minutos usando únicamente los cinco contratos de operación documentados.
- **CE-002**: El 100 % de las solicitudes con los cuatro campos requeridos válidos crea o actualiza
  el curso y permite verlo en una consulta posterior.
- **CE-003**: El 100 % de las solicitudes con un estado distinto de los tres valores permitidos se
  rechaza sin modificar cursos existentes.
- **CE-004**: El 100 % de las operaciones sobre identificadores inexistentes informa que el recurso
  no existe, sin afectar otros cursos.
- **CE-005**: Tras reiniciar el servicio, el 100 % de los cursos guardados correctamente antes del
  reinicio sigue disponible para consulta.

## Suposiciones

- La primera versión atiende a una sola persona y no requiere autenticación ni concurrencia entre
  múltiples procesos.
- Las actualizaciones reemplazan los cuatro campos editables; el identificador y la fecha de
  creación no son editables.
- Una marca de tiempo de creación puede expresarse en un formato de fecha y hora estándar, siempre
  que sea consistente en todas las respuestas.
- El archivo de datos se almacena en una ubicación local configurable por el entorno de ejecución;
  el nombre del archivo permanece `courses.json`.
- La documentación, mensajes de error y comentarios para aprendizaje se redactarán en español;
  los identificadores del código se redactarán en inglés, de acuerdo con la constitución.
