package com.citas.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.LinkedHashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(
            MethodArgumentNotValidException.class
    )
    public ResponseEntity<Map<String, Object>>
    manejarValidaciones(
            MethodArgumentNotValidException ex) {

        Map<String, String> errores =
                new LinkedHashMap<>();

        for (FieldError error :
                ex.getBindingResult()
                        .getFieldErrors()) {

            errores.put(
                    error.getField(),
                    error.getDefaultMessage()
            );
        }

        Map<String, Object> respuesta =
                new LinkedHashMap<>();

        respuesta.put("status", 400);
        respuesta.put("error", "Datos inválidos");
        respuesta.put("errores", errores);

        return ResponseEntity
                .badRequest()
                .body(respuesta);
    }

    @ExceptionHandler(
            IllegalArgumentException.class
    )
    public ResponseEntity<Map<String, Object>>
    manejarArgumentos(
            IllegalArgumentException ex) {

        return respuesta(
                HttpStatus.BAD_REQUEST,
                ex.getMessage()
        );
    }

    @ExceptionHandler(
            DataIntegrityViolationException.class
    )
    public ResponseEntity<Map<String, Object>>
    manejarIntegridad(
            DataIntegrityViolationException ex) {

        return respuesta(
                HttpStatus.CONFLICT,
                "No se pudo completar la operación porque existe un conflicto de integridad en la base de datos."
        );
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String, Object>>
    manejarGeneral(Exception ex) {

        return respuesta(
                HttpStatus.INTERNAL_SERVER_ERROR,
                "Ocurrió un error interno en el servidor."
        );
    }

    private ResponseEntity<Map<String, Object>>
    respuesta(
            HttpStatus status,
            String mensaje) {

        Map<String, Object> cuerpo =
                new LinkedHashMap<>();

        cuerpo.put(
                "status",
                status.value()
        );

        cuerpo.put(
                "error",
                status.getReasonPhrase()
        );

        cuerpo.put(
                "mensaje",
                mensaje
        );

        return ResponseEntity
                .status(status)
                .body(cuerpo);
    }
}