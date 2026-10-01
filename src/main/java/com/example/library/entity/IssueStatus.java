package com.example.library.entity;

public enum IssueStatus {
    ISSUED,      // currently borrowed
    RETURNED,    // given back
    OVERDUE      // past the due date and not returned
}