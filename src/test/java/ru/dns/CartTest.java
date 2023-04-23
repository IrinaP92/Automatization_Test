package ru.dns;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;

public class CartTest
{
    static WebDriver driver;

    @BeforeAll
    public static void setUp()
    {
        //Объявление WebDriver
        WebDriverManager.chromedriver().setup();
        driver = new ChromeDriver();
        driver.manage().window().maximize();
    }

    /**
     * Тест на добавление товара в корзину
     */
    @Test
    public void testAddItem()
    {
        //Открыть карточку товара
        driver.get("https://www.dns-shop.ru/product/3e3a344e642b3332/elektrosamokat-ninebot-by-segway-max-g30p-seryj/");

        //Добавить товар в корзину
        driver.findElement(By.cssSelector(".buy-btn:nth-child(3)")).click();
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[contains(@class, 'button-ui_passive-done')]")));

        //Перейти в корзину
        driver.get("https://www.dns-shop.ru/cart/");

        //Проверка, что в корзине 1 товар
        WebElement element = driver.findElement(By.xpath("//span[@class = 'cart-link-counter__badge']"));
        String expectedResult = "1";
        String actualResult = element.getText();
        String message = String.format("В корзине неверное количество товаров. Ожидалось: %s. Получили: %s", expectedResult, actualResult);
        Assertions.assertEquals(expectedResult, actualResult, message);
    }

    /**
     * Тест на удаление товара из корзины
     */
    @Test
    public void testDelItem()
    {
        WebElement element = null;

        //Перейти в корзину
        driver.get("https://www.dns-shop.ru/cart/");

        try
        {
            //Удаление товара из корзины
            driver.findElement(By.xpath("//p[text() = 'Удалить']")).click();
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
            wait.until(ExpectedConditions.urlToBe("https://www.dns-shop.ru/cart/?activeTabId=1"));
        }
        catch (NoSuchElementException e)
        {
            Assertions.fail("Элемент удаления товара на странице не найден");
        }

        //Проверка, что корзина пустая
        try
        {
            element = driver.findElement(By.xpath("//div[text() = 'Корзина пуста']"));
        }
        catch (NoSuchElementException e)
        {
            WebElement count = driver.findElement(By.xpath("//span[@class = 'cart-link-counter__badge']"));
            String expectedResult = "0";
            String actualResult = count.getText();
            Assertions.assertEquals(expectedResult, actualResult, "В корзине есть товары");
        }
    }

    @AfterAll
    public static void close()
    {
        driver.quit();
    }
}
