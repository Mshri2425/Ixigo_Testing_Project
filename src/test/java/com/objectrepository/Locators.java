package com.objectrepository;

import org.openqa.selenium.By;

/**
 * Centralized locators for Ixigo project.
 * - Preserves your original login locators exactly.
 * - Adjusted the new search-related locators to reuse existing, stable locators from the file.
 */
public class Locators {
    // ---------- original login locators (kept as you gave) ----------
    public static By loginbutton = By.xpath("//button[text()='Log in/Sign up']");
    public static By mobile = By.xpath("//input[@placeholder='Enter Mobile Number']");
    public static By continuebutton = By.xpath("//button[text()='Continue']");
    public static By otpInputs = By.xpath("//input[@type='tel' or @inputmode='numeric']");
    public static By verify = By.xpath("//*[text()='Verify' or normalize-space()='Verify']");

    // ---------- original flight locators you provided ----------
    public static By flight = By.xpath("//a[@href='/flights']");
    public static By round = By.xpath("//button[text()='Round Trip']");	
    public static By from = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]/div[1]/div");
    public static By click_from = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]/div[2]/div/div/div[2]/input");
    public static By to = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[2]/div[1]/div");
    public static By click_to = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[2]/div[2]/div/div/div[2]/input");
    public static By departure_btn = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[2]/div[1]/div/div/div/div/p[2]");
    public static By dep_month = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[2]/div[3]/div/div[1]/div[1]/button[2]/span[1]");
    public static By dep_year = By.xpath("//span[text()='2025']");

    // --- Changed the NEW locators to reuse existing stable locators on the page ---
    // Use the existing 'from' and input 'click_from' as the clickable wrapper / input
    public static By frmbtn = from;          // alias to existing 'from' locator
    public static By fromPlace = click_from; // alias to existing 'click_from' locator

    // Use the existing 'to' and input 'click_to' as the clickable wrapper / input
    public static By tobtn = to;             // alias to existing 'to' locator
    public static By toPlace = click_to;     // alias to existing 'click_to' locator

    // ---------- added locators for search flow (adjusted to reuse existing ones) ----------
    public static By searchButton = By.xpath("//button[normalize-space()='Search' or contains(@class,'search')]");
    public static By calendarNextBtn = By.xpath("//button[contains(@aria-label,'Next') or contains(@class,'next')]");
    // reuse existing dep_month as primary calendar header locator
    public static By calendarMonthYear = dep_month;
    public static By travellersPanel = By.xpath("//div[contains(text(),'Travellers') or contains(@class,'traveller') or contains(@class,'passenger')]");
    public static By adultsPlusBtn = By.xpath("(//button[contains(.,'+')])[1]");
    public static By childrenPlusBtn = By.xpath("(//button[contains(.,'+')])[2]");
    public static By infantsPlusBtn = By.xpath("(//button[contains(.,'+')])[3]");
    public static By travellersApplyBtn = By.xpath("//button[normalize-space()='Apply' or normalize-space()='Done']");

    public static By travelClassDropdown = By.xpath("//div[contains(@class,'cabin') or contains(.,'Class') or contains(@data-testid,'cabin')]");
    // results container generic (keeps your original combined expression)
    public static By resultsContainer = By.xpath("//div[contains(@class,'listing') or contains(@class,'results') or contains(@id,'results') or //h1[contains(.,'Flights')]]");

    // helper dynamic locators
    public static By dayCell(String day) {
        // looks for day cells represented by <p>, <div> or <span> with exact visible text equal to day.
        String xpath1 = "//p[normalize-space()='" + day + "']";
        String xpath2 = "//div[normalize-space()='" + day + "']";
        String xpath3 = "//span[normalize-space()='" + day + "']";
        return By.xpath(xpath1 + " | " + xpath2 + " | " + xpath3);
    }

    public static By travelClassOption(String cls) {
        String xpath1 = "//li[normalize-space()='" + cls + "']";
        String xpath2 = "//div[contains(@class,'option') and normalize-space()='" + cls + "']";
        String xpath3 = "//button[normalize-space()='" + cls + "']";
        return By.xpath(xpath1 + " | " + xpath2 + " | " + xpath3);
    }

    // Flight card container (used to find all result cards)
    public static By flightCard = By.cssSelector(".flight-card");

    // Elements inside a flight card
    public static By airlineNameInCard = By.cssSelector(".airline-name");
    public static By stopsInCard = By.cssSelector(".stops");
    public static By departureTimeInCard = By.cssSelector(".departure-time");
    public static By arrivalTimeInCard = By.cssSelector(".arrival-time");
    public static By priceInCard = By.cssSelector(".price");

    // Non-Stop filter: prefer label-based click, fallback to checkbox value '0'
    public static By nonStopFilter = By.xpath(
        "//label[contains(., 'Non-Stop')]/input | //input[@type='checkbox' and @value='0']"
    );
    
    public static By return_btn = By.xpath(
    	    "//div[normalize-space()='Return' or contains(@aria-label,'Return') or contains(@class,'return')]" 
    	    + " | //p[normalize-space()='Return']"
    	);

    // IndiGo airline filter: prefer label text, fallback to checkbox value '6E'
    public static By indigoFilter = By.xpath(
        "//label[contains(., 'IndiGo')]/input | //input[@type='checkbox' and @value='6E']"
    );

    // Departure-time filter for MAA -> "Before 6AM"
    public static By maaBefore6amFilter = By.xpath(
        "//input[@type='checkbox' and @name='takeOff' and @value='EARLY_MORNING'] | " +
        "//*[contains(., 'MAA')]/following::label[contains(., 'Before 6AM')][1]/input | " +
        "//label[contains(., 'Before 6AM') and contains(., 'MAA')]/input"
    );

    // Arrival-time filter for PNQ -> "Before 6AM"
    public static By pnqBefore6amFilter = By.xpath(
        "//input[@type='checkbox' and @name='landing' and @value='EARLY_MORNING'] | " +
        "//*[contains(., 'PNQ')]/following::label[contains(., 'Before 6AM')][1]/input | " +
        "//label[contains(., 'Before 6AM') and contains(., 'PNQ')]/input"
    );

    // First flight selection button: prefer visible "Book" button, fallback to a button inside the first flight card
    public static By firstFlightSelectButton = By.xpath(
        "(//button[normalize-space()='Book' or contains(normalize-space(.), 'Book')])[1] | " +
        "(//div[contains(@class,'flight-card') or contains(@class,'listing-card')])[1]//button[normalize-space()='Book' or contains(normalize-space(.), 'Book')]"
    );
}
