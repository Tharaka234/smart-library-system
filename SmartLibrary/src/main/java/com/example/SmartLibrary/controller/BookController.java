package com.example.SmartLibrary.controller;

import com.example.SmartLibrary.model.Book;
import com.example.SmartLibrary.model.Notification;
import com.example.SmartLibrary.repository.BookRepository;
import com.example.SmartLibrary.repository.NotificationRepository;
import com.example.SmartLibrary.dto.BorrowRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @Autowired
    private NotificationRepository notificationRepository;

    // Search with list
    @GetMapping
    public List<Book> getAll(@RequestParam(defaultValue = "") String search) {
        if (search.isEmpty()) return bookRepository.findAll();
        return bookRepository.findByTitleContainingIgnoreCase(search);
    }

    // Add new book
    @PostMapping
    public ResponseEntity<String> addBook(@RequestBody Book book) {
        if (book.getAuthor() == null || book.getAuthor().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Author is required");
        }
        try {
            Book saved = bookRepository.save(book);

            // Create notification for all users (userId null => broadcast)
            String msg = "New book added: " + (saved.getTitle() != null ? saved.getTitle() : "Untitled");
            Notification n = new Notification();
            n.setMessage(msg);
            n.setCreatedAt(LocalDateTime.now());
            n.setUserId(null);
            notificationRepository.save(n);
            System.out.println("[Notification] " + msg);

            return ResponseEntity.ok("Book added successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error: " + e.getMessage());
        }
    }

    // Update existing book
    @PutMapping("/{id}")
    public ResponseEntity<String> updateBook(@PathVariable Long id, @RequestBody Book updatedBook) {
        return bookRepository.findById(id).map(book -> {
            book.setTitle(updatedBook.getTitle());
            book.setAuthor(updatedBook.getAuthor());
            book.setCategory(updatedBook.getCategory());
            book.setPublicationYear(updatedBook.getPublicationYear());
            book.setImageUrl(updatedBook.getImageUrl());
            bookRepository.save(book);

            // Notification
            String msg = "Book updated: " + (book.getTitle() != null ? book.getTitle() : ("ID " + id));
            Notification n = new Notification();
            n.setMessage(msg);
            n.setCreatedAt(LocalDateTime.now());
            n.setUserId(null);
            notificationRepository.save(n);
            System.out.println("[Notification] " + msg);

            return ResponseEntity.ok("Book updated successfully!");
        }).orElseGet(() -> ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found!"));
    }

    // Delete a book
    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteBook(@PathVariable Long id) {
        if (!bookRepository.existsById(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("Book not found!");
        }
        try {
            Book book = bookRepository.findById(id).orElse(null);
            String title = (book != null && book.getTitle() != null) ? book.getTitle() : ("ID " + id);

            bookRepository.deleteById(id);

            // Notification
            String msg = "Book deleted: " + title;
            Notification n = new Notification();
            n.setMessage(msg);
            n.setCreatedAt(LocalDateTime.now());
            n.setUserId(null);
            notificationRepository.save(n);
            System.out.println("[Notification] " + msg);

            return ResponseEntity.ok("Book deleted successfully!");
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Delete failed: " + e.getMessage());
        }
    }

    // Upload photo endpoint
    @PostMapping("/upload")
    public String uploadImage(@RequestParam("file") MultipartFile file) {
        try {
            String uploadDir = new File("src/main/resources/static/uploads").getAbsolutePath();
            File dir = new File(uploadDir);
            if (!dir.exists()) dir.mkdirs();

            String fileName = System.currentTimeMillis() + "_" + file.getOriginalFilename();
            Path path = Paths.get(uploadDir, fileName);
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            return "http://localhost:8081/uploads/" + fileName;
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }

    // Borrow book endpoint
    @PostMapping("/borrow")
    public String borrowBook(@RequestBody BorrowRequest request) {
        if (request.getBookId() == null || request.getBorrowDate() == null || request.getReturnDate() == null) {
            return "Missing required fields!";
        }

        if (!bookRepository.existsById(request.getBookId())) {
            return "Book not found!";
        }

        System.out.println("Borrowed book ID: " + request.getBookId());
        System.out.println("From: " + request.getBorrowDate() + " To: " + request.getReturnDate());

        return "Book borrowed successfully!";
    }
}
