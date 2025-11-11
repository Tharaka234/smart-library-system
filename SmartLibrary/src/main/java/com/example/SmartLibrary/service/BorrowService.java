package com.example.SmartLibrary.service;

import com.example.SmartLibrary.model.Book;
import com.example.SmartLibrary.model.Borrow;
import com.example.SmartLibrary.model.User;
import com.example.SmartLibrary.repository.BookRepository;
import com.example.SmartLibrary.repository.BorrowRepository;
import com.example.SmartLibrary.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
@Transactional
public class BorrowService {

    @Autowired private BookRepository bookRepository;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JavaMailSender mailSender;

    public Borrow addBorrow(Borrow borrow) {
        // Validate user exists
        User user = userRepository.findById(borrow.getUser().getId())
                .orElseThrow(() -> new RuntimeException("User not found with ID: " + borrow.getUser().getId()));

        // Validate book exists
        Book book = bookRepository.findById(borrow.getBook().getId())
                .orElseThrow(() -> new RuntimeException("Book not found with ID: " + borrow.getBook().getId()));

        // Check if book is available
        if (!book.isAvailable()) {
            throw new RuntimeException("Book '" + book.getTitle() + "' is not available for borrowing");
        }

        // Update book availability
        book.setAvailable(false);
        bookRepository.save(book);

        // Set the managed entities
        borrow.setUser(user);
        borrow.setBook(book);

        // ✅ Automatically handle borrowDate and dueDate
        LocalDate borrowDate = borrow.getBorrowDate() != null ? borrow.getBorrowDate() : LocalDate.now();
        LocalDate dueDate = borrow.getDueDate();

        // If due date not provided or before borrow date, set 14 days ahead
        if (dueDate == null || dueDate.isBefore(borrowDate)) {
            dueDate = borrowDate.plusDays(14);
        }

        borrow.setBorrowDate(borrowDate);
        borrow.setDueDate(dueDate);
        borrow.setReturned(false);

        Borrow savedBorrow = borrowRepository.save(borrow);

        // Send confirmation email
        sendBorrowConfirmationEmail(savedBorrow);

        return savedBorrow;
    }


    public Borrow updateReturnDate(Long id, LocalDate returnDate) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found with ID: " + id));

        if (borrow.isReturned()) {
            throw new RuntimeException("Book already returned");
        }

        borrow.setReturnDate(returnDate);
        borrow.setReturned(true);

        // Make book available again
        Book book = borrow.getBook();
        book.setAvailable(true);
        bookRepository.save(book);

        Borrow updatedBorrow = borrowRepository.save(borrow);

        // Send return confirmation email
        sendReturnConfirmationEmail(updatedBorrow);

