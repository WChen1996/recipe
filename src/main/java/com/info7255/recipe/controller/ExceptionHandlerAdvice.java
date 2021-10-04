package com.info7255.recipe.controller;

import org.everit.json.schema.ValidationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(ValidationException.class)
    public ResponseEntity handleException(ValidationException e) {
        // log exception
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("Json schema validation fail");
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleException(Exception e) {
        // log exception
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body(e.getMessage());
    }
    @ExceptionHandler(org.json.JSONException.class)
    public ResponseEntity handleException(org.json.JSONException e) {
        // log exception
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .body("not valid json input");
    }

}
