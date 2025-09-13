package com.pages;

import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.File;
import java.io.IOException;
import java.nio.file.StandardCopyOption;
import java.time.Duration;

public class TrainSearch_Page {
    private WebDriver driver;
    private WebDriverWait wait;

    //Locators
    private By trainsTab = By.xpath("//p[normalize-space()='Trains']");
    private By trainsLink = By.xpath("//a[.//p[normalize-space()='Trains'] or contains(.,'Trains')]");
    private By fromInput = By.xpath("//*[@id='origin-destination-input']/div[1]//input[@placeholder='Enter Origin']");
    private By fromSuggestionItem = By.xpath("//p[normalize-space()='Chennai - All stations(MAS)']");
    private By toInput = By.xpath("//*[@id='origin-destination-input']/div[2]//input[@placeholder='Enter Destination']");
    private By toSuggestionItem = By.xpath("//p[normalize-space()='Pune Jn (PUNE)']");
    private By datePickerOpener = By.xpath("//span[contains(@class,'body-lg')]");
    private String dateCellXpath = "//abbr[@aria-label='%s']"; // format with full date string like "October 22, 2025"
    private By searchButton = By.xpath("//button[@data-testid='book-train-tickets']");

    public TrainSearch_Page(WebDriver driver) {
        if (driver == null) throw new IllegalArgumentException("WebDriver passed to TrainSearchPage is null");
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
    }

    //Open page and wait for visible Trains tab 
    public void openHomePage() {
        driver.get("https://www.ixigo.com");
        // Wait for the Trains tab itself to be present (no reliance on <nav>)
        wait.until(ExpectedConditions.presenceOfElementLocated(trainsTab));
    }

    public void clickTrainsTab() {
        closePopupIfExists();

        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(8));

