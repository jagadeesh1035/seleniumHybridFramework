package suiterunner;

import java.util.Date;

import com.cust.framework.FrameworkParameters;
import com.cust.framework.ResultsSummaryManager;
import com.cust.framework.SeleniumTestParameters;
import com.cust.framework.Util;

import supportLibraries.DriverScript;

public class ParallelRunner implements Runnable {

	private final SeleniumTestParameters testParameters;
	private final ResultsSummaryManager resultSummaryManager;

	public ParallelRunner(SeleniumTestParameters testParameters, ResultsSummaryManager resultSummaryManager) {
		super();
		this.testParameters = testParameters;
		this.resultSummaryManager = resultSummaryManager;
	}

	@Override
	public void run() {
		Date startTime = Util.getCurrentTime();
		String testStatus = invokeTestScript();
		Date endTime = Util.getCurrentTime();
		String executionTime = Util.getTimeDifference(startTime, endTime);
		resultSummaryManager.updateResultSummary(testParameters.getCurrentScenario(),
				testParameters.getCurrentTestCase(), testParameters.getCurrentTestDescription(), executionTime,
				testStatus);
	}

	private String invokeTestScript() {
		String testStatus;
		FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();
		if (frameworkParameters.getStopExecution()) {
			testStatus = "Aborted";
		} else {
			DriverScript driverScript = new DriverScript(this.testParameters);
			driverScript.setTestExecutedInUnitTestFramework(false);
			driverScript.driveTestExecution();
			testStatus = driverScript.getTestStatus();
		}
		return testStatus;
	}

}
