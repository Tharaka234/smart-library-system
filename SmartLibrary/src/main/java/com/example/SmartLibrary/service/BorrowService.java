package com.example.SmartLibrary.service;

import com.example.smartlibrary.model.Borrow;
import com.example.smartlibrary.repository.BorrowRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class BorrowService {

    @Autowired
    private BorrowRepository borrowRepository;

    public Borrow saveBorrow(Borrow borrow) {
        return borrowRepository.save(borrow);
    }

    public List<Borrow> getAllBorrows() {
        return borrowRepository.findAll();
    }

    public void deleteBorrow(Long id) {
        borrowRepository.deleteById(id);
    }
}
