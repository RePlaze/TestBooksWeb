package com.example.bookservice.repository;

import com.example.bookservice.model.Book;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BookRepository extends JpaRepository<Book, Long> {
    Page<Book> findByTitleContainingIgnoreCase(String title, Pageable pageable);
    Page<Book> findByTitleContainingIgnoreCaseAndBrandContainingIgnoreCase(String title, String brand, Pageable pageable);
    Page<Book> findByTitleContainingIgnoreCaseAndBrandContainingIgnoreCaseAndYear(String title, String brand, Integer year, Pageable pageable);
} 