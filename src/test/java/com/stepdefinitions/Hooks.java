package com.stepdefinitions;

import org.openqa.selenium.WebDriver;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.parameters.ExcelReader;
import com.setup.Base;

import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.BeforeAll;
import io.cucumber.java.Scenario;

public class Hooks extends Base {

	public static WebDriver driver; // WebDriver instance
	public static ExtentReports extReports;
	public static ExtentTest extTest;
	public static ExtentSparkReporter spark;
	

	// Make excelData static
		public static String[][] excelData;
		public static int currentrow = 0;

		@BeforeAll
		public static void setup() {
			// Initialize excelData in a static method that runs once
			try {
				excelData = ExcelReader.readData();
			} catch (Exception e) {
				e.printStackTrace();
				// Handle the exception appropriately, e.g., by throwing a more specific
				// exception
				throw new RuntimeException("Failed to read data from Excel file.", e);
			}

			spark = new ExtentSparkReporter("reports//ExtentReport.html");
			extReports = new ExtentReports();
			extReports.attachReporter(spark);
		}

	@AfterAll
	public static void afterAll() {
		if (extReports != null) {
			extReports.flush();
		}
	}

	@Before
	public void setUp(Scenario scenario) {
		launchBrowser();                       // Initialize driver
		driver = Base.driver;                 // Assign driver from Base
		extTest = extReports.createTest(scenario.getName());
	}

	@After
	public void tearDown() {
		Base.sleep();
		
		  if (driver != null) { driver.quit(); }
		 
	}
}
