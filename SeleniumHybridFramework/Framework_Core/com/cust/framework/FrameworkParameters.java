package com.cust.framework;

public class FrameworkParameters {

	private String relativePath;
	private String runConfiguration;
	private boolean stopExecution = false;

	private static final FrameworkParameters frameworkParameters = new FrameworkParameters();

	public String getRelativePath() {
		return relativePath;
	}

	public void setRelativePath(String relativePath) {
		this.relativePath = relativePath;
	}

	public String getRunConfiguration() {
		return runConfiguration;
	}

	public void setRunConfiguration(String runConfiguration) {
		this.runConfiguration = runConfiguration;
	}

	public boolean getStopExecution() {
		return stopExecution;
	}

	public void setStopExecution(boolean stopExecution) {
		this.stopExecution = stopExecution;
	}

	public static FrameworkParameters getInstance() {
		return frameworkParameters;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
