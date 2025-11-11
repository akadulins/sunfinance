package com.sunfinance.verification.exception;

import com.sunfinance.common.exceptions.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.Instant;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleMalformedJson(HttpMessageNotReadableException ex) {
        log.warn("Malformed JSON request: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            "Malformed JSON passed.",
            ""
        );
    }

    @ExceptionHandler(DuplicateVerificationException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public ErrorResponse handleDuplicateVerification(DuplicateVerificationException ex) {
        log.warn("Duplicate verification attempt: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.CONFLICT.value(),
            ex.getMessage(),
            ""
        );
    }

    @ExceptionHandler(VerificationNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public ErrorResponse handleVerificationNotFound(VerificationNotFoundException ex) {
        log.warn("Verification not found: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.NOT_FOUND.value(),
            ex.getMessage(),
            ""
        );
    }

    @ExceptionHandler(InvalidCodeException.class)
    @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY)
    public ErrorResponse handleInvalidCode(InvalidCodeException ex) {
        log.warn("Invalid code provided: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.UNPROCESSABLE_ENTITY.value(),
            ex.getMessage(),
            ""
        );
    }

    @ExceptionHandler(VerificationExpiredException.class)
    @ResponseStatus(HttpStatus.GONE)
    public ErrorResponse handleVerificationExpired(VerificationExpiredException ex) {
        log.warn("Verification expired");
        return new ErrorResponse(
            HttpStatus.GONE.value(),
            "Verification has expired",
            ""
        );
    }

    @ExceptionHandler(VerificationForbiddenException.class)
    @ResponseStatus(HttpStatus.FORBIDDEN)
    public ErrorResponse handleVerificationForbidden(VerificationForbiddenException ex) {
        log.warn("Forbidden verification access: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.FORBIDDEN.value(),
            ex.getMessage(),
            ""
        );
    }

    @ExceptionHandler(IllegalArgumentException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ErrorResponse handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Invalid argument: {}", ex.getMessage());
        return new ErrorResponse(
            HttpStatus.BAD_REQUEST.value(),
            ex.getMessage(),
            ""
        );
    }

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public ErrorResponse handleGenericException(Exception ex) {
        log.error("Unexpected error occurred", ex);
        return new ErrorResponse(
            HttpStatus.INTERNAL_SERVER_ERROR.value(),
            "An unexpected error occurred",
            ""
        );
    }

    public static class ErrorResponse {
        private int status;
        private String message;
        private String path;
        private Instant timestamp;

        public ErrorResponse(int status, String message, String path) {
            this.status = status;
            this.message = message;
            this.path = path;
            this.timestamp = Instant.now();
        }

        public int getStatus() { return status; }
        public String getMessage() { return message; }
        public String getPath() { return path; }
        public Instant getTimestamp() { return timestamp; }
    }
}