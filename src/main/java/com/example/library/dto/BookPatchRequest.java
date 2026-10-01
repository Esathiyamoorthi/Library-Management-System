package com.example.library.dto;

import com.example.library.entity.BookStatus;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class BookPatchRequest {

    // Everything is optional. But IF a value is sent, it must be valid.

    @Pattern(regexp = ".*\\S.*", message = "Title cannot be blank")
    private String title;

    private String author;

    @Pattern(regexp = ".*\\S.*", message = "ISBN cannot be blank")
    private String isbn;

    private String category;

    @Positive(message = "Price must be greater than 0")
    private BigDecimal price;

    @Positive(message = "Total copies must be greater than 0")
    private Integer totalCopies;

    private BookStatus status;
}