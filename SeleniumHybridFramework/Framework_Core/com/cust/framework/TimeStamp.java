package com.cust.framework;

import java.io.File;
import java.util.Properties;

public class TimeStamp {

	private static volatile String mReportPathWithTimeStamp;

	public static String getInstance() {

		if (mReportPathWithTimeStamp == null) {
			synchronized (TimeStamp.class) {
				if (mReportPathWithTimeStamp == null) {
					FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();
					if (frameworkParameters.getRelativePath() == null) {
						throw new FrameworkException("FrameworkParameters.relativePath is not set!");
					}
					if (frameworkParameters.getRunConfiguration() == null) {
						throw new FrameworkException("FrameworkParameters.runConfiguration is not set!");
					}

					Properties properties = Settings.getPropertiesInstance();
					String timeStamp = "Run_" + Util.getFormattedCurrentTime(properties.getProperty("DateFormatString"))
							.replace(" ", "_").replace(":", "_");

					mReportPathWithTimeStamp = frameworkParameters.getRelativePath() + Util.getFileSeparator()
							+ "Results" + Util.getFileSeparator() + frameworkParameters.getRunConfiguration()
							+ Util.getFileSeparator() + timeStamp;

					new File(mReportPathWithTimeStamp).mkdirs();
				}
			}
		}
		return mReportPathWithTimeStamp;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}
}
