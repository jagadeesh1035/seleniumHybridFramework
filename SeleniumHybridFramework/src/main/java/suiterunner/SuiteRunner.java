package suiterunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.openqa.selenium.Platform;

import com.cust.framework.Browser;
import com.cust.framework.ExcelDataAccess;
import com.cust.framework.FrameworkParameters;
import com.cust.framework.IterationOptions;
import com.cust.framework.ResultsSummaryManager;
import com.cust.framework.SeleniumTestParameters;
import com.cust.framework.Settings;

public class SuiteRunner {

	private FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();
	private Properties properties;
	private ResultsSummaryManager resultSummaryManager = new ResultsSummaryManager();

	public static void main(String[] args) {
		SuiteRunner suiteRunner = new SuiteRunner();
		suiteRunner.driveBatchExecution();
	}

	private void driveBatchExecution() {
		resultSummaryManager.setRelativePath();
		properties = Settings.getPropertiesInstance();
		resultSummaryManager.initializeTestBatch(properties.getProperty("RunConfiguration"));

		int nThreads = Integer.parseInt(properties.getProperty("NumberOfThreads"));
		resultSummaryManager.initializeSummaryReport(nThreads);
		resultSummaryManager.setUpErrorLog();
		executeTestBatch(nThreads);
		resultSummaryManager.wrapUp(false);
//		resultSummaryManager.launchResultSummary();
	}

	private void executeTestBatch(int nThreads) {
		List<SeleniumTestParameters> testInstancesToRun = getRunInfo(frameworkParameters.getRunConfiguration());
		ExecutorService parallelExecutor = Executors.newFixedThreadPool(nThreads);
		
		for (int currentTestInstance = 0; currentTestInstance < testInstancesToRun.size(); currentTestInstance++) {
			ParallelRunner testRunner = new ParallelRunner(testInstancesToRun.get(currentTestInstance),
					resultSummaryManager);
			parallelExecutor.execute(testRunner);
			if (frameworkParameters.getStopExecution()) {
				break;
			}
		}
		parallelExecutor.shutdown();
		while (!parallelExecutor.isTerminated()) {
			try {
				Thread.sleep(3000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private List<SeleniumTestParameters> getRunInfo(String sheeName) {
		ExcelDataAccess runManagerAccess = new ExcelDataAccess(frameworkParameters.getRelativePath(), "Run Manager");
		runManagerAccess.setDataSheetName(sheeName);

		int nTestInstances = runManagerAccess.getLastRowNum();
		List<SeleniumTestParameters> testInstancesToRun = new ArrayList<SeleniumTestParameters>();

		for (int currentTestInstance = 1; currentTestInstance <= nTestInstances; currentTestInstance++) {
			String executeFlag = runManagerAccess.getValue(currentTestInstance, "Execute");

			if (executeFlag.equalsIgnoreCase("Yes")) {
				String currentScenario = runManagerAccess.getValue(currentTestInstance, "TestScenario");
				String currentTestcase = runManagerAccess.getValue(currentTestInstance, "TestCase");
				SeleniumTestParameters testParameters = new SeleniumTestParameters(currentScenario, currentTestcase);

				testParameters.setCurrentTestDescription(runManagerAccess.getValue(currentTestInstance, "Description"));

				String iterationMode = runManagerAccess.getValue(currentTestInstance, "IterationMode");
				if (!iterationMode.equals("")) {
					testParameters.setIterationMode(IterationOptions.valueOf(iterationMode));
				} else {
					testParameters.setIterationMode(IterationOptions.RunAllIterations);
				}

				String startIteration = runManagerAccess.getValue(currentTestInstance, "StartIteration");
				if (!startIteration.equals("")) {
					testParameters.setStartIteration(Integer.parseInt(startIteration));
				}

				String endIteration = runManagerAccess.getValue(currentTestInstance, "EndIteration");
				if (!endIteration.equals("")) {
					testParameters.setStartIteration(Integer.parseInt(endIteration));
				}

				String browser = runManagerAccess.getValue(currentTestInstance, "Browser");
				if (!browser.equals("")) {
					testParameters.setBrowser(Browser.valueOf(browser));
				} else {
					testParameters.setBrowser(Browser.valueOf(properties.getProperty("DefaultBrowser")));
				}

				String browserVersion = runManagerAccess.getValue(currentTestInstance, "BrowserVersion");
				if (!browserVersion.equals("")) {
					testParameters.setBrowserVersion(browserVersion);
				}

				String platform = runManagerAccess.getValue(currentTestInstance, "Browser");
				if (!platform.equals("")) {
					testParameters.setPlatform(Platform.valueOf(platform));
				} else {
					testParameters.setPlatform(Platform.valueOf(properties.getProperty("DefaultPlatform")));
				}

				testInstancesToRun.add(testParameters);
			}
		}
		return testInstancesToRun;
	}

}