package com.example.SmartLibrary.controller;

import com.example.SmartLibrary.model.Admin;
import com.example.SmartLibrary.model.Book;
import com.example.SmartLibrary.model.Notification;
import com.example.SmartLibrary.repository.AdminRepository;
import com.example.SmartLibrary.repository.BookRepository;
import com.example.SmartLibrary.repository.NotificationRepository;
import com.example.SmartLibrary.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@CrossOrigin(origins = "*")
public class AdminController {

    @Autowired private BookRepository bookRepository;
    @Autowired private NotificationService notificationService;
    @Autowired private NotificationRepository notificationRepository;
    @Autowired private AdminRepository adminRepository;

    // 📚 Get all books
    @GetMapping("/books")
    public ResponseEntity<List<Book>> getAllBooks() {
        return ResponseEntity.ok(bookRepository.findAll());
    }

    // ❌ REMOVED DUPLICATE ADD BOOK ENDPOINT - Now only in BookController
    // This prevents conflict with /api/books POST endpoint

    // ✏️ Edit book
    @PutMapping("/books/{id}")
    public ResponseEntity<?> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id)
                .map(book -> {
                    book.setTitle(updatedBook.getTitle());
                    book.setAuthor(updatedBook.getAuthor());
                    book.setCategory(updatedBook.getCategory());
                    book.setIsbn(updatedBook.getIsbn());
                    book.setPublicationYear(updatedBook.getPublicationYear());
                    bookRepository.save(book);
                    return ResponseEntity.ok("Book updated successfully!");
                })
                .orElse(ResponseEntity.badRequest().body("Book not found"));
    }

    // ❌ Delete book
    @DeleteMapping("/books/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        return bookRepository.findById(id)
                .map(book -> {
                    bookRepository.delete(book);
                    notificationService.sendNotificationToAllUsers("❌ Book deleted: " + book.getTitle());
                    return ResponseEntity.ok("Book deleted and users notified!");
                })
                .orElse(ResponseEntity.badRequest().body("Book not found"));
    }

    // 🔔 Send notification manually
    @PostMapping("/notifyAll")
    public ResponseEntity<String> sendCustomNotification(@RequestBody Notification notification) {
        if (notification.getMessage() == null || notification.getMessage().isEmpty()) {
            return ResponseEntity.badRequest().body("Message cannot be empty");
        }
        notificationService.sendNotificationToAllUsers(notification.getMessage());
        return ResponseEntity.ok("Notification sent to all users!");
    }

    // 🧾 Get all notifications
    @GetMapping("/notifications")
    public ResponseEntity<List<Notification>> getAllNotifications() {
        return ResponseEntity.ok(notificationRepository.findAllSorted());
    }

    // 👤 Create admin (registration)
    @PostMapping("/create-admin")
    public ResponseEntity<?> createAdmin(@RequestBody Admin admin) {
        try {
            if (admin == null ||
                    admin.getUsername() == null || admin.getUsername().isEmpty() ||
                    admin.getPassword() == null || admin.getPassword().isEmpty()) {
                return ResponseEntity.badRequest().body("Username and Password are required");
            }

            if (adminRepository.findByUsername(admin.getUsername()) != null) {
                return ResponseEntity.badRequest().body("Username already exists");
            }

            Admin saved = adminRepository.save(admin);
            return ResponseEntity.ok(saved);

        } catch (DataIntegrityViolationException e) {
            return ResponseEntity.status(409).body("Duplicate username or invalid data");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }

    // 🔑 Admin login
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Admin loginRequest) {
        try {
            if (loginRequest == null || loginRequest.getUsername() == null || loginRequest.getPassword() == null) {
                return ResponseEntity.badRequest().body("Username and password are required");
            }

            Admin admin = adminRepository.findByUsername(loginRequest.getUsername());
            if (admin == null || !admin.getPassword().equals(loginRequest.getPassword())) {
                return ResponseEntity.status(401).body("Invalid credentials");
            }

            return ResponseEntity.ok("Login successful");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Server error: " + e.getMessage());
        }
    }
}