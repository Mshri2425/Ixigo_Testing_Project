package com.stepdefinitions;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.pages.Loginpage;
import com.pages.SearchPage;
import com.parameters.PropertyReader;
import com.setup.Base;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.Then;

import java.util.Properties;

public class SearchDefinitions {

    WebDriver driver = Hooks.driver;
    ExtentTest extTest = Hooks.extTest;

    Loginpage loginPage;
    SearchPage searchingPage;

    Properties prop = PropertyReader.readProperties();

    @Given("the user is on the homepage")
    public void the_user_is_on_the_homepage() {
        // initialize page objects
        loginPage = new Loginpage(driver, extTest);
        searchingPage = new SearchPage(driver, extTest);

        // verify homepage URL
        String act = driver.getCurrentUrl();
        Assert.assertTrue(act.contains("ixigo.com"), "User is not on Ixigo homepage");

        // ensure user is logged in
        String mobileNo = prop.getProperty("loginMobile", "9345535247");
        loginPage.enterMobileNumber(mobileNo);
        loginPage.clickContinueForMobile();
        loginPage.enterOtpManually();
        Base.sleep();
    }

    @When("the user selects Round Trip option")
    public void the_user_selects_round_trip_option() {
        searchingPage.selectingRoundtrip();
    }

    @When("the user enters origin as {string}")
    public void the_user_enters_origin_as(String from) {
        searchingPage.selectingfrom(from);
    }

    @When("the user enters destination as {string}")
    public void the_user_enters_destination_as(String to) {
        searchingPage.selectingto(to);
    }

    @When("the user clicks Search")
    public void the_user_clicks_search() {
        driver.findElement(com.objectrepository.Locators.searchButton).click();
        Base.sleep();
    }

    @Then("search results should be displayed")
    public void search_results_should_be_displayed() {
        boolean ok = driver.findElements(com.objectrepository.Locators.resultsContainer).size() > 0;
        Assert.assertTrue(ok, "Search results not displayed!");
    }
}
