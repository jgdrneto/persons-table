package com.example.spring.swagger.controllers;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.spring.swagger.exceptions.APIError;

import io.swagger.v3.oas.annotations.Hidden;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;

@Hidden
@RestControllerAdvice
@Slf4j
public class GlobalControllerExceptionHandler {

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<APIError> handleIllegalIndex(Exception ex) {
        log.error(ex.getMessage(),ex);
        return new ResponseEntity<>(new APIError(ex.getMessage(), HttpStatus.BAD_GATEWAY.name(), HttpStatus.BAD_GATEWAY.value()), HttpStatus.BAD_REQUEST);
    }
    
    @ExceptionHandler(EntityNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ResponseEntity<APIError> handleEntityNotFound(Exception ex) {
        log.error(ex.getMessage(),ex);
        return new ResponseEntity<>(new APIError(ex.getMessage(), HttpStatus.NOT_FOUND.name(), HttpStatus.NOT_FOUND.value()), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(EntityExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ResponseEntity<APIError> handleEntityAlreadyExists(Exception ex) {
        log.error(ex.getMessage(),ex);
        return new ResponseEntity<>(new APIError(ex.getMessage(), HttpStatus.CONFLICT.name(), HttpStatus.CONFLICT.value()), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ResponseEntity<APIError> handleException(Exception ex) {
        ex.printStackTrace();
        log.error(ex.getMessage(),ex);
        return new ResponseEntity<>(new APIError(ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.name(), HttpStatus.INTERNAL_SERVER_ERROR.value()), HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
