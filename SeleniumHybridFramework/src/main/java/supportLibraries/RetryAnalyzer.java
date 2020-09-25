package supportLibraries;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

	FileInputStream fis;
	Properties properties;

	int counter = 1;
	int retryLimit = setRetryLimit();

	public RetryAnalyzer() {
		super();
	}

	public boolean retry(ITestResult result) {
		if (counter < retryLimit) {
			counter++;
			return true;
		}
		return false;
	}

	public int setRetryLimit() {
		int retryLimit = 1;
		try {
			fis = new FileInputStream(System.getProperty("user.dir") + "\\Global Settings.properties");
			properties = new Properties();
			properties.load(fis);
			retryLimit = Integer.parseInt(properties.getProperty("RetryLimit"));
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fis.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return retryLimit;
	}

}
