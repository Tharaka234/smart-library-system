git statuspackage com.example.SmartLibrary.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
public class Borrow {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private LocalDate borrowDate;
    private LocalDate dueDate;       //  Added this field
    private LocalDate returnDate;
    private boolean returned;

    @ManyToOne
    @JoinColumn(name = "book_id", nullable = false)
    private Book book;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    // Convenience methods
    public Long getBookId() {
        return book != null ? book.getId() : null;
    }

    public Long getUserId() {
        return user != null ? user.getId() : null;
    }
}