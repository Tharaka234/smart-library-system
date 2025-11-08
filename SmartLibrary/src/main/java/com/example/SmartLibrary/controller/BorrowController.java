package com.example.SmartLibrary.controller;

import com.example.SmartLibrary.model.Borrow;
import com.example.SmartLibrary.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/borrows")
@CrossOrigin(origins = "*")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    // ------------------ User endpoints ------------------

    // User adds a borrow
    @PostMapping
    public ResponseEntity<?> userAddBorrow(@RequestBody Borrow borrow) {
        try {
            Borrow saved = borrowService.addBorrow(borrow);
            return ResponseEntity.ok(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // User views own borrows (optional filters)
    @GetMapping
    public ResponseEntity<?> getUserBorrows(@RequestParam(required = false) Long userId,
                                            @RequestParam(required = false) Long bookId) {
        try {
            List<Borrow> borrows = borrowService.filterBorrows(userId, bookId);
            if (borrows.isEmpty()) return ResponseEntity.ok(Map.of("message", "No borrow records found"));
            return ResponseEntity.ok(borrows);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // User returns book
    @PutMapping("/{id}/return")
    public ResponseEntity<?> userReturnBook(@PathVariable Long id, @RequestParam String date) {
        try {
            LocalDate returnDate = LocalDate.parse(date);
            Borrow updated = borrowService.updateReturnDate(id, returnDate);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ------------------ Admin endpoints ------------------

    // Admin: all borrows with filters
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllBorrowsAdmin(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId) {
        try {
            List<Borrow> borrows = borrowService.filterBorrows(userId, bookId);
            return ResponseEntity.ok(borrows);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Admin deletes borrow
    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBorrow(@PathVariable Long id) {
        try {
            borrowService.deleteBorrow(id);
            return ResponseEntity.ok(Map.of("message", "Borrow record deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // Admin overdue by due date
    @GetMapping("/admin/overdue/by-due-date")
    public ResponseEntity<?> getOverdueByDueDateAdmin() {
        return getOverdueByDueDate();
    }

    // Admin overdue by return date
    @GetMapping("/admin/overdue/by-return-date")
    public ResponseEntity<?> getOverdueByReturnDateAdmin() {
        return getOverdueByReturnDate();
    }

    // Admin stats
    @GetMapping("/admin/stats")
    public ResponseEntity<?> getStatsAdmin() {
        return getStats();
    }


    @GetMapping("/overdue/by-due-date")
    public ResponseEntity<?> getOverdueByDueDate() {
        try {
            List<Borrow> overdue = borrowService.getOverdueBorrowsByDueDate();
            if (overdue.isEmpty()) return ResponseEntity.ok(Map.of("message", "No overdue books (due date)"));
            return ResponseEntity.ok(overdue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/overdue/by-return-date")
    public ResponseEntity<?> getOverdueByReturnDate() {
        try {
            List<Borrow> overdue = borrowService.getOverdueBorrowsByReturnDate();
            if (overdue.isEmpty()) return ResponseEntity.ok(Map.of("message", "No overdue books (return date)"));
            return ResponseEntity.ok(overdue);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/stats")
    public ResponseEntity<Map<String, Long>> getStats() {
        return ResponseEntity.ok(borrowService.getBorrowStats());
    }

}