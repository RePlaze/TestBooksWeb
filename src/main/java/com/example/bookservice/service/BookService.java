package com.example.bookservice.service;

import com.example.bookservice.exception.ResourceNotFoundException;
import com.example.bookservice.model.Book;
import com.example.bookservice.repository.BookRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Сервис для работы с книгами.
 * Предоставляет методы для CRUD операций и поиска книг.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class BookService {

    private final BookRepository bookRepository;

    /**
     * Получение списка всех книг
     * @return список всех книг
     */
    @Cacheable("books")
    public List<Book> getAllBooks() {
        log.debug("Получение списка всех книг");
        return bookRepository.findAll();
    }

    /**
     * Получение книги по ID
     * @param id идентификатор книги
     * @return найденная книга
     * @throws ResourceNotFoundException если книга не найдена
     */
    @Cacheable(value = "books", key = "#id")
    public Book getBookById(Long id) {
        log.debug("Получение книги по ID: {}", id);
        return bookRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Книга с ID " + id + " не найдена"));
    }

    /**
     * Создание новой книги
     * @param book данные новой книги
     * @return созданная книга
     */
    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public Book createBook(Book book) {
        log.debug("Создание новой книги: {}", book);
        return bookRepository.save(book);
    }

    /**
     * Обновление существующей книги
     * @param id идентификатор книги
     * @param bookDetails новые данные книги
     * @return обновленная книга
     * @throws ResourceNotFoundException если книга не найдена
     */
    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public Book updateBook(Long id, Book bookDetails) {
        log.debug("Обновление книги с ID {}: {}", id, bookDetails);

        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Книга с ID " + id + " не найдена");
        }

        Book book = getBookById(id);
        book.setVendorCode(bookDetails.getVendorCode());
        book.setTitle(bookDetails.getTitle());
        book.setAuthor(bookDetails.getAuthor());
        book.setPublicationYear(bookDetails.getPublicationYear());
        book.setBrand(bookDetails.getBrand());
        book.setStock(bookDetails.getStock());
        book.setPrice(bookDetails.getPrice());

        return bookRepository.save(book);
    }

    /**
     * Удаление книги
     * @param id идентификатор книги
     * @throws ResourceNotFoundException если книга не найдена
     */
    @Transactional
    @CacheEvict(value = "books", allEntries = true)
    public void deleteBook(Long id) {
        log.debug("Удаление книги с ID: {}", id);
        if (!bookRepository.existsById(id)) {
            throw new ResourceNotFoundException("Книга с ID " + id + " не найдена");
        }
        bookRepository.deleteById(id);
    }

    /**
     * Поиск книг с фильтрацией и пагинацией
     * @param title название книги (частичное совпадение)
     * @param brand издательство (частичное совпадение)
     * @param year год издания
     * @param pageable параметры пагинации
     * @return страница с найденными книгами
     */
    public Page<Book> findAll(String title, String brand, Integer year, Pageable pageable) {
        log.debug("Поиск книг с параметрами: title={}, brand={}, year={}", title, brand, year);

        if (title != null && brand != null && year != null) {
            return bookRepository.findByTitleContainingIgnoreCaseAndBrandContainingIgnoreCaseAndYear(
                    title, brand, year, pageable);
        } else if (title != null && brand != null) {
            return bookRepository.findByTitleContainingIgnoreCaseAndBrandContainingIgnoreCase(
                    title, brand, pageable);
        } else if (title != null) {
            return bookRepository.findByTitleContainingIgnoreCase(title, pageable);
        }
        return bookRepository.findAll(pageable);
    }
}