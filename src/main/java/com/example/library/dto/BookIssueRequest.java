package com.example.library.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BookIssueRequest {

    @NotNull(message = "User id is required")
    private Long userId;

    @NotNull(message = "Book id is required")
    private Long bookId;
}