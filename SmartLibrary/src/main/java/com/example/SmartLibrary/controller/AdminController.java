package com.example.SmartLibrary.controller;

import com.example.SmartLibrary.model.Book;
import com.example.SmartLibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private NotificationService notificationService;

    // Get all books
    @GetMapping("/books")
    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    // Add book
    @PostMapping("/add-book")
    public Book addBook(@RequestBody Book book) {
        return bookRepository.save(book);
    }

    // Delete book and notify users
    @DeleteMapping("/delete-book/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.badRequest().body("Book not found");
        }
        bookRepository.deleteById(id);
        notificationService.sendNotificationToAllUsers("Admin deleted a book (ID: " + id + ")");
        return ResponseEntity.ok("Book deleted and users notified");
    }
}
