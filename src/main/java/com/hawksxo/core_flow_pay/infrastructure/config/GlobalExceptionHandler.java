package com.hawksxo.core_flow_pay.infrastructure.config;

import java.time.LocalDateTime;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.hawksxo.core_flow_pay.domain.exceptions.DuplicateIdempotencyKeyException;
import com.hawksxo.core_flow_pay.domain.exceptions.SubscriptionAlreadyActiveException;
import com.hawksxo.core_flow_pay.domain.exceptions.SubscriptionNotFoundException;

@RestControllerAdvice
public class GlobalExceptionHandler {
    public record ErrorResponse(int status, String error, String message, LocalDateTime timestamp) {}

    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(SubscriptionNotFoundException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            "NOT_FOUND",
            ex.getMessage(),
            LocalDateTime.now()
        );
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(SubscriptionAlreadyActiveException.class)
    public ResponseEntity<ErrorResponse> handleAlreadyActive(SubscriptionAlreadyActiveException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "CONFLICT",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }

    @ExceptionHandler(DuplicateIdempotencyKeyException.class)
    public ResponseEntity<ErrorResponse> handleDuplicateKey(DuplicateIdempotencyKeyException ex) {
        ErrorResponse response = new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            "DUPLICATE_TRANSACTION",
            ex.getMessage(),
            LocalDateTime.now()
        );
        
        return ResponseEntity.status(HttpStatus.CONFLICT).body(response);
    }   
}
