package com.example.library.controller;

import com.example.library.dto.BookIssueRequest;
import com.example.library.dto.BookIssueResponse;
import com.example.library.service.BookIssueService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/book-issues")
@RequiredArgsConstructor
public class BookIssueController {

    private final BookIssueService bookIssueService;
    
    @PostMapping                            // POST /api/book-issues
    public ResponseEntity<BookIssueResponse> issueBook(
            @Valid @RequestBody BookIssueRequest request) {
        BookIssueResponse issued = bookIssueService.issueBook(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(issued);      // 201
    }

    @PutMapping("/{id}/return")             // PUT /api/book-issues/3/return
    public ResponseEntity<BookIssueResponse> returnBook(@PathVariable Long id) {
        return ResponseEntity.ok(bookIssueService.returnBook(id));          // 200
    }

    @GetMapping("/overdue")                 // GET /api/book-issues/overdue
    public ResponseEntity<List<BookIssueResponse>> getOverdueIssues() {
        return ResponseEntity.ok(bookIssueService.getOverdueIssues());
    }
}