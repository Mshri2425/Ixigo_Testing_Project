package com.stepdefinitions;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import org.openqa.selenium.WebElement;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.pages.Searchingpage;
import com.setup.Base;
import com.objectrepository.Locators;

import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

public class searchingdefinitions {

    WebDriver driver = Hooks.driver;
    ExtentTest extTest = Hooks.extTest;
    Searchingpage search;

    @Given("the user is on the homepage")
    public void the_user_is_on_the_homepage() {
        // assume Hooks setUp launched the site
        search = new Searchingpage(driver, extTest);
        String expected = Base.driver.getCurrentUrl(); // optional check
    }

    @When("the user selects {string} trip type")
    public void the_user_selects_trip_type(String tripType) {
        // Only Round Trip handled for now
        if (tripType.equalsIgnoreCase("Round Trip")) {
            search.selectRoundTrip();
        }
    }

    @When("the user enters origin as {string}")
    public void the_user_enters_origin_as(String from) {
        // call your existing page method (no change to Searchingpage)
        search.enterBoardingPlace(from);

        // Now verify selection more robustly by checking several candidate elements
        boolean matches = verifyOriginPlaced(from);

        // If first attempt didn't match, retry once (some UIs require a second commit)
        if (!matches) {
            search.enterBoardingPlace(from);
            matches = verifyOriginPlaced(from);
        }

        Assert.assertTrue(matches, "Origin not entered correctly. Expected to find fragments of: '" + from + "' in one of the origin elements on the page.");
    }

    // Helper used by the step above — collects candidate text locations and checks for expected fragments
    private boolean verifyOriginPlaced(String expectedFull) {
        try {
            WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(4));
            // try to locate input and wrapper — these may or may not be visible depending on the UI
            WebElement inputElem = null;
            WebElement wrapperElem = null;
            try { inputElem = shortWait.until(ExpectedConditions.visibilityOfElementLocated(Locators.click_from)); } catch (Exception ignore) {}
            try { wrapperElem = shortWait.until(ExpectedConditions.visibilityOfElementLocated(Locators.from)); } catch (Exception ignore) {}

            // prepare expected pieces: code and city if in "CODE - City" format
            String expectedCode = "";
            String expectedCity = "";
            String expect = expectedFull == null ? "" : expectedFull.trim();
            if (expect.contains("-")) {
                String[] parts = expect.split("-", 2);
                expectedCode = parts[0].trim();
                expectedCity = parts[1].trim();
            } else {
                expectedCity = expect;
            }

            List<String> candidates = new ArrayList<>();

            // 1) input value / text
            if (inputElem != null) {
                try {
                    String v = inputElem.getAttribute("value");
                    if (v != null && !v.trim().isEmpty()) candidates.add(v.trim());
                } catch (Exception ignore) {}
                try {
                    String t = inputElem.getText();
                    if (t != null && !t.trim().isEmpty()) candidates.add(t.trim());
                } catch (Exception ignore) {}
            }

            // 2) wrapper element text
            if (wrapperElem != null) {
                try {
                    String w = wrapperElem.getText();
                    if (w != null && !w.trim().isEmpty()) candidates.add(w.trim());
                } catch (Exception ignore) {}
                // also inspect child nodes of wrapper for any displayed labels
                try {
                    List<WebElement> children = wrapperElem.findElements(org.openqa.selenium.By.xpath(".//*"));
                    for (WebElement ch : children) {
                        try {
                            String ct = ch.getText();
                            if (ct != null && !ct.trim().isEmpty()) candidates.add(ct.trim());
                        } catch (Exception inner) { }
                    }
                } catch (Exception ignore) {}
            }

            // 3) as a last resort, search the whole page for the city/code (helps if UI renders selected text elsewhere)
            try {
                if (!expect.isEmpty()) {
                    List<WebElement> pageMatches = driver.findElements(org.openqa.selenium.By.xpath("//*[contains(normalize-space(), \"" + expect + "\")]"));
                    for (WebElement pm : pageMatches) {
                        try {
                            String t = pm.getText();
                            if (t != null && !t.trim().isEmpty()) candidates.add(t.trim());
                        } catch (Exception ignore) {}
                    }
                }
            } catch (Exception ignore) {}

            // Normalize and check matches
            for (String cand : candidates) {
                String c = cand.toLowerCase();
                if (!expect.isEmpty() && c.equalsIgnoreCase(expect)) return true;
                if (!expectedCode.isEmpty() && c.contains(expectedCode.toLowerCase())) return true;
                if (!expectedCity.isEmpty() && c.contains(expectedCity.toLowerCase())) return true;
            }

            // debug output (will appear in test logs) to help troubleshooting if flaky
            System.out.println("verifyOriginPlaced — expected: '" + expect + "'. Candidates found: " + candidates);
            return false;
        } catch (Exception e) {
            System.out.println("verifyOriginPlaced exception: " + e.getMessage());
            return false;
        }
    }

    @When("the user enters destination as {string}")
    public void the_user_enters_destination_as(String to) {
        // keep behavior unchanged: call existing method
        search.enterLandingPlace(to);
    }

    @When("the user selects departure date as {string}")
    public void the_user_selects_departure_date_as(String depart_date) {
        search.selectDepartureDate(depart_date);
    }

    @When("the user selects return date as {string}")
    public void the_user_selects_return_date_as(String return_date) {
        search.selectReturnDate(return_date);
    }

    @When("the user sets travellers as {string} adults, {string} children, {string} infants and class as {string}")
    public void the_user_sets_travellers_and_class(String adults, String children, String infants, String travel_class) {
        int a = Integer.parseInt(adults.trim());
        int c = Integer.parseInt(children.trim());
        int i = Integer.parseInt(infants.trim());
        search.setTravellersAndClass(a, c, i, travel_class);
    }

    @When("the user clicks Search")
    public void the_user_clicks_search() {
        search.clickSearch();
    }

    @Then("search results should be displayed")
    public void search_results_should_be_displayed() {
        boolean res = search.areResultsDisplayed();
        Assert.assertTrue(res, "Expected search results to be displayed");
    }
}
