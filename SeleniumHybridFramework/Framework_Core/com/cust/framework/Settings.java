package com.cust.framework;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public class Settings {

	private static Properties properties = loadPropertiesFile();

	public static Properties getPropertiesInstance() {
		return properties;
	}

	private static Properties loadPropertiesFile() {
		FrameworkParameters frameworkParameters = FrameworkParameters.getInstance();
		if (frameworkParameters.getRelativePath() == null) {
			throw new FrameworkException("FramworkParamenters.relativePath is not set!");
		}
		Properties properties = new Properties();
		try {
			properties.load(new FileInputStream(
					frameworkParameters.getRelativePath() + Util.getFileSeparator() + "Global Settings.properties"));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FrameworkException("FileNotFoundException while loading 'Global Settings.properties' file");
		} catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException("IOException while loading 'Global Settings.properties' file");
		}
		return properties;
	}

	public Object clone() throws CloneNotSupportedException {
		throw new CloneNotSupportedException();
	}

}
