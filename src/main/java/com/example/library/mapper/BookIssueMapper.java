package com.example.library.mapper;

import com.example.library.dto.BookIssueResponse;
import com.example.library.entity.BookIssue;
import org.springframework.stereotype.Component;

@Component
public class BookIssueMapper {

    // BookIssue (database object) -> receipt
    public BookIssueResponse toResponse(BookIssue issue) {
        BookIssueResponse response = new BookIssueResponse();
        response.setId(issue.getId());
        response.setUserId(issue.getUser().getId());
        response.setUserName(issue.getUser().getName());
        response.setBookId(issue.getBook().getId());
        response.setBookTitle(issue.getBook().getTitle());
        response.setIssueDate(issue.getIssueDate());
        response.setDueDate(issue.getDueDate());
        response.setReturnDate(issue.getReturnDate());
        response.setStatus(issue.getStatus());
        return response;
    }
}