package runner;

import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
	    features = "src/test/resources/features",
	    glue = "stepdefinitions",
	    plugin = {
	        "pretty",
	        "html:reports/cucumber-html-report.html",
	        "json:reports/cucumber-report.json"
	    }
	)
public class TestRunner extends AbstractTestNGCucumberTests{

}
