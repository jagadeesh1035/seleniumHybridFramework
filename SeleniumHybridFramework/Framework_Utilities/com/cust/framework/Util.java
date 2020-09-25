package com.cust.framework;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

public class Util {

	public static String getFileSeparator() {
		return System.getProperty("file.separator");
	}

	public static Date getCurrentTime() {
		return Calendar.getInstance().getTime();
	}

	public static String getFormattedTime(Date time, String dateFormatString) {
		return new SimpleDateFormat(dateFormatString).format(time);
	}

	public static String getFormattedCurrentTime(String dateFormatString) {
		return getFormattedTime(getCurrentTime(), dateFormatString);
	}

	public static String getTimeDifference(Date startTime, Date endTime) {
		long timeDifference = endTime.getTime() - startTime.getTime();
		timeDifference /= 1000L;
		return Long.toString(timeDifference / 60L) + " Minute(s) " + Long.toString(timeDifference % 60L) + " Second(s)";
	}
	
	public static String getScreenShot(WebDriver driver) throws IOException {
		String dateName = new SimpleDateFormat("yyyyMMddhhmmss").format(new Date());
		File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		String destination = System.getProperty("user.dir") + "\\Reports\\Screenshots\\Screenshot"+ dateName
				+ ".png";
		File finalDestination = new File(destination);
		FileUtils.copyFile(source, finalDestination);
		return destination;
	}

}
