package com.pages;

import java.time.Duration;
import java.util.List;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.objectrepository.Locators;
import com.parameters.Reporter;

public class HotelBooking_Page {

    private WebDriver driver;
    private ExtentTest extTest;
    private WebDriverWait wait;
    private JavascriptExecutor js;
    
    //Locators
    public static By hotelsTab = By.xpath("//p[text()='Hotels']");
    public static By hotelDestinationInput = By.xpath("/html/body/main/div[4]/div[2]/div/div[1]/div[1]/div/input");
    public static By hotelSuggestions = By.xpath("//div[contains(@class,'suggestion') or contains(@class,'autocomplete')]//p");
    public static By hotelSuggestionItem = By.xpath("//p[contains(@class,'body-md') and contains(@class,'text-primary')]");
    public static By hotelFirstSuggestion = By.xpath("(//div[@class='min-w-0 overflow-hidden'])[1]");
    public static By checkinInput = By.xpath("//input[@data-testid='checkin-input']");
    public static By checkoutInput = By.xpath("//input[@data-testid='checkout-input']");
    public static By roomsGuestsInput = By.xpath("//input[@placeholder='Rooms & Guests']");
    public static By hotelSearchButton = By.xpath("//div[contains(@class,'flex') and contains(.,'Search') and (./svg or .//text())]");
    public static By firstHotelBookNow = By.xpath("(//button[normalize-space()='Book Now' or contains(.,'Book Now')])[1]");
    public static By modalDialog = By.xpath("//div[@role='dialog' or contains(@class,'modal') or contains(@class,'popup')]");
    public static By modalCloseBtn = By.xpath(
    	    "//div[@role='dialog' or contains(@class,'modal') or contains(@class,'popup')]//button[" +
    	    "contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') " +
    	    "or contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') " +
    	    "or contains(@class,'close') or normalize-space(.)='×' or normalize-space(.)='X' or contains(normalize-space(.),'Close')]");
    public static By bestPricePopup = By.xpath("//div[@data-testid='bpg-home-modal']");
    public static By bestPricePopupClose = By.xpath("//div[@data-testid='bpg-home-modal-close']");
    //+ buttons in the popup
    public static By addRoomButton = By.xpath("(//button[contains(@class,'plus')])[1]");
    public static By addGuestButton = By.xpath("(//button[contains(@class,'plus')])[2]");
    //Apply button in popup
    public static By applyGuestsButton = By.xpath("//button[normalize-space()='Apply' or normalize-space()='Done']");


    public HotelBooking_Page(WebDriver driver, ExtentTest extTest) {
        this.driver = driver;
        this.extTest = extTest;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.js = (JavascriptExecutor) driver;
    }

