package com.stepdefinitions;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import com.aventstack.extentreports.ExtentTest;
import com.pages.HotelBooking_Page;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

public class HotelBooking_definitions {

    WebDriver driver = Hooks.driver;
    ExtentTest extTest = Hooks.extTest;
    HotelBooking_Page hotelPage;

    static String[][] excelData;   

    @When("user navigates to Hotels section")
    public void user_navigates_to_hotels_section() {
        hotelPage = new HotelBooking_Page(driver, extTest);
        hotelPage.navigateToHotels();
    }

    @When("user enters destination {string}")
    public void user_enters_destination(String destination) {
        int row = Hooks.currentrow;
        destination = Hooks.excelData[row][0];
        hotelPage.enterDestination(destination);
    }

    @When("user selects {string} room and {string} guests")
    public void user_selects_room_and_guests(String rooms, String guests) {
        int row = Hooks.currentrow;
        rooms = Hooks.excelData[row][1];
        guests = Hooks.excelData[row][2];
        hotelPage.selectRoomsAndGuests(rooms, guests);
    }

    @When("user clicks Search")
    public void user_clicks_search() {
        hotelPage.clickSearch();
    }

    @Then("search results for {string} should be displayed")
    public void search_results_for_should_be_displayed(String destination) {
        int row = Hooks.currentrow;
        destination = Hooks.excelData[row][0];
        Assert.assertTrue(hotelPage.isResultsDisplayed(), "Search results not displayed for: " + destination);
    }
}
