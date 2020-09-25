package supportLibraries;

import java.util.Properties;
import org.openqa.selenium.WebDriver;
import com.cust.framework.AccelerateProcessDataTable;
import com.cust.framework.FrameworkParameters;
import com.cust.framework.SeleniumReport;
import com.cust.framework.Settings;

public abstract class ReusableLibrary {
	protected AccelerateProcessDataTable dataTable;
	protected SeleniumReport report;
	protected WebDriver driver;
	protected ScriptHelper scriptHelper;
	protected Properties properties;
	protected FrameworkParameters frameworkParameters;

	public ReusableLibrary(ScriptHelper scriptHelper) {
		this.scriptHelper = scriptHelper;
		this.driver = scriptHelper.getDriver();
		this.dataTable = scriptHelper.getDataTable();
		this.report = scriptHelper.getReport();
		this.properties = Settings.getPropertiesInstance();
		frameworkParameters = FrameworkParameters.getInstance();

	}
}
