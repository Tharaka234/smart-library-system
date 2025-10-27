package com.example.smartlibrary.controller;

import com.example.smartlibrary.model.Borrow;
import com.example.smartlibrary.service.BorrowService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/borrows")
public class BorrowController {

    @Autowired
    private BorrowService borrowService;

    @PostMapping
    public Borrow addBorrow(@RequestBody Borrow borrow) {
        return borrowService.addBorrow(borrow);
    }

    @GetMapping
    public List<Borrow> getAllBorrows(
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) Long bookId
    ) {
        return borrowService.filterBorrows(userId, bookId);
    }

    @DeleteMapping("/{id}")
    public void deleteBorrow(@PathVariable Long id) {
        borrowService.deleteBorrow(id);
    }

    @PutMapping("/{id}/return")
    public Borrow updateReturnDate(
            @PathVariable Long id,
            @RequestParam String date
    ) {
        LocalDate returnDate = LocalDate.parse(date);
        return borrowService.updateReturnDate(id, returnDate);
    }
}