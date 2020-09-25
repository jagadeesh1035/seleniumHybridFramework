package testscripts.endtoend;

import org.testng.annotations.Test;

import com.cust.framework.IterationOptions;
import com.cust.framework.Status;

import supportLibraries.DriverScript;
import supportLibraries.RetryAnalyzer;
import supportLibraries.TestCase;

public class E2ESCENARIO1 extends TestCase {

	@Test(retryAnalyzer = RetryAnalyzer.class)
	public void run() {
		testParameters.setCurrentTestDescription(
				"Test case 1 description	Test case 1 description	Test case 1 description	Test case 1 description	Test case 1 description	Test case 1 description");
		testParameters.setIterationMode(IterationOptions.RunOneIterationOnly);
		driverScript = new DriverScript(testParameters);
		driverScript.driveTestExecution();
	}

	@Override
	public void setUp() {
		report.addTestLogSection("Setup");
		report.updateTestLog("TestUpdateTestUpdateTestUpdate", "TestUpdate_PASS_WE have to check the justification",
				Status.PASS);
		report.updateTestLog("TestUpdateTestUpdateTestUpdate", "TestUpdate_FAIL_WE have to check the justification",
				Status.FAIL);
		/*
		 * Instances of functional library to be placed in this section
		 */
	}

	@Override
	public void executeTest() {
		/*
		 * Actual test case to be written in this section
		 */
		report.updateTestLog("TestUpdateTestUpdateTestUpdate", "TestUpdate_PASS_WE have to check the justification",
				Status.PASS);
		report.updateTestLog("TestUpdateTestUpdateTestUpdate", "TestUpdate_FAIL_WE have to check the justification",
				Status.FAIL);
		report.updateTestLog("TestUpdateTestUpdateTestUpdate", "TestUpdate_DEBUG_WE have to check the justification",
				Status.DEBUG);
		report.updateTestLog("TestUpdateTestUpdateTestUpdate", "TestUpdate_DONE_WE have to check the justification",
				Status.DONE);
		report.updateTestLog("TestUpdateTestUpdateTestUpdate", "TestUpdate_INFO_WE have to check the justification",
				Status.INFO);
		report.updateTestLog("TestUpdateTestUpdateTestUpdate",
				"TestUpdate_SCREENSHOT_WE have to check the justification", Status.SCREENSHOT);
		System.out.println("Hey successfull run attempt");
	}

	@Override
	public void tearDown() {
		report.updateTestLog("Tear Down", "Tearing down", Status.DONE);
	}

}
