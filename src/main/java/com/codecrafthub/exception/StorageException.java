package com.codecrafthub.exception;

/** Encapsula fallos de lectura, escritura o formato del archivo de cursos. */
public class StorageException extends RuntimeException {

    public StorageException(String message, Throwable cause) {
        super(message, cause);
    }
}
