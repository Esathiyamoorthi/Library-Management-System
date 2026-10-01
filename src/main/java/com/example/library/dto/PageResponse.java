package com.example.library.dto;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

// One page of results, plus information about where we are.
@Getter
@Setter
@NoArgsConstructor
public class PageResponse<T> {

    private List<T> content;        // the items on this page
    private int page;               // current page number (starts at 0)
    private int size;               // items per page
    private long totalElements;     // how many items exist in total
    private int totalPages;         // how many pages exist
    private boolean last;           // is this the last page?
}