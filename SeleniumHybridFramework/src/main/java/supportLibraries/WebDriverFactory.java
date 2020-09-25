package supportLibraries;

import java.io.File;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Collections;
import java.util.Properties;

import org.openqa.selenium.Platform;
import org.openqa.selenium.Proxy;
import org.openqa.selenium.UnexpectedAlertBehaviour;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;
import org.openqa.selenium.firefox.FirefoxProfile;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.openqa.selenium.ie.InternetExplorerOptions;
import org.openqa.selenium.remote.CapabilityType;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import com.cust.framework.Browser;
import com.cust.framework.FrameworkException;
import com.cust.framework.Settings;


public class WebDriverFactory {
	private static Properties properties;
	public static ChromeOptions ops;

	public static WebDriver getDriver(Browser browser) {
		WebDriver driver = null;
		properties = Settings.getPropertiesInstance();
		switch (browser) {
		case Chrome:
			System.setProperty("webdriver.chrome.driver", properties.getProperty("ChromeDriverPath"));
			ChromeOptions options = new ChromeOptions();
			options.addArguments("test-type");
			options.addArguments("--disable-extensions");
			options.setExperimentalOption("useAutomationExtension", false);
			options.setExperimentalOption("excludeSwitches", Collections.singletonList("enable-automation"));
			driver = new ChromeDriver(options);
			break;
		case Firefox:
			System.setProperty("webdriver.gecko.driver", properties.getProperty("GeckoDriverPath"));
			FirefoxProfile ffProfile = new FirefoxProfile();
			ffProfile.setPreference("network.negotiate-auth.delegation-uris", "http://,https://");
			ffProfile.setPreference("network.negotiate-auth.trusted-uris", "http://,https://");
			ffProfile.setPreference("network.auth.force-generic-ntlm", true);
			DesiredCapabilities capabilities = DesiredCapabilities.firefox();
			capabilities.setCapability("firefox_profile", ffProfile);
			capabilities.setCapability("marionette", true);
			FirefoxOptions ffOptions = new FirefoxOptions(capabilities);
			ffOptions.setBinary(properties.getProperty("GeckoDriverPath"));
			driver = new FirefoxDriver(ffOptions);
			break;
		case InternetExplorer:
			System.setProperty("webdriver.ie.driver", properties.getProperty("InternetExplorerDriverPath"));
			DesiredCapabilities ieCapabilities = DesiredCapabilities.internetExplorer();
			ieCapabilities.setCapability(CapabilityType.BROWSER_NAME, "IE");
			ieCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS, true);
			ieCapabilities.setCapability(InternetExplorerDriver.UNEXPECTED_ALERT_BEHAVIOR,
					UnexpectedAlertBehaviour.IGNORE);
			ieCapabilities.setJavascriptEnabled(true);
			InternetExplorerOptions ieOptions = new InternetExplorerOptions(ieCapabilities);
			driver = new InternetExplorerDriver(ieOptions);
			break;
		case Edge:
			System.setProperty("webdriver.edge.driver", properties.getProperty("EdgeDriverPath"));
			driver = new EdgeDriver();
			break;
		default:
			break;
		}
		driver.manage().window().maximize();
		driver.manage().deleteAllCookies();
		return driver;
	}
	
	
	private static DesiredCapabilities getProxyCapabilities() {
		Proxy proxy = new Proxy();
		proxy.setProxyType(Proxy.ProxyType.MANUAL);
		properties = Settings.getPropertiesInstance();
		String proxyUrl = properties.getProperty("ProxyHost") + ":" + properties.getProperty("ProxyPort");
		proxy.setHttpProxy(proxyUrl);
		proxy.setFtpProxy(proxyUrl);
		proxy.setSslProxy(proxyUrl);
		DesiredCapabilities desiredCapabilities = new DesiredCapabilities();
		desiredCapabilities.setCapability("proxy", proxy);
		return desiredCapabilities;
	}
	
	
	public static WebDriver getDriver(Browser browser, String remoteUrl) {
		properties = Settings.getPropertiesInstance();
		boolean proxyRequired = Boolean.parseBoolean(properties.getProperty("ProxyRequired"));
		DesiredCapabilities desiredCapabilities = null;
		URL url;

		if ((browser.equals(Browser.HtmlUnit) || browser.equals(Browser.Opera)) && proxyRequired) {
			desiredCapabilities = getProxyCapabilities();
		} else if (browser.equals(Browser.InternetExplorer)) {
			desiredCapabilities = DesiredCapabilities.internetExplorer();
			desiredCapabilities.setCapability(InternetExplorerDriver.INTRODUCE_FLAKINESS_BY_IGNORING_SECURITY_DOMAINS,
					true);
			desiredCapabilities.setJavascriptEnabled(true);
		} else if (browser.equals(Browser.Chrome) || browser.equals(Browser.Chromium)) {
			ChromeOptions options = new ChromeOptions();
			if (browser.equals(Browser.Chromium)) {
				options.setBinary(new File(properties.getProperty("ChromiumDriverPath")));
				options.setExperimentalOption("debuggerAddress", properties.getProperty("DebuggerAddress"));
			} else {
				options.addArguments("test-type");
			}
			desiredCapabilities = DesiredCapabilities.chrome();
			desiredCapabilities.setCapability(ChromeOptions.CAPABILITY, options);
		} else if (browser.equals(Browser.Firefox)) {
			File firefoxProfileFolder = new File(properties.getProperty("FirefoxProfilePath"));
			FirefoxProfile profile = new FirefoxProfile(firefoxProfileFolder);
			desiredCapabilities = DesiredCapabilities.firefox();
			desiredCapabilities.setCapability(FirefoxDriver.PROFILE, profile);
			profile.setPreference("plugin.state.java", 2);
		} else {
			desiredCapabilities = new DesiredCapabilities();
		}

		desiredCapabilities.setBrowserName(browser.getValue());
		desiredCapabilities.setJavascriptEnabled(true);

		try {
			if (browser.equals(Browser.Chromium)) {
				url = new URL(properties.getProperty("RemoteDriverURL"));
			} else {
				url = new URL(remoteUrl);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
			throw new FrameworkException("Specified url is malfunctioned!");
		}
		return new RemoteWebDriver(url, desiredCapabilities);
	}
	
	
	public static WebDriver getDriver(Browser browser, String browserVersion, Platform platform, String remoteUrl) {
		return getDriver(browser, null, null, remoteUrl);
	}
	
}
