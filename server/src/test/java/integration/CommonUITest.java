package integration;

import com.codeborne.selenide.logevents.SelenideLogger;
import com.stasdev.backend.BackendApplication;
import common.logs.AllureSelenide;
import common.ApiFunctions;
import org.junit.jupiter.api.*;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;

import static com.codeborne.selenide.Condition.visible;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;


@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.DEFINED_PORT, classes = BackendApplication.class)//Так мы запускаем приложение используя тестовое окружение (Junit 5 вклинивается в модель спринг. За счет этого даже BeforeAll выполнится перед стартом спринг приложения). Важно помнить не смешивать данную аннотацию с MockMVC потому что она делает dirtyContext и спринг будет стартовать каждый раз заново для каждого теста
@ActiveProfiles("test")
@TestPropertySource(locations = "classpath:application-test.properties")// переопределяем проперти для запуска
@TestInstance(TestInstance.Lifecycle.PER_CLASS)//Это необходимо что бы BeforeAll выполнялся после старта спринга (потому что будет выполняться только при создание инстанса тестового класса)
abstract class CommonUITest extends ApiFunctions{

    //  Эти методы выполняются в отдельных потоках (при паралелльном запуске) поэтому сюда не следует помещать open
    @BeforeAll
    static void setUp() {
        SelenideLogger.addListener("allure", new AllureSelenide());
    }

    @AfterAll
    static void tearDown() {
        SelenideLogger.removeListener("allure");
    }

    @AfterEach
    void logout(){
        $(byText("logout")).click();
        $(byText("Login")).shouldBe(visible);
    }

}
