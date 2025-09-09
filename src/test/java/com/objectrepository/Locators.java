package com.objectrepository;

import org.openqa.selenium.By;

public class Locators {

    // ---------- Login ----------
	public static By Loginbutton=By.xpath("//button[text()='Log in/Sign up']");
    public static By mobile=By.xpath("//input[@placeholder='Enter Mobile Number']");
    public static By continuebutton=By.xpath("//button[text()='Continue']");
    public static By otpInputs=By.xpath("//input[@type='tel' or @inputmode='numeric']");
    public static By verify = By.xpath("//*[text()='Verify' or normalize-space()='Verify']");
    
    // ---------- Flight Search (trip type) ----------
    public static By flight = By.xpath("//p[normalize-space()='Flights' or contains(.,'Flights')]");
    public static By oneWay = By.xpath("//button[normalize-space()='One Way']");
    public static By round = By.xpath("//button[normalize-space()='Round Trip']");

    // ---------- From / To (top-level visible fields) ----------
    // used to open the From/To picker (clickable visible element)
    public static By from = By.xpath("//p[@data-testid='originId' or contains(.,'From') or //div[contains(@class,'origin')]]");
    public static By to   = By.xpath("//p[@data-testid='destinationId' or contains(.,'To') or //div[contains(@class,'destination')]]");

    // ---------- From / To popup input fields (where you type text) ----------
    // Prefer label-sibling pattern for stability
    public static By click_from = By.xpath("//label[normalize-space()='From']/following-sibling::input | //div[.//label[normalize-space()='From']]//input");
    public static By click_to   = By.xpath("//label[normalize-space()='To']/following-sibling::input   | //div[.//label[normalize-space()='To']]//input");

    // ---------- Autosuggest / suggestions ----------
    public static By citySuggestionItems = By.xpath("//div[@role='listitem' or @role='option' or contains(@class,'list-item') or contains(@class,'suggest')]");
    public static By cityFirstSuggestion = By.xpath("(//div[@role='listitem' or @role='option' or contains(@class,'list-item') or contains(@class,'suggest')])[1]");

    // Generic autosuggest containers (fallback)
    public static By autosuggestList = By.xpath("//ul[@role='listbox' or contains(@class,'autosuggest') or contains(@class,'suggest')]");
    public static By autosuggestItem = By.xpath("//ul[@role='listbox']//li | //div[@role='listitem']");

    // ---------- Dates & calendar ----------
    public static By deptbtn = By.xpath("//p[normalize-space()='Departure'] | //div[.//p[normalize-space()='Departure']]");
    public static By departureLabel = By.xpath("//p[normalize-space()='Departure']");
    public static By returnLabel = By.xpath("//p[normalize-space()='Return']");

    // The element that shows current month + year in the calendar widget.
    public static By monthYear = By.xpath(
        "//div[contains(@class,'Month') or contains(@class,'month') or contains(@class,'Calendar') or contains(@class,'calendar')]//p[normalize-space() and (contains(.,'20') or contains(.,'Jan') or contains(.,'Feb') or contains(.,'Mar') or contains(.,'Apr') or contains(.,'May') or contains(.,'Jun') or contains(.,'Jul') or contains(.,'Aug') or contains(.,'Sep') or contains(.,'Oct') or contains(.,'Nov') or contains(.,'Dec'))]"
    );
    public static By nextBtn = By.xpath("//button[@aria-label='Next' or contains(@class,'next') or contains(.,'Next') or contains(@data-testid,'next')]");
    public static By prevBtn = By.xpath("//button[@aria-label='Previous' or contains(@class,'prev') or contains(.,'Prev') or contains(@data-testid,'prev')]");

    // ---------- Travellers & Class ----------
    public static By travellersLabel = By.xpath("//p[normalize-space()='Travellers & Class' or contains(.,'Travellers')]");
    public static By adultsLabel = By.xpath("//p[normalize-space()='Adults']");
    public static By childrenLabel = By.xpath("//p[normalize-space()='Children']");
    public static By infantsLabel = By.xpath("//p[normalize-space()='Infants']");

    public static By adultsIncrease = By.xpath("//div[.//p[normalize-space()='Adults']]//button[contains(.,'+') or contains(@aria-label,'increase') or contains(@class,'inc')]");
    public static By childrenIncrease = By.xpath("//div[.//p[normalize-space()='Children']]//button[contains(.,'+') or contains(@aria-label,'increase') or contains(@class,'inc')]");
    public static By infantsIncrease = By.xpath("//div[.//p[normalize-space()='Infants']]//button[contains(.,'+') or contains(@aria-label,'increase') or contains(@class,'inc')]");

    public static By classEconomy = By.xpath("//span[normalize-space()='Economy']");
    public static By classPremiumEconomy = By.xpath("//span[normalize-space()='Premium Economy' or contains(.,'Premium')]");
    public static By classBusiness = By.xpath("//span[normalize-space()='Business']");

    public static By doneTravellersBtn = By.xpath("//button[normalize-space()='Done' or normalize-space()='Apply' or contains(.,'Done') or contains(.,'Apply')]");

    // ---------- Search ----------
    public static By searchButton = By.xpath("//button[normalize-space()='Search' or contains(@class,'Search')]");
    public static By resultsContainer = By.xpath("//*[contains(@class,'searchResults') or contains(@class,'flightResults') or contains(.,'Select') or contains(.,'Results')]");

    // ---------- Misc / fallback helpers ----------
    public static By byExactText(String text) {
        return By.xpath("//*[normalize-space()='" + text + "']");
    }
}
