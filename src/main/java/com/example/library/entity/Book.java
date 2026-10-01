package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "books", indexes = {@Index(name = "idx_books_category", columnList = "category")})
@Getter
@Setter
@NoArgsConstructor
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String title;

    private String author;                        // optional

    @Column(nullable = false, unique = true)      // no two books share an ISBN
    private String isbn;

    private String category;                      // optional

    @Column(nullable = false, precision = 10, scale = 2)   // e.g. 12345678.99
    private BigDecimal price;

    @Column(nullable = false)
    private Integer totalCopies;                  // how many the library OWNS

    @Column(nullable = false)
    private Integer availableCopies;              // how many are on the shelf RIGHT NOW

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private BookStatus status;

    @CreationTimestamp
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    private LocalDateTime updatedAt;

    // ONE book has MANY issues.
    @OneToMany(mappedBy = "book")
    private List<BookIssue> bookIssues = new ArrayList<>();
}