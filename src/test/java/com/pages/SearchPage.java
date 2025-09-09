package com.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.objectrepository.Locators;
import com.setup.Base;

public class SearchPage {

    WebDriver driver;
    WebDriverWait wait;
    ExtentTest extTest;

    public SearchPage(WebDriver driver, ExtentTest extTest) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.extTest = extTest;
    }

    private void handlePopupIfExists() {
        try {
            List<WebElement> popups = driver.findElements(By.id("wiz-iframe-intent"));
            if (!popups.isEmpty()) {
                driver.switchTo().frame(popups.get(0));
                driver.findElement(By.id("closeButton")).click();
                driver.switchTo().defaultContent();
                System.out.println("Popup closed successfully");
            }
        } catch (Exception ignore) {
            // ignore any popup-handling errors
        }
    }

    public void selectingRoundtrip() {
        // 🔹 Click Round Trip (keeps original logic)
        WebElement roundTripBtn = wait
                .until(ExpectedConditions.elementToBeClickable(By.xpath("//button[text()='Round Trip']")));
        roundTripBtn.click();
        Base.sleep();
    }

    public void selectingfrom(String from) {
        // open From popup
        wait.until(ExpectedConditions.elementToBeClickable(Locators.from)).click();

        // type into the from input
        WebElement fromInput = driver.findElement(Locators.click_from);
        fromInput.click();
        fromInput.clear();
        fromInput.sendKeys(from);

        // small wait, then handle any popup that might have appeared
        Base.sleep();
        handlePopupIfExists();

        // send ENTER to choose first suggestion (keeps your original behavior)
        fromInput.sendKeys(Keys.ENTER);

        // give UI time to reflect selection
        Base.sleep();
    }

    public void selectingto(String to) {
        // open To popup
        wait.until(ExpectedConditions.elementToBeClickable(Locators.to)).click();

        // type into the to input
        WebElement toInput = driver.findElement(Locators.click_to);
        toInput.click();
        toInput.clear();
        toInput.sendKeys(to);

        // small wait, handle popups if any
        Base.sleep();
        handlePopupIfExists();

        // send ENTER to choose first suggestion (keeps your original behavior)
        toInput.sendKeys(Keys.ENTER);

        // give UI time to reflect selection
        Base.sleep();
    }
}
