package com.example.SmartLibrary.controller;

import com.example.smartlibrary.model.Borrow;
import com.example.smartlibrary.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // Add new borrow record with proper error handling
    @PostMapping
    public ResponseEntity<?> addBorrow(@RequestBody Borrow borrow) {
        try {
            Borrow savedBorrow = borrowService.addBorrow(borrow);
            return ResponseEntity.ok(savedBorrow);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Get all borrow records or filter by userId/bookId
    @GetMapping
    public List<Borrow> getAllBorrows(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId
    ) {
        return borrowService.filterBorrows(userId, bookId);
    }

    // Delete a borrow record
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBorrow(@PathVariable Long id) {
        try {
            borrowService.deleteBorrow(id);
            return ResponseEntity.ok(Map.of("message", "Borrow record deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // Update return date and mark book as available
    @PutMapping("/{id}/return")
    public ResponseEntity<?> updateReturnDate(
            @PathVariable Long id,
            @RequestParam String date
    ) {
        try {
            LocalDate returnDate = LocalDate.parse(date);
            Borrow updatedBorrow = borrowService.updateReturnDate(id, returnDate);
            return ResponseEntity.ok(updatedBorrow);
        } catch (RuntimeException e) {
            return ResponseEntity
                    .badRequest()
                    .body(Map.of("error", e.getMessage()));
        }
    }

    //  Overdue by dueDate and null returnDate
    @GetMapping("/overdue/by-due-date")
    public ResponseEntity<?> getOverdueBorrowsByDueDate() {
        try {
            List<Borrow> overdueList = borrowService.getOverdueBorrowsByDueDate();
            if (overdueList.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No overdue books found (due date logic)"));
            }
            return ResponseEntity.ok(overdueList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    //  Overdue by returnDate and returned flag
    @GetMapping("/overdue/by-return-date")
    public ResponseEntity<?> getOverdueBorrowsByReturnDate() {
        try {
            List<Borrow> overdueList = borrowService.getOverdueBorrowsByReturnDate();
            if (overdueList.isEmpty()) {
                return ResponseEntity.ok(Map.of("message", "No overdue books found (return date logic)"));
            }
            return ResponseEntity.ok(overdueList);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }
}
