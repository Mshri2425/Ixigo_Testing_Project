package com.pages;

import java.time.Duration;
import java.util.List;
import java.util.Scanner;

import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.objectrepository.Locators;
import com.parameters.Reporter;
import com.setup.Base;

public class Loginpage {

    WebDriver driver;
    WebDriverWait wait;
    ExtentTest extTest;

    public Loginpage(WebDriver driver, ExtentTest extTest) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(20));
        this.extTest = extTest;
    }

    // ---------- common ----------
    private void handlePopupIfExists() {
        try {
            List<WebElement> popups = driver.findElements(By.id("wiz-iframe-intent"));
            if (!popups.isEmpty()) {
                driver.switchTo().frame(popups.get(0));
                driver.findElement(By.id("closeButton")).click();
                driver.switchTo().defaultContent();
                System.out.println("Popup closed successfully");
            }
        } catch (Exception ignore) {
        }
    }

    private void clickLoginButton() {
        wait.until(ExpectedConditions.elementToBeClickable(Locators.Loginbutton)).click();
        Base.sleep();
        handlePopupIfExists();
    }

    // ---------- positive login ----------
    public boolean enterMobileNumber(String mobileNo) {
        handlePopupIfExists();
        clickLoginButton();
        try {
            WebElement mobileField = wait.until(ExpectedConditions.visibilityOfElementLocated(Locators.mobile));
            mobileField.clear();
            mobileField.sendKeys(mobileNo);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    public void clickContinueForMobile() {
        handlePopupIfExists();
        WebElement continueBtn = wait.until(ExpectedConditions.elementToBeClickable(Locators.continuebutton));
        continueBtn.click();
        System.out.println("Clicked Continue successfully");
    }

    public void enterOtpManually() {
        Scanner kebd = new Scanner(System.in);
        System.out.print("Enter OTP from SMS: ");
        String otp = kebd.nextLine();

        List<WebElement> otpInputs = wait.until(
                ExpectedConditions.visibilityOfAllElementsLocatedBy(Locators.otpInputs)
        );

        System.out.println("DEBUG: Found " + otpInputs.size() + " OTP input boxes");
        System.out.println("DEBUG: Entering OTP: " + otp);

        if (otpInputs.size() > 1) {
            for (int i = 0; i < otp.length() && i < otpInputs.size(); i++) {
                otpInputs.get(i).clear();
                otpInputs.get(i).sendKeys(Character.toString(otp.charAt(i)));
            }
        } else {
            otpInputs.get(0).clear();
            otpInputs.get(0).sendKeys(otp);
        }
    }

    public boolean navigatedpage() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//button[text()='One Way']")));
            Reporter.generateReport(driver, extTest, Status.PASS, "Flight details displayed");
            return true;
        } catch (TimeoutException te) {
            Reporter.generateReport(driver, extTest, Status.FAIL, "Navigation failed");
            return false;
        }
    }
}
