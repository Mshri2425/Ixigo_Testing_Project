package com.objectrepository;

import org.openqa.selenium.By;

public class Locators {
	
    public static By loginbutton = By.xpath("//button[text()='Log in/Sign up']");
    public static By mobile = By.xpath("//input[@placeholder='Enter Mobile Number']");
    public static By continuebutton = By.xpath("//button[text()='Continue']");
    public static By otpInputs = By.xpath("//input[@type='tel' or @inputmode='numeric']");
    public static By verify = By.xpath("//*[text()='Verify' or normalize-space()='Verify']");
    public static By flight = By.xpath("//a[@href='/flights']");
    public static By round = By.xpath("//button[text()='Round Trip']");    
    public static By from = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]/div[1]/div");
    public static By click_from = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]/div[2]/div/div/div[2]/input");
    public static By to = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[2]/div[1]/div");

    public static By click_to = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[2]/div[2]/div/div/div[2]/input");

    public static By searchButton = By.xpath("//button[normalize-space()='Search' or contains(@class,'search')]");
    public static By travellersPanel = By.xpath("//div[contains(text(),'Travellers') or contains(@class,'traveller') or contains(@class,'passenger')]");

    public static By childrenPlusBtn = By.xpath("(//button[contains(.,'+')])[2]");
    public static By infantsPlusBtn = By.xpath("(//button[contains(.,'+')])[3]");
    public static By travellersApplyBtn = By.xpath("//button[normalize-space()='Apply' or normalize-space()='Done']");

    public static By travelClassDropdown = By.xpath("//div[contains(@class,'cabin') or contains(.,'Class') or contains(@data-testid,'cabin')]");
    public static By resultsContainer = By.xpath("//p[text()='Filters']");
    public static By dept = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[2]/div[2]/div/div");
    public static By ret = By.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[2]/div[1]/div/div/div");
    
    public static By monthYearHeader = By.xpath("//div[contains(@class,'DayPicker-Caption')]");
    public static By calendarNextBtn = By.xpath("//span[@aria-label='Next Month']");
    public static By calendarPrevBtn = By.xpath("//span[@aria-label='Previous Month']");
    public static By dayCell(String day) {
        return By.xpath("//div[contains(@class,'DayPicker-Day') and not(contains(@class,'disabled'))]//p[text()='" + day + "']");
    }
    public static By dayBlock(String day) {
        return By.xpath("//div[contains(@class,'DayPicker-Day') and not(contains(@class,'disabled'))]//p[text()='" + day + "']/..");
    }
    public static By dayPrice(String day) {
        return By.xpath("//div[contains(@class,'DayPicker-Day') and not(contains(@class,'disabled'))]//p[text()='" + day + "']/following-sibling::p");
    }
    public static By yearLabel = By.xpath("//div[contains(@class,'DayPicker-Caption')]");

    
    
    public static By travelClassOption(String cls) {
        String xpath = String.format(
            "//li[normalize-space(.)='%s'] | " +
            "//div[contains(@class,'option') and normalize-space(.)='%s'] | " +
            "//button[normalize-space(.)='%s']",
            cls, cls, cls
        );
        return By.xpath(xpath);
    }

    // ------------------- Airport Cabs Locators -------------------

    public static By airportCabs = By.xpath("//a[@data-testid='submenu-item' and .//p[normalize-space()='Airport Cabs']]");
    public static By homeToAirport = By.xpath("//label[contains(.,'Home To Airport') or contains(.,'Home To Aiport')]");
    public static By fromLocationInput = By.xpath("//input[@id='from-input' and @placeholder='Pick Up Location']");
    public static By fromSuggestionMAA = By.xpath("//ul[contains(@class,'auto-from')]//li[contains(.,'Chennai International Airport (MAA)')]");
    public static By toLocationInput = By.xpath("//input[@id='to-input' and contains(@placeholder,'Search Nearest Airport')]");
    public static By toSuggestionBOM_T1 = By.xpath("//ul[@id='to-options']//li[contains(normalize-space(.),'Mumbai - CSMI Airport-T1')]");
    public static By pickupDateDisplay = By.xpath("//*[@id='display-date']");
    public static By pickupDateHiddenInput = By.xpath("//input[@id='hidden-date-input']");
    public static By pickupTimeDisplay = By.xpath("//*[@id='displayed-time']");
    public static By pickupTimeInput = By.xpath("//input[@id='pickup-time' and @type='time']");
    public static By searchCabsButton = By.xpath("//button[@id='submit-btn' and normalize-space()='SEARCH']");
    
    // Profile / Logout locators
    public static final String profileMenu = "(//button[contains(@class,'profile') or contains(@aria-label,'profile') or contains(.,'My account') or contains(.,'Hi')])[1]";
    public static final String logoutElement = "(//a[normalize-space()='Logout'] | //a[normalize-space()='Log out'] | //button[normalize-space()='Logout'] | //button[normalize-space()='Log out'] | //a[normalize-space()='Sign out'])[1]";
    public static final String loginButton = "//button[normalize-space()='Log in/Sign up']";

    // ------------------- Cab Selection Locators -------------------

    public static By hatchbackOption = By.xpath("//*[@id='filter-form']/div[1]/label");
    public static By gozoBookNow = By.xpath("//button[@class='submit-btn book-btn' and @data-partner='GOZO CABS' and @data-type='Hatchback']");

    // ------------------- Hotel Booking Locators (add these) -------------------

    public static By hotelsTab = By.xpath("//p[text()='Hotels']");

    // Destination input on Hotels form
    public static By hotelDestinationInput = By.xpath("/html/body/main/div[4]/div[2]/div/div[1]/div[1]/div/input");

    public static By hotelSuggestions = By.xpath("//div[contains(@class,'suggestion') or contains(@class,'autocomplete')]//p");
    public static By hotelSuggestionItem = By.xpath("//p[contains(@class,'body-md') and contains(@class,'text-primary')]");

    public static By hotelFirstSuggestion = By.xpath("(//div[@class='min-w-0 overflow-hidden'])[1]");

    // Check-in / Check-out visible inputs 
    public static By checkinInput = By.xpath("//input[@data-testid='checkin-input']");
    public static By checkoutInput = By.xpath("//input[@data-testid='checkout-input']");

    //Rooms & Guests input 
    public static By roomsGuestsInput = By.xpath("//input[@placeholder='Rooms & Guests']");

    //Hotel Search button 
    public static By hotelSearchButton = By.xpath("//div[contains(@class,'flex') and contains(.,'Search') and (./svg or .//text())]");

    //First hotel's Book Now button 
    public static By firstHotelBookNow = By.xpath("(//button[normalize-space()='Book Now' or contains(.,'Book Now')])[1]");

    //------------------- Popup / modal helpers-------------------

    //Dialog container 
    public static By modalDialog = By.xpath("//div[@role='dialog' or contains(@class,'modal') or contains(@class,'popup')]");

    //Close button inside modal 
    public static By modalCloseBtn = By.xpath(
    "//div[@role='dialog' or contains(@class,'modal') or contains(@class,'popup')]//button[" +
    "contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') " +
    "or contains(translate(@title,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'close') " +
    "or contains(@class,'close') or normalize-space(.)='×' or normalize-space(.)='X' or contains(normalize-space(.),'Close')]");
  
    //Best Price Guarantee modal 
    public static By bestPricePopup = By.xpath("//div[@data-testid='bpg-home-modal']");
    public static By bestPricePopupClose = By.xpath("//div[@data-testid='bpg-home-modal-close']");

    //+ buttons in the popup
    public static By addRoomButton = By.xpath("(//button[contains(@class,'plus')])[1]");
    public static By addGuestButton = By.xpath("(//button[contains(@class,'plus')])[2]");

    //Apply button in popup
    public static By applyGuestsButton = By.xpath("//button[normalize-space()='Apply' or normalize-space()='Done']");


   // Non-Stop filter (wrapper and input)
   public static By nonStopInput = By.xpath("//input[@name='stops' and (normalize-space(@value)='0' or @value='0')]");
   public static By nonStopWrapper = By.xpath("//input[@name='stops' and (normalize-space(@value)='0' or @value='0')]/ancestor::span[1]");
   public static By nonStopAbsolute = By.xpath("/html/body/div[3]/div[2]/div[2]/div/div[1]/div/div[2]/div[1]/section[1]/div/div/div[2]/span");

   // Airline: IndiGo
   public static By airlineIndiGoByText = By.xpath(
       "//*[self::label or self::div or self::span or self::button][contains(translate(normalize-space(.),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'indigo')]");
   public static By airlineIndiGoCheckbox = By.xpath(
       "//input[@type='checkbox' and (contains(translate(@value,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'indigo') or contains(translate(@aria-label,'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),'indigo'))]");

   // ------------------- Flight Filters  -------------------

   // IndiGo filter
   public static By indigoInput = By.xpath("//input[@type='checkbox' and @name='airlines' and @value='6E']");
   public static By indigoWrapper = By.xpath("//input[@type='checkbox' and @name='airlines' and @value='6E']/ancestor::span[1]");

}
