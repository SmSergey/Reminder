package com.smirnov.app.web.handlers;

import jakarta.persistence.EntityNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Optional;

@RestControllerAdvice
public class GlobalHandler {

    @ExceptionHandler(BindException.class)
    public ResponseEntity<String> handleValidationErrors(BindException err) {
        err.printStackTrace();
        FieldError fieldError = Optional.ofNullable(err.getFieldError())
                .orElse(new FieldError("error", "error", err.getMessage()));

        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(fieldError.getField() + " : " + fieldError);
    }

    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> handleError(EntityNotFoundException err) {
        err.printStackTrace();

        return ResponseEntity
                .status(HttpStatus.NOT_FOUND)
                .body(err.getMessage());
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleError(Exception err) {
        err.printStackTrace();

        return ResponseEntity
                .status(500)
                .body("something went wrong");
    }
}
