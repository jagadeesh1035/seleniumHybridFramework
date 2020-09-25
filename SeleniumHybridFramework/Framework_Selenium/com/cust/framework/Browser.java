package com.cust.framework;

public enum Browser {
	Chrome("chrome"), Chromium("chromium"), ChromeHeadless("chrome"),
	Firefox("firefox"), 
	InternetExplorer("internet explorer"), 
	HtmlUnit("htmlunit"), 
	Opera("opera"),
	Safari("safari"),
	Edge("edge");

	private String value;

	private Browser(String value) {
		this.value = value;
	}

	public String getValue() {
		return this.value;
	}
}
