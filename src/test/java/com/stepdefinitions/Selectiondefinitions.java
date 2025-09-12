package com.stepdefinitions;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.pages.SelectionPage;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;

public class Selectiondefinitions {

    WebDriver driver = Hooks.driver;
    ExtentTest extTest = Hooks.extTest;
    SelectionPage selectionPage;

    @Given("the user is on the selection page")
    public void the_user_is_on_the_selection_page() {
        selectionPage = new SelectionPage(driver, extTest);
        boolean loaded = selectionPage.loadPage();
        Assert.assertTrue(loaded, "Page did not load successfully");
        System.out.println("The page has been loaded");
    }

    @Given("the user clicks third filter departure as {string}")
    public void the_user_clicks_third_filter_departure_as(String departure) {
        boolean applied = selectionPage.applyTimeFilter("departure", departure);
        Assert.assertTrue(applied, "Failed to apply Departure Filter: " + departure);
    }

    @Given("the user clicks fourth filter arrival as {string}")
    public void the_user_clicks_fourth_filter_arrival_as(String arrival) {
        boolean applied = selectionPage.applyTimeFilter("arrival", arrival);
        Assert.assertTrue(applied, "Failed to apply Arrival Filter: " + arrival);
    }

    @Then("select the first available flight")
    public void select_the_first_available_flight() {
        boolean selected = selectionPage.selectFirstAvailableFlight();
        Assert.assertTrue(selected, "Failed to select the first available flight");
    }
}
