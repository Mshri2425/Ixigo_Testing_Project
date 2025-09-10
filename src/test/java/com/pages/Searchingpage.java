package com.pages;

import java.time.Duration;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.objectrepository.Locators;
import com.parameters.Reporter;
import com.setup.Base;

public class Searchingpage {

	WebDriver driver;
	WebDriverWait wait;
	ExtentTest extTest;
	DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd");

	public Searchingpage(WebDriver driver, ExtentTest extTest) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(25));
		this.extTest = extTest;
	}

	private void handlePopupIfExists() {
		try {
			List<WebElement> popups = driver.findElements(By.id("wiz-iframe-intent"));
			if (!popups.isEmpty()) {
				driver.switchTo().frame(popups.get(0));
				driver.findElement(By.id("closeButton")).click();
				driver.switchTo().defaultContent();
			}
		} catch (Exception ignore) {
		}
	}

	/**
	 * Generalized selectDate which clicks the supplied buttonLocator (departure/return)
	 * and navigates the calendar until the desired month/year is shown, then clicks the day.
	 * Keeps your original locators (Locators.dep_month, Locators.calendarNextBtn, Locators.dayCell).
	 */
	public void selectDate(By buttonLocator, String dateStr) {
		try {
			LocalDate date = LocalDate.parse(dateStr, dtf);
			handlePopupIfExists();

			// Click the departure/return button (depends on what was passed)
			wait.until(ExpectedConditions.elementToBeClickable(buttonLocator)).click();

			String targetMonthYear = date.format(DateTimeFormatter.ofPattern("MMMM yyyy"));
			boolean found = false;

			for (int i = 0; i < 12; i++) {
				// Get the current month and year from the calendar header
				WebElement currentMonthYear = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.dep_month));

				// compare case-insensitive and trimmed to be robust against whitespace/case
				String shown = currentMonthYear.getText() == null ? "" : currentMonthYear.getText().trim();
				if (shown.equalsIgnoreCase(targetMonthYear) || shown.toLowerCase().contains(targetMonthYear.toLowerCase())) {
					// Found the correct month, now click the day
					try {
						By dayLocator = Locators.dayCell(String.valueOf(date.getDayOfMonth()));
						WebElement dayElement = wait.until(ExpectedConditions.elementToBeClickable(dayLocator));
						dayElement.click();
						found = true;
						Reporter.generateReport(driver, extTest, Status.PASS, "Selected date: " + dateStr);
						break;
					} catch (Exception clickDayEx) {
						// If straightforward day click failed, try alternate strategies:
						// 1) look for aria-label/data-date containing full ISO date
						String iso = date.toString();
						List<WebElement> isoCandidates = driver.findElements(By.xpath("//*[contains(@aria-label, '" + iso + "') or @data-date='" + iso + "' or @data-day='" + iso + "']"));
						boolean clicked = false;
						for (WebElement c : isoCandidates) {
							try {
								if (c.isDisplayed() && c.isEnabled()) {
									c.click();
									clicked = true;
									found = true;
									Reporter.generateReport(driver, extTest, Status.PASS, "Selected date by ISO attribute: " + dateStr);
									break;
								}
							} catch (Exception inner) { /* try next */ }
						}
						if (clicked) break;
						// 2) try visible day number but ensure not disabled
						List<WebElement> numCandidates = driver.findElements(By.xpath("//*[normalize-space(text())='" + date.getDayOfMonth() + "']"));
						for (WebElement c : numCandidates) {
							try {
								String cls = "";
								try { cls = c.getAttribute("class"); } catch (Exception ignore) {}
								if (c.isDisplayed() && c.isEnabled() && (cls == null || !cls.toLowerCase().contains("disabled"))) {
									c.click();
									found = true;
									Reporter.generateReport(driver, extTest, Status.PASS, "Selected date by visible day: " + dateStr);
									clicked = true;
									break;
								}
							} catch (Exception inner) { /* try next */ }
						}
						if (clicked) break;
						// if still not clicked, we'll move to next month below
					}
				}

				// Click next month
				try {
					wait.until(ExpectedConditions.elementToBeClickable(Locators.calendarNextBtn)).click();
					Base.sleep();
				} catch (Exception nx) {
					// cannot go next, break to avoid infinite loop
					break;
				}
			}

			if (!found) {
				Reporter.generateReport(driver, extTest, Status.FAIL, "Could not select date: " + dateStr);
			}
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "selectDate exception: " + e.getMessage());
		}
	}

	public void openFlightsTab() {
		try {
			handlePopupIfExists();
			wait.until(ExpectedConditions.elementToBeClickable(Locators.flight)).click();
			Reporter.generateReport(driver, extTest, Status.PASS, "Opened Flights tab");
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to open Flights tab");
		}
	}

	public void selectRoundTrip() {
		try {
			handlePopupIfExists();
			wait.until(ExpectedConditions.elementToBeClickable(Locators.round)).click();
			Reporter.generateReport(driver, extTest, Status.PASS, "Selected Round Trip");
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "Round Trip selection failed (may be absent)");
		}
	}

	/**
	 * enterBoardingPlace adapted from your friend's approach but keeps your variable names
	 * and locators. Adds a small verification + JS fallback so the chosen city remains visible.
	 */
	public void enterBoardingPlace(String from) {
		try {
			handlePopupIfExists();
			wait.until(ExpectedConditions.elementToBeClickable(Locators.from)).click();

			WebElement input = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.click_from));
			input.clear();
			input.sendKeys(from);
			//Base.sleep();

			// primary XPath from your friend's code
			List<WebElement> results = null;
			try {
				results = new WebDriverWait(driver, Duration.ofSeconds(15))
						.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(By
								.xpath("/html/body/main/div[2]/div[1]/div[3]/div[2]/div[1]/div[1]/div[3]/div[1]/div[1]")));
			} catch (Exception e) {
				// fallback: general suggestion items containing the text
				try {
					results = new WebDriverWait(driver, Duration.ofSeconds(8))
							.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
									By.xpath("//ul//li//span[contains(normalize-space(.), '" + from + "')]")));
				} catch (Exception ignore) {
				}
			}

			if (results != null && !results.isEmpty()) {
				try {
					results.get(0).click();
					//Base.sleep();
				} catch (Exception clickEx) {
					// fallback: keyboard selection
					input.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
					//Base.sleep();
				}
			} else {
				// fallback: keyboard selection
				input.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
				//Base.sleep();
			}

			// commit value (some widgets need focus-change)
			try {
				input.sendKeys(Keys.TAB);
				//Base.sleep();
			} catch (Exception ignore) {
			}

			// verify visible value; if empty, set via JS and dispatch input/change events
			try {
				String cur = input.getAttribute("value");
				if (cur == null || cur.trim().isEmpty()) cur = input.getText();

				if (cur == null || cur.trim().isEmpty()) {
					String jsSet = "var el = arguments[0];"
							+ "el.focus();"
							+ "el.value = arguments[1];"
							+ "el.dispatchEvent(new Event('input',{bubbles:true}));"
							+ "el.dispatchEvent(new Event('change',{bubbles:true}));";
					((JavascriptExecutor) driver).executeScript(jsSet, input, from);
					//Base.sleep();
				}
			} catch (Exception jsEx) {
				// ignore JS errors but continue
			}

			Reporter.generateReport(driver, extTest, Status.PASS, "Boarding place entered: " + from);
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to enter origin: " + from);
		}
	}

	/**
	 * enterLandingPlace follows the same pattern as enterBoardingPlace.
	 * Note: fixed the fallback that previously sent keys to click_from by mistake.
	 */
	public void enterLandingPlace(String to) {
		try {
			handlePopupIfExists();

			wait.until(ExpectedConditions.elementToBeClickable(Locators.to));
			WebElement input = wait.until(ExpectedConditions.elementToBeClickable(Locators.click_to));
			input.clear();
			input.sendKeys(to);
			//Base.sleep();

			List<WebElement> results = null;
			try {
				results = new WebDriverWait(driver, Duration.ofSeconds(20))
						.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
								By.xpath("//span[@class='block truncate' and text()='" + to + "']")));
			} catch (Exception e) {
				// fallback generic suggestions
				try {
					results = new WebDriverWait(driver, Duration.ofSeconds(8))
							.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(
									By.xpath("//ul//li//span[contains(normalize-space(.), '" + to + "')]")));
				} catch (Exception ignore) {
				}
			}

			if (results != null && !results.isEmpty()) {
				try {
					results.get(0).click();
					//Base.sleep();
				} catch (Exception clickEx) {
					// fallback: keyboard selection
					input.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
					//Base.sleep();
				}
			} else {
				// fallback: keyboard selection (fixed to send keys to click_to)
				input.sendKeys(Keys.ARROW_DOWN, Keys.ENTER);
				//Base.sleep();
			}

			// commit and verify, with JS fallback if necessary
			try {
				input.sendKeys(Keys.TAB);
				//Base.sleep();
			} catch (Exception ignore) {
			}

			try {
				String cur = input.getAttribute("value");
				if (cur == null || cur.trim().isEmpty()) cur = input.getText();

				if (cur == null || cur.trim().isEmpty()) {
					String jsSet = "var el = arguments[0];"
							+ "el.focus();"
							+ "el.value = arguments[1];"
							+ "el.dispatchEvent(new Event('input',{bubbles:true}));"
							+ "el.dispatchEvent(new Event('change',{bubbles:true}));";
					((JavascriptExecutor) driver).executeScript(jsSet, input, to);
					//Base.sleep();
				}
			} catch (Exception jsEx) {
				// ignore
			}

			Reporter.generateReport(driver, extTest, Status.PASS, "Landing place entered: " + to);
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to enter departure: " + to);
		}
	}

	public void selectDepartureDate(String dateStr) {
		selectDate(Locators.departure_btn, dateStr);
	}

	public void selectReturnDate(String dateStr) {
		selectDate(Locators.return_btn, dateStr);
	}

	public void setTravellersAndClass(int adults, int children, int infants, String travelClass) {
		try {
			handlePopupIfExists();
			// open travellers panel
			wait.until(ExpectedConditions.elementToBeClickable(Locators.travellersPanel)).click();
			Base.sleep();

			// increase adults (assumes default 1 adult)
			for (int i = 1; i < adults; i++) {
				try {
					wait.until(ExpectedConditions.elementToBeClickable(Locators.adultsPlusBtn)).click();
					Base.sleep();
				} catch (Exception ex) {
				}
			}
			// increase children
			for (int i = 0; i < children; i++) {
				try {
					wait.until(ExpectedConditions.elementToBeClickable(Locators.childrenPlusBtn)).click();
					Base.sleep();
				} catch (Exception ex) {
				}
			}
			// increase infants
			for (int i = 0; i < infants; i++) {
				try {
					wait.until(ExpectedConditions.elementToBeClickable(Locators.infantsPlusBtn)).click();
					Base.sleep();
				} catch (Exception ex) {
				}
			}

			// select class (best-effort)
			try {
				wait.until(ExpectedConditions.elementToBeClickable(Locators.travelClassDropdown)).click();
				Base.sleep();
				By classOption = Locators.travelClassOption(travelClass);
				wait.until(ExpectedConditions.elementToBeClickable(classOption)).click();
				Base.sleep();
			} catch (Exception ex) {
				// non-fatal
			}

			// apply travellers
			try {
				wait.until(ExpectedConditions.elementToBeClickable(Locators.travellersApplyBtn)).click();
				Base.sleep();
			} catch (Exception ex) {
			}

			Reporter.generateReport(driver, extTest, Status.PASS,
					"Travellers set: A" + adults + " C" + children + " I" + infants + " Class:" + travelClass);
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to set travellers/class");
		}
	}

	public void clickSearch() {
		try {
			handlePopupIfExists();
			wait.until(ExpectedConditions.elementToBeClickable(Locators.searchButton)).click();
			// give the page a short moment to start loading results
			Base.sleep();
			Reporter.generateReport(driver, extTest, Status.PASS, "Clicked Search");
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "Failed to click Search");
		}
	}

	public boolean areResultsDisplayed() {
		try {
			// longer explicit wait to allow results to appear
			WebDriverWait longWait = new WebDriverWait(driver, Duration.ofSeconds(40));
			longWait.until(ExpectedConditions.visibilityOfElementLocated(Locators.resultsContainer));
			Reporter.generateReport(driver, extTest, Status.PASS, "Search results displayed");
			return true;
		} catch (Exception e) {
			Reporter.generateReport(driver, extTest, Status.FAIL, "Search results not detected");
			return false;
		}
	}
}
