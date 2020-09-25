package supportLibraries;

import java.awt.AWTException;
import java.text.ParseException;
import java.util.Date;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.ITestContext;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeSuite;

import com.cust.framework.AccelerateProcessDataTable;
import com.cust.framework.FrameworkException;
import com.cust.framework.FrameworkParameters;
import com.cust.framework.ResultsSummaryManager;
import com.cust.framework.SeleniumReport;
import com.cust.framework.SeleniumTestParameters;
import com.cust.framework.Util;

/*
 * Abstract base class for all the test cases
 */
public abstract class TestCase {

	protected SeleniumTestParameters testParameters;
	protected DriverScript driverScript;
	private ResultsSummaryManager resultSummaryManager = new ResultsSummaryManager();
	private Date startTime;
	protected AccelerateProcessDataTable dataTable;
	protected SeleniumReport report;
	protected WebDriver driver;
	protected ScriptHelper scriptHelper;
	protected Properties properties;

	/*
	 * Functionality to initialize the ScriptHelper object and intern the objects
	 * wrapped by it, as well as to load properties object using the class
	 */

	public void initialize(ScriptHelper scriptHelper) {
		this.scriptHelper = scriptHelper;
		this.dataTable = scriptHelper.getDataTable();
		this.driver = scriptHelper.getDriver();
		this.report = scriptHelper.getReport();
	}

	/*
	 * Function to do the require setup activities before executing overall test
	 * suite in TestNG
	 */
	@BeforeSuite
	public void suiteSetup(ITestContext testContext) {
		resultSummaryManager.setRelativePath();
		resultSummaryManager.initializeTestBatch(testContext.getSuite().getName());

		int nThreads;
		if (testContext.getSuite().getParallel().equalsIgnoreCase("False")) {
			nThreads = 1;
		} else {
			nThreads = testContext.getCurrentXmlTest().getThreadCount();
		}

		resultSummaryManager.initializeSummaryReport(nThreads);
		resultSummaryManager.setUpErrorLog();
	}

	/*
	 * Function to do the require setup activities before executing each test case
	 * in TestNG
	 */
	@BeforeMethod(alwaysRun = true)
	public void testMethodSetup() {
		FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();
		if (frameworkParameters.getStopExecution()) {
			suiteTearDown();
			throw new FrameworkException("Aborting all subsequent tests!");
		} else {
			startTime = Util.getCurrentTime();
			String currentScenario = capitalizeFirstLetter(this.getClass().getPackage().getName().substring(12));
			String currentTestcase = this.getClass().getSimpleName();
			testParameters = new SeleniumTestParameters(currentScenario, currentTestcase);
		}
	}

	public String capitalizeFirstLetter(String myString) {
		StringBuilder stringBuilder = new StringBuilder(myString);
		stringBuilder.setCharAt(0, Character.toUpperCase(stringBuilder.charAt(0)));
		return stringBuilder.toString();
	}

	/*
	 * Function to be required setup activities before starting test execution
	 */
	public abstract void setUp();

	/*
	 * Function which implements tests to be automated
	 */
	public abstract void executeTest() throws InterruptedException, AWTException, ParseException;

	/*
	 * Function to do required tear down activities at the end of test execution
	 */
	public abstract void tearDown();

	private int retryCount = 1;

	@AfterMethod(alwaysRun = true)
	public void testMethodTearDown() {
		String testStatus = driverScript.getTestStatus();
		Date endTime = Util.getCurrentTime();
		String executionTime = Util.getTimeDifference(startTime, endTime);

		RetryAnalyzer retryAnalyzer = new RetryAnalyzer();
		if (testStatus.equals("Passed") || retryCount == retryAnalyzer.retryLimit) {
			resultSummaryManager.updateResultSummary(testParameters.getCurrentScenario(),
					testParameters.getCurrentTestCase(), testParameters.getCurrentTestDescription(), executionTime,
					testStatus);
			retryCount = 0;
		}
		retryCount++;
	}

	@AfterSuite(alwaysRun = true)
	public void suiteTearDown() {
		resultSummaryManager.wrapUp(true);
	}
}
