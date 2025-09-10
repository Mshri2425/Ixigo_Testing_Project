package com.stepdefinitions;

import com.pages.FlightFilterPage;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

/**
 * Step definitions for FlightFilters.feature
 * Uses FlightFilterPage (page object) to perform actions and verifications.
 * Filename: FlightFilterdefinitions.java
 */
public class FlightFilterdefinitions {

    // Use the shared driver from Hooks (your project already provides this)
    private WebDriver driver = Hooks.driver;
    private FlightFilterPage filterPage = new FlightFilterPage(driver);

    @When("the user applies {string} filter")
    public void the_user_applies_filter(String filterType) {
        String f = filterType.trim().toLowerCase();

        switch (f) {
            case "non-stop":
            case "non stop":
                filterPage.applyNonStopFilter();
                break;

            case "indigo":
            case "indigo airline":
                filterPage.applyAirlineFilter("IndiGo");
                break;

            case "departure from maa before 6am":
            case "maa before 6am":
            case "departure before 6am":
                filterPage.applyDepartureTimeFilter("MAA", "Before 6AM");
                break;

            case "arrival at pnq before 6am":
            case "pnq before 6am":
            case "arrival before 6am":
                filterPage.applyArrivalTimeFilter("PNQ", "Before 6AM");
                break;

            default:
                throw new IllegalArgumentException("Unsupported filter type in feature file: " + filterType);
        }
    }

    @Then("{string} should be displayed")
    public void expected_result_should_be_displayed(String expectedResult) {
        String exp = expectedResult.trim().toLowerCase();

        switch (exp) {
            case "only non-stop flights should be displayed":
            case "only non-stop flights":
                Assert.assertTrue(filterPage.areAllDisplayedFlightsNonStop(),
                        "Expected only non-stop flights, but some results do not match.");
                break;

            case "only indigo flights should be displayed":
            case "only indigo flights":
                Assert.assertTrue(filterPage.areAllDisplayedFlightsFromAirline("IndiGo"),
                        "Expected only IndiGo flights, but found other airlines.");
                break;

            case "only flights departing from chennai before 6am should be displayed":
            case "only flights departing from chennai before 6am":
            case "only flights departing from chennai before 6am should be displayed ":
                // use "06:00" as threshold
                Assert.assertTrue(filterPage.areAllDisplayedFlightsDepartingBefore("MAA", "06:00"),
                        "Expected flights departing from Chennai before 06:00, but found others.");
                break;

            case "only flights arriving at pune before 6am should be displayed":
            case "only flights arriving at pune before 6am":
                Assert.assertTrue(filterPage.areAllDisplayedFlightsArrivingBefore("PNQ", "06:00"),
                        "Expected flights arriving at Pune before 06:00, but found others.");
                break;

            default:
                throw new IllegalArgumentException("Unsupported expected result in feature file: " + expectedResult);
        }
    }

    @And("the user selects the first available flight")
    public void the_user_selects_the_first_available_flight() {
        int count = filterPage.getDisplayedFlightsCount();
        Assert.assertTrue(count > 0, "No flights available to select after applying filter.");
        filterPage.selectFirstFlight();
    }
}
