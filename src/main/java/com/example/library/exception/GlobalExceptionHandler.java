package com.example.library.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.validation.FieldError;

import java.time.LocalDateTime;

@RestControllerAdvice    // "watch every controller. If one throws an exception, send it to me."
public class GlobalExceptionHandler {

    // ---------- 404: something does not exist ----------

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleUserNotFound(UserNotFoundException ex, HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookNotFound(BookNotFoundException ex,
                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    @ExceptionHandler(BookIssueNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleBookIssueNotFound(BookIssueNotFoundException ex,
                                                                 HttpServletRequest request) {
        return buildResponse(HttpStatus.NOT_FOUND, ex.getMessage(), request);
    }

    // ---------- 409: conflicts ----------

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ErrorResponse> handleDuplicate(DuplicateResourceException ex,
                                                         HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(BookNotAvailableException.class)
    public ResponseEntity<ErrorResponse> handleBookNotAvailable(BookNotAvailableException ex,
                                                                HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    @ExceptionHandler(ResourceInUseException.class)
    public ResponseEntity<ErrorResponse> handleResourceInUse(ResourceInUseException ex,
                                                             HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT, ex.getMessage(), request);
    }

    // Safety net for any database rule that gets broken (unique or foreign key)
    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ErrorResponse> handleDataIntegrity(DataIntegrityViolationException ex,
                                                             HttpServletRequest request) {
        return buildResponse(HttpStatus.CONFLICT,
                "The operation conflicts with existing data", request);
    }

    // ---------- 400: the client sent something wrong ----------

    @ExceptionHandler(InvalidBookIssueException.class)
    public ResponseEntity<ErrorResponse> handleInvalidBookIssue(InvalidBookIssueException ex,
                                                                HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    @ExceptionHandler(InvalidRequestException.class)
    public ResponseEntity<ErrorResponse> handleInvalidRequest(InvalidRequestException ex,
                                                              HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST, ex.getMessage(), request);
    }

    // A @Valid rule failed (bad phone, empty name, ...)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex,
                                                          HttpServletRequest request) {
        StringBuilder message = new StringBuilder();

        for (FieldError error : ex.getBindingResult().getFieldErrors()) {
            if (message.length() > 0) {
                message.append(", ");
            }
            message.append(error.getField()).append(": ").append(error.getDefaultMessage());
        }

        return buildResponse(HttpStatus.BAD_REQUEST, message.toString(), request);
    }

    // Broken JSON, or a wrong value such as "status": "BANANA"
    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ErrorResponse> handleUnreadable(HttpMessageNotReadableException ex,
                                                          HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Malformed JSON or invalid value in request body", request);
    }

    // Wrong type in the URL, such as ?minPrice=abc
    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ErrorResponse> handleTypeMismatch(MethodArgumentTypeMismatchException ex,
                                                            HttpServletRequest request) {
        return buildResponse(HttpStatus.BAD_REQUEST,
                "Invalid value for parameter: " + ex.getName(), request);
    }

    // ---------- 500: anything we did not plan for (the last safety net) ----------

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGeneral(Exception ex,
                                                       HttpServletRequest request) {
        return buildResponse(HttpStatus.INTERNAL_SERVER_ERROR,
                "Something went wrong. Please try again later.", request);
    }

    // ---------- helper: builds the same error format every time ----------

    private ResponseEntity<ErrorResponse> buildResponse(HttpStatus status,
                                                        String message,
                                                        HttpServletRequest request) {
        ErrorResponse body = new ErrorResponse(
                LocalDateTime.now(),
                status.value(),
                message,
                request.getRequestURI()
        );
        return ResponseEntity.status(status).body(body);
    }
}