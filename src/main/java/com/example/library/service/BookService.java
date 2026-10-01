package com.example.library.service;

import com.example.library.dto.BookPatchRequest;
import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.dto.PageResponse;
import org.springframework.data.domain.Pageable;

import java.math.BigDecimal;
import java.util.List;

public interface BookService {

    BookResponse createBook(BookRequest request);

    PageResponse<BookResponse> searchBooks(String title, String author, String category,
                                           BigDecimal minPrice, BigDecimal maxPrice,
                                           Pageable pageable);         // we upgrade this in Step 8 (search + paging)

    BookResponse getBookById(Long id);

    BookResponse updateBook(Long id, BookRequest request);      // PUT

    BookResponse patchBook(Long id, BookPatchRequest request);  // PATCH

    void deleteBook(Long id);
}