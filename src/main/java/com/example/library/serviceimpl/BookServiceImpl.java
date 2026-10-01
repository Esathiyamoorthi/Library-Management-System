package com.example.library.serviceimpl;

import com.example.library.dto.BookPatchRequest;
import com.example.library.dto.BookRequest;
import com.example.library.dto.BookResponse;
import com.example.library.dto.PageResponse;
import com.example.library.entity.Book;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.DuplicateResourceException;
import com.example.library.exception.InvalidRequestException;
import com.example.library.exception.ResourceInUseException;
import com.example.library.mapper.BookMapper;
import com.example.library.repository.BookIssueRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.BookSpecification;
import com.example.library.service.BookService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class BookServiceImpl implements BookService {

    private final BookRepository bookRepository;
    private final BookMapper bookMapper; 

    private final BookIssueRepository bookIssueRepository;

    @Override
    public BookResponse createBook(BookRequest request) {
        // Rule: no two books may share an ISBN
        if (bookRepository.existsByIsbn(request.getIsbn())) {
            throw new DuplicateResourceException("ISBN already exists: " + request.getIsbn());
        }

        Book book = bookMapper.toEntity(request);
        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    @Override
    @Transactional(readOnly = true)
    public PageResponse<BookResponse> searchBooks(String title, String author, String category,
                                                  BigDecimal minPrice, BigDecimal maxPrice,
                                                  Pageable pageable) {

        // A sanity check: this can never match anything
        if (minPrice != null && maxPrice != null && minPrice.compareTo(maxPrice) > 0) {
            throw new InvalidRequestException("minPrice cannot be greater than maxPrice");
        }

        // Start with "no filter" (matches every book)
        Specification<Book> spec = (root, query, cb) -> cb.conjunction();

        // Snap on a filter block ONLY if the client sent that filter
        if (title != null && !title.isBlank()) {
            spec = spec.and(BookSpecification.titleContains(title));
        }
        if (author != null && !author.isBlank()) {
            spec = spec.and(BookSpecification.authorContains(author));
        }
        if (category != null && !category.isBlank()) {
            spec = spec.and(BookSpecification.categoryEquals(category));
        }
        if (minPrice != null) {
            spec = spec.and(BookSpecification.priceAtLeast(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(BookSpecification.priceAtMost(maxPrice));
        }

        // One call: filters + paging + sorting
        Page<Book> bookPage = bookRepository.findAll(spec, pageable);

        // Convert each Book to a BookResponse
        List<BookResponse> content = new ArrayList<>();
        for (Book book : bookPage.getContent()) {
            content.add(bookMapper.toResponse(book));
        }

        // Fill the envelope
        PageResponse<BookResponse> response = new PageResponse<>();
        response.setContent(content);
        response.setPage(bookPage.getNumber());
        response.setSize(bookPage.getSize());
        response.setTotalElements(bookPage.getTotalElements());
        response.setTotalPages(bookPage.getTotalPages());
        response.setLast(bookPage.isLast());
        return response;
    }

    @Override
    @Transactional(readOnly = true)
    public BookResponse getBookById(Long id) {
        Book book = findBookOrThrow(id);
        return bookMapper.toResponse(book);
    }

    @Override
    public BookResponse updateBook(Long id, BookRequest request) {
        Book book = findBookOrThrow(id);

        // Rule: the ISBN may not belong to a DIFFERENT book
        if (bookRepository.existsByIsbnAndIdNot(request.getIsbn(), id)) {
            throw new DuplicateResourceException("ISBN already exists: " + request.getIsbn());
        }

        changeTotalCopies(book, request.getTotalCopies());   // business rule (see below)
        bookMapper.updateEntity(book, request);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    @Override
    public BookResponse patchBook(Long id, BookPatchRequest request) {
        Book book = findBookOrThrow(id);

        // Only check the ISBN if the client actually sent one
        if (request.getIsbn() != null
                && bookRepository.existsByIsbnAndIdNot(request.getIsbn(), id)) {
            throw new DuplicateResourceException("ISBN already exists: " + request.getIsbn());
        }

        // Only touch the copies if the client actually sent totalCopies
        if (request.getTotalCopies() != null) {
            changeTotalCopies(book, request.getTotalCopies());
        }
        bookMapper.patchEntity(book, request);

        Book savedBook = bookRepository.save(book);
        return bookMapper.toResponse(savedBook);
    }

    @Override
    public void deleteBook(Long id) {
        Book book = findBookOrThrow(id);

        // Rule: a book that has ever been issued cannot be deleted
        if (bookIssueRepository.existsByBookId(id)) {
            throw new ResourceInUseException("Book " + id
                    + " has been issued before and cannot be deleted. "
                    + "Set the status to INACTIVE instead.");
        }

        bookRepository.delete(book);
    }

    // Helper: find the book or throw a 404
    private Book findBookOrThrow(Long id) {
        return bookRepository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    // Helper: keeps availableCopies correct when totalCopies changes
    private void changeTotalCopies(Book book, Integer newTotal) {
        int lentOut = book.getTotalCopies() - book.getAvailableCopies();

        if (newTotal < lentOut) {
            throw new InvalidRequestException(
                    "Total copies cannot be less than the " + lentOut
                            + " copies currently issued");
        }

        book.setTotalCopies(newTotal);
        book.setAvailableCopies(newTotal - lentOut);
    }
}