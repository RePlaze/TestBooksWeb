import com.codeborne.selenide.Configuration;
import io.restassured.RestAssured;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    @BeforeClass
    public void setup() {
        // Настройка Selenide
        Configuration.browser = "chrome";
        Configuration.baseUrl = "http://localhost:8080";
        Configuration.timeout = 10000;

        // Настройка RestAssured
        RestAssured.baseURI = "http://localhost:8080";
        RestAssured.basePath = "/api";
    }
}