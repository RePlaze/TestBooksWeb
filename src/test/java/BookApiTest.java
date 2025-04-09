
import com.example.bookservice.model.Book;
import io.restassured.http.ContentType;
import org.testng.annotations.Test;

import java.math.BigDecimal;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

public class BookApiTest extends BaseTest {

    @Test
    public void testGetAllBooks() {
        given()
                .when()
                .get("/books")
                .then()
                .statusCode(200)
                .body("$", hasSize(greaterThan(0)));
    }

    @Test
    public void testGetBookById() {
        // Сначала создаем книгу
        Book newBook = createTestBook();
        Book createdBook = given()
                .contentType(ContentType.JSON)
                .body(newBook)
                .when()
                .post("/books")
                .then()
                .statusCode(200)
                .extract().as(Book.class);

        // Теперь получаем её по ID
        given()
                .when()
                .get("/books/" + createdBook.getId())
                .then()
                .statusCode(200)
                .body("id", equalTo(createdBook.getId().intValue()))
                .body("title", equalTo(newBook.getTitle()))
                .body("author", equalTo(newBook.getAuthor()));
    }

    @Test
    public void testCreateBook() {
        Book newBook = createTestBook();

        given()
                .contentType(ContentType.JSON)
                .body(newBook)
                .when()
                .post("/books")
                .then()
                .statusCode(200)
                .body("title", equalTo(newBook.getTitle()))
                .body("author", equalTo(newBook.getAuthor()))
                .body("id", notNullValue());
    }

    @Test
    public void testUpdateBook() {
        // Сначала создаем книгу
        Book newBook = createTestBook();
        Book createdBook = given()
                .contentType(ContentType.JSON)
                .body(newBook)
                .when()
                .post("/books")
                .then()
                .extract().as(Book.class);

        // Обновляем книгу
        createdBook.setTitle("Updated Title");

        given()
                .contentType(ContentType.JSON)
                .body(createdBook)
                .when()
                .put("/books/" + createdBook.getId())
                .then()
                .statusCode(200)
                .body("title", equalTo("Updated Title"));
    }

    @Test
    public void testDeleteBook() {
        // Сначала создаем книгу
        Book newBook = createTestBook();
        Book createdBook = given()
                .contentType(ContentType.JSON)
                .body(newBook)
                .when()
                .post("/books")
                .then()
                .extract().as(Book.class);

        // Удаляем книгу
        given()
                .when()
                .delete("/books/" + createdBook.getId())
                .then()
                .statusCode(200);

        // Проверяем, что книга удалена
        given()
                .when()
                .get("/books/" + createdBook.getId())
                .then()
                .statusCode(404);
    }

    @Test
    public void testSearchBooks() {
        given()
                .when()
                .get("/books/search?title=Test&page=0&size=10")
                .then()
                .statusCode(200)
                .body("content", notNullValue())
                .body("totalElements", greaterThanOrEqualTo(0));
    }

    private Book createTestBook() {
        Book book = new Book();
        book.setTitle("Test Book");
        book.setAuthor("Test Author");
        book.setPublicationYear(2023);
        book.setBrand("Test Publisher");
        book.setStock(10);
        book.setPrice(BigDecimal.valueOf(19.99));
        book.setVendorCode("TEST123");
        return book;
    }
}