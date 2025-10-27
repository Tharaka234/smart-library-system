package com.example.smartlibrary.repository;

import com.example.smartlibrary.model.Borrow;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface BorrowRepository extends JpaRepository<Borrow, Long> {
    List<Borrow> findByUser_Id(Long userId);
    List<Borrow> findByBook_Id(Long bookId);
}