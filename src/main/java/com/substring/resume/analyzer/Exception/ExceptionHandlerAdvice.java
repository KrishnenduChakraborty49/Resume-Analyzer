package com.substring.resume.analyzer.Exception;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.multipart.MaxUploadSizeExceededException;

import java.util.Map;

@RestControllerAdvice
public class ExceptionHandlerAdvice {

    @ExceptionHandler(DocumentParseException.class)
    public ResponseEntity<Map<String, String>> handleDocumentParseException(DocumentParseException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", ex.getMessage(),
            "type", "DOCUMENT_PARSE_ERROR"
        ));
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<Map<String, String>> handleFileTooLarge(MaxUploadSizeExceededException ex) {
        return ResponseEntity.badRequest().body(Map.of(
            "error", "File is too large. Maximum allowed size is 15 MB.",
            "type", "FILE_TOO_LARGE"
        ));
    }
}
