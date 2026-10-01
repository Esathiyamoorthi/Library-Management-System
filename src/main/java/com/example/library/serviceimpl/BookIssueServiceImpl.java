package com.example.library.serviceimpl;

import com.example.library.dto.BookIssueRequest;
import com.example.library.dto.BookIssueResponse;
import com.example.library.entity.Book;
import com.example.library.entity.BookIssue;
import com.example.library.entity.BookStatus;
import com.example.library.entity.IssueStatus;
import com.example.library.entity.User;
import com.example.library.entity.userStatus;
import com.example.library.exception.BookIssueNotFoundException;
import com.example.library.exception.BookNotAvailableException;
import com.example.library.exception.BookNotFoundException;
import com.example.library.exception.InvalidBookIssueException;
import com.example.library.exception.UserNotFoundException;
import com.example.library.mapper.BookIssueMapper;
import com.example.library.repository.BookIssueRepository;
import com.example.library.repository.BookRepository;
import com.example.library.repository.UserRepository;
import com.example.library.service.BookIssueService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional     // every method is ONE all-or-nothing unit of database work
public class BookIssueServiceImpl implements BookIssueService {

    private static final int LOAN_DAYS = 14;    // how long a member may keep a book

    private final BookIssueRepository bookIssueRepository;
    private final UserRepository userRepository;
    private final BookRepository bookRepository;
    private final BookIssueMapper bookIssueMapper;

    // ------------------------------------------------------------------
    // ISSUE A BOOK
    // ------------------------------------------------------------------
    @Override
    public BookIssueResponse issueBook(BookIssueRequest request) {

        // Check 1: the user must exist
        User user = userRepository.findById(request.getUserId())
                .orElseThrow(() -> new UserNotFoundException(request.getUserId()));

        // Check 2: the user must be ACTIVE
        if (user.getStatus() != userStatus.ACTIVE) {
            throw new InvalidBookIssueException(
                    "User is not active and cannot borrow books. User id: " + user.getId());
        }

        // Check 3: the book must exist
        Book book = bookRepository.findById(request.getBookId())
                .orElseThrow(() -> new BookNotFoundException(request.getBookId()));

        // Check 4: the book must be in the catalog AND have a copy on the shelf
        if (book.getStatus() != BookStatus.ACTIVE) {
            throw new BookNotAvailableException(
                    "Book is not available in the catalog. Book id: " + book.getId());
        }
        if (book.getAvailableCopies() <= 0) {
            throw new BookNotAvailableException(
                    "No copies available for book id: " + book.getId());
        }

        // Change 1: take one copy off the shelf
        book.setAvailableCopies(book.getAvailableCopies() - 1);
        bookRepository.save(book);

        // Change 2: write the borrowing slip
        LocalDate today = LocalDate.now();

        BookIssue issue = new BookIssue();
        issue.setUser(user);
        issue.setBook(book);
        issue.setIssueDate(today);
        issue.setDueDate(today.plusDays(LOAN_DAYS));
        issue.setStatus(IssueStatus.ISSUED);

        BookIssue savedIssue = bookIssueRepository.save(issue);
        return bookIssueMapper.toResponse(savedIssue);
    }

    // ------------------------------------------------------------------
    // RETURN A BOOK
    // ------------------------------------------------------------------
    @Override
    public BookIssueResponse returnBook(Long issueId) {

        // Check 1: the slip must exist
        BookIssue issue = bookIssueRepository.findById(issueId)
                .orElseThrow(() -> new BookIssueNotFoundException(issueId));

        // Check 2: it must not be returned already
        if (issue.getStatus() == IssueStatus.RETURNED) {
            throw new InvalidBookIssueException(
                    "Book issue " + issueId + " has already been returned");
        }4

        // Change 1: close the slip
        issue.setReturnDate(LocalDate.now());
        issue.setStatus(IssueStatus.RETURNED);
        BookIssue savedIssue = bookIssueRepository.save(issue);

        // Change 2: put the copy back on the shelf
        Book book = issue.getBook();
        book.setAvailableCopies(book.getAvailableCopies() + 1);
        bookRepository.save(book);

        return bookIssueMapper.toResponse(savedIssue);
    }

    // ------------------------------------------------------------------
    // REPORT: books currently issued to one user
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getCurrentIssuesByUser(Long userId) {

        // The user must exist, so a wrong id gives a clear 404, not a misleading empty list
        if (!userRepository.existsById(userId)) {
            throw new UserNotFoundException(userId);
        }

        // "Currently issued" = not returned yet
        List<BookIssue> issues = bookIssueRepository.findCurrentIssuesByUserId(
                userId, List.of(IssueStatus.ISSUED, IssueStatus.OVERDUE));

        return toResponseList(issues);
    }

    // ------------------------------------------------------------------
    // REPORT: overdue books
    // ------------------------------------------------------------------
    @Override
    @Transactional(readOnly = true)
    public List<BookIssueResponse> getOverdueIssues() {
        List<BookIssue> issues = bookIssueRepository.findOverdue(
                IssueStatus.ISSUED, LocalDate.now());

        return toResponseList(issues);
    }

    // Helper: converts a list of issues to a list of responses
    private List<BookIssueResponse> toResponseList(List<BookIssue> issues) {
        List<BookIssueResponse> responses = new ArrayList<>();
        for (BookIssue issue : issues) {
            responses.add(bookIssueMapper.toResponse(issue));
        }
        return responses;
    }
}