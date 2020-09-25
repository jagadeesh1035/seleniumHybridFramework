package com.cust.framework;

import org.openqa.selenium.Platform;

public class SeleniumTestParameters extends TestParameters {

	private Browser browser;
	private String browserVersion;
	private Platform platform;

	public SeleniumTestParameters(String currentScenario, String currentTestcase) {
		super(currentScenario, currentTestcase);
	}

	public Browser getBrowser() {
		return browser;
	}

	public void setBrowser(Browser browser) {
		this.browser = browser;
	}

	public String getBrowserVersion() {
		return browserVersion;
	}

	public void setBrowserVersion(String browserVersion) {
		this.browserVersion = browserVersion;
	}

	public Platform getPlatform() {
		return platform;
	}

	public void setPlatform(Platform platform) {
		this.platform = platform;
	}

}
