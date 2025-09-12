package com.stepdefinitions;

import com.pages.Searchingpage;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.When;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import org.testng.Assert;

public class searchingdefinitions {

    Searchingpage sp = new Searchingpage(Hooks.driver, Hooks.extTest);

    @Given("the user is on the homepage")
    public void the_user_is_on_the_homepage() {
        // Hooks already opens the site, nothing extra needed here
    }

    @When("the user selects {string} trip type")
    public void the_user_selects_trip_type(String tripType) {
        if (tripType.equalsIgnoreCase("Round Trip")) {
            sp.selectRoundTrip();
        }
    }

    @And("the user enters origin as {string}")
    public void the_user_enters_origin_as(String origin) {
        sp.enterBoardingPlace(origin);
    }

    @And("the user enters destination as {string}")
    public void the_user_enters_destination_as(String destination) {
        sp.enterLandingPlace(destination);
    }

    @And("the user sets travellers as {string} adults, {string} children, {string} infants and class as {string}")
    public void the_user_sets_travellers_and_class(String adults, String children, String infants, String travelClass) {
        int a = Integer.parseInt(adults);
        int c = Integer.parseInt(children);
        int i = Integer.parseInt(infants);
        sp.setTravellersAndClass(a, c, i, travelClass);
    }

    @When("the user clicks Search")
    public void the_user_clicks_search() {
        sp.clickSearch();
    }

    @Then("search results should be displayed")
    public void search_results_should_be_displayed() {
        Assert.assertTrue(sp.areResultsDisplayed(), "Expected search results to be displayed");
    }
}
