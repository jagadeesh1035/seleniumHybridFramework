package supportLibraries;

import java.io.IOException;
import java.util.List;
import java.util.Properties;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.MediaEntityBuilder;
import com.aventstack.extentreports.Status;
import com.cust.framework.FrameworkException;
import com.cust.framework.Util;

public class WebDriverUtils {
	public WebDriver driver;
	public ExtentTest logger;
	public Properties properties;

	public void waitFor(long milliSeconds) {
		try {
			Thread.sleep(milliSeconds);
		} catch (InterruptedException e) {
			e.printStackTrace();
			throw new FrameworkException("Interrupted Exception while thread.sleep");
		}
	}

	public WebElement findElement(By by) {
		WebElement element = null;
		try {
			element = this.driver.findElement(by);
		} catch (Exception e) {
			e.printStackTrace();
			updateLogStatus(Status.DEBUG,
					"Exception is thrown while finding element['" + by + "']|| Exception:" + e.getMessage());
		}
		return element;
	}

	public List<WebElement> findElements(By by) {
		List<WebElement> elements = null;
		try {
			elements = this.driver.findElements(by);
		} catch (Exception e) {
			e.printStackTrace();
			updateLogStatus(Status.DEBUG,
					"Exception is thrown while finding element['" + by + "']|| Exception:" + e.getMessage());
		}
		return elements;
	}

	public WebDriverWait getWebDriverWait(long timeOutInSeconds) {
		return new WebDriverWait(this.driver, timeOutInSeconds);
	}

	public void waitUntilElementIsLocated(By by, long timeOutInSeconds) {
		getWebDriverWait(timeOutInSeconds).until(ExpectedConditions.presenceOfElementLocated(by));
	}

	public void waitUntilElementIsVisible(By by, long timeOutInSeconds) {
		getWebDriverWait(timeOutInSeconds).until(ExpectedConditions.visibilityOfElementLocated(by));
	}

	public void waitUntilElementIsClickable(By by, long timeOutInSeconds) {
		getWebDriverWait(timeOutInSeconds).until(ExpectedConditions.elementToBeClickable(by));
	}

	public void waitUntilElementIsEnabled(By by, long timeOutInSeconds) {
		getWebDriverWait(timeOutInSeconds).until(ExpectedConditions.elementToBeClickable(by));
	}

	public void waitUntilElementIsDisabled(By by, long timeOutInSeconds) {
		getWebDriverWait(timeOutInSeconds).until(ExpectedConditions.not(ExpectedConditions.elementToBeClickable(by)));
	}

	public boolean objectExists(By by) {
		if (findElements(by).size() == 0) {
			return Boolean.valueOf(false);
		} else {
			return Boolean.valueOf(true);
		}
	}

	public boolean isAlertPresent(long timeOutInMilliSeconds) {
		try {
			new WebDriverWait(this.driver, timeOutInMilliSeconds).until(ExpectedConditions.alertIsPresent());
			return Boolean.valueOf(true);
		} catch (Exception e) {
		}
		return Boolean.valueOf(false);
	}

	public void updateLogStatus(Status status, String statusLog, String screenshotLog) {
		try {
			logger.log(status, statusLog,
					MediaEntityBuilder.createScreenCaptureFromPath(Util.getScreenShot(driver), screenshotLog).build());
		} catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException(
					"IO exception is thrown while creating a screenshot.Exception:" + e.getMessage());
		}
	}

	public void updateLogStatus(Status status, String statusLog) {
		if (status == Status.PASS || status == Status.FAIL) {
			try {
				logger.log(status, statusLog,
						MediaEntityBuilder.createScreenCaptureFromPath(Util.getScreenShot(driver)).build());
			} catch (IOException e) {
				e.printStackTrace();
				throw new FrameworkException(
						"IO exception is thrown while creating a screenshot.Exception:" + e.getMessage());
			}
		} else {
			logger.log(status, statusLog);
		}
	}

	public void click(By element) {
		findElement(element).click();
	}

	public void click(By element, String logMessage) {
		findElement(element).click();
		logger.log(Status.INFO, logMessage);
	}

	public void inputText(By element, String input) {
		findElement(element).sendKeys(input);
	}

	public void inputText(By element, String input, String logMessage) {
		findElement(element).sendKeys(input);
		logger.log(Status.INFO, logMessage);
	}

}
