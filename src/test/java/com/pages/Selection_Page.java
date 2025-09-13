package com.pages;

import java.time.Duration;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.ExtentTest;
import com.objectrepository.Locators;
import com.parameters.Reporter;

public class Selection_Page extends Searching_Page {
	
    public Selection_Page(WebDriver driver, ExtentTest extTest) {
        super(driver, extTest);
    }

    public boolean loadPage() {
        try {
            openFlightsTab();
            handlePopupIfExists();
            selectRoundTrip();
            enterBoardingPlace("chennai");
            enterLandingPlaceSelectExact("Mumbai");
            setTravellersAndClass(1, 0, 1, "Economy");
            clickSearch();
            try {
                wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.resultsContainer));
                closePriceLockPopupIfPresent();
                Reporter.generateReport(driver, extTest, Status.PASS, "The page has been landed (initial attempt)");
                return true;
            } catch (Exception firstWaitEx) {
                Reporter.generateReport(driver, extTest, Status.WARNING,
                        "Results not visible after initial search attempt. Will retry selection + search. Reason: " + firstWaitEx.getMessage());
                try {
                    enterLandingPlaceReliable("Mumbai");
                    try {
                        ((JavascriptExecutor) driver).executeScript("window.scrollTo(0,0);");
                    } catch (Exception ignore) {}
                    clickSearch();
                    wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.resultsContainer));
                    closePriceLockPopupIfPresent();
                    Reporter.generateReport(driver, extTest, Status.PASS, "The page has been landed (retry attempt)");
                    return true;
                } catch (Exception secondEx) {
                    Reporter.generateReport(driver, extTest, Status.FAIL,
                            "Retry attempt failed to load results: " + secondEx.getMessage());
                    return false;
                }
            }
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to load page (exception): " + e.getMessage());
            return false;
        }
    }
    // Popup handling
    public void closePriceLockPopupIfPresent() {
        By[] popupLocators = new By[] {
                By.xpath("//*[@id='portal-root']/div/div[2]/div/button"),
                By.cssSelector("#portal-root div.bg-white button"),
                By.cssSelector("button.OnboardingSheetLottie_OnboardingSheetInternationalButton__CUHff"),
                By.xpath("//*[@id='portal-root']//button"),
                By.cssSelector("div.modal-backdrop, div.portal-overlay")
        };

        for (By pLoc : popupLocators) {
            try {
                org.openqa.selenium.WebElement btn = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.presenceOfElementLocated(pLoc));
                try {
                    org.openqa.selenium.WebElement clickable = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(2))
                            .until(ExpectedConditions.elementToBeClickable(pLoc));
                    clickable.click();
                    Reporter.generateReport(driver, extTest, Status.INFO, "Popup dismissed using locator: " + pLoc);
                    return;
                } catch (Exception clickEx) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                        Reporter.generateReport(driver, extTest, Status.INFO, "Popup JS-clicked using locator: " + pLoc);
                        return;
                    } catch (Exception jsEx) {
                        try {
                            ((JavascriptExecutor) driver).executeScript(
                                    "arguments[0].style.opacity = 1; arguments[0].style.pointerEvents='auto'; arguments[0].style.visibility='visible';",
                                    btn);
                            ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn);
                            Reporter.generateReport(driver, extTest, Status.INFO,
                                    "Popup forced-visible + clicked for locator: " + pLoc);
                            return;
                        } catch (Exception forceEx) {
                            // try next locator
                        }
                    }
                }
            } catch (Exception e) {
                // locator not present then try next
            }
        }
        Reporter.generateReport(driver, extTest, Status.INFO, "Price-lock popup not found or could not be dismissed");
    }

    // Generic filters 
    public boolean applyFilter(String filterName) {
        if (filterName == null) filterName = "";
        String normalized = filterName.trim();
        closePriceLockPopupIfPresent();

        String lower = normalized.toLowerCase();
        String xpath = "//*[self::label or self::button or self::span or self::div or self::a]" +
                "[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), '" + lower + "')]";
        By filterLocator = By.xpath(xpath);

        int attempts = 2;
        for (int i = 0; i < attempts; i++) {
            try {
                org.openqa.selenium.WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(filterLocator));
                try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignore) {}
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(filterLocator)).click();
                    Reporter.generateReport(driver, extTest, Status.PASS, "Filter clicked: " + normalized);
                    return true;
                } catch (Exception clickEx) {
                    try {
                        ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                        Reporter.generateReport(driver, extTest, Status.PASS, "Filter JS-clicked: " + normalized);
                        return true;
                    } catch (Exception jsEx) {
                        // retry loop
                    }
                }
            } catch (Exception e) {
                try { Thread.sleep(250); } catch (InterruptedException ie) { /* ignore */ }
            }
        }

        try {
            java.util.List<org.openqa.selenium.WebElement> boxes = driver.findElements(By.xpath(
                    "//input[contains(@placeholder,'Search') or contains(@aria-label,'search') or contains(@data-testid,'filter-search')]"));
            if (!boxes.isEmpty()) {
                org.openqa.selenium.WebElement box = boxes.get(0);
                box.clear();
                box.sendKeys(normalized);
                Thread.sleep(300);
                box.sendKeys(org.openqa.selenium.Keys.ENTER);
                org.openqa.selenium.WebElement match = wait.until(ExpectedConditions.elementToBeClickable(filterLocator));
                try {
                    match.click();
                    Reporter.generateReport(driver, extTest, Status.PASS, "Filter selected after typing: " + normalized);
                    return true;
                } catch (Exception ex) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", match);
                    Reporter.generateReport(driver, extTest, Status.PASS, "Filter JS-clicked after typing: " + normalized);
                    return true;
                }
            }
        } catch (Exception ignore) {}

        Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to select filter: " + filterName);
        return false;
    }

    public boolean applyAirlineFilter(String airlineName) {
        return applyFilter(airlineName);
    }

    // Time-of-day helpers
    public boolean applyTimeFilter(String type, String humanLabel) {
        closePriceLockPopupIfPresent();
        try {
            if (humanLabel == null) humanLabel = "";
            String mappedValue = mapTimeLabelToValue(humanLabel);
            String nameAttr = type.equalsIgnoreCase("arrival") ? "landing" : "takeOff";

            // try inputs first (exact value match)
            By checkbox = By.xpath("//input[@name='" + nameAttr + "' and normalize-space(@value)='" + mappedValue + "']");
            try {
                org.openqa.selenium.WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(checkbox));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                Reporter.generateReport(driver, extTest, Status.PASS,
                        String.format("%s filter applied: %s -> %s", type, humanLabel, mappedValue));
                return true;
            } catch (Exception e) {
                // fallback to text-based label matching
                String textToMatch = humanLabel.trim();
                By textLocator = By.xpath("//*[self::label or self::button or self::span or self::div or self::a]" +
                        "[contains(normalize-space(.), \"" + textToMatch + "\")]");
                org.openqa.selenium.WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(textLocator));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el);
                try {
                    wait.until(ExpectedConditions.elementToBeClickable(textLocator)).click();
                } catch (Exception clickEx) {
                    ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                }
                Reporter.generateReport(driver, extTest, Status.PASS,
                        String.format("%s filter applied by text match: %s", type, humanLabel));
                return true;
            }
        } catch (Exception ex) {
            Reporter.generateReport(driver, extTest, Status.FAIL,
                    String.format("Could not apply %s filter (%s): %s", type, humanLabel, ex.getMessage()));
            return false;
        }
    }

    private String mapTimeLabelToValue(String label) {
        if (label == null) return "MORNING";
        String l = label.toLowerCase();

        if ((l.contains("06am") && l.contains("12pm")) || l.contains("06am-12pm") || l.contains("6am-12pm") || l.contains("06am 12pm") || l.contains("06am - 12pm"))
            return "MORNING";

        if ((l.contains("12pm") && l.contains("6pm")) || l.contains("12pm-6pm") || l.contains("12 pm - 6 pm") || l.contains("12pm 6pm"))
            return "AFTERNOON";

        if (l.contains("before") && l.contains("6") || l.contains("before 6 am"))
            return "EARLY";

        if (l.contains("night") || l.contains("after 6pm") || l.contains("6pm"))
            return "NIGHT";

        return "MORNING";
    }

    public boolean applyDepartureMorning() {
        return applyTimeFilter("departure", "06AM-12PM");
    }

    public boolean applyArrivalMorning() {
        return applyTimeFilter("arrival", "06AM-12PM");
    }

    // Select first flight
    public boolean selectFirstAvailableFlight() {
        closePriceLockPopupIfPresent();

        By[] candidateXPaths = new By[] {
                By.xpath("(//button[contains(.,'Select') or contains(.,'Book') or contains(.,'Continue')])[1]"),
                By.xpath("(//a[contains(.,'Select') or contains(.,'Book')])[1]"),
                By.xpath("(//button[contains(@data-testid,'select') or contains(@class,'select')])[1]"),
                By.xpath("(//div[contains(@class,'FlightCard') or contains(@class,'result-card')])[1]//button")
        };

        for (By cand : candidateXPaths) {
            try {
                org.openqa.selenium.WebElement el = wait.until(ExpectedConditions.elementToBeClickable(cand));
                try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignore) {}
                try { el.click(); } catch (Exception clickEx) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); }
                Reporter.generateReport(driver, extTest, Status.PASS, "Clicked first available flight using " + cand);
                return true;
            } catch (Exception ignore) {
                // try next candidate
            }
        }

        try {
            org.openqa.selenium.WebElement results = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.resultsContainer));
            org.openqa.selenium.WebElement btn = results.findElement(By.xpath(".//button[.//text() or @aria-label][1]"));
            wait.until(ExpectedConditions.elementToBeClickable(btn));
            try { btn.click(); } catch (Exception clickEx) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
            Reporter.generateReport(driver, extTest, Status.PASS, "Clicked first available flight (fallback)");
            return true;
        } catch (Exception ignore) {}

        Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to select first available flight");
        return false;
    }
}
