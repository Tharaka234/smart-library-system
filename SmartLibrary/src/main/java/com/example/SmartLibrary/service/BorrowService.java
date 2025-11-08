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

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

@Service
public class BorrowService {

    @Autowired private BookRepository bookRepository;
    @Autowired private BorrowRepository borrowRepository;
    @Autowired private UserRepository userRepository;
    @Autowired private JavaMailSender mailSender;

    // User borrows a book
    public Borrow addBorrow(Borrow borrow) {
        Book book = borrow.getBook();
        if (book == null || book.getId() == null)
            throw new RuntimeException("Book information is missing");

        Optional<Book> bookOpt = bookRepository.findById(book.getId());
        if (bookOpt.isPresent()) {
            Book foundBook = bookOpt.get();
            if (!"available".equalsIgnoreCase(foundBook.getStatus()))
                throw new RuntimeException("Book is not available");
            foundBook.setStatus("borrowed");
            bookRepository.save(foundBook);
        }

        Borrow savedBorrow = borrowRepository.save(borrow);

        // Send borrow confirmation email
        sendBorrowConfirmationEmail(savedBorrow);

        return savedBorrow;
    }

    // User returns a book
    public Borrow updateReturnDate(Long id, LocalDate returnDate) {
        Borrow borrow = borrowRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Borrow record not found"));
        borrow.setReturnDate(returnDate);
        borrow.setReturned(true);

        Book book = borrow.getBook();
        if (book != null) {
            book.setStatus("available");
            bookRepository.save(book);
        }

        Borrow updatedBorrow = borrowRepository.save(borrow);

        // Send return confirmation email
        sendReturnConfirmationEmail(updatedBorrow);

        return updatedBorrow;
    }

    // Admin view/filter
    public List<Borrow> filterBorrows(Long userId, Long bookId) {
        if (userId != null) return borrowRepository.findByUser_Id(userId);
        if (bookId != null) return borrowRepository.findByBook_Id(bookId);
        return borrowRepository.findAll();
    }

    public void deleteBorrow(Long id) {
        borrowRepository.deleteById(id);
    }

    // Currently Overdue - ONLY books NOT returned but past due
    public List<Borrow> getOverdueBorrowsByDueDate() {
        LocalDate today = LocalDate.now();
        return borrowRepository.findAll().stream()
                .filter(b -> b.getReturnDate() == null &&
                        b.getDueDate() != null &&
                        b.getDueDate().isBefore(today))
                .toList();
    }

    // Returned Late - ONLY books returned after due date
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
        long active = allBorrows.stream().filter(b -> b.getReturnDate() == null).count();
        long returned = allBorrows.stream().filter(b -> b.getReturnDate() != null).count();
        long overdue = allBorrows.stream()
                .filter(b -> b.getReturnDate() == null && b.getDueDate() != null && b.getDueDate().isBefore(LocalDate.now()))
                .count();

        Map<String, Long> stats = new HashMap<>();
        stats.put("total", total);
        stats.put("active", active);
        stats.put("returned", returned);
        stats.put("overdue", overdue);

        // Send weekly report to admin if it's Monday
        if (LocalDate.now().getDayOfWeek().getValue() == 1) { // Monday
            sendWeeklyReport(stats);
        }

        return stats;
    }

    // ========================= EMAIL METHODS =========================

    // 1. Borrow Confirmation Email
    public void sendBorrowConfirmationEmail(Borrow borrow) {  // Changed to PUBLIC
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
    }

    // 2. Return Confirmation Email
    public void sendReturnConfirmationEmail(Borrow borrow) {  // Changed to PUBLIC
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
    }

    // 3. Weekly Report to Admin
    public void sendWeeklyReport(Map<String, Long> stats) {  // Changed to PUBLIC
        // Get admin emails (you can hardcode or get from database)
        String adminEmail = "tharakaisuru2000@gmail.com"; // Or get from UserRepository where role=ADMIN

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
    }

    // 4. System Alert to Admin
    public void sendSystemAlert(String alertMessage) {  // Already PUBLIC
        String adminEmail = "tharakaisuru2000@gmail.com";

        String subject = "🔔 System Alert - SmartLibrary";
        String text = "Admin Alert:\n\n" +
                alertMessage + "\n\n" +
                "Time: " + LocalDate.now() + " " + java.time.LocalTime.now() + "\n\n" +
                "SmartLibrary System";

        sendEmail(adminEmail, subject, text);
    }

    // 5. Due Date Reminders (Existing - Enhanced)
    @Scheduled(fixedRate = 10000)
    public void sendDueDateReminders() {
        List<Borrow> allBorrows = borrowRepository.findAll();
        LocalDate today = LocalDate.now();

        for (Borrow borrow : allBorrows) {
            if (borrow.getReturnDate() == null && borrow.getDueDate() != null) {
                long daysUntilDue = ChronoUnit.DAYS.between(today, borrow.getDueDate());
                String userEmail = borrow.getUser().getEmail();
                String userName = borrow.getUser().getName();
                String bookTitle = borrow.getBook().getTitle();

                if (daysUntilDue == 0) {
                    sendDueTodayReminder(borrow);
                } else if (daysUntilDue == 1) {
                    sendDueTomorrowReminder(borrow);
                } else if (daysUntilDue < 0) {
                    sendOverdueAlert(borrow, Math.abs(daysUntilDue));
                }
            }
        }
    }

    // Due Today Reminder
    public void sendDueTodayReminder(Borrow borrow) {  // Changed to PUBLIC
        String userEmail = borrow.getUser().getEmail();
        String userName = borrow.getUser().getName();
        String bookTitle = borrow.getBook().getTitle();

        String subject = "⏰ Book Due TODAY - SmartLibrary";
        String text = "Dear " + userName + ",\n\n" +
                "The book \"" + bookTitle + "\" is due TODAY (" + borrow.getDueDate() + ").\n\n" +
                "Please return it today to avoid late fees.\n\n" +
                "SmartLibrary Team";

        sendEmail(userEmail, subject, text);
    }

    // Due Tomorrow Reminder
    public void sendDueTomorrowReminder(Borrow borrow) {  // Changed to PUBLIC
        String userEmail = borrow.getUser().getEmail();
        String userName = borrow.getUser().getName();
        String bookTitle = borrow.getBook().getTitle();

        String subject = "📅 Book Due Tomorrow - SmartLibrary";
        String text = "Dear " + userName + ",\n\n" +
                "The book \"" + bookTitle + "\" is due TOMORROW (" + borrow.getDueDate() + ").\n\n" +
                "Please prepare to return it tomorrow.\n\n" +
                "SmartLibrary Team";

        sendEmail(userEmail, subject, text);
    }

    // Overdue Alert
    public void sendOverdueAlert(Borrow borrow, long overdueDays) {  // Changed to PUBLIC
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

        // Also send alert to admin for overdue books
        sendSystemAlert("Overdue Book Alert:\n" +
                "User: " + userName + " (" + userEmail + ")\n" +
                "Book: " + bookTitle + "\n" +
                "Due Date: " + borrow.getDueDate() + "\n" +
                "Overdue Days: " + overdueDays);
    }

    // Generic Email Sender
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
//email service covered
/*
Borrow Confirmation
Return Confirmation
Due Today Reminder
Due Tomorrow Reminder
Overdue Alert
System Alert
Weekly Report
 */