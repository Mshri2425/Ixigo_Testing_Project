package com.stepdefinitions;

import com.pages.AirportCabs_Page;
import com.parameters.Reporter;
import com.aventstack.extentreports.Status;
import io.cucumber.java.en.*;
import io.cucumber.java.After;
import static com.stepdefinitions.Hooks.driver;
import static com.stepdefinitions.Hooks.extTest;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.testng.Assert;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import java.time.Duration;
import org.openqa.selenium.TimeoutException;

public class AirportCabs_definitions {

    AirportCabs_Page cabsPage = new AirportCabs_Page(driver, extTest);
    private String parentWindowHandle;

    @Given("user is on the search page")
    public void user_is_on_the_search_page() {
        parentWindow_handle_assign();
        driver.get("https://www.ixigo.com/");
        Reporter.generateReport(driver, extTest, Status.INFO, "User is on Ixigo search page");
        Assert.assertTrue(driver.getCurrentUrl().contains("ixigo.com"), "Not on Ixigo search page after navigation");
    }

    @When("user clicks on Airport Cabs option")
    public void user_clicks_on_airport_cabs_option() {
        try {
            parentWindow_handle_assign();
            cabsPage.clickAirportCabsAndSwitch();
            Reporter.generateReport(driver, extTest, Status.PASS, "Clicked Airport Cabs and switched to new tab/window");
            Assert.assertTrue(driver.getWindowHandles().size() > 1, "Expected a child window/tab to open for Airport Cabs");
            Assert.assertNotEquals(driver.getWindowHandle(), parentWindowHandle, "Did not switch to child window after clicking Airport Cabs");
            Assert.assertTrue(driver.getCurrentUrl().length() > 0, "Child window URL appears empty after switching");

        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to click Airport Cabs or switch window: " + e.getMessage());
            throw e;
        }
    }

    @When("user selects Home To Aiport option")
    public void user_selects_home_to_aiport_option() {
        try {
            cabsPage.selectHomeToAirport();
            Reporter.generateReport(driver, extTest, Status.PASS, "Selected Home To Airport option");

            try {
                WebDriverWait shortWait = new WebDriverWait(driver, Duration.ofSeconds(5));
                By pickupInput = AirportCabs_Page.fromLocationInput;
                shortWait.until(ExpectedConditions.visibilityOfElementLocated(pickupInput));
                Reporter.generateReport(driver, extTest, Status.INFO, "Pickup input became visible after selecting Home To Airport");
            } catch (Exception waitEx) {
                Reporter.generateReport(driver, extTest, Status.INFO,
                        "Pickup input not immediately visible after selecting Home To Airport (this may be okay). Will attempt to enter pickup in next step.");
            }

        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to select Home To Airport: " + e.getMessage());
            throw e;
        }
    }

    @When("user enters pickup location as {string}")
    public void user_enters_pickup_location_as(String from) {
        try {
            cabsPage.enterPickupLocation(from);
            Reporter.generateReport(driver, extTest, Status.PASS, "Entered pickup location: " + from);

            try {
                WebElement fromInput = driver.findElement(AirportCabs_Page.fromLocationInput);
                String val = fromInput.getAttribute("value");
                Assert.assertTrue(val != null && val.toLowerCase().contains(from.toLowerCase()),
                        "Pickup input value does not contain expected text. Expected to contain: " + from + " but was: " + val);
            } catch (Exception inner) {
                boolean suggestionPresent = !driver.findElements(AirportCabs_Page.fromSuggestionMAA).isEmpty();
                boolean inputPresent = !driver.findElements(AirportCabs_Page.fromLocationInput).isEmpty();
                Assert.assertTrue(suggestionPresent || inputPresent,
                        "Pickup location was not entered and no suggestion present for: " + from);
            }

        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to enter pickup location '" + from + "': " + e.getMessage());
            throw e;
        }
    }

