package com.example.library.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity                          // this class becomes a database table
@Table(name = "users")           // table name is "users" ("user" is reserved in PostgreSQL)
@Getter                          // Lombok writes getName(), getEmail() ... for us
@Setter                          // Lombok writes setName(), setEmail() ... for us
@NoArgsConstructor               // JPA needs an empty constructor
public class User {

    @Id                                                     // primary key
    @GeneratedValue(strategy = GenerationType.IDENTITY)     // the database makes 1, 2, 3 ...
    private Long id;

    @Column(nullable = false)                // cannot be empty
    private String name;

    @Column(nullable = false, unique = true) // cannot be empty, no duplicates
    private String email;

    @Column(nullable = false, length = 10)
    private String phone;

    private String address;                  // optional

    private LocalDate dateOfBirth;           // optional

    @Enumerated(EnumType.STRING)             // save the word "ACTIVE", not a number
    @Column(nullable = false)
    private userStatus status;

    @CreationTimestamp                       // filled automatically when created
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp                         // refreshed automatically on every update
    private LocalDateTime updatedAt;

    // ONE user has MANY issues. "mappedBy" = the real link is the "user" field inside BookIssue.
    @OneToMany(mappedBy = "user")
    private List<BookIssue> bookIssues = new ArrayList<>();
}