package com.example.library.entity;

// ACTIVE = the book is in the catalog. INACTIVE = it has been withdrawn.
// (This is separate from copies: an ACTIVE book can still have 0 copies on the shelf.)
public enum BookStatus {
    ACTIVE,
    INACTIVE
}