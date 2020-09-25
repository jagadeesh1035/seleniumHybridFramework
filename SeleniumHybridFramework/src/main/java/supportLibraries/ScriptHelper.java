package supportLibraries;

import org.openqa.selenium.WebDriver;

import com.cust.framework.AccelerateProcessDataTable;
import com.cust.framework.SeleniumReport;

public class ScriptHelper {
	private final AccelerateProcessDataTable dataTable;
	private final SeleniumReport report;
	private final WebDriver driver;

	public ScriptHelper(AccelerateProcessDataTable dataTable, SeleniumReport report, WebDriver driver) {
		super();
		this.dataTable = dataTable;
		this.report = report;
		this.driver = driver;
	}

	public AccelerateProcessDataTable getDataTable() {
		return dataTable;
	}

	public SeleniumReport getReport() {
		return report;
	}

	public WebDriver getDriver() {
		return driver;
	}

}
