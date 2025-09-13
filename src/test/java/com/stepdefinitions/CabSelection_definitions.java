package com.stepdefinitions;

import com.aventstack.extentreports.Status;
import com.pages.AirportCabs_Page;
import com.pages.CabSelection_Page;
import com.parameters.Reporter;
import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;
import java.time.Duration;

public class CabSelection_definitions {

    private final WebDriver driver;
    private final com.aventstack.extentreports.ExtentTest extTest;
    private final AirportCabs_Page airportCabsPage;
    private final CabSelection_Page cabPage;
    private final WebDriverWait wait;

    public CabSelection_definitions() {
        this.driver = Hooks.driver;
        this.extTest = Hooks.extTest;
        this.airportCabsPage = new AirportCabs_Page(driver, extTest);
        this.cabPage = new CabSelection_Page(driver, extTest);
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(15));
    }

    @Given("user is on the cab results page")
    public void user_is_on_the_cab_results_page() {
        Reporter.generateReport(driver, extTest, Status.INFO, "Navigating to Airport Cabs and performing search...");

        airportCabsPage.clickAirportCabsAndSwitch();
        airportCabsPage.selectHomeToAirport();
        airportCabsPage.enterPickupLocation("Chennai");
        airportCabsPage.enterDropLocation("Mumbai - CSMI Airport-T1");
        airportCabsPage.clickSearchButton();

        boolean loaded = cabPage.userIsOnCabResultsPage();
        Reporter.generateReport(driver, extTest, loaded ? Status.PASS : Status.FAIL, "Cab results page loaded: " + loaded);
        Assert.assertTrue(loaded, "Cab results page did not load.");
    }

    @When("user selects {string} cab type")
    public void user_selects_cab_type(String cabType) {
        Reporter.generateReport(driver, extTest, Status.INFO, "Selecting cab type: " + cabType);
        boolean ok = cabPage.selectCabType(cabType);
        Reporter.generateReport(driver, extTest, ok ? Status.PASS : Status.FAIL, "selectCabType returned: " + ok);
        Assert.assertTrue(ok, "Failed to select cab type: " + cabType);
    }

    @When("user books {string} cab")
    public void user_books_cab(String provider) {
        Reporter.generateReport(driver, extTest, Status.INFO, "Booking provider: " + provider);

        boolean clicked = false;
        try {
            clicked = cabPage.bookCabByProvider(provider);
            Reporter.generateReport(driver, extTest, clicked ? Status.PASS : Status.WARNING, "Initial bookCabByProvider returned: " + clicked);
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Exception in cabPage.bookCabByProvider: " + e.getMessage());
        }

        if (!clicked) {
            Reporter.generateReport(driver, extTest, Status.INFO, "Falling back to robust click for provider: " + provider);
            clicked = clickBookNowByProviderName(provider, 2);
            Reporter.generateReport(driver, extTest, clicked ? Status.PASS : Status.FAIL, "Fallback click result: " + clicked);
        }

        Assert.assertTrue(clicked, "Failed to click Book Now for: " + provider);
    }

    @Then("booking confirmation page should be displayed")
    public void booking_confirmation_page_should_be_displayed() {
        Reporter.generateReport(driver, extTest, Status.INFO, "Waiting for booking confirmation...");
        boolean ok = cabPage.waitForReviewBookingAndClose();
        Reporter.generateReport(driver, extTest, ok ? Status.PASS : Status.FAIL, "Review/booking handled: " + ok);
        Assert.assertTrue(ok, "Review/booking page was not displayed/handled.");
    }

    private boolean clickBookNowByProviderName(String providerName, int maxAttempts) {
       
        String xpathForBookNow = String.format("//div[contains(., '%s')]//button[contains(., 'Book') or contains(., 'Book Now')]", providerName);
        int attempts = 0;

        while (attempts < maxAttempts) {
            attempts++;
            try {
                closeAnyOverlaysIfPresent();
                By by = By.xpath(xpathForBookNow);
                WebElement bookBtn = wait.until(ExpectedConditions.elementToBeClickable(by));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", bookBtn);
                try {
                    bookBtn.click();
                } catch (ElementClickInterceptedException | StaleElementReferenceException ex) {
                    Reporter.generateReport(driver, extTest, Status.WARNING, "Normal click failed (" + ex.getClass().getSimpleName() + "), trying JS click. Attempt: " + attempts);
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", bookBtn);
                }

                return true;
            } catch (TimeoutException te) {
                Reporter.generateReport(driver, extTest, Status.WARNING, String.format("Timed out waiting for Book Now for '%s' (attempt %d).", providerName, attempts));
            } catch (ElementClickInterceptedException eci) {
                Reporter.generateReport(driver, extTest, Status.WARNING, "Click intercepted for '" + providerName + "' attempt " + attempts);
            } catch (StaleElementReferenceException sere) {
                Reporter.generateReport(driver, extTest, Status.WARNING, "Stale element while clicking '" + providerName + "' attempt " + attempts);
            } catch (NoSuchElementException nse) {
                Reporter.generateReport(driver, extTest, Status.FAIL, "Book Now not found for provider: " + providerName);
                break;
            } catch (Exception ex) {
                Reporter.generateReport(driver, extTest, Status.WARNING, "Unexpected error clicking Book Now: " + ex.getMessage());
            }

            // small pause before retry
            try {
                Thread.sleep(500);
            } catch (InterruptedException ignored) {}
        }

        return false;
    }

    private void closeAnyOverlaysIfPresent() {
        try {
            By closeBtnSelectors[] = new By[] {
                    By.xpath("//button[contains(@class,'close') or contains(.,'Close') or contains(.,'×')]"),
                    By.xpath("//div[contains(@class,'toast')]//button"),
                    By.xpath("//button[contains(@aria-label,'Close')]")
            };

            for (By sel : closeBtnSelectors) {
                if (driver.findElements(sel).size() > 0) {
                    WebElement close = driver.findElement(sel);
                    if (close.isDisplayed()) {
                        try {
                            close.click();
                            new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.invisibilityOf(close));
                            Reporter.generateReport(driver, extTest, Status.INFO, "Closed overlay using selector: " + sel.toString());
                        } catch (Exception e) {
                            try {
                                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", close);
                                new WebDriverWait(driver, Duration.ofSeconds(3)).until(ExpectedConditions.invisibilityOf(close));
                                Reporter.generateReport(driver, extTest, Status.INFO, "Closed overlay via JS using selector: " + sel.toString());
                            } catch (Exception ignored) {}
                        }
                    }
                }
            }
        } catch (Exception ignored) {
        }
    }
}
