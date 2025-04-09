import org.testng.annotations.Test;

import static com.codeborne.selenide.Condition.*;
import static com.codeborne.selenide.Selenide.*;

public class BookWebTest extends BaseTest {

    @Test
    public void testBookListPage() {
        open("/books");

        // Проверяем наличие элементов на странице
        $("h1").shouldHave(text("Список книг"));
//        $$("table tbody tr").shouldHave(size(greaterThan(0)));
    }

    @Test
    public void testCreateBook() {
        open("/books");

        // Переходим на форму создания
        $("a[href*='/books/new']").click();

        // Заполняем форму
        $("#title").setValue("Test Book");
        $("#author").setValue("Test Author");
        $("#publicationYear").setValue("2023");
        $("#brand").setValue("Test Publisher");
        $("#stock").setValue("10");
        $("#price").setValue("19.99");
        $("#vendorCode").setValue("TEST123");

        // Отправляем форму
        $("button[type='submit']").click();

        // Проверяем успешное создание
        $(".alert-success").shouldBe(visible);
    }

    @Test
    public void testEditBook() {
        open("/books");

        // Находим первую книгу и кликаем на кнопку редактирования
        $$("table tbody tr").first()
                .$("a[href*='/edit']")
                .click();

        // Меняем название
        $("#title").setValue("Updated Title");

        // Сохраняем изменения
        $("button[type='submit']").click();

        // Проверяем успешное обновление
        $(".alert-success").shouldBe(visible);
    }

    @Test
    public void testDeleteBook() {
        open("/books");

        // Находим первую книгу и кликаем на кнопку удаления
        $$("table tbody tr").first()
                .$("a[href*='/delete']")
                .click();

        // Проверяем успешное удаление
        $(".alert-success").shouldBe(visible);
    }

    @Test
    public void testSearchBooks() {
        open("/books");

        // Заполняем форму поиска
        $("#searchTitle").setValue("Test");
        $("#searchBrand").setValue("Publisher");
        $("#searchYear").setValue("2023");

        // Отправляем форму
        $("button[type='submit']").click();

        // Проверяем результаты поиска
//        $$("table tbody tr").shouldHave(size(greaterThan(0)));
    }
}