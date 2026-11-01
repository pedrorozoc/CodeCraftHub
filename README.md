# CodeCraftHub

API REST educativa para seguir cursos de aprendizaje. Usa Spring Boot, Java y un archivo local
`courses.json`; no usa base de datos, autenticación ni gestión de usuarios.

## Ejecutar

```bash
mvn test
mvn spring-boot:run
```

La API queda disponible en `http://localhost:8080/api/courses`. El archivo `courses.json` se crea
automáticamente la primera vez que inicia la aplicación. Consulta la guía completa de validación en
[quickstart.md](specs/001-course-crud-api/quickstart.md).
