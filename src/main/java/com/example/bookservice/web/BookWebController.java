package com.example.bookservice.web;

import com.example.bookservice.exception.ResourceNotFoundException;
import com.example.bookservice.model.Book;
import com.example.bookservice.service.BookService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

/**
 * Контроллер для веб-интерфейса управления книгами.
 * Предоставляет методы для отображения списка книг, создания, редактирования и удаления книг.
 */
@Slf4j
@Controller
@RequestMapping("/books")
@RequiredArgsConstructor
public class BookWebController {
    private final BookService bookService;

    /**
     * Отображение списка книг с возможностью фильтрации и пагинации
     */
    @GetMapping
    public String listBooks(
            @RequestParam(required = false) String title,
            @RequestParam(required = false) String brand,
            @RequestParam(required = false) Integer year,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        log.debug("Получение списка книг с параметрами: title={}, brand={}, year={}, page={}, size={}",
                title, brand, year, page, size);

        Page<Book> books = bookService.findAll(title, brand, year, PageRequest.of(page, size));
        model.addAttribute("books", books);
        model.addAttribute("title", title);
        model.addAttribute("brand", brand);
        model.addAttribute("year", year);
        return "books";
    }

    /**
     * Отображение формы создания новой книги
     */
    @GetMapping("/new")
    public String showCreateForm(Model model) {
        log.debug("Отображение формы создания новой книги");
        model.addAttribute("book", new Book());
        return "book_form";
    }

    /**
     * Отображение формы редактирования существующей книги
     */
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model, RedirectAttributes redirectAttributes) {
        log.debug("Отображение формы редактирования книги с ID: {}", id);
        try {
            model.addAttribute("book", bookService.getBookById(id));
            return "book_form";
        } catch (ResourceNotFoundException e) {
            log.warn("Книга с ID {} не найдена", id);
            redirectAttributes.addFlashAttribute("error", "Книга не найдена");
            return "redirect:/books";
        }
    }

    /**
     * Сохранение книги (создание или обновление)
     */
    @PostMapping("/save")
    public String saveBook(@Valid @ModelAttribute Book book,
                           BindingResult result,
                           RedirectAttributes redirectAttributes) {
        log.debug("Сохранение книги: {}", book);

        if (result.hasErrors()) {
            log.warn("Ошибки валидации при сохранении книги: {}", result.getAllErrors());
            return "book_form";
        }

        try {
            if (book.getId() == null) {
                bookService.createBook(book);
                redirectAttributes.addFlashAttribute("success", "Книга успешно создана");
            } else {
                bookService.updateBook(book.getId(), book);
                redirectAttributes.addFlashAttribute("success", "Книга успешно обновлена");
            }
            return "redirect:/books";
        } catch (Exception e) {
            log.error("Ошибка при сохранении книги: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при сохранении книги");
            return "book_form";
        }
    }

    /**
     * Удаление книги
     */
    @GetMapping("/delete/{id}")
    public String deleteBook(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        log.debug("Удаление книги с ID: {}", id);
        try {
            bookService.deleteBook(id);
            redirectAttributes.addFlashAttribute("success", "Книга успешно удалена");
        } catch (ResourceNotFoundException e) {
            log.warn("Книга с ID {} не найдена при попытке удаления", id);
            redirectAttributes.addFlashAttribute("error", "Книга не найдена");
        } catch (Exception e) {
            log.error("Ошибка при удалении книги: {}", e.getMessage());
            redirectAttributes.addFlashAttribute("error", "Произошла ошибка при удалении книги");
        }
        return "redirect:/books";
    }
}