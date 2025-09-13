package com.stepdefinitions;

import io.cucumber.java.en.*;
import com.pages.TrainSearch_Page;
import com.pages.TrainBooking_Page;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import java.time.Duration;

public class TrainSearch_definitions {
    WebDriver driver = Hooks.driver;
    private TrainSearch_Page search;
    private TrainBooking_Page booking;

    private static final By FROM_INPUT = By.xpath("//*[@id='origin-destination-input']/div[1]//input[@placeholder='Enter Origin']");
    private static final By TO_INPUT = By.xpath("//*[@id='origin-destination-input']/div[2]//input[@placeholder='Enter Destination']");
    private static final By TRAINS_TAB = By.xpath("//p[normalize-space()='Trains']");

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

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        try {
            wait.until(ExpectedConditions.urlContains("ixigo.com"));
        } catch (TimeoutException te) {

        }
        Assert.assertTrue(driver.getCurrentUrl().toLowerCase().contains("ixigo.com"),
                "Expected to be on ixigo homepage, but current URL: " + driver.getCurrentUrl());

        boolean tabPresent = !driver.findElements(TRAINS_TAB).isEmpty();
        Assert.assertTrue(tabPresent, "Trains tab is not present on the homepage.");
    }

    @Given("the user navigates to the {string} tab")
    public void user_navigates_to_tab(String tabName) {
        initPages();
        search.clickTrainsTab();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(10));
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(FROM_INPUT));
        } catch (TimeoutException te) {
            Assert.fail("After clicking Trains tab, origin input was not visible. Current URL: " + driver.getCurrentUrl());
        }
    }

    @When("the user enters train origin as {string}")
    public void user_enters_train_origin(String from) {
        initPages();
        search.enterFrom(from);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        boolean matched = false;
        String expectedToken = from.split(" ")[0].toLowerCase(); 
        try {
            matched = wait.until(d -> {
                try {
                    WebElement el = d.findElement(FROM_INPUT);
                    String val = el.getAttribute("value");
                    return val != null && val.toLowerCase().contains(expectedToken.toLowerCase());
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (TimeoutException ignored) { matched = false; }

        if (!matched) {
            String actual = "";
            try {
                actual = driver.findElement(FROM_INPUT).getAttribute("value");
            } catch (Exception ignored) {}
            Assert.fail("Origin input did not update correctly. Expected to contain: '" + expectedToken + "' but was: '" + actual + "'");
        }
    }

    @When("the user enters train destination as {string}")
    public void user_enters_train_destination(String to) {
        initPages();
        search.enterTo(to);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        boolean matched = false;
        String expectedToken = to.split(" ")[0].toLowerCase(); 
        try {
            matched = wait.until(d -> {
                try {
                    WebElement el = d.findElement(TO_INPUT);
                    String val = el.getAttribute("value");
                    return val != null && val.toLowerCase().contains(expectedToken.toLowerCase());
                } catch (Exception e) {
                    return false;
                }
            });
        } catch (TimeoutException ignored) { matched = false; }

        if (!matched) {
            String actual = "";
            try {
                actual = driver.findElement(TO_INPUT).getAttribute("value");
            } catch (Exception ignored) {}
            Assert.fail("Destination input did not update correctly. Expected to contain: '" + expectedToken + "' but was: '" + actual + "'");
        }
    }

    @When("the user selects departure date as {string}")
    public void user_selects_date(String date) {
        initPages();
        search.selectDepartureDate(date);

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        try {
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//button[@data-testid='book-train-tickets']")));
        } catch (TimeoutException te) {
            // still continue — final results check will validate actual search
        }
    }

    @When("the user clicks the Search button")
    public void user_clicks_search() {
        initPages();
        search.clickSearch();

        WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(8));
        boolean progressed = false;
        try {
            progressed = wait.until(d -> {
                String url = d.getCurrentUrl().toLowerCase();
                return url.contains("train") || url.contains("search");
            });
        } catch (Exception ignored) { progressed = false; }

        if (!progressed) {
            // attempt to continue — the final results check will catch a failure
        }
    }

    @Then("train search results should be displayed")
    public void train_results_should_be_displayed() {
        initPages();
        boolean visible = booking.isResultsVisible();
        Assert.assertTrue(visible, "Train search results page is not displayed.");
    }
}
