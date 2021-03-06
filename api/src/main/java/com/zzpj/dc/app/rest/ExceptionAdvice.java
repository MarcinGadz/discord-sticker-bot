package com.zzpj.dc.app.rest;

import com.zzpj.dc.app.exceptions.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.io.IOException;
import java.util.Map;

/**
 * Advice to handle exceptions
 */

@RestControllerAdvice
public class ExceptionAdvice {
    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handle(MaxUploadSizeExceededException ex) {
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(Map.of("message", "Passed file is too big"));
    }
    @ExceptionHandler(WrongFileTypeException.class)
    public ResponseEntity<Map<String, String>> handle(WrongFileTypeException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File is not valid PNG"));
    }
    @ExceptionHandler(ImageContentEmptyException.class)
    public ResponseEntity<Map<String, String>> handle(ImageContentEmptyException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "File is empty"));
    }
    @ExceptionHandler(IOException.class)
    public ResponseEntity<Map<String, String>> handle(IOException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(Map.of("message", "Error"));
    }
    @ExceptionHandler(UserLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handle(UserLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", "Limit exceeded"));
    }
    @ExceptionHandler(ImageDoesntExistException.class)
    public ResponseEntity<Map<String, String>> handle(ImageDoesntExistException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Object not found"));
    }
    @ExceptionHandler(ImageAlreadyExistsException.class)
    public ResponseEntity<Map<String, String>> handle(ImageAlreadyExistsException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT).body(Map.of("message", "Such image already exists"));
    }
}
