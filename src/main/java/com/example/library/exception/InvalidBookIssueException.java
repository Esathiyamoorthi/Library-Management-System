package com.example.library.exception;

public class InvalidBookIssueException extends RuntimeException {

    public InvalidBookIssueException(String message) {
        super(message);
    }
}