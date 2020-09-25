package com.cust.framework;

import java.io.File;
import java.util.Properties;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

public class ExtentReport implements ReportType{

	private ExtentHtmlReporter htmlReporter;
	private ExtentReports extent;
	protected ExtentTest logger;
	private static ThreadLocal<ExtentTest> test = new ThreadLocal<ExtentTest>();
	private String extentReportPath;
	private ReportSettings reportSettings;
	private Properties properties = Settings.getPropertiesInstance();
	private String currentSection = "";
	private String currentSubSection = "";
	private int currentContentNumber = 1;
	File imgPath = new File(Settings.getPropertiesInstance().getProperty("ReportImagePath"));
	
	public ExtentReport(ReportSettings reportSettings) {
		this.reportSettings = reportSettings;
		this.extentReportPath = reportSettings.getReportPath() + Util.getFileSeparator() + "Extent Reports"
				+ Util.getFileSeparator() + reportSettings.getReportName() + ".html";
	}
	
	public void initializeExtentReports() {
		htmlReporter = new ExtentHtmlReporter(new File(extentReportPath));
		htmlReporter.config().setDocumentTitle("Automation Execution Report - " + reportSettings.getProjectName());
		htmlReporter.config().setTimeStampFormat(reportSettings.getDateFormatString());
		htmlReporter.config().setEncoding("utf-8");
		if (properties.getProperty("Theme").equalsIgnoreCase("Standard")) {
			htmlReporter.config().setTheme(Theme.STANDARD);
		} else if (properties.getProperty("Theme").equalsIgnoreCase("Dark")) {
			htmlReporter.config().setTheme(Theme.DARK);
		}

		// Create an object of Extent Reports
		extent = new ExtentReports();
		extent.attachReporter(htmlReporter);
		extent.setSystemInfo("Host Name", properties.getProperty("HostName"));
		extent.setSystemInfo("Environment", properties.getProperty("Environment"));
		extent.setSystemInfo("Tester", properties.getProperty("TesterName"));
		extent.setReportUsesManualConfiguration(true);
	}
	
	
	public void initializeTestLog() {
		
	}

	public void addTestLogHeading(String heading) {
		logger = extent.createTest(heading.substring(0, heading.indexOf("<br>") - 1));
		test.set(logger);
	}

	public void addTestLogSubHeading(String paramString1, String paramString2, String paramString3,
			String paramString4) {
		// TODO Auto-generated method stub
		
	}

	public void addTestLogTableHeadings() {
		// TODO Auto-generated method stub
		
	}

	public void addTestLogSection(String paramString) {
		// TODO Auto-generated method stub
		
	}

	public void addTestLogSubSection(String paramString) {
		// TODO Auto-generated method stub
		
	}

	public void updateTestLog(String paramString1, String paramString2, String paramString3, Status paramStatus,
			String paramString4) {
		// TODO Auto-generated method stub
		
	}

	public void addTestLogFooter(String paramString, int paramInt1, int paramInt2) {
		// TODO Auto-generated method stub
		
	}

	public void initializeResultSummary() {
		initializeExtentReports();		
	}

	public void addResultSummaryHeading(String heading) {
		// TODO Auto-generated method stub
		
	}

	public void addResultSummarySubHeading(String subHeading1, String subHeading2, String subHeading3,
			String subHeading4) {
		extent.setSystemInfo(subHeading1, subHeading2.substring(1, subHeading2.length()));
		extent.setSystemInfo(subHeading3, subHeading4.substring(1, subHeading4.length()));
	}

	public void addResultSummaryTableHeadings() {
		// TODO Auto-generated method stub
		
	}

	public void updateResultSummary(String paramString1, String paramString2, String paramString3, String paramString4,
			String paramString5) {
		// TODO Auto-generated method stub
		
	}

	public void addResultSummaryFooter(String paramString, int paramInt1, int paramInt2, int paramInt3) {
		extent.flush();
	}

}
