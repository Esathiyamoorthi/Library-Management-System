package com.example.library.service;

import com.example.library.dto.BookIssueRequest;
import com.example.library.dto.BookIssueResponse;

import java.util.List;

public interface BookIssueService {

    BookIssueResponse issueBook(BookIssueRequest request);

    BookIssueResponse returnBook(Long issueId);

    List<BookIssueResponse> getCurrentIssuesByUser(Long userId);

    List<BookIssueResponse> getOverdueIssues();
}