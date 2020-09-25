package com.cust.framework;

import java.awt.AWTException;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.imageio.ImageIO;

public class Report {

	private ReportSettings reportSettings;
	private ReportTheme reportTheme;
	private int stepNumber;
	private int nStepsPassed = 0;
	private int nStepsFailed = 0;
	private int nTestsPassed = 0;
	private int nTestsFailed = 0;
	private int nTestsSkipped = 0;

	private List<ReportType> reportTypes = new ArrayList<ReportType>();

	public String testStatus = "Passed";
	private String failureDescription;

	public String getTestStatus() {
		return this.testStatus;
	}

	public String getFailureDescription() {
		return this.failureDescription;
	}

	public Report(ReportSettings reportSettings, ReportTheme reportTheme) {
		this.reportSettings = reportSettings;
		this.reportTheme = reportTheme;
	}

	public void initialize() {
		if (this.reportSettings.generateHtmlReports) {
			new File(this.reportSettings.getReportPath() + Util.getFileSeparator() + "HTML Results").mkdir();
			HtmlReport htmlReport = new HtmlReport(reportSettings, reportTheme);
			this.reportTypes.add(htmlReport);
		}

		if (this.reportSettings.generateExtentReports) {
			new File(this.reportSettings.getReportPath() + Util.getFileSeparator() + "Extent Reports").mkdir();
			ExtentReport extentReport = new ExtentReport(reportSettings);
			this.reportTypes.add(extentReport);
		}
		new File(this.reportSettings.getReportPath() + Util.getFileSeparator() + "Screenshots").mkdir();
	}

	public void initializeTestLog() {
		if (this.reportSettings.getReportName().equals("")) {
			throw new FrameworkException("The Report name cannot be empty!");
		}
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).initializeTestLog();
		}
	}

	public void addTestLogHeading(String heading) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addTestLogHeading(heading);
		}
	}

	public void addTestLogSubHeading(String subHeading1, String subHeading2, String subHeading3, String subHeading4) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addTestLogSubHeading(subHeading1, subHeading2, subHeading3,
					subHeading4);
		}
	}

	public void addTestLogTableHeadings() {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addTestLogTableHeadings();
		}
	}

	public void addTestLogSection(String section) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addTestLogSection(section);
		}
		this.stepNumber = 1;
	}

	public void addTestLogSubSection(String subSection) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addTestLogSubSection(subSection);
		}
	}

	public void updateTestLog(String stepName, String stepDescription, Status stepStatus) {
		if (stepStatus.equals(Status.FAIL)) {
			this.testStatus = "Failed";
			if (this.failureDescription == null) {
				this.failureDescription = stepDescription;
			} else {
				this.failureDescription = (this.failureDescription + "; " + stepDescription);
			}
			++this.nStepsFailed;
		}

		if (stepStatus.equals(Status.PASS)) {
			++this.nStepsPassed;
		}

		if (stepStatus.ordinal() <= this.reportSettings.getLogLevel()) {
			String screenshotName = null;

			if (stepStatus.equals(Status.FAIL) && this.reportSettings.takeScreenshotFailedStep) {
				getScreenshotTaken(screenshotName);
			}
			if (stepStatus.equals(Status.PASS) && this.reportSettings.takeScreenshotPassedStep) {
				getScreenshotTaken(screenshotName);
			}
			if (stepStatus.equals(Status.SCREENSHOT)) {
				getScreenshotTaken(screenshotName);
			}

			for (int i = 0; i < this.reportTypes.size(); i++) {
				((ReportType) this.reportTypes.get(i)).updateTestLog(Integer.toString(this.stepNumber), stepName,
						stepDescription, stepStatus, screenshotName);
			}
			++this.stepNumber;
		}

	}

	private void getScreenshotTaken(String screenshotName) {
		screenshotName = this.reportSettings.getReportName() + "_" + Util
				.getFormattedCurrentTime(this.reportSettings.getDateFormatString()).replace(" ", "_").replace(":", "_")
				+ ".png";
		takeScreenshot(this.reportSettings.getReportPath() + Util.getFileSeparator() + "Screenshots"
				+ Util.getFileSeparator() + screenshotName);
	}

	protected void takeScreenshot(String screenshotPath) {
		Toolkit toolKit = Toolkit.getDefaultToolkit();
		Dimension screenSize = toolKit.getScreenSize();
		Rectangle rectangle = new Rectangle(0, 0, screenSize.width, screenSize.height);
		Robot robot;

		try {
			robot = new Robot();
		} catch (AWTException e) {
			e.printStackTrace();
			throw new FrameworkException("Error while creating Robot object(for taking screenshot)!");
		}

		BufferedImage screenshotImage = robot.createScreenCapture(rectangle);
		File screenshotFile = new File(screenshotPath);

		try {
			ImageIO.write(screenshotImage, "jpg", screenshotFile);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException("Error while writing screenshot to .jpg file!");
		}
	}

	public void addTestLogFooter(String executionTime) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addTestLogFooter(executionTime, this.nStepsPassed,
					this.nStepsFailed);
		}
	}

	public void initializeResultSummary() {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).initializeResultSummary();
		}
	}

	public void addResultSummaryHeading(String heading) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addResultSummaryHeading(heading);
		}
	}

	public void addResultSummarySubHeading(String subHeading1, String subHeading2, String subHeading3,
			String subHeading4) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addResultSummarySubHeading(subHeading1, subHeading2, subHeading3,
					subHeading4);
		}
	}

	public void addResultSummaryTableHeadings() {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addResultSummaryTableHeadings();
		}
	}

	public void updateResultSummary(String scenarioName, String testcaseName, String testcaseDescription,
			String executionTime, String testStatus) {
		if (testStatus.equalsIgnoreCase("failed")) {
			++this.nTestsFailed;
		} else if (testStatus.equalsIgnoreCase("passed")) {
			++this.nTestsPassed;
		} else if (testStatus.equalsIgnoreCase("skipped")) {
			++this.nTestsSkipped;
		}

		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).updateResultSummary(scenarioName, testcaseName, testcaseDescription,
					executionTime, testStatus);
		}
	}

	public void addResultSummaryFooter(String totalExecutionTime) {
		for (int i = 0; i < this.reportTypes.size(); i++) {
			((ReportType) this.reportTypes.get(i)).addResultSummaryFooter(totalExecutionTime, this.nTestsPassed,
					this.nTestsFailed, this.nTestsSkipped);
		}
	}

}
