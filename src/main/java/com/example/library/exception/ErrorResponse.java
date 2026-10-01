package com.example.library.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor      // Lombok writes: new ErrorResponse(timestamp, status, message, path)
public class ErrorResponse {

    private LocalDateTime timestamp;
    private int status;
    private String message;
    private String path;
}