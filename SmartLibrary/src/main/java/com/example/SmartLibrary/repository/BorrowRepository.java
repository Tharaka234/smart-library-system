package com.example.sampleproject.repository;

import com.example.sampleproject.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUserId(Long userId);
    List<Borrow> findByBookId(Long bookId);
    List<Borrow> findByReturnDateIsNullAndDueDateBefore(LocalDate today);

    @Query("SELECT b FROM Borrow b WHERE b.returned = false AND b.returnDate IS NULL AND b.dueDate < :today")
    List<Borrow> findOverdueBorrows(@Param("today") LocalDate today);

    @Query("SELECT b FROM Borrow b WHERE b.returned = false AND b.returnDate IS NULL AND b.dueDate < :today AND b.user.id = :userId")
    List<Borrow> findOverdueByUser(@Param("userId") Long userId, @Param("today") LocalDate today);

    List<Borrow> findByReturnedFalse();
}