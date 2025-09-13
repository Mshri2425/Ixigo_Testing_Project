package com.stepdefinitions;

import io.cucumber.java.en.*;
import com.pages.TrainSearch_Page;
import com.pages.TrainBooking_Page;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class TrainSearch_definitions {
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

    @Given("the user is on the ixigo homepage")
    public void user_on_homepage() {
        initPages();
        search.openHomePage();
    }

    @Given("the user navigates to the {string} tab")
    public void user_navigates_to_tab(String tabName) {
        initPages();
        search.clickTrainsTab();
    }

    @When("the user enters train origin as {string}")
    public void user_enters_train_origin(String from) {
        initPages();
        search.enterFrom(from);
    }

    @When("the user enters train destination as {string}")
    public void user_enters_train_destination(String to) {
        initPages();
        search.enterTo(to);
    }

    @When("the user selects departure date as {string}")
    public void user_selects_date(String date) {
        initPages();
        search.selectDepartureDate(date);
    }

    @When("the user clicks the Search button")
    public void user_clicks_search() {
        initPages();
        search.clickSearch();
    }

    @Then("train search results should be displayed")
    public void train_results_should_be_displayed() {
        initPages();
        boolean visible = booking.isResultsVisible();
        Assert.assertTrue(visible, "Train search results page is not displayed.");
    }
}
