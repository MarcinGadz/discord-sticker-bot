package com.zzpj.dc.app.rest;

import com.zzpj.dc.app.exceptions.WrongFileTypeException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

/**
 * Advice to handle exception when uploaded file is to big for tomcat
 * By default max file size is 1MiB
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
}
