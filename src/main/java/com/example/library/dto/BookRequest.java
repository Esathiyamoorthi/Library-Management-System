package com.example.library.dto;

import com.example.library.entity.BookStatus;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookRequest {

    @NotBlank(message = "Title is required")
    private String title;
        
    private String author;

    @NotBlank(message = "ISBN is required")
    private String isbn;

    private String category;

    @NotNull(message = "Price is required")
    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @NotNull(message = "Total copies is required")
    @Positive(message = "Total copies must be greater than 0")
    private Integer totalCopies;

    

    private BookStatus status;       // optional, defaults to ACTIVE
}