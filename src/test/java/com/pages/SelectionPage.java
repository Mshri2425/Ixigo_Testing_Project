package com.pages;

import java.time.Duration;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;

import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.ExtentTest;
import com.objectrepository.Locators;
import com.parameters.Reporter;

/**
 * SelectionPage - robust interactions on search results page (filters, selecting flights, autocomplete handling).
 *
 * Requires Searchingpage to initialize driver, wait, extTest.
 */
public class SelectionPage extends Searchingpage {

    public SelectionPage(WebDriver driver, ExtentTest extTest) {
        super(driver, extTest);
    }

    /**
     * Load the flights search results page using Searchingpage helpers.
     * If Searchingpage already has an enterLandingPlace method you use in search,
     * keep it for initial search. For additional typing on results page, use enterPlaceOnResultsPage.
     */
    public boolean loadPage() {
        openFlightsTab();
        handlePopupIfExists();
        selectRoundTrip();
        enterBoardingPlace("chennai"); // from Searchingpage
        enterLandingPlace("Mumbai");   // from Searchingpage (initial search)
        setTravellersAndClass(1, 0, 1, "Economy");
        clickSearch();

        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.resultsContainer));
            closePriceLockPopupIfPresent();
            Reporter.generateReport(driver, extTest, Status.PASS, "The page has been landed");
            return true;
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to load page: " + e.getMessage());
            return false;
        }
    }

    // -------------------- Popup handling --------------------
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
                WebElement btn = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.presenceOfElementLocated(pLoc));
                try {
                    WebElement clickable = new org.openqa.selenium.support.ui.WebDriverWait(driver, Duration.ofSeconds(2))
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
                // locator not present -> try next
            }
        }
        Reporter.generateReport(driver, extTest, Status.INFO, "Price-lock popup not found or could not be dismissed");
    }

    // -------------------- Generic filters --------------------
    public boolean applyFilter(String filterName) {
        if (filterName == null) filterName = "";
        String normalized = filterName.trim();
        closePriceLockPopupIfPresent();

        String lower = normalized.toLowerCase();
        String xpath = "//*[self::label or self::button or self::span or self::div or self::a]" +
                "[contains(translate(normalize-space(.), 'ABCDEFGHIJKLMNOPQRSTUVWXYZ', 'abcdefghijklmnopqrstuvwxyz'), \"" + lower + "\")]";
        By filterLocator = By.xpath(xpath);

        int attempts = 2;
        for (int i = 0; i < attempts; i++) {
            try {
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(filterLocator));
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

        // fallback: type into any filter-search box if available (common patterns)
        try {
            List<WebElement> boxes = driver.findElements(By.xpath(
                    "//input[contains(@placeholder,'Search') or contains(@aria-label,'search') or contains(@data-testid,'filter-search')]"));
            if (!boxes.isEmpty()) {
                WebElement box = boxes.get(0);
                box.clear();
                box.sendKeys(normalized);
                Thread.sleep(300);
                box.sendKeys(Keys.ENTER);
                WebElement match = wait.until(ExpectedConditions.elementToBeClickable(filterLocator));
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

    // -------------------- Time-of-day filter helpers --------------------
    public boolean applyTimeFilter(String type, String humanLabel) {
        closePriceLockPopupIfPresent();
        try {
            if (humanLabel == null) humanLabel = "";
            String mappedValue = mapTimeLabelToValue(humanLabel);
            String nameAttr = type.equalsIgnoreCase("arrival") ? "landing" : "takeOff";

            // try inputs first (exact value match)
            By checkbox = By.xpath("//input[@name='" + nameAttr + "' and normalize-space(@value)='" + mappedValue + "']");
            try {
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(checkbox));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
                Reporter.generateReport(driver, extTest, Status.PASS,
                        String.format("%s filter applied: %s -> %s", type, humanLabel, mappedValue));
                return true;
            } catch (Exception e) {
                // fallback to text-based label matching
                String textToMatch = humanLabel.trim();
                By textLocator = By.xpath("//*[self::label or self::button or self::span or self::div or self::a]" +
                        "[contains(normalize-space(.), \"" + textToMatch + "\")]");
                WebElement el = wait.until(ExpectedConditions.presenceOfElementLocated(textLocator));
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

    // -------------------- Autocomplete helper for results page --------------------
    /**
     * Enter a city/place in autocomplete on results page and select suggestion.
     *
     * Usage example:
     * By[] candidates = new By[] { By.cssSelector("input[placeholder='To']"), By.xpath("//label[contains(.,'To')]/following::input[1]") };
     * enterPlaceOnResultsPage(candidates, "Mumbai");
     */
    public boolean enterPlaceOnResultsPage(By[] inputLocatorCandidates, String city) {
        try {
            closePriceLockPopupIfPresent();

            WebElement input = null;
            for (By cand : inputLocatorCandidates) {
                try {
                    input = wait.until(ExpectedConditions.elementToBeClickable(cand));
                    if (!"input".equalsIgnoreCase(input.getTagName())) {
                        input = null;
                        continue;
                    }
                    break;
                } catch (Exception ignore) {}
            }

            if (input == null) {
                Reporter.generateReport(driver, extTest, Status.FAIL, "Landing input not found on results page");
                return false;
            }

            // ensure overlay not blocking
            try {
                By overlay = By.cssSelector("div.modal-backdrop, div.portal-overlay");
                wait.withTimeout(Duration.ofSeconds(1)).until(ExpectedConditions.invisibilityOfElementLocated(overlay));
            } catch (Exception ignore) {}

            // Focus and type via Actions
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input); } catch (Exception ignore) {}
            try { input.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input); }
            try { input.clear(); } catch (Exception ignore) {}

            Actions actions = new Actions(driver);
            actions.moveToElement(input).click().pause(Duration.ofMillis(150)).sendKeys(city).perform();

            // Wait for suggestions
            By[] suggestionContainers = new By[] {
                    By.cssSelector("ul[role='listbox']"),
                    By.cssSelector("div[role='listbox']"),
                    By.xpath("//div[contains(@class,'suggestion') or contains(@class,'PopularAirports')]"),
                    By.cssSelector("div.PopularAirports, div.autocomplete-list, div.suggestions")
            };

            WebElement suggestions = null;
            for (By sc : suggestionContainers) {
                try {
                    suggestions = wait.until(ExpectedConditions.visibilityOfElementLocated(sc));
                    break;
                } catch (Exception ignored) {}
            }

            String txt = city.trim().toLowerCase();
            if (suggestions != null) {
                try {
                    WebElement match = suggestions.findElement(By.xpath(
                            ".//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + txt + "')]"));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", match);
                    try { wait.until(ExpectedConditions.elementToBeClickable(match)).click(); }
                    catch (Exception clickEx) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", match); }
                    Reporter.generateReport(driver, extTest, Status.PASS, "Selected suggestion: " + city);
                    return true;
                } catch (Exception ignore) {}
            }

            // global match fallback
            try {
                WebElement global = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + txt + "')]")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", global);
                try { global.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", global); }
                Reporter.generateReport(driver, extTest, Status.PASS, "Selected place via global match: " + city);
                return true;
            } catch (Exception ignored) {}

            // JS fallback: set value + dispatch events
            try {
                String js = "arguments[0].value = arguments[1];" +
                        "arguments[0].dispatchEvent(new Event('input', {bubbles:true}));" +
                        "arguments[0].dispatchEvent(new Event('change', {bubbles:true}));";
                ((JavascriptExecutor) driver).executeScript(js, input, city);
                Thread.sleep(300);

                WebElement afterJs = wait.until(ExpectedConditions.elementToBeClickable(
                        By.xpath("//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + txt + "')]")));
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", afterJs);
                Reporter.generateReport(driver, extTest, Status.PASS, "Selected after JS set: " + city);
                return true;
            } catch (Exception jsEx) {
                // ignore and fail below
            }

            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to type/select landing place on results page: " + city);
            return false;
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Exception while selecting landing place: " + e.getMessage());
            return false;
        }
    }

    // -------------------- Select first flight --------------------
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
                WebElement el = wait.until(ExpectedConditions.elementToBeClickable(cand));
                try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", el); } catch (Exception ignore) {}
                try { el.click(); } catch (Exception clickEx) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el); }
                Reporter.generateReport(driver, extTest, Status.PASS, "Clicked first available flight using " + cand);
                return true;
            } catch (Exception ignore) {
                // try next candidate
            }
        }

        try {
            WebElement results = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.resultsContainer));
            WebElement btn = results.findElement(By.xpath(".//button[.//text() or @aria-label][1]"));
            wait.until(ExpectedConditions.elementToBeClickable(btn));
            try { btn.click(); } catch (Exception clickEx) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", btn); }
            Reporter.generateReport(driver, extTest, Status.PASS, "Clicked first available flight (fallback)");
            return true;
        } catch (Exception ignore) {}

        Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to select first available flight");
        return false;
    }
}
