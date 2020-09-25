package supportLibraries;

import java.io.File;
import java.io.IOException;
import java.util.Date;
import java.util.Properties;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.Platform;
import org.openqa.selenium.WebDriver;
import org.testng.Assert;

import com.cust.framework.AccelerateProcessDataTable;
import com.cust.framework.Browser;
import com.cust.framework.ExcelDataAccess;
import com.cust.framework.ExecutionMode;
import com.cust.framework.FrameworkException;
import com.cust.framework.FrameworkParameters;
import com.cust.framework.IterationOptions;
import com.cust.framework.OnError;
import com.cust.framework.ReportSettings;
import com.cust.framework.ReportTheme;
import com.cust.framework.ReportThemeFactory;
import com.cust.framework.ReportThemeFactory.Theme;
import com.cust.framework.SeleniumReport;
import com.cust.framework.SeleniumTestParameters;
import com.cust.framework.Settings;
import com.cust.framework.Status;
import com.cust.framework.TimeStamp;
import com.cust.framework.Util;

public class DriverScript {
	private TestCase testcase;
	private int currentIteration;
	private Date startTime;
	private Date endTime;

	private AccelerateProcessDataTable dataTable;
	private ReportSettings reportSettings;
	private SeleniumReport report;
	private WebDriver driver;

	private Properties properties;
	private ExecutionMode executionMode;
	private final FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();
	private boolean testExecutedInUnitTestFramework = true;
	private boolean linkScreenshotsToTestlog = true;
	private String testStatus;

	private final SeleniumTestParameters testParameters;
	private String reportPath;

	public DriverScript(SeleniumTestParameters testParameters) {
		this.testParameters = testParameters;
	}

	/*
	 * Function to indicate whether test is executed in TestNg/Junit or not
	 */
	public void setTestExecutedInUnitTestFramework(boolean testExecutedInUnitTestFramework) {
		this.testExecutedInUnitTestFramework = testExecutedInUnitTestFramework;
	}

	/*
	 * Function to configure the linking of screenshots to the corresponding log
	 */
	public void setLinkScreenshotsToTestlog(boolean linkScreenshotsToTestlog) {
		this.linkScreenshotsToTestlog = linkScreenshotsToTestlog;
	}

	/*
	 * Function to get the status of the test case executed
	 */
	public String getTestStatus() {
		return testStatus;
	}

	public void driveTestExecution() {
		startUp();
		initializeTestIterations();
		initializeWebDriver();
		initializeTestReport();
		initializeDataTable();
		initializeTestScript();

		try {
			testcase.setUp();
			executeTestIterations();
		} catch (FrameworkException e) {
			exceptionHandler(e, e.errorName);
		} catch (Exception e1) {
			exceptionHandler(e1, "Error");
		} finally {
			testcase.tearDown();
		}
		quitWebDriver();
		wrapUp();
	}

	private void startUp() {
		startTime = Util.getCurrentTime();
		properties = Settings.getPropertiesInstance();
		setDefaultTestParameters();
	}

	private void setDefaultTestParameters() {
		if (testParameters.getIterationMode() == null) {
			testParameters.setIterationMode(IterationOptions.RunAllIterations);
		}

		if (System.getProperty("Browser") != null) {
			testParameters.setBrowser(Browser.valueOf(System.getProperty("Browser")));
		} else {
			testParameters.setBrowser(Browser.valueOf(properties.getProperty("DefaultBrowser")));
		}

		if (System.getProperty("BrowserVersion") != null) {
			testParameters.setBrowserVersion(System.getProperty("BrowserVersion"));
		}

		if (System.getProperty("Platform") != null) {
			testParameters.setPlatform(Platform.valueOf(System.getProperty("Platform")));
		} else {
			if (testParameters.getPlatform() == null) {
				testParameters.setPlatform(Platform.valueOf(properties.getProperty("DefaultPlatform")));
			}
		}
	}

	private void initializeTestIterations() {
		switch (testParameters.getIterationMode()) {
		case RunAllIterations:
			String dataTablePath = System.getProperty("user.dir") + "//DataTables";
			ExcelDataAccess testDataAccess = new ExcelDataAccess(dataTablePath, testParameters.getCurrentScenario());
			testDataAccess.setDataSheetName(properties.getProperty("DefaultDataSheet"));
			int iterations = testDataAccess.getRowCount(testParameters.getCurrentTestCase(), 0);
			testParameters.setEndIteration(iterations);
			currentIteration = 1;
			break;

		case RunOneIterationOnly:
			currentIteration = 1;
			break;

		case RunRangeOfIterations:
			if (testParameters.getStartIteration() > testParameters.getEndIteration()) {
				throw new FrameworkException("StartIteration cannot be greater than EndIteration in Data Sheet");
			}
			currentIteration = testParameters.getStartIteration();
			break;

		default:
			throw new FrameworkException("Unhandled Iteration mode!");
		}
	}

