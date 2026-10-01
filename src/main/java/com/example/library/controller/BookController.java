package com.example.library.controller;

import com.example.library.dto.BookPatchRequest;
import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.dto.PageResponse;
import com.example.library.service.BookService;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springdoc.core.annotations.ParameterObject;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User management APIs")
public class BookController {

    private final BookService bookService;

    @PostMapping                             // POST /api/books
    public ResponseEntity<BookResponse> createBook(@Valid @RequestBody BookRequest request) {
        BookResponse created = bookService.createBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @GetMapping                              // GET /api/books?title=java&page=0&size=10 ...
    public ResponseEntity<PageResponse<BookResponse>> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String author,
            @RequestParam(required = false) String category,
            @RequestParam(required = false) BigDecimal minPrice,
            @RequestParam(required = false) BigDecimal maxPrice,
            @PageableDefault(size = 10, sort = "id") Pageable pageable) {

        return ResponseEntity.ok(
                bookService.searchBooks(title, author, category, minPrice, maxPrice, pageable));
    }

    @GetMapping("/{id}")                     // GET /api/books/5
    public ResponseEntity<BookResponse> getBookById(@PathVariable Long id) {
        return ResponseEntity.ok(bookService.getBookById(id));
    }

    @PutMapping("/{id}")                     // PUT /api/books/5
    public ResponseEntity<BookResponse> updateBook(@PathVariable Long id,
                                                   @Valid @RequestBody BookRequest request) {
        return ResponseEntity.ok(bookService.updateBook(id, request));
    }

    @PatchMapping("/{id}")                   // PATCH /api/books/5
    public ResponseEntity<BookResponse> patchBook(@PathVariable Long id,
                                                  @Valid @RequestBody BookPatchRequest request) {
        return ResponseEntity.ok(bookService.patchBook(id, request));
    }

    @DeleteMapping("/{id}")                  // DELETE /api/books/5
    public ResponseEntity<Void> deleteBook(@PathVariable Long id) {
        bookService.deleteBook(id);
        return ResponseEntity.noContent().build();
    }
}