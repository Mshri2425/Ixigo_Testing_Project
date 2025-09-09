package com.stepdefinitions;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.pages.Loginpage;
import com.setup.Base;
import com.utils.CookieManager;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

import java.time.Duration;

public class Logindefinitions {

    WebDriver driver = Hooks.driver;
    Loginpage loginPage;

    @Given("the user is on the ixigo homepage with mobile {string}")
    public void the_user_is_on_the_ixigo_homepage_with_mobile(String mobileNumber) {
        loginPage = new Loginpage(driver, null);

        // 1️⃣ Visit site first
        driver.get("https://www.ixigo.com/");

        // 2️⃣ Try loading saved cookies
        boolean cookiesLoaded = CookieManager.load(driver);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));

        try {
            // Check if correct user is already logged in
            WebElement heyUser = wait.until(ExpectedConditions.visibilityOfElementLocated(
                    By.xpath("//span[contains(text(),'Hey')]")
            ));
            System.out.println("✅ User already logged in via cookies: " + heyUser.getText());

        } catch (Exception e) {
            // Cookies not valid / new user, perform OTP login
            System.out.println("⚠️ User not logged in or mismatch. Performing OTP login.");

            CookieManager.clearBrowserData(driver);

            loginPage.enterMobileNumber(mobileNumber);
            loginPage.clickContinueForMobile();
            loginPage.enterOtpManually();
            Base.sleep();

            // Save cookies for future
            CookieManager.save(driver);
        }
    }

    @Then("the user should see the home page after login")
    public void the_user_should_see_home_page_after_login() {
        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(15));
        WebElement heyUser = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//span[contains(text(),'Hey')]")));
        Assert.assertTrue(heyUser.isDisplayed(), "❌ Login failed!");
        System.out.println("✅ Home page loaded correctly for " + heyUser.getText());
    }
}
