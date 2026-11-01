<!--
Informe de impacto de sincronización
- Cambio de versión: 1.0.0 → 1.1.0
- Principios modificados: I. Simplicidad centrada en principiantes y II. REST primero con Spring Boot.
- Secciones modificadas: Restricciones técnicas y Flujo de trabajo de desarrollo.
- Secciones añadidas: ninguna.
- Secciones eliminadas: ninguna.
- TODO de seguimiento: ninguno.
-->
# Constitución de CodeCraftHub

## Principios fundamentales

### I. Simplicidad centrada en principiantes
CodeCraftHub DEBE favorecer componentes pequeños, legibles y flujo de control directo en lugar de
abstracciones que no enseñen un fundamento de API REST. Cada funcionalidad DEBE tener una
responsabilidad clara y documentación en español apropiada para quien aprende Spring Boot. Las
nuevas dependencias, capas o patrones requieren un beneficio concreto de aprendizaje o
mantenimiento. Esto mantiene la aplicación accesible y facilita rastrear su comportamiento.

### II. REST primero con Spring Boot
El backend DEBE implementarse en Java con Spring Boot y exponer las capacidades de cursos mediante
endpoints HTTP REST. Los controladores DEBEN usar rutas orientadas a recursos, métodos y códigos
de estado HTTP estándar, validar las solicitudes y devolver representaciones JSON consistentes.
Los ejemplos de API y las pruebas DEBEN cubrir solicitudes válidas e inválidas. La API es el
artefacto de aprendizaje principal, por lo que su comportamiento debe ser explícito y predecible.

### III. JSON basado en archivos como fuente de datos
Los datos de cursos DEBEN persistirse en archivos JSON simples; no se permite una base de datos,
ORM ni servicio externo de persistencia. Un límite de persistencia dedicado DEBE cargar datos de
forma segura al inicio y escribir cambios con suficiente atomicidad para evitar archivos mal
formados durante la operación normal. Los archivos ausentes o inválidos DEBEN producir errores
accionables o una colección vacía inicializada intencionalmente, nunca pérdida silenciosa de datos.
Esto preserva el enfoque en REST básico y almacenamiento transparente.

### IV. Integridad del modelo de curso
Cada curso DEBE contener un nombre no vacío, una descripción, una fecha objetivo de finalización y
exactamente un estado actual: `NO_INICIADO`, `EN_PROGRESO` o `COMPLETADO`. Las solicitudes y los
datos persistidos DEBEN validarse frente a estas restricciones antes de modificarse. Cualquier
representación futura para personas PUEDE traducir las etiquetas de estado, pero DEBE conservar un
valor inequívoco para la máquina. Los datos consistentes hacen confiable el seguimiento.

### V. Alcance y privacidad por defecto
La aplicación DEBE gestionar únicamente cursos de aprendizaje. NO DEBE incluir autenticación,
cuentas, roles, permisos ni administración de usuarios salvo que esta constitución sea enmendada.
Las funcionalidades fuera del seguimiento de cursos requieren un objetivo de aprendizaje
documentado y un cambio de alcance justificado. Mantener un alcance de un solo usuario y sin
autenticación limita la complejidad incidental.

## Restricciones técnicas

- Java y Spring Boot son obligatorios para el código de la aplicación.
- Los identificadores del código, como paquetes, clases, métodos, variables, endpoints y pruebas,
  DEBEN escribirse en inglés.
- La documentación del proyecto, descripciones de API, comentarios y Javadoc DEBEN escribirse en
  español.
- Los archivos JSON almacenados localmente son la única fuente persistente de datos de cursos.
- La API base DEBE permitir crear, listar, obtener, actualizar y eliminar cursos, además de
  actualizar el estado actual de un curso cuando dicho comportamiento se exponga por separado.
- Los endpoints de cursos DEBEN usar cuerpos de solicitud y respuesta JSON de manera consistente,
  salvo cuando la semántica HTTP requiera no incluir un cuerpo.
- Las migraciones de bases de datos, flujos de autenticación y endpoints de gestión de usuarios
  están fuera de alcance.

## Flujo de trabajo de desarrollo

Cada cambio DEBE comenzar con una especificación escrita en español del comportamiento para quien
aprende antes de su implementación. El trabajo DEBE incluir pruebas enfocadas en validación,
comportamiento de códigos HTTP y lectura/escritura JSON afectados por el cambio. La revisión de
código o autorrevisión DEBE verificar el modelo de curso, las convenciones REST, la persistencia
JSON, las restricciones de alcance y el idioma aplicable. Si un cambio aumenta la complejidad, su
justificación y alternativas más simples DEBEN documentarse en español en el plan o pull request.

## Gobernanza

Esta constitución prevalece sobre las prácticas de proyecto que entren en conflicto. Las enmiendas
DEBEN documentar el cambio de regla propuesto, su justificación, impacto de compatibilidad y toda
migración requerida de archivos JSON o consumidores de API. La versión sigue versionado semántico:
MAJOR para cambios de gobernanza incompatibles, MINOR para principios o secciones nuevos o
materialmente ampliados y PATCH para aclaraciones sin cambio de obligaciones. Cada especificación,
plan, revisión de implementación y revisión de lanzamiento DEBE comprobar el cumplimiento; las
excepciones requieren una enmienda explícita con plazo definido o una dispensa registrada.

**Versión**: 1.1.0 | **Ratificada**: 2026-11-01 | **Última enmienda**: 2026-11-01
