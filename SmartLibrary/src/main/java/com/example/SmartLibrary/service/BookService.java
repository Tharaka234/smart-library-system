package com.example.SmartLibrary.service;

import com.example.SmartLibrary.model.Book;
import com.example.SmartLibrary.repository.BookRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BookService {

    private final BookRepository bookRepository;
    private final NotificationService notificationService;

    public BookService(BookRepository bookRepository, NotificationService notificationService) {
        this.bookRepository = bookRepository;
        this.notificationService = notificationService;
    }

    public List<Book> getAllBooks() {
        return bookRepository.findAll();
    }

    public Book getBookById(Long id) {
        return bookRepository.findById(id).orElse(null);
    }

    @Transactional
    public Book saveBook(Book book) {
        boolean isNew = book.getId() == null;
        Book savedBook = bookRepository.save(book);

        if (isNew) {
            notificationService.notifyBookAction("added", savedBook.getTitle(), savedBook.getId(), getCurrentAdminId());
        } else {
            notificationService.notifyBookAction("updated", savedBook.getTitle(), savedBook.getId(), getCurrentAdminId());
        }

        return savedBook;
    }

    @Transactional
    public void deleteBook(Long id) {
        Book book = bookRepository.findById(id).orElse(null);
        if (book != null) {
            String bookTitle = book.getTitle();
            bookRepository.deleteById(id);
            notificationService.notifyBookAction("deleted", bookTitle, id, getCurrentAdminId());
        }
    }

    @Transactional
    public Book createBook(Book book) {
        Book savedBook = bookRepository.save(book);
        notificationService.notifyBookAction("added", savedBook.getTitle(), savedBook.getId(), getCurrentAdminId());
        return savedBook;
    }

    // Update existing book - FIXED VERSION
    @Transactional
    public Book updateBook(Long id, Book bookDetails) {
        Book existingBook = bookRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Book not found with id: " + id));

        // Update book fields - publicationYear is always updated since it's primitive
        if (bookDetails.getTitle() != null) {
            existingBook.setTitle(bookDetails.getTitle());
        }
        if (bookDetails.getAuthor() != null) {
            existingBook.setAuthor(bookDetails.getAuthor());
        }
        if (bookDetails.getCategory() != null) {
            existingBook.setCategory(bookDetails.getCategory());
        }
        // For primitive int, we don't check for null - always update
        existingBook.setPublicationYear(bookDetails.getPublicationYear());

        if (bookDetails.getImageUrl() != null) {
            existingBook.setImageUrl(bookDetails.getImageUrl());
        }

        Book updatedBook = bookRepository.save(existingBook);
        notificationService.notifyBookAction("updated", updatedBook.getTitle(), id, getCurrentAdminId());
        return updatedBook;
    }

    // Search books by title, author, or category
    public List<Book> searchBooks(String searchTerm) {
        if (searchTerm == null || searchTerm.trim().isEmpty()) {
            return bookRepository.findAll();
        }
        return bookRepository.findByTitleContainingIgnoreCaseOrAuthorContainingIgnoreCaseOrCategoryContainingIgnoreCase(
                searchTerm, searchTerm, searchTerm);
    }

    // Helper method to get current admin ID
    private Long getCurrentAdminId() {
        return 1L; // Default admin ID
    }
}