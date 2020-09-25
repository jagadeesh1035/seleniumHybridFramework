package com.cust.framework;

public class TestParameters {
	private final String currentScenario;
	private final String currentTestCase;
	private String currentTestDescription;
	private IterationOptions iterationMode;
	private int startIteration = 1;
	private int endIteration = 1;

	public String getCurrentTestDescription() {
		return currentTestDescription;
	}

	public void setCurrentTestDescription(String currentTestDescription) {
		this.currentTestDescription = currentTestDescription;
	}

	public IterationOptions getIterationMode() {
		return iterationMode;
	}

	public void setIterationMode(IterationOptions iterationMode) {
		this.iterationMode = iterationMode;
	}

	public int getStartIteration() {
		return startIteration;
	}

	public void setStartIteration(int startIteration) {
		if (startIteration > 0)
			this.startIteration = startIteration;
	}

	public int getEndIteration() {
		return endIteration;
	}

	public void setEndIteration(int endIteration) {
		if (endIteration > 0)
			this.endIteration = endIteration;
	}

	public String getCurrentScenario() {
		return currentScenario;
	}

	public String getCurrentTestCase() {
		return currentTestCase;
	}

	public TestParameters(String currentScenario, String currentTestCase) {
		this.currentScenario = currentScenario;
		this.currentTestCase = currentTestCase;
	}
}
