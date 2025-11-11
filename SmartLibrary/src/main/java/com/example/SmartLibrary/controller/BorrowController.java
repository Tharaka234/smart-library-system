package com.example.SmartLibrary.controller;

import com.example.SmartLibrary.model.Borrow;
import com.example.SmartLibrary.repository.UserRepository;
import com.example.SmartLibrary.service.BorrowService;
import com.example.SmartLibrary.dto.BorrowRequest;
import com.example.SmartLibrary.repository.BookRepository;
import com.example.SmartLibrary.repository.BorrowRepository;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;
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
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private BookRepository bookRepository;


    // ------------------ User endpoints ------------------
    @PostMapping
    public ResponseEntity<?> addBorrow(@RequestBody Borrow borrow) {
        try {
            // Defensive date handling here (controller-level)
            // If borrowDate null -> use today
            LocalDate borrowDate = borrow.getBorrowDate();
            if (borrowDate == null) {
                borrowDate = LocalDate.now();
                borrow.setBorrowDate(borrowDate);
            }

            // If dueDate missing or earlier than borrowDate -> set borrowDate + 14 days
            LocalDate dueDate = borrow.getDueDate();
            if (dueDate == null || dueDate.isBefore(borrowDate)) {
                dueDate = borrowDate.plusDays(14); // enforce 14 days
                borrow.setDueDate(dueDate);
            }

            Borrow saved = borrowService.addBorrow(borrow);
            return ResponseEntity.status(201).body(saved);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Internal server error"));
        }
    }


    @GetMapping
    public ResponseEntity<?> getBorrows(@RequestParam(required = false) Long userId,
                                        @RequestParam(required = false) Long bookId) {
        try {
            List<Borrow> borrows = borrowService.filterBorrows(userId, bookId);
            if (borrows.isEmpty()) return ResponseEntity.ok(Map.of("message", "No borrow records found"));
            return ResponseEntity.ok(borrows);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @PutMapping("/{id}/return")
    public ResponseEntity<?> returnBook(@PathVariable Long id, @RequestParam String date) {
        try {
            LocalDate returnDate = LocalDate.parse(date);
            Borrow updated = borrowService.updateReturnDate(id, returnDate);
            return ResponseEntity.ok(updated);
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    // ------------------ Admin endpoints ------------------
    @GetMapping("/admin/all")
    public ResponseEntity<?> getAllBorrowsAdmin(@RequestParam(required = false) Long userId,
                                                @RequestParam(required = false) Long bookId) {
        try {
            List<Borrow> borrows = borrowService.filterBorrows(userId, bookId);
            return ResponseEntity.ok(borrows);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteBorrow(@PathVariable Long id) {
        try {
            borrowService.deleteBorrow(id);
            return ResponseEntity.ok(Map.of("message", "Borrow record deleted successfully"));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/admin/overdue/by-due-date")
    public ResponseEntity<?> getOverdueByDueDateAdmin() {
        return getOverdueByDueDate();
    }

    @GetMapping("/admin/overdue/by-return-date")
    public ResponseEntity<?> getOverdueByReturnDateAdmin() {
        return getOverdueByReturnDate();
    }

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