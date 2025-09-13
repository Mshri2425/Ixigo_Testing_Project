package com.stepdefinitions;

import io.cucumber.java.en.*;
import com.pages.TrainSearch_Page;
import com.pages.TrainBooking_Page;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class TrainBooking_definitions {
    WebDriver driver = Hooks.driver;
    private TrainSearch_Page search;
    private TrainBooking_Page booking;

    private void initPages() {
        if (search == null) {
            if (driver == null) throw new IllegalStateException("Hooks.driver is null. Ensure Hooks @Before runs before steps.");
            search = new TrainSearch_Page(driver);
            booking = new TrainBooking_Page(driver);
        }
    }

    @Given("the user has searched trains from {string} to {string} on {string}")
    public void user_has_searched_trains(String from, String to, String date) {
        initPages();
        search.openHomePage();
        search.clickTrainsTab();
        search.enterFrom(from);
        search.enterTo(to);
        search.selectDepartureDate(date);
        search.clickSearch();
        booking.waitForResults();
    }

    @When("the user selects the first available train")
    public void user_selects_first_train() {
        initPages();
        // Currently selecting class 2A will implicitly select the train row
    }

    @When("the user chooses class {string}")
    public void user_chooses_class(String className) {
        initPages();
        if (className.equalsIgnoreCase("2A") || className.equalsIgnoreCase("2AC")) {
            booking.selectClass2A();
        } else {
            throw new UnsupportedOperationException("Class selection for " + className + " not implemented.");
        }
    }

    @When("the user clicks on Show Availability")
    public void user_clicks_show_availability() {
        initPages();
        // No-op for now, because selectClass2A() opens availability directly
    }

    @When("the user clicks on Book for the first available option")
    public void user_clicks_book_first_available() {
        initPages();
        booking.clickFirstBookButton();
    }

    @Then("the login popup should be displayed")
    public void login_popup_should_be_displayed() {
        initPages();
        boolean loginVisible = booking.isLoginPopupVisible();
        Assert.assertTrue(loginVisible, "Login popup was not displayed after clicking BOOK.");
    }

    @Then("the test should stop")
    public void test_should_stop() {
        // test ends here
    }
}
