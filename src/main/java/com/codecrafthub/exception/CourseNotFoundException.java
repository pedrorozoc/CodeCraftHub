package com.codecrafthub.exception;

/** Se usa cuando el identificador solicitado no existe. */
public class CourseNotFoundException extends RuntimeException {

    public CourseNotFoundException(long id) {
        super("No se encontró el curso con id " + id + ".");
    }
}
