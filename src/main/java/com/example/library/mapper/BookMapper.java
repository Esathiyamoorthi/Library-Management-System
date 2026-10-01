package com.example.library.mapper;

import com.example.library.dto.BookPatchRequest;
import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BookStatus;
import org.springframework.stereotype.Component;

@Component
public class BookMapper {

    // Form -> new database object (used by POST)
    public Book toEntity(BookRequest request) {
        Book book = new Book();
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setPrice(request.getPrice());
        book.setTotalCopies(request.getTotalCopies());
        book.setAvailableCopies(request.getTotalCopies());   // nothing is lent out yet

        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        } else {
            book.setStatus(BookStatus.ACTIVE);
        }
        return book;
    }

    // Database object -> receipt
    public BookResponse toResponse(Book book) {
        BookResponse response = new BookResponse();
        response.setId(book.getId());
        response.setTitle(book.getTitle());
        response.setAuthor(book.getAuthor());
        response.setIsbn(book.getIsbn());
        response.setCategory(book.getCategory());
        response.setPrice(book.getPrice());
        response.setTotalCopies(book.getTotalCopies());
        response.setAvailableCopies(book.getAvailableCopies());
        response.setStatus(book.getStatus());
        response.setCreatedAt(book.getCreatedAt());
        response.setUpdatedAt(book.getUpdatedAt());
        return response;
    }

    // PUT: replace the descriptive fields.
    // NOTE: totalCopies is NOT copied here. The service handles it,
    // because changing it also affects availableCopies (a business rule).
    public void updateEntity(Book book, BookRequest request) {
        book.setTitle(request.getTitle());
        book.setAuthor(request.getAuthor());
        book.setIsbn(request.getIsbn());
        book.setCategory(request.getCategory());
        book.setPrice(request.getPrice());
        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        }
    }

    // PATCH: change only the fields that were sent (again, not totalCopies)
    public void patchEntity(Book book, BookPatchRequest request) {
        if (request.getTitle() != null) {
            book.setTitle(request.getTitle());
        }
        if (request.getAuthor() != null) {
            book.setAuthor(request.getAuthor());
        }
        if (request.getIsbn() != null) {
            book.setIsbn(request.getIsbn());
        }
        if (request.getCategory() != null) {
            book.setCategory(request.getCategory());
        }
        if (request.getPrice() != null) {
            book.setPrice(request.getPrice());
        }
        if (request.getStatus() != null) {
            book.setStatus(request.getStatus());
        }
    }
}