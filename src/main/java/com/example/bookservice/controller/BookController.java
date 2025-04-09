package com.example.bookservice.controller;

import com.example.bookservice.exception.ResourceNotFoundException;
import com.example.bookservice.model.Book;
import com.example.bookservice.service.BookService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Locale;

@Slf4j
@RestController
@RequestMapping("/api/books")
@RequiredArgsConstructor
public class BookController {

    private final BookService bookService;
    private final MessageSource messageSource;

    @GetMapping
    public ResponseEntity<List<Book>> getAllBooks() {
        log.debug("Получение списка всех книг");
        return ResponseEntity.ok(bookService.getAllBooks());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Book> getBookById(@PathVariable Long id, Locale locale) {
        log.debug("Получение книги по ID: {}", id);
        try {
            Book book = bookService.getBookById(id);
            return ResponseEntity.ok(book);
        } catch (ResourceNotFoundException e) {
            log.warn("Книга с ID {} не найдена", id);
            throw new ResourceNotFoundException(
                    messageSource.getMessage("book.not.found", new Object[]{id}, locale));
        }
    }

    @PostMapping
    public ResponseEntity<Book> createBook(@RequestBody Book book, Locale locale) {
        log.debug("Создание новой книги: {}", book);
        try {
            return ResponseEntity.ok(bookService.createBook(book));
        } catch (Exception e) {
            log.error("Ошибка при создании книги: {}", e.getMessage());
            throw new IllegalArgumentException(
                    messageSource.getMessage("book.create.error", null, locale));
        }
    }

    @PutMapping("/{id}")
    public ResponseEntity<Book> updateBook(@PathVariable Long id, @RequestBody Book book, Locale locale) {
        log.debug("Обновление книги с ID {}: {}", id, book);
        try {
            return ResponseEntity.ok(bookService.updateBook(id, book));
        } catch (ResourceNotFoundException e) {
            log.warn("Книга с ID {} не найдена при обновлении", id);
            throw new ResourceNotFoundException(
                    messageSource.getMessage("book.not.found", new Object[]{id}, locale));
        } catch (Exception e) {
            log.error("Ошибка при обновлении книги: {}", e.getMessage());
            throw new IllegalArgumentException(
                    messageSource.getMessage("book.update.error", null, locale));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteBook(@PathVariable Long id, Locale locale) {
        log.debug("Удаление книги с ID: {}", id);
        try {
            bookService.deleteBook(id);
            return ResponseEntity.ok().build();
        } catch (ResourceNotFoundException e) {
            log.warn("Книга с ID {} не найдена при удалении", id);
            throw new ResourceNotFoundException(
                    messageSource.getMessage("book.not.found", new Object[]{id}, locale));
        } catch (Exception e) {
            log.error("Ошибка при удалении книги: {}", e.getMessage());
            throw new IllegalArgumentException(
                    messageSource.getMessage("book.delete.error", null, locale));
        }
    }

    @GetMapping("/search")
    public Page<Book> searchBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer year,
            Pageable pageable) {
        log.debug("Поиск книг с параметрами: title={}, brand={}, year={}", title, brand, year);
        return bookService.findAll(title, brand, year, pageable);
    }
}