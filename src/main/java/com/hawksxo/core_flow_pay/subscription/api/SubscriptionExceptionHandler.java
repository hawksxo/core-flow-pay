package com.hawksxo.core_flow_pay.subscription.api;

import com.hawksxo.core_flow_pay.subscription.domain.DuplicateIdempotencyKeyException;
import com.hawksxo.core_flow_pay.subscription.domain.InvalidSubscriptionStateTransitionException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionAlreadyActiveException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionInvalidStateException;
import com.hawksxo.core_flow_pay.subscription.domain.SubscriptionNotFoundException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingRequestHeaderException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import java.time.Instant;

@RestControllerAdvice(basePackages = "com.hawksxo.core_flow_pay.subscription.api")
public class SubscriptionExceptionHandler {
    @ExceptionHandler(SubscriptionNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(SubscriptionNotFoundException ex, HttpServletRequest request) {
        return buildError(HttpStatus.NOT_FOUND, "Subscription Not Found", ex.getMessage(), request);
    }

    @ExceptionHandler({DuplicateIdempotencyKeyException.class, SubscriptionAlreadyActiveException.class, InvalidSubscriptionStateTransitionException.class, SubscriptionInvalidStateException.class})
    public ResponseEntity<ErrorResponse> handleConflict(RuntimeException ex, HttpServletRequest request) {
        return buildError(HttpStatus.CONFLICT, "Conflict", ex.getMessage(), request);
    }

    @ExceptionHandler({MethodArgumentNotValidException.class, ConstraintViolationException.class, MissingRequestHeaderException.class})
    public ResponseEntity<ErrorResponse> handleValidation(Exception ex, HttpServletRequest request) {
        String message = ex instanceof MissingRequestHeaderException mrh
            ? "Missing required header: " + mrh.getHeaderName()
            : ex.getMessage();
        return buildError(HttpStatus.BAD_REQUEST, "Bad Request", message, request);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleUnexpected(Exception ex, HttpServletRequest request) {
        return buildError(HttpStatus.INTERNAL_SERVER_ERROR, "Internal Server Error", "An unexpected error occurred", request);
    }

    private ResponseEntity<ErrorResponse> buildError(HttpStatus status, String error, String message, HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
            Instant.now(),
            status.value(),
            error,
            message,
            request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }

    public record ErrorResponse(Instant timestamp, int status, String error, String message, String path) {}
}