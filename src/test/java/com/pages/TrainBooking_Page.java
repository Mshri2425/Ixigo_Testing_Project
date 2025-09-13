package com.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.time.Duration;

public class TrainBooking_Page {
    private WebDriver driver;
    private WebDriverWait wait;

    private By resultsContainer = By.xpath("//*[@id='content']/div/div[2]/div[3]/div[2]/ul");
    private By class2A = By.xpath("//*[@id='content']/div/div[2]/div[3]/div[2]/ul/li[2]/div/div/div[2]/div[3]/div[1]");
    private By firstBookButton = By.xpath("//*[@id='content']/div/div[2]/div[3]/div[2]/ul/li[2]/div/div/div[3]/div/div[1]/div/div[3]/div/button/div");
    private By loginPopupClose = By.xpath("//*[@id='content']/div/div[3]/div/div/div[1]");

    public TrainBooking_Page(WebDriver driver) {
        if (driver == null) throw new IllegalArgumentException("WebDriver passed to TrainBookingPage is null");
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    public void waitForResults() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(resultsContainer));
    }

    public boolean isResultsVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(resultsContainer));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void selectClass2A() {
        WebElement classElement = wait.until(ExpectedConditions.elementToBeClickable(class2A));
        classElement.click();
    }

    public void clickFirstBookButton() {
        WebElement bookBtn = wait.until(ExpectedConditions.elementToBeClickable(firstBookButton));
        bookBtn.click();
    }

    public boolean isLoginPopupVisible() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(loginPopupClose));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }
}
