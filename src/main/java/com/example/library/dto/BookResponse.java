package com.example.library.dto;

import com.example.library.entity.BookStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
public class BookResponse {

    private Long id;
    
    private String title;

    private String author;

    private String isbn;

    private String category;

    private BigDecimal price;

    private Integer totalCopies;

    private Integer availableCopies;

    private BookStatus status;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;
}