        try {
            try {
                WebElement link = shortWait.until(ExpectedConditions.elementToBeClickable(trainsLink));
                safeClick(link);
                wait.until(ExpectedConditions.visibilityOfElementLocated(fromInput));
                return;
            } catch (Exception ignored) {}

            WebElement p = shortWait.until(ExpectedConditions.presenceOfElementLocated(trainsTab));
            ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", p);

            try {
                new Actions(driver).moveToElement(p).click().perform();
                wait.until(ExpectedConditions.visibilityOfElementLocated(fromInput));
                return;
            } catch (Exception ignored) {}

            try {
                WebElement linkAncestor = driver.findElement(trainsLink);
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", linkAncestor);
                wait.until(ExpectedConditions.visibilityOfElementLocated(fromInput));
                return;
            } catch (Exception ignored) {}

            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", p);
                wait.until(ExpectedConditions.visibilityOfElementLocated(fromInput));
                return;
            } catch (Exception finalEx) {
                takeScreenshot("clickTrainsTab_fail");
                throw new RuntimeException("Failed to click Trains tab using multiple strategies. See screenshot clickTrainsTab_fail.png", finalEx);
            }
        } catch (TimeoutException te) {
            takeScreenshot("clickTrainsTab_timeout");
            throw new RuntimeException("Trains tab not found/clickable. Screenshot: clickTrainsTab_timeout.png", te);
        }
    }

    public void enterFrom(String originText) {
        // precise input under origin-destination-input first block
        By fromInputLocator = By.xpath("//*[@id='origin-destination-input']/div[1]//input[@placeholder='Enter Origin']");
        By suggestionContains = By.xpath("//div[@id and .//p][.//p[contains(normalize-space(.), \"" + escapeForXpath(originText) + "\")]]");
        By suggestionP = By.xpath("//p[contains(normalize-space(.), \"" + escapeForXpath(originText) + "\")]");

        try {
            WebElement from = wait.until(ExpectedConditions.elementToBeClickable(fromInputLocator));
            // focus and clear
            try { from.click(); } catch (Exception ignored) {}
            from.clear();

            // type char-by-char to mimic user (helps some JS listeners)
            for (char c : originText.toCharArray()) {
                from.sendKeys(String.valueOf(c));
                Thread.sleep(80); // small delay
            }

            // wait for suggestion container item that contains the text
            try {
                WebElement suggestion = wait.until(ExpectedConditions.elementToBeClickable(suggestionContains));
                safeClick(suggestion);
                System.out.println("Selected origin suggestion by container for: " + originText);
                return;
            } catch (Exception e) {
                
            }

            try {
                WebElement p = wait.until(ExpectedConditions.elementToBeClickable(suggestionP));
                safeClick(p);
                System.out.println("Selected origin suggestion by p for: " + originText);
                return;
            } catch (Exception e) {
                // fallback to keyboard selection
            }

            // fallback: press ARROW_DOWN + ENTER to choose first suggestion
            from.sendKeys(Keys.ARROW_DOWN);
            from.sendKeys(Keys.ENTER);
            System.out.println("Fallback: used keyboard to select origin for: " + originText);

        } catch (Exception ex) {
            takeScreenshot("enterFrom_fail");
            throw new RuntimeException("Failed to enter/select origin: " + originText, ex);
        }
    }

    public void enterTo(String toText) {
        By toInputLocator = By.xpath("//*[@id='origin-destination-input']/div[2]//input[@placeholder='Enter Destination']");
        By suggestionContains = By.xpath("//div[@id and .//p][.//p[contains(normalize-space(.), \"" + escapeForXpath(toText) + "\")]]");
        By suggestionP = By.xpath("//p[contains(normalize-space(.), \"" + escapeForXpath(toText) + "\")]");

        try {
            WebElement to = wait.until(ExpectedConditions.elementToBeClickable(toInputLocator));
            try { to.click(); } catch (Exception ignored) {}
            to.clear();

            for (char c : toText.toCharArray()) {
                to.sendKeys(String.valueOf(c));
                Thread.sleep(80);
            }

            try {
                WebElement suggestion = wait.until(ExpectedConditions.elementToBeClickable(suggestionContains));
                safeClick(suggestion);
                System.out.println("Selected destination suggestion by container for: " + toText);
                return;
            } catch (Exception e) {}

            try {
                WebElement p = wait.until(ExpectedConditions.elementToBeClickable(suggestionP));
                safeClick(p);
                System.out.println("Selected destination suggestion by p for: " + toText);
                return;
            } catch (Exception e) {}

            // fallback keyboard
            to.sendKeys(Keys.ARROW_DOWN);
            to.sendKeys(Keys.ENTER);
            System.out.println("Fallback: used keyboard to select destination for: " + toText);

        } catch (Exception ex) {
            takeScreenshot("enterTo_fail");
            throw new RuntimeException("Failed to enter/select destination: " + toText, ex);
        }
    }

    private String escapeForXpath(String s) {
        if (s == null) return "";
        return s.replace("\"", "\\\""); 
    }

    public void selectDepartureDate(String dateIso /* e.g. "2025-10-22" */) {
        wait.until(ExpectedConditions.elementToBeClickable(datePickerOpener)).click();

        // convert dateIso to aria-label format like "October 22, 2025"
        String[] parts = dateIso.split("-");
        if (parts.length != 3) throw new IllegalArgumentException("dateIso must be YYYY-MM-DD");
        String year = parts[0];
        String month = parts[1];
        String day = parts[2];
        String monthName = getMonthName(Integer.parseInt(month));
        String ariaLabel = monthName + " " + Integer.parseInt(day) + ", " + year;

        By ariaDate = By.xpath(String.format(dateCellXpath, ariaLabel));
        WebElement dateCell = wait.until(ExpectedConditions.elementToBeClickable(ariaDate));
        dateCell.click();
    }

    public void clickSearch() {
        WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(searchButton));
        safeClick(btn);
    }

    //Helpers 
    private void waitABitForSuggestions() {
        try {
            Thread.sleep(500);
        } catch (InterruptedException ignored) {}
    }

    private String getMonthName(int month) {
        switch (month) {
            case 1:  return "January";
            case 2:  return "February";
            case 3:  return "March";
            case 4:  return "April";
            case 5:  return "May";
            case 6:  return "June";
            case 7:  return "July";
            case 8:  return "August";
            case 9:  return "September";
            case 10: return "October";
            case 11: return "November";
            case 12: return "December";
            default: throw new IllegalArgumentException("Invalid month: " + month);
        }
    }

    private void safeClick(WebElement el) {
        try {
            el.click();
        } catch (Exception e) {
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            } catch (Exception ex) {
                throw new RuntimeException("safeClick failed", ex);
            }
        }
    }

    private void closePopupIfExists() {
        WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
        By[] closeSelectors = new By[] {
            By.cssSelector(".close-popup"),
            By.cssSelector(".ixi-icon-cross"),
            By.xpath("//button[contains(., 'Accept') or contains(., 'OK') or contains(., 'Allow')]"),
            By.xpath("//button[contains(translate(., 'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'got it')]"),
            By.xpath("//div[contains(@class,'cookie') or contains(@class,'consent')]//button")
        };

        for (By sel : closeSelectors) {
            try {
                WebElement e = shortWait.until(ExpectedConditions.elementToBeClickable(sel));
                if (e.isDisplayed()) {
                    try { e.click(); System.out.println("Closed overlay using: " + sel); }
                    catch (Exception clickEx) { ((JavascriptExecutor) driver).executeScript("arguments[0].click();", e); }
                    return; // once we close one overlay, proceed
                }
            } catch (Exception ignored) {
                // not found, try next
            }
        }
    }

    //Takes a screenshot 
     
    private void takeScreenshot(String name) {
        try {
            File src = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            String path = System.getProperty("user.dir") + File.separator + "screenshots" + File.separator + name + ".png";
            File dest = new File(path);
            dest.getParentFile().mkdirs();
            java.nio.file.Files.copy(src.toPath(), dest.toPath(), StandardCopyOption.REPLACE_EXISTING);
            System.out.println("Saved screenshot: " + path);
        } catch (IOException | WebDriverException e) {
            System.err.println("Failed to take screenshot: " + e.getMessage());
        }
    }
}
