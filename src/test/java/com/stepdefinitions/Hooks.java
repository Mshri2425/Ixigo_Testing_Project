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

	public static WebDriver driver; 
	public static ExtentReports extReports;
	public static ExtentTest extTest;
	public static ExtentSparkReporter spark;
	
	public static String[][] excelData;
	public static int currentrow = 0;

		@BeforeAll
		public static void setup() {
			try {
				excelData = ExcelReader.readData();
			} catch (Exception e) {
				e.printStackTrace();
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
		launchBrowser();                       
		driver = Base.driver;                 
		extTest = extReports.createTest(scenario.getName());
	}

	@After
	public void tearDown() {
		Base.sleep();
		
		  if (driver != null) { driver.quit(); }
		 
	}
}