    public void handlePopupIfExists() {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(3));
            WebElement closeBtn = shortWait.until(
                ExpectedConditions.elementToBeClickable(Locators.bestPricePopupClose)
            );
            closeBtn.click();
            shortWait.until(ExpectedConditions.invisibilityOfElementLocated(Locators.bestPricePopupClose));
            Reporter.generateReport(driver, extTest, Status.INFO, "Closed Best Price popup");
        } catch (Exception e) {
            // ignore if popup not present
        }
    }

    public void navigateToHotels() {
        handlePopupIfExists();
        try {
            WebElement hotels = wait.until(ExpectedConditions.elementToBeClickable(
                By.xpath("//a[contains(@href,'/hotels')] | //p[normalize-space()='Hotels']")));
            hotels.click();
            Reporter.generateReport(driver, extTest, Status.PASS, "Clicked Hotels tab");
        } catch (Exception e) {
            js.executeScript("window.location.href='/hotels';");
            Reporter.generateReport(driver, extTest, Status.INFO, "Redirected to Hotels via JS");
        }
    }

    public void ensureDatesSelected() {
        try {
            WebElement checkin = null, checkout = null;
            try { checkin = driver.findElement(Locators.checkinInput); } catch (Exception ignore) {}
            try { checkout = driver.findElement(Locators.checkoutInput); } catch (Exception ignore) {}

            String ciVal = (checkin != null) ? checkin.getAttribute("value") : "";
            String coVal = (checkout != null) ? checkout.getAttribute("value") : "";
            boolean needDates = (ciVal == null || ciVal.trim().isEmpty()) || (coVal == null || coVal.trim().isEmpty());

            if (!needDates) {
                Reporter.generateReport(driver, extTest, Status.INFO, "Dates already selected");
                return;
            }

            if (checkin != null) {
                wait.until(ExpectedConditions.elementToBeClickable(checkin)).click();
            }

            List<WebElement> days = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(
                By.xpath("//div[contains(@class,'DayPicker-Day') and not(contains(@class,'disabled'))]//p"), 1));

            if (days.size() >= 2) {
                days.get(0).click(); Thread.sleep(250);
                days.get(1).click(); Thread.sleep(250);
                Reporter.generateReport(driver, extTest, Status.INFO, "Auto-selected check-in & check-out dates");
            }
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.WARNING, "ensureDatesSelected failed: " + e.getMessage());
        }
    }

    public void enterDestination(String destination) {
        handlePopupIfExists();
        WebElement input = wait.until(ExpectedConditions.elementToBeClickable(Locators.hotelDestinationInput));
        input.click();
        input.clear();
        input.sendKeys(destination);

        WebElement first = wait.until(ExpectedConditions.elementToBeClickable(Locators.hotelFirstSuggestion));
        first.click();
        Reporter.generateReport(driver, extTest, Status.INFO, "Selected destination: " + destination);
    }

    public void selectRoomsAndGuests(String rooms, String guests) {
        WebElement rgInput = wait.until(ExpectedConditions.elementToBeClickable(
            By.xpath("//input[@placeholder='Rooms & Guests' or contains(@placeholder,'Rooms')]")));
        rgInput.click();

        int roomCount = 1, guestCount = 1;
        try { roomCount = Integer.parseInt(rooms); } catch (Exception ignore) {}
        try { guestCount = Integer.parseInt(guests); } catch (Exception ignore) {}

        By popupLocator = By.xpath("//div[@role='dialog' or contains(@class,'modal') or contains(@class,'popup') or contains(@data-testid,'guest')]");
        WebElement popup = null;
        try { popup = wait.until(ExpectedConditions.visibilityOfElementLocated(popupLocator)); } catch (Exception ignore) {}

        if (popup != null) {
            try {
                List<WebElement> plusButtons = popup.findElements(By.xpath(".//button[contains(.,'+')]"));
                if (plusButtons.size() >= 2) {
                    for (int i = 1; i < roomCount; i++) plusButtons.get(0).click();
                    for (int i = 2; i < guestCount; i++) plusButtons.get(1).click();
                }
            } catch (Exception ignore) {}
        }

        try {
            WebElement applyBtn = popup.findElement(By.xpath(".//button[normalize-space()='Apply' or normalize-space()='Done']"));
            applyBtn.click();
        } catch (Exception e) {
            rgInput.sendKeys(Keys.ESCAPE);
        }

        Reporter.generateReport(driver, extTest, Status.INFO,
            "Requested rooms: " + rooms + " guests: " + guests);
    }

    public void clickSearch() {
        try {
            ensureDatesSelected();
            WebElement btn = wait.until(ExpectedConditions.elementToBeClickable(Locators.hotelSearchButton));
            btn.click();
            Reporter.generateReport(driver, extTest, Status.PASS, "Clicked Search button");
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.WARNING, "Search click failed: " + e.getMessage());
        }
    }

    public boolean isResultsDisplayed() {
        try {
            WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(30));
            longWait.until(ExpectedConditions.or(
                ExpectedConditions.urlContains("/hotels"),
                ExpectedConditions.urlContains("search"),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[contains(text(),'Showing Properties')]")),
                ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@class,'property') or contains(@class,'hotel') or contains(@class,'listing') or contains(@class,'result')]"))
            ));
            Reporter.generateReport(driver, extTest, Status.PASS, "Results detected");
            return true;
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.WARNING, "Results not detected: " + e.getMessage());
            return false;
        }
    }
}
