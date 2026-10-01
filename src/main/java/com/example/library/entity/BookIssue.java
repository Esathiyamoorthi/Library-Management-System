package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;

@Entity
@Table(name = "book_issues", indexes = {
        @Index(name = "idx_book_issues_user_id", columnList = "user_id"),
        @Index(name = "idx_book_issues_book_id", columnList = "book_id"),
        @Index(name = "idx_book_issues_status_due_date", columnList = "status, due_date")
})
@Getter
@Setter
@NoArgsConstructor
public class BookIssue {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // MANY issues belong to ONE user.  Stored as a "user_id" column (the foreign key).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // MANY issues belong to ONE book.  Stored as a "book_id" column (the foreign key).
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @Column(nullable = false)
    private LocalDate issueDate;       // the day it was borrowed

    @Column(nullable = false)
    private LocalDate dueDate;         // the day it must be back

    private LocalDate returnDate;      // empty (null) until the book comes back

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private IssueStatus status;
    
}