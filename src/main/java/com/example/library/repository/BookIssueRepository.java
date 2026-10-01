package com.example.library.repository;

import com.example.library.entity.BookIssue;
import com.example.library.entity.IssueStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BookIssueRepository extends JpaRepository<BookIssue, Long> {

    // All open issues of one user. JOIN FETCH loads the book and user in the SAME query.
    @Query("SELECT bi FROM BookIssue bi "
            + "JOIN FETCH bi.book "
            + "JOIN FETCH bi.user "
            + "WHERE bi.user.id = :userId AND bi.status IN :statuses")
    List<BookIssue> findCurrentIssuesByUserId(@Param("userId") Long userId,
                                              @Param("statuses") List<IssueStatus> statuses);

    // Issues that are still ISSUED but whose due date is before today.
    @Query("SELECT bi FROM BookIssue bi "
            + "JOIN FETCH bi.book "
            + "JOIN FETCH bi.user "
            + "WHERE bi.status = :status AND bi.dueDate < :today")
    List<BookIssue> findOverdue(@Param("status") IssueStatus status,
                                @Param("today") LocalDate today);

    // "Does any issue slip refer to this user?"
    boolean existsByUserId(Long userId);

    // "Does any issue slip refer to this book?"
    boolean existsByBookId(Long bookId);
}