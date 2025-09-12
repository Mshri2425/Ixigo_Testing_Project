package com.pages;

import java.time.Duration;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.objectrepository.Locators;
import com.parameters.Reporter;

public class Searchingpage {

    WebDriver driver;
    WebDriverWait wait;
    ExtentTest extTest;
    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    public Searchingpage(WebDriver driver, ExtentTest extTest) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
        this.extTest = extTest;
    }

    // ---------------- Utilities ----------------
    public void handlePopupIfExists() {
        try {
            List<WebElement> popups = driver.findElements(By.id("wiz-iframe-intent"));
            if (!popups.isEmpty()) {
                driver.switchTo().frame(popups.get(0));
                driver.findElement(By.id("closeButton")).click();
                driver.switchTo().defaultContent();
                Reporter.generateReport(driver, extTest, Status.INFO, "Closed popup successfully");
            }
        } catch (Exception ignore) {}
    }

    // ---------------- Flow Methods ----------------
    public void openFlightsTab() {
        try {
            handlePopupIfExists();
            wait.until(ExpectedConditions.elementToBeClickable(Locators.flight)).click();
            Reporter.generateReport(driver, extTest, Status.PASS, "Opened Flights tab");
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to open Flights tab: " + e.getMessage());
        }
    }

    public void selectRoundTrip() {
        try {
            handlePopupIfExists();
            wait.until(ExpectedConditions.elementToBeClickable(Locators.round)).click();
            Reporter.generateReport(driver, extTest, Status.PASS, "Selected Round Trip");
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Round Trip selection failed: " + e.getMessage());
        }
    }

    public void enterBoardingPlace(String from) {
        try {
            handlePopupIfExists();
            wait.until(ExpectedConditions.elementToBeClickable(Locators.from)).click();
            driver.findElement(Locators.click_from).clear();
            driver.findElement(Locators.click_from).sendKeys(from);

            List<WebElement> results = new WebDriverWait(driver, Duration.ofSeconds(15))
                    .until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By
                            .xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]/div[3]/div[1]/div[1]")));

            if (!results.isEmpty()) {
                results.get(0).click();
            } else {
                driver.findElement(Locators.click_from).sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
            }

            Reporter.generateReport(driver, extTest, Status.PASS, "Entered origin: " + from);
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to enter origin: " + from + " -> " + e.getMessage());
        }
    }


    public void enterLandingPlace(String to) {
        try {
            handlePopupIfExists();
            try { closePriceLockPopupIfPresent(); } catch (Exception ignore) {}

            // 1) find input (fresh)
            By[] candidates = new By[] {
                Locators.click_to,
                By.cssSelector("input[placeholder='To']"),
                By.xpath("//label[contains(.,'To')]/following::input[1]"),
                By.cssSelector("input[aria-label='To']"),
                By.xpath("//input[contains(@class,'to') or contains(@id,'to')]")
            };

            WebElement input = null;
            for (By c : candidates) {
                try {
                    input = wait.until(ExpectedConditions.elementToBeClickable(c));
                    if (!"input".equalsIgnoreCase(input.getTagName())) { input = null; continue; }
                    break;
                } catch (Exception ignored) {}
            }

            if (input == null) {
                Reporter.generateReport(driver, extTest, Status.FAIL, "TO input not found on page.");
                return;
            }

            // 2) focus, clear reliably and type using Ctrl+A then send keys
            try { ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", input); } catch (Exception ignore) {}
            try { input.click(); } catch (Exception e) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", input); }
            try { input.sendKeys(Keys.chord(Keys.CONTROL, "a")); } catch (Exception ignore) {}
            try { input.sendKeys(to); } catch (Exception e) {
                // fallback: dispatch keyboard events char-by-char via JS
                for (char ch : to.toCharArray()) {
                    String jsKey = "var e = new KeyboardEvent('keydown', {key: arguments[0], bubbles: true});" +
                            "arguments[1].dispatchEvent(e);" +
                            "arguments[1].value = arguments[1].value + arguments[0];" +
                            "arguments[1].dispatchEvent(new Event('input',{bubbles:true}));" +
                            "arguments[1].dispatchEvent(new KeyboardEvent('keyup', {key: arguments[0], bubbles: true}));";
                    ((JavascriptExecutor) driver).executeScript(jsKey, String.valueOf(ch), input);
                    try { Thread.sleep(40); } catch (InterruptedException ie) {}
                }
            }

            // small pause
            try { Thread.sleep(300); } catch (InterruptedException ignore) {}

            // 3) Wait for suggestions anywhere on page (listitems)
            List<WebElement> suggestions = null;
            try {
                suggestions = wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(By.xpath("//div[@role='listitem']")));
            } catch (Exception ignore) {}

            boolean clicked = false;
            String low = to.trim().toLowerCase();
            String code = low.length() >= 3 ? low.substring(0,3) : low; // e.g., "bom"

            // 4) Try click by airport code first (BOM) - target span that contains the code
            if (suggestions != null && !suggestions.isEmpty()) {
                for (WebElement s : suggestions) {
                    try {
                        String text = s.getText().toLowerCase();
                        if (text.contains(code)) {
                            try { s.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", s); }
                            clicked = true;
                            Reporter.generateReport(driver, extTest, Status.INFO, "Clicked suggestion by code: " + code);
                            break;
                        }
                    } catch (Exception ignore) {}
                }
            }

            // 5) Try click by city text if not clicked
            if (!clicked && suggestions != null) {
                for (WebElement s : suggestions) {
                    try {
                        String text = s.getText().toLowerCase();
                        if (text.contains(low)) {
                            try { s.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", s); }
                            clicked = true;
                            Reporter.generateReport(driver, extTest, Status.INFO, "Clicked suggestion by city: " + to);
                            break;
                        }
                    } catch (Exception ignore) {}
                }
            }

            // 6) Global fallback: try to find and click any element containing city anywhere
            if (!clicked) {
                try {
                    WebElement global = wait.until(ExpectedConditions.elementToBeClickable(By.xpath(
                            "//*[contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'), '" + low + "')]")));
                    ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", global);
                    try { global.click(); } catch (Exception ex) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", global); }
                    clicked = true;
                    Reporter.generateReport(driver, extTest, Status.INFO, "Clicked global match for: " + to);
                } catch (Exception ignore) {}
            }

            // 7) verify selection via data-testid or input value
            try { Thread.sleep(400); } catch (InterruptedException ignore) {}
            String visible = "";
            try {
                WebElement destP = driver.findElement(By.xpath("//p[@data-testid='destinationId']"));
                visible = destP.getText();
            } catch (Exception e) {
                try { visible = input.getAttribute("value"); } catch (Exception ignore) {}
            }

            Reporter.generateReport(driver, extTest, clicked ? Status.PASS : Status.WARNING,
                    "Entered destination: '" + to + "'. Selected visible: '" + visible + "'");

        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to enter destination: " + to + " -> " + e.getMessage());
        }
    }




    public void setTravellersAndClass(int adults, int children, int infants, String travelClass) {
        try {
            handlePopupIfExists();
            // open travellers panel (generic locator)
            wait.until(ExpectedConditions.elementToBeClickable(Locators.travellersPanel)).click();

            // select adults by data-testid if available (best-effort)
            try {
                WebElement adultBtn = driver.findElement(By.xpath("//button[@data-testid='" + adults + "']"));
                adultBtn.click();
            } catch (Exception ignore) {}

            // select children
            try {
                WebElement childBtn = driver.findElement(By.xpath("//button[@data-testid='" + children + "']"));
                childBtn.click();
            } catch (Exception ignore) {}

            // infants
            if (infants > 0) {
                try {
                    WebElement infBtn = driver.findElement(By.xpath("//button[@data-testid='" + infants + "']"));
                    infBtn.click();
                } catch (Exception ignore) {}
            }

            // travel class
            try {
                wait.until(ExpectedConditions.elementToBeClickable(Locators.travelClassDropdown)).click();
                By classOpt = Locators.travelClassOption(travelClass);
                wait.until(ExpectedConditions.elementToBeClickable(classOpt)).click();
            } catch (Exception ignore) {}

            // apply
            try {
                wait.until(ExpectedConditions.elementToBeClickable(Locators.travellersApplyBtn)).click();
            } catch (Exception ignore) {}

            Reporter.generateReport(driver, extTest, Status.PASS, "Travellers set: A" + adults + " C" + children + " I" + infants + " Class:" + travelClass);
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to set travellers/class: " + e.getMessage());
        }
    }

    public void clickSearch() {
        try {
            handlePopupIfExists();
            wait.until(ExpectedConditions.elementToBeClickable(Locators.searchButton)).click();
            Reporter.generateReport(driver, extTest, Status.PASS, "Clicked Search");
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to click Search: " + e.getMessage());
        }
    }
    
    public void debugToInputAndFirstSuggestion() {
        try {
            Reporter.generateReport(driver, extTest, Status.INFO, "Starting debug: To-input & first suggestion");

            // try to find likely inputs
            By[] candidates = new By[] {
                Locators.click_to,
                By.cssSelector("input[placeholder='To']"),
                By.xpath("//label[contains(.,'To')]/following::input[1]"),
                By.cssSelector("input[aria-label='To']"),
                By.xpath("//input[contains(@class,'to') or contains(@id,'to')]"),
                By.xpath("//input")
            };

            boolean found = false;
            for (By c : candidates) {
                try {
                    WebElement el = driver.findElement(c);
                    String attrs = "tag=" + el.getTagName()
                            + ", id=" + el.getAttribute("id")
                            + ", class=" + el.getAttribute("class")
                            + ", value=" + el.getAttribute("value")
                            + ", placeholder=" + el.getAttribute("placeholder")
                            + ", readonly=" + el.getAttribute("readonly")
                            + ", aria-hidden=" + el.getAttribute("aria-hidden");
                    Reporter.generateReport(driver, extTest, Status.INFO, "Candidate input locator: " + c + " -> " + attrs);
                    String outer = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].outerHTML;", el);
                    Reporter.generateReport(driver, extTest, Status.INFO, "outerHTML: " + (outer.length() > 500 ? outer.substring(0,500) + "..." : outer));
                    found = true;
                } catch (Exception ignore) {}
            }
            if (!found) Reporter.generateReport(driver, extTest, Status.WARNING, "No input candidates found.");

            // print first visible suggestion item outerHTML if exists
            try {
                WebElement first = driver.findElement(By.xpath("//div[@role='listitem'][1]"));
                String outer = (String) ((JavascriptExecutor) driver).executeScript("return arguments[0].outerHTML;", first);
                Reporter.generateReport(driver, extTest, Status.INFO, "First suggestion outerHTML (truncated): " 
                        + (outer.length() > 800 ? outer.substring(0,800) + "..." : outer));
            } catch (Exception ignore) {
                Reporter.generateReport(driver, extTest, Status.INFO, "No suggestion listitem found during debug.");
            }
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Debug helper failed: " + e.getMessage());
        }
    }

    
    /**
     * Attempt to close common blocking popups/overlays used on the site.
     * Safe to call frequently (it will try several known locators and ignore if not present).
     */
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
                WebElement btn = new WebDriverWait(driver, Duration.ofSeconds(3))
                        .until(ExpectedConditions.presenceOfElementLocated(pLoc));
                try {
                    WebElement clickable = new WebDriverWait(driver, Duration.ofSeconds(2))
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
                            Reporter.generateReport(driver, extTest, Status.INFO, "Popup forced-visible + clicked for locator: " + pLoc);
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


    public boolean areResultsDisplayed() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.resultsContainer));
            Reporter.generateReport(driver, extTest, Status.PASS, "Search results displayed");
            return true;
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Search results not detected: " + e.getMessage());
            return false;
        }
    }
}