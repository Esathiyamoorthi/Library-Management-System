package com.example.library.repository;

import com.example.library.entity.Book;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;

// Each method below is ONE filter block.
// (root, query, cb) -> ...  is a small recipe. Think of the three helpers as:
//   root = the books table,  cb = a toolbox of conditions (like, equal, >= ...)
public class BookSpecification {

    // title CONTAINS the text, ignoring capital letters   ->  WHERE lower(title) LIKE '%java%'
    public static Specification<Book> titleContains(String title) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("title")), "%" + title.toLowerCase() + "%");
    }

    // author CONTAINS the text                            ->  WHERE lower(author) LIKE '%martin%'
    public static Specification<Book> authorContains(String author) {
        return (root, query, cb) ->
                cb.like(cb.lower(root.get("author")), "%" + author.toLowerCase() + "%");
    }

    // category EQUALS the text (ignoring capitals)        ->  WHERE lower(category) = 'programming'
    public static Specification<Book> categoryEquals(String category) {
        return (root, query, cb) ->
                cb.equal(cb.lower(root.get("category")), category.toLowerCase());
    }

    // price is at least this much                         ->  WHERE price >= 300
    public static Specification<Book> priceAtLeast(BigDecimal minPrice) {
        return (root, query, cb) ->
                cb.greaterThanOrEqualTo(root.get("price"), minPrice);
    }

    // price is at most this much                          ->  WHERE price <= 800
    public static Specification<Book> priceAtMost(BigDecimal maxPrice) {
        return (root, query, cb) ->
                cb.lessThanOrEqualTo(root.get("price"), maxPrice);
    }
}