package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {

    List<Borrow> findByUser_Id(Long userId);

    List<Borrow> findByBook_Id(Long bookId);

    // 🆕 Overdue filter: books not returned and returnDate < today
    @Query("SELECT b FROM Borrow b WHERE b.returned = false AND b.returnDate < :today")
    List<Borrow> findOverdueBorrows(@Param("today") LocalDate today);

    // 🆕 Overdue + user combination
    @Query("SELECT b FROM Borrow b WHERE b.returned = false AND b.returnDate < :today AND b.user.id = :userId")
    List<Borrow> findOverdueByUser(@Param("userId") Long userId, @Param("today") LocalDate today);
}