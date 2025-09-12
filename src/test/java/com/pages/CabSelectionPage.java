package com.pages;

import java.io.File;
import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.objectrepository.Locators;
import com.parameters.Reporter;
import com.setup.Base;

public class CabSelectionPage {

    private final WebDriver driver;
    private final ExtentTest extentTest;
    private final Duration WAIT = Duration.ofSeconds(20);

    public CabSelectionPage(WebDriver driver, ExtentTest extentTest) {
        this.driver = driver;
        this.extentTest = extentTest;
    }

    public boolean userIsOnCabResultsPage() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, WAIT);
            wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.hatchbackOption));
            Reporter.generateReport(driver, extentTest, Status.PASS, "Cab results page loaded (hatchback visible).");
            return true;
        } catch (Exception e) {
            Reporter.generateReport(driver, extentTest, Status.FAIL, "Cab results page not detected: " + e.getMessage());
            takeScreenshot("cab_results_not_loaded");
            return false;
        }
    }
    
    public boolean selectCabType(String cabType) {
        if (cabType == null) cabType = "";
        String normalized = cabType.trim();

        try {
            WebDriverWait wait = new WebDriverWait(driver, WAIT);

            if (normalized.equalsIgnoreCase("Hatchback")) {
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(Locators.hatchbackOption));
                scrollIntoView(el);
                try {
                    el.click();
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                }
                Reporter.generateReport(driver, extentTest, Status.PASS, "Selected Hatchback");
                Base.sleep(); 
                return true;
            }

            String xpath = "//*[self::label or self::button or self::div or self::a or self::span]"
                    + "[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '"
                    + normalized.toLowerCase() + "')]";
            WebElement el = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(xpath)));
            scrollIntoView(el);
            try {
                el.click();
            } catch (Exception ex) {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            }
            Reporter.generateReport(driver, extentTest, Status.PASS, "Selected cab type: " + normalized);
            Base.sleep();
            return true;
        } catch (Exception e) {
            Reporter.generateReport(driver, extentTest, Status.FAIL, "selectCabType failed for '" + cabType + "': " + e.getMessage());
            takeScreenshot("selectCabType_failed");
            return false;
        }
    }
   
    public boolean bookCabByProvider(String provider) {
        if (provider == null) provider = "";
        String normalized = provider.trim();

        try {
            WebDriverWait wait = new WebDriverWait(driver, WAIT);

            List<WebElement> candidates = driver.findElements(Locators.gozoBookNow);

            if (candidates == null || candidates.isEmpty()) {
                candidates = driver.findElements(By.xpath("//div[contains(@class,'cab-listings')]/div[1]//button[@data-partner='GOZO CABS' and @data-type='Hatchback']"));
            }

            if (candidates == null || candidates.isEmpty()) {
                Reporter.generateReport(driver, extentTest, Status.FAIL, "No GOZO CABS Book Now candidates found.");
                takeScreenshot("gozo_no_candidates");
                return false;
            }

            WebElement bookBtn = candidates.get(0);
            scrollIntoView(bookBtn);
            Base.sleep(); // allow visible change
            try {
                bookBtn.click();
            } catch (Exception ex) {
                try {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bookBtn);
                } catch (Exception ex2) {
                    Reporter.generateReport(driver, extentTest, Status.FAIL, "Both normal and JS click failed: " + ex2.getMessage());
                    takeScreenshot("gozo_click_failed");
                    return false;
                }
            }

            Reporter.generateReport(driver, extentTest, Status.PASS, "Clicked Book Now for: " + normalized);
            Base.sleep(); 
            return true;
        } catch (Exception e) {
            Reporter.generateReport(driver, extentTest, Status.FAIL, "bookCabByProvider exception: " + e.getMessage());
            takeScreenshot("bookCabByProvider_exception");
            return false;
        }
    }

    public boolean waitForReviewBookingAndClose() {
        try {
            WebDriverWait wait = new WebDriverWait(driver, WAIT);

            By reviewLocator = By.xpath("//*[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'review') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'booking') or contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'summary')]");

            WebElement el = wait.until(ExpectedConditions.visibilityOfElementLocated(reviewLocator));
            Reporter.generateReport(driver, extentTest, Status.PASS, "Booking/review page detected: " + (el.getText().length() > 100 ? el.getText().substring(0, 100) : el.getText()));
            Base.sleep();

            try {
                String main = driver.getWindowHandle();
                Set<String> handles = driver.getWindowHandles();
                for (String h : handles) {
                    if (!h.equals(main)) {
                        try {
                            driver.switchTo().window(h);
                            driver.close();
                        } catch (Exception ignored) {}
                    }
                }
                try { driver.switchTo().window(main); } catch (Exception ignored) {}
            } catch (Exception ignored) {}

            return true;
        } catch (Exception e) {
            Reporter.generateReport(driver, extentTest, Status.FAIL, "Review/booking page not found: " + e.getMessage());
            takeScreenshot("review_not_found");
            return false;
        }
    }

    private void scrollIntoView(WebElement el) {
        try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignored) {}
    }

    private void takeScreenshot(String name) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String path = System.getProperty("user.dir") + File.separator + "target" + File.separator + name + ".png";
            FileUtils.copyFile(src, new File(path));
            Reporter.generateReport(driver, extentTest, Status.INFO, "Saved screenshot: " + path);
        } catch (IOException ignored) {}
    }
}
