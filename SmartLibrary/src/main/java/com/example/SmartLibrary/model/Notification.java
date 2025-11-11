package com.example.SmartLibrary.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "notifications")
public class Notification {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
    private String message;

    @Column(name = "user_id")
    private Long userId;

    private String type; //
    private String action; //

    @Column(name = "related_book_id")
    private Long relatedBookId; //

    @Column(name = "is_read")
    private boolean isRead = false;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(name = "created_at")
    private LocalDateTime createdAt = LocalDateTime.now();

    // Default constructor
    public Notification() {}

    // Constructor for broadcast notifications (all users)
    public Notification(String title, String message, String type, String action, Long relatedBookId) {
        this.title = title;
        this.message = message;
        this.type = type;
        this.action = action;
        this.relatedBookId = relatedBookId;
        this.userId = null; // NULL means all users
    }

    // Constructor for user-specific notifications
    public Notification(String title, String message, Long userId, String type, String action, Long relatedBookId) {
        this.title = title;
        this.message = message;
        this.userId = userId;
        this.type = type;
        this.action = action;
        this.relatedBookId = relatedBookId;
    }

    // Fix JSON serialization for isRead
    @JsonProperty("isRead")
    public boolean isRead() {
        return isRead;
    }

    public void setRead(boolean read) {
        isRead = read;
    }
}