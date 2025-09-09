package com.stepdefinitions;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.setup.Base;
import com.utils.CookieManager;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class Hooks extends Base {

    public static ExtentSparkReporter spark;
    public static ExtentReports extReports;
    public static ExtentTest extTest;

    @BeforeAll
    public static void ExtentReportSetup() {
        spark = new ExtentSparkReporter("reports//ExtentReport.html");
        extReports = new ExtentReports();
        extReports.attachReporter(spark);
    }

    @AfterAll
    public static void afterAll() {
        extReports.flush();
    }

    @Before
    public void setup(Scenario scenario) {
        // 1️⃣ Launch browser
        launchBrowser();

        // 2️⃣ Create test in ExtentReports
        extTest = extReports.createTest(scenario.getName());

        // 3️⃣ Clear old browser data to avoid multiple users appearing
        CookieManager.clearBrowserData(driver);

        // 4️⃣ Load saved cookies, localStorage, sessionStorage (if exists)
        boolean cookiesLoaded = CookieManager.load(driver);
        if (cookiesLoaded) {
            System.out.println("✅ Cookies, localStorage, and sessionStorage loaded in Hooks");
        } else {
            System.out.println("⚠️ No saved cookies found. Fresh login may be required");
        }
    }

    @After
    public void teardown() {
        Base.sleep(); // small wait before closing
        if(driver != null) {
            driver.quit(); // close browser after each scenario
        }
    }
}
