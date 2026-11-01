# Investigación técnica: Gestión CRUD de cursos

## Plataforma y dependencias

**Decisión**: Usar Java 21 y Spring Boot 3.4.5 con Maven, `spring-boot-starter-web`,
`spring-boot-starter-validation` y `spring-boot-starter-test`.

**Justificación**: Java 21 ofrece una línea LTS ampliamente disponible y Spring Boot 3.4.5 está
disponible en el entorno de construcción sin requerir una descarga externa. El starter web
proporciona la base MVC y conversión JSON; el starter de validación habilita validación declarativa
de solicitudes.

**Alternativas consideradas**: Spring Boot 4.1.2 se descartó porque las dependencias no estaban
disponibles localmente y el entorno no podía acceder a Maven Central. Java 17 también es
compatible, pero Java 21 es la opción LTS más moderna. No se incorpora una biblioteca JSON
adicional porque Jackson llega gestionado por Spring Boot y es el requisito de la funcionalidad.

**Fuentes**:

- [Requisitos de sistema de Spring Boot](https://docs.spring.io/spring-boot/system-requirements.html)
- [Spring MVC en Spring Boot](https://docs.spring.io/spring-boot/how-to/spring-mvc.html)

## Contrato JSON y fechas

**Decisión**: Representar `target_date` como fecha ISO `YYYY-MM-DD` y `created_at` como marca ISO
8601 en UTC. Usar anotaciones Jackson para mantener los campos snake_case del contrato cuando los
atributos Java se llamen `targetDate` y `createdAt`.

**Justificación**: Las fechas ISO son inequívocas y se validan directamente. La anotación explícita
expone el contrato de forma didáctica y evita depender de una política global implícita.

**Alternativas consideradas**: Una política global de nombres snake_case reduce anotaciones, pero
oculta el mapeo que se busca enseñar. Se descarta un formato local de fechas porque dificulta a
clientes y pruebas.

## Estados de curso

**Decisión**: Implementar una enumeración Java con identificadores en inglés y serializar sus tres
etiquetas JSON exactas: `Not Started`, `In Progress` y `Completed`.

**Justificación**: Satisface el contrato externo exacto, mantiene el código en inglés y restringe
los estados a un conjunto cerrado. La validación rechaza diferencias de mayúsculas, espacios o
valores no listados.

**Alternativas consideradas**: Aceptar texto libre o normalizar mayúsculas añade ambigüedad y
contradice el contrato. Se descarta almacenar estados diferentes a los expuestos por JSON.

## Archivo de persistencia

**Decisión**: Crear `courses.json` al iniciar si no existe, con un objeto raíz que contiene
`next_id` y `courses`. Leerlo completo antes de cada operación y escribirlo completo después de
cualquier mutación. Escribir primero un archivo temporal en el mismo directorio y moverlo al
archivo final; si el sistema de archivos no admite movimiento atómico, usar un movimiento de
reemplazo como alternativa y registrar el error si falla.

**Justificación**: `next_id` evita reutilizar identificadores después de borrar el último curso y
permite preservar la regla de secuencia tras reinicios. El patrón temporal reduce el riesgo de
dejar un JSON parcialmente escrito.

**Alternativas consideradas**: Un arreglo JSON sin metadatos es más corto, pero no puede preservar
la secuencia al borrar el identificador mayor. Una base de datos o bloqueo entre procesos exceden
el alcance educativo y contradicen la constitución.

**Fuentes**:

- [Operaciones de archivos de Java](https://docs.oracle.com/en/java/javase/24/docs/api/java.base/java/nio/file/Files.html)
- [Movimiento atómico de archivos en Java](https://docs.oracle.com/javase/tutorial/essential/io/move.html)

## Errores y pruebas

**Decisión**: Centralizar respuestas de error con un manejador global y probar rutas con MockMvc;
probar el servicio con un directorio temporal aislado.

**Justificación**: Mantiene los controladores cortos, entrega el mismo formato de error para 400,
404 y 500, y separa las pruebas HTTP de los efectos del archivo real.

**Alternativas consideradas**: Manejar errores dentro de cada ruta duplicaría lógica. Probar contra
un `courses.json` compartido volvería las pruebas frágiles y dependientes del orden.
