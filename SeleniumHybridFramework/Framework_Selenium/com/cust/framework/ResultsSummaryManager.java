package com.cust.framework;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.util.Date;
import java.util.Properties;
import org.apache.commons.io.FileUtils;
import com.cust.framework.ReportThemeFactory.Theme;

public class ResultsSummaryManager {
	private static SeleniumReport summaryReport;
	private static ReportSettings reportSettings;
	private static String reportPath;
	private static Date overallStartTime;
	private static Date overallEndTime;
	private Properties properties;
	private FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();

	public void setRelativePath() {
		String relativePath = (new File(System.getProperty("user.dir"))).getAbsolutePath();
		if (relativePath.contains("supportlibraries")) {
			relativePath = (new File(System.getProperty("user.dir"))).getParent();
		}
		this.frameworkParameters.setRelativePath(relativePath);
	}

	public void initializeTestBatch(String runConfiguration) {
		overallStartTime = Util.getCurrentTime();
		this.properties = Settings.getPropertiesInstance();
		this.frameworkParameters.setRunConfiguration(runConfiguration);
	}

	public void initializeSummaryReport(int nThreads) {
		this.initializeReportSettings();
		ReportTheme reportTheme = ReportThemeFactory
				.getReportsTheme(Theme.valueOf(this.properties.getProperty("ReportTheme")));
		summaryReport = new SeleniumReport(reportSettings, reportTheme);
		summaryReport.initialize();
		summaryReport.initializeResultSummary();
		this.createResultSummaryHeader(nThreads);
	}

	private void initializeReportSettings() {
		if (System.getProperty("ReportPath") != null) {
			reportPath = System.getProperty("ReportPath");
		} else {
			reportPath = TimeStamp.getInstance();
		}

		reportSettings = new ReportSettings(reportPath, "");
		reportSettings.setDateFormatString(this.properties.getProperty("DateFormatString"));
		reportSettings.setProjectName(this.properties.getProperty("ProjectName"));
		reportSettings.generateHtmlReports = Boolean.parseBoolean(this.properties.getProperty("HtmlReport"));
		reportSettings.generateExtentReports = Boolean.parseBoolean(this.properties.getProperty("ExtentReport"));
		reportSettings.linkTestLogToSummary = true;
	}

	private void createResultSummaryHeader(int nThreads) {
		summaryReport.addResultSummaryHeading(
				reportSettings.getProjectName() + " - " + "Automation Execution Result Summary");
		summaryReport.addResultSummarySubHeading("Date & Time",
				": " + Util.getFormattedCurrentTime(this.properties.getProperty("DateFormatString")), "OnError",
				": " + this.properties.getProperty("OnError"));
		summaryReport.addResultSummarySubHeading("Run Configuration",
				": " + this.frameworkParameters.getRunConfiguration(), "No. Of Threads", ": " + nThreads);
		summaryReport.addResultSummaryTableHeadings();
	}

	public void setUpErrorLog() {
		String errorLog = reportPath + Util.getFileSeparator() + "ErrorLog.txt";
		try {
			System.setErr(new PrintStream(new FileOutputStream(errorLog)));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FrameworkException("Error while setting up the error log");
		}
	}

	public void updateResultSummary(String scenarioName, String testcaseName, String testcaseDescription,
			String executionTime, String testStatus) {
		summaryReport.updateResultSummary(scenarioName, testcaseName, testcaseDescription, executionTime, testStatus);
	}

	public void wrapUp(Boolean testExecutedInUnitTestFramework) {
		overallEndTime = Util.getCurrentTime();
		String totalExecutionTime = Util.getTimeDifference(overallStartTime, overallEndTime);
		summaryReport.addResultSummaryFooter(totalExecutionTime);
		if (testExecutedInUnitTestFramework.booleanValue()) {
			File testNgResultSrc = new File(this.frameworkParameters.getRelativePath() + Util.getFileSeparator()
					+ this.properties.getProperty("TestNgReportPath"));
			File testNgResultDest = new File(reportPath + Util.getFileSeparator() + "TestNG Results");

			try {
				FileUtils.copyDirectory(testNgResultSrc, testNgResultDest);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public void launchResultSummary() {
		if (reportSettings.generateHtmlReports) {
			try {
				Runtime.getRuntime().exec(
						"RunDLL32.EXE shell32.dll,shellExec_RunDLL " + reportPath + "\\HTML Results\\Summary.html");
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

}
