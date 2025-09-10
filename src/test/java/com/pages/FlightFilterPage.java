package com.pages;

import com.objectrepository.Locators;
import org.openqa.selenium.*;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * Implements filter actions and verification helpers for:
 * - Non-Stop
 * - IndiGo
 * - Departure from MAA : Before 6AM
 * - Arrival at PNQ : Before 6AM
 */
public class FlightFilterPage {
    private WebDriver driver;
    private WebDriverWait wait;
    private final Duration TIMEOUT = Duration.ofSeconds(15);

    public FlightFilterPage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TIMEOUT);
        // wait until results container or at least one flight card appears
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(Locators.flightCard));
        } catch (TimeoutException e) {
            // If no flight-card appears quickly, it's OK — some flows may show a "no results" state.
        }
    }

    // -------------------- Filter Actions --------------------

    public void applyNonStopFilter() {
        try {
            WebElement el = safeFind(Locators.nonStopFilter);
            if (el == null) throw new NoSuchElementException("Non-Stop filter not found (checked Locators.nonStopFilter).");
            clickableClick(el);
            waitForResultsToRefresh();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply Non-Stop filter: " + e.getMessage(), e);
        }
    }

    public void applyAirlineFilter(String airlineName) {
        try {
            // We only expect "IndiGo" for now, but use general locator if available
            By airlineLocator;
            if ("IndiGo".equalsIgnoreCase(airlineName) && Locators.indigoFilter != null) {
                airlineLocator = Locators.indigoFilter;
            } else {
                // fallback: build dynamic locator by label text (safe approach)
                airlineLocator = By.xpath("//label[contains(., '" + airlineName + "')]/input | //label[contains(., '" + airlineName + "')]");
            }
            WebElement el = safeFind(airlineLocator);
            if (el == null) throw new NoSuchElementException("Airline filter not found for: " + airlineName);
            clickableClick(el);
            waitForResultsToRefresh();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply airline filter '" + airlineName + "': " + e.getMessage(), e);
        }
    }

    /**
     * Apply departure time filter for origin (e.g., MAA, "Before 6AM").
     * Uses specific locator Locators.maaBefore6amFilter when originCode == "MAA" and timeLabel matches.
     */
    public void applyDepartureTimeFilter(String originCode, String timeLabel) {
        try {
            By locator = null;
            if ("MAA".equalsIgnoreCase(originCode) && timeLabel != null && timeLabel.toLowerCase().contains("before")) {
                locator = Locators.maaBefore6amFilter;
            }
            if (locator == null) {
                // fallback: find label by text
                locator = By.xpath("//label[contains(., '" + timeLabel + "') and ancestor::*[contains(., '" + originCode + "')]] | //label[contains(., '" + timeLabel + "')]");
            }
            WebElement el = safeFind(locator);
            if (el == null) throw new NoSuchElementException("Departure time filter element not found for " + originCode + " / " + timeLabel);
            clickableClick(el);
            waitForResultsToRefresh();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply departure time filter: " + e.getMessage(), e);
        }
    }

    /**
     * Apply arrival time filter for destination (e.g., PNQ, "Before 6AM").
     * Uses specific locator Locators.pnqBefore6amFilter when destCode == "PNQ".
     */
    public void applyArrivalTimeFilter(String destCode, String timeLabel) {
        try {
            By locator = null;
            if ("PNQ".equalsIgnoreCase(destCode) && timeLabel != null && timeLabel.toLowerCase().contains("before")) {
                locator = Locators.pnqBefore6amFilter;
            }
            if (locator == null) {
                locator = By.xpath("//label[contains(., '" + timeLabel + "') and ancestor::*[contains(., '" + destCode + "')]] | //label[contains(., '" + timeLabel + "')]");
            }
            WebElement el = safeFind(locator);
            if (el == null) throw new NoSuchElementException("Arrival time filter element not found for " + destCode + " / " + timeLabel);
            clickableClick(el);
            waitForResultsToRefresh();
        } catch (Exception e) {
            throw new RuntimeException("Failed to apply arrival time filter: " + e.getMessage(), e);
        }
    }

    // -------------------- Verification Helpers --------------------

    public boolean areAllDisplayedFlightsNonStop() {
        List<WebElement> cards = getFlightCards();
        if (cards.isEmpty()) return false;
        for (WebElement c : cards) {
            String stopsText = safeGetText(c, Locators.stopsInCard).toLowerCase();
            if (!(stopsText.contains("non-stop") || stopsText.contains("nonstop") || stopsText.contains("direct"))) {
                return false;
            }
        }
        return true;
    }

    public boolean areAllDisplayedFlightsFromAirline(String airline) {
        List<WebElement> cards = getFlightCards();
        if (cards.isEmpty()) return false;
        for (WebElement c : cards) {
            String name = safeGetText(c, Locators.airlineNameInCard).toLowerCase();
            if (!name.contains(airline.toLowerCase())) return false;
        }
        return true;
    }

    /**
     * hhmm should be like "06:00" or "6:00"
     */
    public boolean areAllDisplayedFlightsDepartingBefore(String originCode, String hhmm) {
        int thresholdMinutes = toMinutes(hhmm);
        List<WebElement> cards = getFlightCards();
        if (cards.isEmpty()) return false;
        for (WebElement c : cards) {
            String dep = safeGetText(c, Locators.departureTimeInCard);
            if (dep == null || dep.isEmpty()) return false;
            int depMin = parseTimeToMinutes(dep);
            if (depMin == -1) return false;
            if (depMin >= thresholdMinutes) return false;
        }
        return true;
    }

    public boolean areAllDisplayedFlightsArrivingBefore(String destCode, String hhmm) {
        int thresholdMinutes = toMinutes(hhmm);
        List<WebElement> cards = getFlightCards();
        if (cards.isEmpty()) return false;
        for (WebElement c : cards) {
            String arr = safeGetText(c, Locators.arrivalTimeInCard);
            if (arr == null || arr.isEmpty()) return false;
            int arrMin = parseTimeToMinutes(arr);
            if (arrMin == -1) return false;
            if (arrMin >= thresholdMinutes) return false;
        }
        return true;
    }

    // -------------------- Utilities --------------------

    public int getDisplayedFlightsCount() {
        return getFlightCards().size();
    }

    public void selectFirstFlight() {
        try {
            // Prefer clicking the explicit firstBook button locator
            WebElement btn = safeFind(Locators.firstFlightSelectButton);
            if (btn != null) {
                clickableClick(btn);
                waitForNextPageOrBooking();
                return;
            }

            // Fallback: click a button inside the first flight card
            List<WebElement> cards = getFlightCards();
            if (cards.isEmpty()) throw new NoSuchElementException("No flight cards available to select.");
            WebElement first = cards.get(0);
            try {
                WebElement innerBtn = first.findElement(By.xpath(".//button[normalize-space()='Book' or contains(normalize-space(.),'Book') or contains(@class,'select') or contains(@class,'book')]"));
                clickableClick(innerBtn);
            } catch (NoSuchElementException e) {
                // fallback: click the card itself
                clickableClick(first);
            }
            waitForNextPageOrBooking();
        } catch (Exception e) {
            throw new RuntimeException("Failed to select the first flight: " + e.getMessage(), e);
        }
    }

    // -------------------- Private helpers --------------------

    private List<WebElement> getFlightCards() {
        try {
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(Locators.flightCard));
        } catch (TimeoutException ignored) {
            // If timeout, still attempt to return whatever is present
        }
        return driver.findElements(Locators.flightCard);
    }

    private WebElement safeFind(By by) {
        try {
            wait.until(ExpectedConditions.presenceOfElementLocated(by));
            WebElement el = driver.findElement(by);
            // ensure it's visible/clickable if it's an input overlay, allow clicking label too
            return el;
        } catch (Exception e) {
            return null;
        }
    }

    private String safeGetText(WebElement parent, By child) {
        try {
            WebElement el = parent.findElement(child);
            return el.getText().trim();
        } catch (Exception e) {
            return "";
        }
    }

    private void clickableClick(WebElement el) {
        try {
            wait.until(ExpectedConditions.elementToBeClickable(el));
            el.click();
        } catch (Exception e) {
            // last-resort JS click if normal click fails (useful for hidden overlay checkboxes)
            try {
                ((JavascriptExecutor) driver).executeScript("arguments[0].click();", el);
            } catch (Exception ex) {
                throw new RuntimeException("Element not clickable and JS click failed: " + ex.getMessage(), ex);
            }
        }
    }

    private void waitForResultsToRefresh() {
        // Prefer to wait for a known spinner or results count update; fallback to a short pause + presence of cards
        try {
            Thread.sleep(900); // small pause for DOM to update (replace with spinner wait when available)
            wait.until(ExpectedConditions.presenceOfAllElementsLocatedBy(Locators.flightCard));
        } catch (InterruptedException ignored) {
        } catch (Exception ignored) {
        }
    }

    private void waitForNextPageOrBooking() {
        // brief wait for booking page/modal to appear — replace with explicit booking selector if you have one
        try {
            Thread.sleep(1000);
        } catch (InterruptedException ignored) {
        }
    }

    // Convert "06:00" -> minutes
    private int toMinutes(String hhmm) {
        try {
            String t = hhmm.trim();
            if (!t.contains(":")) {
                // allow "6AM" alternatives if needed (but tests pass "06:00")
                if (t.toLowerCase().contains("am") || t.toLowerCase().contains("pm")) {
                    // not expected here — return 360 for 6:00 by default if "6" provided
                    t = t.replaceAll("[^0-9:]", "");
                }
            }
            String[] parts = t.split(":");
            int hh = Integer.parseInt(parts[0]);
            int mm = parts.length > 1 ? Integer.parseInt(parts[1]) : 0;
            return hh * 60 + mm;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * Parse string like "5:30", "05:30", "5:30 AM", "05:30 am" -> minutes since midnight
     * Returns -1 if cannot parse.
     */
    private int parseTimeToMinutes(String timeText) {
        try {
            String txt = timeText.trim().toUpperCase();
            boolean isPM = txt.contains("PM");
            boolean isAM = txt.contains("AM");
            // remove AM/PM and non-digit/colon characters
            txt = txt.replaceAll("[^0-9:]", "");
            if (!txt.contains(":")) return -1;
            String[] p = txt.split(":");
            int h = Integer.parseInt(p[0]);
            int m = Integer.parseInt(p[1]);
            if (isPM && h < 12) h += 12;
            if (isAM && h == 12) h = 0;
            return h * 60 + m;
        } catch (Exception e) {
            // try simple fallback
            try {
                String[] p = timeText.split(":");
                int h = Integer.parseInt(p[0].trim());
                int m = Integer.parseInt(p[1].trim());
                return h * 60 + m;
            } catch (Exception ex) {
                return -1;
            }
        }
    }
}
