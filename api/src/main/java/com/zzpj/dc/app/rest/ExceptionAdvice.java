package com.zzpj.dc.app.rest;

import com.zzpj.dc.app.exceptions.ImageContentEmptyException;
import com.zzpj.dc.app.exceptions.ImageNotFoundException;
import com.zzpj.dc.app.exceptions.UserLimitExceededException;
import com.zzpj.dc.app.exceptions.WrongFileTypeException;
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
    @ExceptionHandler(ImageNotFoundException.class)
    public ResponseEntity<Map<String, String>> handle(ImageNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Image not found"));
    }
    @ExceptionHandler(UserLimitExceededException.class)
    public ResponseEntity<Map<String, String>> handle(UserLimitExceededException ex) {
        return ResponseEntity.status(HttpStatus.TOO_MANY_REQUESTS).body(Map.of("message", "Limit exceeded"));
    }
}
