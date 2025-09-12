package com.stepdefinitions;

import com.aventstack.extentreports.Status;
import com.pages.AirportCabsPage;
import com.pages.CabSelectionPage;
import com.parameters.Reporter;
import io.cucumber.java.en.*;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

public class CabSelectiondefinitions {

    private final WebDriver driver;
    private final com.aventstack.extentreports.ExtentTest extTest;
    private final AirportCabsPage airportCabsPage;
    private final CabSelectionPage cabPage;

    public CabSelectiondefinitions() {
        this.driver = Hooks.driver;
        this.extTest = Hooks.extTest;
        this.airportCabsPage = new AirportCabsPage(driver, extTest);
        this.cabPage = new CabSelectionPage(driver, extTest);
    }

    @Given("user is on the cab results page")
    public void user_is_on_the_cab_results_page() {
        Reporter.generateReport(driver, extTest, Status.INFO, "Navigating to Airport Cabs and performing search...");

        airportCabsPage.clickAirportCabsAndSwitch();
        airportCabsPage.selectHomeToAirport();
        airportCabsPage.enterPickupLocation("Chennai");
        airportCabsPage.enterDropLocation("Mumbai - CSMI Airport-T1");
        airportCabsPage.clickSearchButton();

        boolean loaded = cabPage.userIsOnCabResultsPage();
        Reporter.generateReport(driver, extTest, loaded ? Status.PASS : Status.FAIL, "Cab results page loaded: " + loaded);
        Assert.assertTrue(loaded, "Cab results page did not load.");
    }

    @When("user selects {string} cab type")
    public void user_selects_cab_type(String cabType) {
        Reporter.generateReport(driver, extTest, Status.INFO, "Selecting cab type: " + cabType);
        boolean ok = cabPage.selectCabType(cabType);
        Reporter.generateReport(driver, extTest, ok ? Status.PASS : Status.FAIL, "selectCabType returned: " + ok);
        Assert.assertTrue(ok, "Failed to select cab type: " + cabType);
    }

    @When("user books {string} cab")
    public void user_books_cab(String provider) {
        Reporter.generateReport(driver, extTest, Status.INFO, "Booking provider: " + provider);
        boolean clicked = cabPage.bookCabByProvider(provider);
        Reporter.generateReport(driver, extTest, clicked ? Status.PASS : Status.FAIL, "bookCabByProvider returned: " + clicked);
        Assert.assertTrue(clicked, "Failed to click Book Now for: " + provider);
    }

    @Then("booking confirmation page should be displayed")
    public void booking_confirmation_page_should_be_displayed() {
        Reporter.generateReport(driver, extTest, Status.INFO, "Waiting for booking confirmation...");
        boolean ok = cabPage.waitForReviewBookingAndClose();
        Reporter.generateReport(driver, extTest, ok ? Status.PASS : Status.FAIL, "Review/booking handled: " + ok);
        Assert.assertTrue(ok, "Review/booking page was not displayed/handled.");
    }
}
