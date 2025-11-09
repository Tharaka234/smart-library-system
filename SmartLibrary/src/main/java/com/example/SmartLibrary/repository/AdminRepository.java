package com.example.SmartLibrary.repository;


import com.example.SmartLibrary.model.Admin;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminRepository extends JpaRepository<Admin, Long> {
    Admin findByUsername(String username); // still valid, will now return a single result because username is unique
}