	private void initializeWebDriver() {
		executionMode = ExecutionMode.valueOf(properties.getProperty("ExecutionMode"));
		switch (executionMode) {
		case Local:
			driver = WebDriverFactory.getDriver(testParameters.getBrowser());
			break;
		case Remote:
			driver = WebDriverFactory.getDriver(testParameters.getBrowser(), properties.getProperty("RemoteURL"));
			break;
		case Grid:
			driver = WebDriverFactory.getDriver(testParameters.getBrowser(), testParameters.getBrowserVersion(),
					testParameters.getPlatform(), properties.getProperty("RemoteURL"));
			break;
		default:
			throw new FrameworkException("Unhandled Execution mode!");
		}
		driver.manage().window().maximize();
	}

	private void initializeTestReport() {
		initializeReportSettings();
		ReportTheme reportTheme = ReportThemeFactory
				.getReportsTheme(Theme.valueOf(properties.getProperty("ReportTheme")));
		report = new SeleniumReport(reportSettings, reportTheme);
		report.initialize();
		report.setDriver(driver);
		report.initializeTestLog();
		createTestLogHeader();
	}

	private void initializeReportSettings() {
		if (System.getProperty("ReportPath") != null) {
			reportPath = System.getProperty("ReportPath");
		} else {
			reportPath = TimeStamp.getInstance();
		}

		reportSettings = new ReportSettings(reportPath,
				testParameters.getCurrentScenario() + "_" + testParameters.getCurrentTestCase());
		reportSettings.setDateFormatString(this.properties.getProperty("DateFormatString"));
		reportSettings.setLogLevel(Integer.parseInt(properties.getProperty("LogLevel")));
		reportSettings.setProjectName(this.properties.getProperty("ProjectName"));
		reportSettings.generateHtmlReports = Boolean.parseBoolean(this.properties.getProperty("HtmlReport"));
		reportSettings.generateExtentReports = Boolean.parseBoolean(this.properties.getProperty("ExtentReport"));
		reportSettings.takeScreenshotFailedStep = Boolean
				.parseBoolean(this.properties.getProperty("TakeScreenshotFailedStep"));
		reportSettings.takeScreenshotPassedStep = Boolean
				.parseBoolean(this.properties.getProperty("TakeScreenshotPassedStep"));

		if (testParameters.getBrowser().equals(Browser.HtmlUnit)) {
			reportSettings.linkScreenshotToTestLog = false;
		} else {
			reportSettings.linkScreenshotToTestLog = this.linkScreenshotsToTestlog;
		}
	}

	public void createTestLogHeader() {
		report.addTestLogHeading(reportSettings.getReportName() + " - " + " Execution report");
		report.addTestLogSubHeading("Date & Time",
				": " + Util.getFormattedCurrentTime(this.properties.getProperty("DateFormatString")), "Iteration Mode",
				": " + testParameters.getIterationMode());
		report.addTestLogSubHeading("Start Iteration", ": " + testParameters.getStartIteration(), "End Iteration",
				": " + testParameters.getEndIteration());

		switch (executionMode) {
		case Local:
			report.addTestLogSubHeading("Browser", ": " + testParameters.getBrowser(), "Executed On",
					": Local  Machine");
			break;

		case Remote:
			report.addTestLogSubHeading("Browser", ": " + testParameters.getBrowser(), "Executed On",
					": " + properties.getProperty("RemoteUrl"));
			break;

		case Grid:
			String browserVersion = testParameters.getBrowserVersion();
			if (browserVersion == null) {
				browserVersion = "Not Specified";
			}
			report.addTestLogSubHeading("Browser", ": " + testParameters.getBrowser(), "Version",
					": " + browserVersion);
			report.addTestLogSubHeading("Platform", ": " + testParameters.getPlatform().toString(), "Executed On",
					": Grid @" + properties.getProperty("RemoteUrl"));
			break;
		default:
			throw new FrameworkException("Unhandled Execution Mode!");
		}
		report.addTestLogTableHeadings();
	}

