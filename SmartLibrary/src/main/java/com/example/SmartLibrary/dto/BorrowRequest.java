package com.example.SmartLibrary.dto;  // ✅ CHANGED

import java.time.LocalDate;

public class BorrowRequest {
    private Long bookId;
    private LocalDate borrowDate;
    private LocalDate returnDate;

    public Long getBookId() { return bookId; }
    public void setBookId(Long bookId) { this.bookId = bookId; }

    public LocalDate getBorrowDate() { return borrowDate; }
    public void setBorrowDate(LocalDate borrowDate) { this.borrowDate = borrowDate; }

    public LocalDate getReturnDate() { return returnDate; }
    public void setReturnDate(LocalDate returnDate) { this.returnDate = returnDate; }
}