package com.info7255.recipe.controller;

import org.everit.json.schema.ValidationException;
import org.json.JSONObject;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
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
                .contentType(MediaType.APPLICATION_JSON)
                .body(new JSONObject().put("Message","Json schema validation fail").toString());
    }
    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity handleException(Exception e) {
        // log exception
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new JSONObject().put("Message",e.getMessage()).toString());
    }
    @ExceptionHandler(org.json.JSONException.class)
    public ResponseEntity handleException(org.json.JSONException e) {
        // log exception
        return ResponseEntity
                .status(HttpStatus.BAD_REQUEST)
                .contentType(MediaType.APPLICATION_JSON)
                .body(new JSONObject().put("Message","not valid json input").toString());
    }

}
