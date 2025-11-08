package com.example.SmartLibrary.model;

import jakarta.persistence.*;

@Entity
@Table(name = "books")
public class Book {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "book_id")
    private Long id;

    @Column(nullable = false, length = 255)
    private String title;

    @Column(nullable = false, length = 255)
    private String author;

    @Column(length = 255)
    private String isbn;

    @Column(length = 255)
    private String category;

    @Column(name = "year_published")
    private int publicationYear;

    @Column(nullable = false)
    private boolean available = true;

    @Column(name = "image_url")
    private String imageUrl;

    // Constructors
    public Book() {}

    public Book(String title, String author, String isbn, String category, int publicationYear) {
        this.title = title;
        this.author = author;
        this.isbn = isbn;
        this.category = category;
        this.publicationYear = publicationYear;
        this.available = true;
    }

    // Getters and Setters
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }

    public String getAuthor() { return author; }
    public void setAuthor(String author) { this.author = author; }

    public String getIsbn() { return isbn; }
    public void setIsbn(String isbn) { this.isbn = isbn; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getPublicationYear() { return publicationYear; }
    public void setPublicationYear(int publicationYear) { this.publicationYear = publicationYear; }

    public boolean isAvailable() { return available; }
    public void setAvailable(boolean available) { this.available = available; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    // ADD THESE TWO METHODS FOR BORROW COMPATIBILITY
    public String getStatus() {
        return available ? "available" : "borrowed";
    }

    public void setStatus(String status) {
        this.available = "available".equalsIgnoreCase(status);
    }
}