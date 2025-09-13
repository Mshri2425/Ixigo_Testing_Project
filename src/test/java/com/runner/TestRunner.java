package com.runner;

import org.testng.annotations.DataProvider;
import com.parameters.Excel_Reader;
import com.stepdefinitions.Hooks;
import io.cucumber.testng.AbstractTestNGCucumberTests;
import io.cucumber.testng.CucumberOptions;

@CucumberOptions(
	features="src/test/resources/Feature",
	glue="com.stepdefinitions",
	plugin= {"pretty","html:reports/cucumber-html-report.html"}
    
)
public class TestRunner extends AbstractTestNGCucumberTests {
	
	@Override
    @DataProvider(parallel = false)
    public Object[][] scenarios() {
        // Read Excel data once
        String[][] excelData = Excel_Reader.readData();
        Hooks.excelData = excelData;   

        Object[][] cucumberScenarios = super.scenarios();

        // Expand scenarios per Excel row
        Object[][] finalScenarios = new Object[excelData.length * cucumberScenarios.length][];
        int index = 0;

        for (int i = 0; i < excelData.length; i++) {
            Hooks.currentrow = i;   
            for (int j = 0; j < cucumberScenarios.length; j++) {
                finalScenarios[index++] = cucumberScenarios[j];
            }
        }

        return finalScenarios;
    }
}
