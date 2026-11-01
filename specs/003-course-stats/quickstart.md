# Guía de validación: Estadísticas de cursos

## Requisitos previos

- Java 21.
- Maven 3.6.3 o superior.
- Desde la raíz del repositorio, una ubicación escribible para el archivo de cursos configurado.

## Ejecutar las pruebas

```bash
mvn test
```

La validación debe cubrir la agregación de los tres estados, el caso vacío, la igualdad entre total y suma de categorías, la ausencia de mutaciones y el error de lectura de almacenamiento.

## Comprobación manual

1. Inicie el servicio:

   ```bash
   mvn spring-boot:run
   ```

2. Cree cursos con los tres estados admitidos usando las operaciones ya documentadas en el contrato de cursos base.
3. Consulte el resumen:

   ```bash
   curl -i http://localhost:8080/api/courses/stats
   ```

4. Verifique una respuesta `200 OK` conforme a [course-stats-api.md](contracts/course-stats-api.md): contiene `total_courses`, las tres categorías y valores cuya suma coincide con el total.
5. Repita la consulta con una colección vacía; los cuatro conteos deben ser cero.
6. Consulte y compare la colección de cursos antes y después de cada comprobación para confirmar que el resumen no modifica cursos ni estados.
