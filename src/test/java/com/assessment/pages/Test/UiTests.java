package com.assessment.tests;

import com.assessment.pages.LoginPage;
import com.assessment.pages.ProductsPage;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

public class UiTests {
    private WebDriver driver;
    private LoginPage loginPage;
    private ProductsPage productsPage;

    @BeforeClass
    public void setUp() {
        ChromeOptions options = new ChromeOptions();
        // Headless mode runs invisible in the background, which is required for cloud/CI environments!
        options.addArguments("--headless=new");
        
        driver = new ChromeDriver(options);
        driver.manage().window().maximize();
        
        loginPage = new LoginPage(driver);
        productsPage = new ProductsPage(driver);
    }

    @Test(priority = 1)
    public void testHappyPathLoginAndAddToCart() {
        loginPage.navigate();
        loginPage.login("standard_user", "secret_sauce");
        
        productsPage.addFirstProductToCart();
        Assert.assertEquals(productsPage.getCartCount(), "1", "The shopping cart item count badge did not update to 1.");
    }

    @Test(priority = 2)
    public void testUnhappyPathLockedOutUser() {
        loginPage.navigate();
        loginPage.login("locked_out_user", "secret_sauce");
        
        String error = loginPage.getErrorMessageText();
        Assert.assertTrue(error.contains("Sorry, this user has been locked out"), "The expected locked out error message was not displayed.");
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }
}