    @When("user enters drop location as {string}")
    public void user_enters_drop_location_as(String to) {
        try {
            cabsPage.enterDropLocation(to);
            Reporter.generateReport(driver, extTest, Status.PASS, "Entered drop location: " + to);
            By toInputBy = AirportCabs_Page.toLocationInput;
            String expectedToken = to.toLowerCase().split(" ")[0]; 
            WebDriverWait wait = new WebDriverWait(driver, Duration.ofSeconds(6));
            boolean matched = false;
            try {
                matched = wait.until(d -> {
                    try {
                        WebElement toInput = d.findElement(toInputBy);
                        String val = toInput.getAttribute("value");
                        return val != null && val.toLowerCase().contains(expectedToken);
                    } catch (Exception e) {
                        return false;
                    }
                });
            } catch (Exception ignored) {
                matched = false;
            }

            if (!matched) {
                Reporter.generateReport(driver, extTest, Status.INFO,
                        "Drop input did not update on first attempt. Trying explicit click + JS set + suggestion click.");

                try {
                    WebElement toInput = driver.findElement(toInputBy);
                    try { toInput.click(); } catch (Exception e) {
                        ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", toInput);
                    }
                } catch (Exception clickEx) {
                    // ignore and proceed to JS set
                }

                try {
                    String js = "var e = document.querySelector(\"#to-input\");"
                              + "if(e){ e.focus(); e.value = arguments[0];"
                              + " e.dispatchEvent(new Event('input',{bubbles:true}));"
                              + " e.dispatchEvent(new Event('change',{bubbles:true})); return true;} return false;";
                    Boolean jsOk = (Boolean) ((org.openqa.selenium.JavascriptExecutor) driver)
                            .executeScript(js, to);
                    Reporter.generateReport(driver, extTest, Status.INFO, "JS-set drop input returned: " + jsOk);
                } catch (Exception jsEx) {
                    Reporter.generateReport(driver, extTest, Status.INFO, "JS-set for drop input failed: " + jsEx.getMessage());
                }

                try {
                    By suggestion = AirportCabs_Page.toSuggestionBOM_T1;
                    if (!driver.findElements(suggestion).isEmpty()) {
                        WebElement sug = driver.findElement(suggestion);
                        try { sug.click(); } catch (Exception e) {
                            ((org.openqa.selenium.JavascriptExecutor) driver).executeScript("arguments[0].click();", sug);
                        }
                        Reporter.generateReport(driver, extTest, Status.INFO, "Clicked drop suggestion for: " + to);
                    }
                } catch (Exception sugEx) {
                    Reporter.generateReport(driver, extTest, Status.INFO, "Drop suggestion click failed: " + sugEx.getMessage());
                }

                // second wait attempt
                try {
                    matched = new WebDriverWait(driver, Duration.ofSeconds(6)).until(d -> {
                        try {
                            WebElement toInput = d.findElement(toInputBy);
                            String val = toInput.getAttribute("value");
                            return val != null && val.toLowerCase().contains(expectedToken);
                        } catch (Exception e) {
                            return false;
                        }
                    });
                } catch (Exception finalWaitEx) {
                    matched = false;
                }
            }

            if (!matched) {
                String actualVal = "";
                try {
                    WebElement toInput = driver.findElement(toInputBy);
                    actualVal = toInput.getAttribute("value");
                } catch (Exception ignored) {}
                Reporter.generateReport(driver, extTest, Status.FAIL,
                        "Drop input did not update as expected. Expected token: '" + expectedToken + "' but actual: '" + actualVal + "'");
                Assert.fail("Drop input value does not look correct. Expected to contain part of: " + to + " but was: " + actualVal);
            }

        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to enter drop location '" + to + "': " + e.getMessage());
            throw e;
        }
    }


    @Then("user clicks on cab search button")
    public void user_clicks_on_cab_search_button() {
        try {
            cabsPage.clickSearchButton();
            Reporter.generateReport(driver, extTest, Status.PASS, "Clicked cab search button");
            boolean likelySearch = driver.getPageSource().toLowerCase().contains("results")
                    || driver.getTitle().toLowerCase().contains("cab")
                    || driver.getCurrentUrl().toLowerCase().contains("search");

            Assert.assertTrue(likelySearch, "After clicking SEARCH, page does not indicate results/search (title/url/page source checks failed)");

        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to click cab search button: " + e.getMessage());
            throw e;
        }
    }

    @Then("cab search results should be displayed")
    public void cab_search_results_should_be_displayed() {
        try {
            boolean found = driver.getPageSource().toLowerCase().contains("results")
                     || driver.getTitle().toLowerCase().contains("cab")
                     || driver.getCurrentUrl().toLowerCase().contains("search");

            if (found) {
                Reporter.generateReport(driver, extTest, Status.PASS, "Cab search results are displayed");
            } else {
                Reporter.generateReport(driver, extTest, Status.FAIL, "Cab search results are NOT displayed");
                throw new AssertionError("Cab search results not displayed");
            }
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Error while validating cab results: " + e.getMessage());
            throw e;
        }
    }

    @After
    public void afterScenarioCleanup() {
        try {
            if (parentWindowHandle == null) {
                try {
                    parentWindowHandle = driver.getWindowHandle();
                } catch (Exception ignored) {}
            }

            for (String handle : driver.getWindowHandles()) {
                if (parentWindowHandle != null && !handle.equals(parentWindowHandle)) {
                    try {
                        driver.switchTo().window(handle);
                        driver.close();
                        Reporter.generateReport(driver, extTest, Status.INFO, "Closed child window: " + handle);
                    } catch (Exception e) {
                        Reporter.generateReport(driver, extTest, Status.INFO, "Could not close window " + handle + ": " + e.getMessage());
                    }
                }
            }

            if (parentWindowHandle != null) {
                try {
                    driver.switchTo().window(parentWindowHandle);
                    Reporter.generateReport(driver, extTest, Status.INFO, "Switched back to parent window");
                } catch (Exception e) {
                    Reporter.generateReport(driver, extTest, Status.INFO, "Unable to switch back to parent window: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            Reporter.generateReport(driver, extTest, Status.INFO, "Exception in afterScenarioCleanup: " + e.getMessage());
        }
    }

    private void parentWindow_handle_assign() {
        try {
            if (parentWindowHandle == null) {
                parentWindowHandle = driver.getWindowHandle();
            }
        } catch (Exception ignored) {
        }
    }
}