        return updatedBorrow;
    }

    public List<Borrow> filterBorrows(Long userId, Long bookId) {
        if (userId != null && bookId != null) {
            return borrowRepository.findAll().stream()
                    .filter(b -> b.getUser().getId().equals(userId) && b.getBook().getId().equals(bookId))
                    .toList();
        } else if (userId != null) {
            return borrowRepository.findByUserId(userId);
        } else if (bookId != null) {
            return borrowRepository.findByBookId(bookId);
        }
        return borrowRepository.findAll();
    }

    public void deleteBorrow(Long id) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found with ID: " + id));

        // If book wasn't returned, make it available again
        if (!borrow.isReturned()) {
            Book book = borrow.getBook();
            book.setAvailable(true);
            bookRepository.save(book);
        }

        borrowRepository.deleteById(id);
    }

    public List<Borrow> getOverdueBorrowsByDueDate() {
        LocalDate today = LocalDate.now();
        return borrowRepository.findByReturnDateIsNullAndDueDateBefore(today);
    }

    public List<Borrow> getOverdueBorrowsByReturnDate() {
        return borrowRepository.findAll().stream()
                .filter(b -> b.getReturnDate() != null &&
                        b.getDueDate() != null &&
                        b.getReturnDate().isAfter(b.getDueDate()))
                .toList();
    }

    public Map<String, Long> getBorrowStats() {
        List<Borrow> allBorrows = borrowRepository.findAll();
        long total = allBorrows.size();
        long active = allBorrows.stream().filter(b -> !b.isReturned()).count();
        long returned = allBorrows.stream().filter(Borrow::isReturned).count();
        long overdue = getOverdueBorrowsByDueDate().size();

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("returned", returned);
        stats.put("overdue", overdue);

        // Send weekly report on Monday
        if (LocalDate.now().getDayOfWeek().getValue() == 1) {
            sendWeeklyReport(stats);
        }

        return stats;
    }

    // ========================= EMAIL METHODS =========================
    public void sendBorrowConfirmationEmail(Borrow borrow) {
        try {
            String userEmail = borrow.getUser().getEmail();
            String userName = borrow.getUser().getName();
            String bookTitle = borrow.getBook().getTitle();

            String subject = "📚 Book Borrowed Successfully - SmartLibrary";
            String text = "Dear " + userName + ",\n\n" +
                    "You have successfully borrowed the following book:\n\n" +
                    "📖 Book: " + bookTitle + "\n" +
                    "📅 Borrow Date: " + borrow.getBorrowDate() + "\n" +
                    "⏰ Due Date: " + borrow.getDueDate() + "\n\n" +
                    "Please return the book by the due date to avoid late fees.\n\n" +
                    "Thank you for using SmartLibrary!\n\n" +
                    "Best regards,\n" +
                    "SmartLibrary Team";

            sendEmail(userEmail, subject, text);
        } catch (Exception e) {
            System.out.println("❌ Failed to send borrow confirmation: " + e.getMessage());
        }
    }

    public void sendReturnConfirmationEmail(Borrow borrow) {
        try {
            String userEmail = borrow.getUser().getEmail();
            String userName = borrow.getUser().getName();
            String bookTitle = borrow.getBook().getTitle();

            String subject = "✅ Book Returned Successfully - SmartLibrary";
            String text = "Dear " + userName + ",\n\n" +
                    "You have successfully returned the following book:\n\n" +
                    "📖 Book: " + bookTitle + "\n" +
                    "📅 Return Date: " + borrow.getReturnDate() + "\n\n" +
                    "Thank you for returning the book on time!\n\n" +
                    "We look forward to serving you again.\n\n" +
                    "Best regards,\n" +
                    "SmartLibrary Team";

            sendEmail(userEmail, subject, text);
        } catch (Exception e) {
            System.out.println("❌ Failed to send return confirmation: " + e.getMessage());
        }
    }

    public void sendWeeklyReport(Map<String, Long> stats) {
        try {
            String adminEmail = "tharakaisuru2000@gmail.com";

            String subject = "📊 Weekly Library Report - SmartLibrary";
            String text = "Weekly Library Statistics Report\n\n" +
                    "📈 Borrow Statistics:\n" +
                    "• Total Borrows: " + stats.get("total") + "\n" +
                    "• Active Borrows: " + stats.get("active") + "\n" +
                    "• Returned Books: " + stats.get("returned") + "\n" +
                    "• Overdue Books: " + stats.get("overdue") + "\n\n" +
                    "Report Period: " + LocalDate.now().minusDays(7) + " to " + LocalDate.now() + "\n\n" +
                    "SmartLibrary System\n" +
                    "Automated Report";

            sendEmail(adminEmail, subject, text);
        } catch (Exception e) {
            System.out.println("❌ Failed to send weekly report: " + e.getMessage());
        }
    }

    @Scheduled(fixedRate = 300000) // Run every 5 minutes
    public void sendDueDateReminders() {
        try {
            List<Borrow> activeBorrows = borrowRepository.findByReturnedFalse();
            LocalDate today = LocalDate.now();

            for (Borrow borrow : activeBorrows) {
                if (borrow.getDueDate() != null) {
                    long daysUntilDue = ChronoUnit.DAYS.between(today, borrow.getDueDate());

                    if (daysUntilDue == 0) {
                        sendDueTodayReminder(borrow);
                    } else if (daysUntilDue == 1) {
                        sendDueTomorrowReminder(borrow);
                    } else if (daysUntilDue < 0) {
                        sendOverdueAlert(borrow, Math.abs(daysUntilDue));
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("❌ Error in due date reminders: " + e.getMessage());
        }
    }

    public void sendDueTodayReminder(Borrow borrow) {
        try {
            String userEmail = borrow.getUser().getEmail();
            String userName = borrow.getUser().getName();
            String bookTitle = borrow.getBook().getTitle();

            String subject = "⏰ Book Due TODAY - SmartLibrary";
            String text = "Dear " + userName + ",\n\n" +
                    "The book \"" + bookTitle + "\" is due TODAY (" + borrow.getDueDate() + ").\n\n" +
                    "Please return it today to avoid late fees.\n\n" +
                    "SmartLibrary Team";

            sendEmail(userEmail, subject, text);
        } catch (Exception e) {
            System.out.println("❌ Failed to send due today reminder: " + e.getMessage());
        }
    }

    public void sendDueTomorrowReminder(Borrow borrow) {
        try {
            String userEmail = borrow.getUser().getEmail();
            String userName = borrow.getUser().getName();
            String bookTitle = borrow.getBook().getTitle();

            String subject = "📅 Book Due Tomorrow - SmartLibrary";
            String text = "Dear " + userName + ",\n\n" +
                    "The book \"" + bookTitle + "\" is due TOMORROW (" + borrow.getDueDate() + ").\n\n" +
                    "Please prepare to return it tomorrow.\n\n" +
                    "SmartLibrary Team";

            sendEmail(userEmail, subject, text);
        } catch (Exception e) {
            System.out.println("❌ Failed to send due tomorrow reminder: " + e.getMessage());
        }
    }

    public void sendOverdueAlert(Borrow borrow, long overdueDays) {
        try {
            String userEmail = borrow.getUser().getEmail();
            String userName = borrow.getUser().getName();
            String bookTitle = borrow.getBook().getTitle();

            String subject = "❌ OVERDUE Book - SmartLibrary";
            String text = "Dear " + userName + ",\n\n" +
                    "URGENT: The book \"" + bookTitle + "\" was due on " + borrow.getDueDate() +
                    " and is now " + overdueDays + " days OVERDUE.\n\n" +
                    "Please return it IMMEDIATELY to avoid additional fees.\n\n" +
                    "SmartLibrary Team";

            sendEmail(userEmail, subject, text);

            // Also send alert to admin
            sendSystemAlert("Overdue Book Alert:\n" +
                    "User: " + userName + " (" + userEmail + ")\n" +
                    "Book: " + bookTitle + "\n" +
                    "Due Date: " + borrow.getDueDate() + "\n" +
                    "Overdue Days: " + overdueDays);
        } catch (Exception e) {
            System.out.println("❌ Failed to send overdue alert: " + e.getMessage());
        }
    }

    public void sendSystemAlert(String alertMessage) {
        try {
            String adminEmail = "tharakaisuru2000@gmail.com";

            String subject = "🔔 System Alert - SmartLibrary";
            String text = "Admin Alert:\n\n" +
                    alertMessage + "\n\n" +
                    "Time: " + LocalDate.now() + " " + java.time.LocalTime.now() + "\n\n" +
                    "SmartLibrary System";

            sendEmail(adminEmail, subject, text);
        } catch (Exception e) {
            System.out.println("❌ Failed to send system alert: " + e.getMessage());
        }
    }

    private void sendEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            mailSender.send(message);
            System.out.println("✅ Email sent to: " + to + " - " + subject);
        } catch (Exception e) {
            System.out.println("❌ Failed to send email to " + to + ": " + e.getMessage());
        }
    }
}