package com.example.smartlibrary.service;

import com.example.smartlibrary.model.Borrow;
import com.example.smartlibrary.model.Book;
import com.example.smartlibrary.repository.BorrowRepository;
import com.example.smartlibrary.repository.BookRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    @Autowired
    private BookRepository bookRepository;

    // Add new borrow record
    public Borrow addBorrow(Borrow borrow) {
        Book book = borrow.getBook();
        if (book == null || book.getId() == null) {
            throw new RuntimeException("Book information is missing");
        }

        Optional<Book> bookOpt = bookRepository.findById(book.getId());
        if (bookOpt.isPresent()) {
            Book foundBook = bookOpt.get();
            if (!"available".equalsIgnoreCase(foundBook.getStatus())) {
                throw new RuntimeException("Book is not available");
            }
            foundBook.setStatus("borrowed");
            bookRepository.save(foundBook);
        }

        return borrowRepository.save(borrow);
    }

    // View all borrow records
    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }

    // Delete a borrow record
    public void deleteBorrow(Long id) {
        borrowRepository.deleteById(id);
    }

    // Update return date / mark as returned
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

        return borrowRepository.save(borrow);
    }

    // Filter borrow records by userId or bookId
    public List<Borrow> filterBorrows(Long userId, Long bookId) {
        if (userId != null) {
            return borrowRepository.findByUser_Id(userId);
        } else if (bookId != null) {
            return borrowRepository.findByBook_Id(bookId);
        }
        return borrowRepository.findAll();
    }
}