package com.parameters;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.remote.RemoteWebDriver;
import java.io.File;
import java.io.IOException;
import java.nio.file.*;

public class Reporter {

    private static final String REPORTS_ROOT = System.getProperty("user.dir") + File.separator + "test-output";
    private static final String SCREENSHOT_DIR = REPORTS_ROOT + File.separator + "screenshots";

    public static void generateReport(WebDriver driver, ExtentTest extTest, Status status, String message) {
        if (extTest != null) {
            extTest.log(status, message);
        }
        // Capture screenshot only for FAIL
        try {
            if (status == Status.FAIL && driver != null && isSessionActive(driver)) {
                String path = captureScreenshotSafe(driver);
                if (path != null && extTest != null) {
                    try {
                        extTest.addScreenCaptureFromPath(path);
                    } catch (Exception e) {
                        extTest.info("Screenshot captured but failed to attach: " + e.getMessage());
                    }
                } else if (extTest != null) {
                    extTest.info("Screenshot not available (capture failed or driver closed).");
                }
            }
        } catch (Exception e) {
            if (extTest != null) {
                extTest.warning("Reporter.generateReport encountered an error: " + e.getMessage());
            }
        }
    }

    public static String captureScreenshotSafe(WebDriver driver) {
        try {
            Path screenshotDir = Paths.get(SCREENSHOT_DIR);
            if (!Files.exists(screenshotDir)) {
                Files.createDirectories(screenshotDir);
            }

            String timestamp = java.time.LocalDateTime.now()
                    .toString()
                    .replace(":", "-")
                    .replace("T", "_")
                    .replace(".", "-");

            String filename = "screenshot_" + timestamp + ".png";
            Path dest = screenshotDir.resolve(filename);

            File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(srcFile.toPath(), dest, StandardCopyOption.REPLACE_EXISTING);

            return dest.toAbsolutePath().toString();
        } catch (IOException ioe) {
            System.err.println("Reporter.captureScreenshotSafe IOException: " + ioe.getMessage());
            return null;
        } catch (WebDriverException wde) {
            System.err.println("Reporter.captureScreenshotSafe WebDriverException: " + wde.getMessage());
            return null;
        } catch (Exception ex) {
            System.err.println("Reporter.captureScreenshotSafe unexpected error: " + ex.getMessage());
            return null;
        }
    }

    private static boolean isSessionActive(WebDriver driver) {
        try {
            if (driver instanceof RemoteWebDriver) {
                return ((RemoteWebDriver) driver).getSessionId() != null;
            }
            driver.getTitle(); 
            return true;
        } catch (Exception e) {
            return false;
        }
    }
}