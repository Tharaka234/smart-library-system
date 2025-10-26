package com.example.SmartLibrary.controller;

import com.example.SmartLibrary.model.Book;
import com.example.SmartLibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/books")
@CrossOrigin(origins = "*")
public class BookController {

    @Autowired
    private BookRepository bookRepository;

    @GetMapping
    public List<Book> getAll(@RequestParam(defaultValue = "") String search) {
        if (search.isEmpty()) return bookRepository.findAll();
        return bookRepository.findByTitleContainingIgnoreCase(search);
    }

    @PostMapping
    public String addBook(@RequestBody Book book) {
        bookRepository.save(book);
        return "Book added successfully!";
    }

    @DeleteMapping("/{id}")
    public String deleteBook(@PathVariable Long id) {
        bookRepository.deleteById(id);
        return "Book deleted!";
    }
}