	public void initializeDataTable() {
		String dataTablePath = System.getProperty("user.dir") + "\\datatables";
		String runTimeDataTablePath;
		Boolean includeTestDataInReport = Boolean.parseBoolean(properties.getProperty("IncludeTestDataInReport"));
		if (includeTestDataInReport) {
			runTimeDataTablePath = reportPath + Util.getFileSeparator() + "Datatables";
			File runTimeDataTable = new File(
					runTimeDataTablePath + Util.getFileSeparator() + testParameters.getCurrentScenario() + ".xlsx");
			if (!runTimeDataTable.exists()) {
				File dataTable = new File(
						dataTablePath + Util.getFileSeparator() + testParameters.getCurrentScenario() + ".xlsx");
				try {
					FileUtils.copyFile(dataTable, runTimeDataTable);
				} catch (IOException e) {
					throw new FrameworkException(
							"Error in creating run-time datatable: Copying the datatable is failed!");
				}
			}

			File runTimeCommonDataTable = new File(
					runTimeDataTablePath + Util.getFileSeparator() + "Common Testdata.xlsx");
			if (!runTimeCommonDataTable.exists()) {
				File commonDataTable = new File(dataTablePath + Util.getFileSeparator() + "Common Testdata.xlsx");
				try {
					FileUtils.copyFile(commonDataTable, runTimeCommonDataTable);
				} catch (IOException e) {
					throw new FrameworkException(
							"Error in creating run-time datatable: Copying the datatable is failed!");
				}
			}
		} else {
			runTimeDataTablePath = dataTablePath;
		}

		dataTable = new AccelerateProcessDataTable(runTimeDataTablePath, testParameters.getCurrentScenario());
		dataTable.setDataReferenceIdentifier(properties.getProperty("DataReferenceIdentifier"));
		dataTable.setCurrentRow(testParameters.getCurrentTestCase(), currentIteration);
	}

	private void initializeTestScript() {
		ScriptHelper scriptHelper = new ScriptHelper(dataTable, report, driver);
		testcase = getTestCaseInstance();
		testcase.initialize(scriptHelper);
	}

	@SuppressWarnings("deprecation")
	public TestCase getTestCaseInstance() {
		Class<?> testScriptClass;
		try {
			testScriptClass = Class.forName("testscripts." + testParameters.getCurrentScenario().toLowerCase() + "."
					+ testParameters.getCurrentTestCase());
		} catch (ClassNotFoundException e) {
			throw new FrameworkException("The specified test script is not found!");
		}

		try {
			return (TestCase) testScriptClass.newInstance();
		} catch (Exception e) {
			throw new FrameworkException("Error while instantiating the specified test script");
		}
	}

	private void executeTestIterations() {
		while (currentIteration <= testParameters.getEndIteration()) {
			report.addTestLogSection("Iteration: " + Integer.toString(currentIteration));

			try {
				testcase.executeTest();
			} catch (FrameworkException e) {
				exceptionHandler(e, e.errorName);
			} catch (Exception e) {
				exceptionHandler(e, "Error");
			}
			currentIteration++;
			dataTable.setCurrentRow(testParameters.getCurrentTestCase(), currentIteration);
		}
	}

	private void exceptionHandler(Exception ex, String exceptionName) {
		String exceptionDescription = ex.getMessage();
		if (exceptionDescription == null) {
			exceptionDescription = ex.toString();
		}
		if (ex.getCause() != null) {
			report.updateTestLog(exceptionName, exceptionDescription + "<b>Caused by: </b>" + ex.getCause(),
					Status.FAIL);
		} else {
			report.updateTestLog(exceptionName, exceptionDescription, Status.FAIL);
		}

		if (frameworkParameters.getStopExecution()) {
			report.updateTestLog("Automation Run Info",
					"Test execution terminated by user! All subsequent tests aborted!", Status.DONE);
			currentIteration = testParameters.getEndIteration();
		} else {
			OnError onError = OnError.valueOf(properties.getProperty("OnError"));
			switch (onError) {
			case NextIteration:
				report.updateTestLog("Automation Run Info",
						"Test execution terminated by user! Proceeding to next iteration(if applicable)..",
						Status.DONE);
				break;
			case NextTestCase:
				report.updateTestLog("Automation Run Info",
						"Test execution terminated by user! Proceeding to next test case(if applicable)..",
						Status.DONE);
				currentIteration = testParameters.getEndIteration();
				break;
			case Stop:
				frameworkParameters.setStopExecution(true);
				report.updateTestLog("Automation Run Info",
						"Test execution terminated by user! All subsequent tests aborted!", Status.DONE);
				currentIteration = testParameters.getEndIteration();
				break;
			default:
				throw new FrameworkException("Unhandled OnError option!");
			}
		}
	}

	private void quitWebDriver() {
		driver.quit();
	}

	private void wrapUp() {
		endTime = Util.getCurrentTime();
		closeTestReport();
		testStatus = report.getTestStatus();
		if (testExecutedInUnitTestFramework && testStatus.equalsIgnoreCase("Failed")) {
			Assert.fail(report.getFailureDescription());
		}
	}

	private void closeTestReport() {
		String executionTime = Util.getTimeDifference(startTime, endTime);
		report.addTestLogFooter(executionTime);
	}

}
