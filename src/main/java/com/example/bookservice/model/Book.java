package com.example.bookservice.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import java.math.BigDecimal;

/**
 * Сущность книги.
 */
@Data
@Entity
@Table(name = "books")
public class Book {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Уникальный артикул книги
     */
    @NotBlank(message = "Артикул обязателен")
    @Size(min = 3, max = 50, message = "Артикул должен быть от 3 до 50 символов")
    @Column(unique = true)
    private String vendorCode;

    /**
     * Название книги
     */
    @NotBlank(message = "Название обязательно")
    @Size(min = 2, max = 200, message = "Название должно быть от 2 до 200 символов")
    private String title;

    /**
     * Автор книги
     */
    @NotBlank(message = "Автор обязателен")
    private String author;

    /**
     * Год издания
     */
    @NotNull(message = "Год обязателен")
    @Min(value = 1900, message = "Год должен быть не ранее 1900")
    @Max(value = 2100, message = "Год должен быть не позднее 2100")
    @Column(name = "year")
    private Integer publicationYear;

    /**
     * Издательство
     */
    @NotBlank(message = "Издательство обязательно")
    @Size(min = 2, max = 100, message = "Издательство должно быть от 2 до 100 символов")
    private String brand;

    /**
     * Количество на складе
     */
    @NotNull(message = "Количество обязательно")
    @Min(value = 0, message = "Количество не может быть отрицательным")
    private Integer stock;

    /**
     * Цена книги
     */
    @NotNull(message = "Цена обязательна")
    @DecimalMin(value = "0.0", inclusive = false, message = "Цена должна быть больше 0")
    @Column(precision = 10, scale = 2)
    private BigDecimal price;
}