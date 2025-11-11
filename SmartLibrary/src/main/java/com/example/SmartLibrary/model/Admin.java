package com.example.SmartLibrary.model;
import jakarta.persistence.*;

@Entity
@Table(name = "admins", uniqueConstraints = @UniqueConstraint(columnNames = "username"))
public class Admin {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true) // enforce unique username
    private String username;

    @Column(nullable = false)
    private String password;

    private String email;

    public Admin() {}

    public Admin(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    // --- Getters and Setters ---
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
}
