package com.cust.framework;

public class AccelerateProcessDataTable {
	private final String dataTablePath;
	private final String dataTableName;
	private String dataReferenceIdentifier = "#";
	private String currentTestCase;
	private int currentIteration = 0;

	public AccelerateProcessDataTable(String dataTablePath, String dataTableName) {
		super();
		this.dataTablePath = dataTablePath;
		this.dataTableName = dataTableName;
	}

	public void setDataReferenceIdentifier(String dataReferenceIdentifier) {
		if (dataReferenceIdentifier.length() != 1) {
			throw new FrameworkException("The data reference identifier must be a single character!");
		}
		this.dataReferenceIdentifier = dataReferenceIdentifier;
	}

	public void setCurrentRow(String currentTestCase, int currentIteration) {
		this.currentTestCase = currentTestCase;
		this.currentIteration = currentIteration;
	}

	private void checkPreRequisites() {
		if (this.currentTestCase == null) {
			throw new FrameworkException("AccelerateProcessDataTable.currentTestCase is not set!");
		}
		if (this.currentIteration == 0) {
			throw new FrameworkException("AccelerateProcessDataTable.currentIteration is not set!");
		}
	}

	public String getData(String dataSheetName, String fieldName) {
		checkPreRequisites();
		ExcelDataAccess testDataAccess = new ExcelDataAccess(this.dataTablePath, this.dataTableName);
		testDataAccess.setDataSheetName(dataSheetName);

		int rowNum = testDataAccess.getRowNum(this.currentTestCase, 0, 1);
		if (rowNum == -1) {
			throw new FrameworkException("The test case '" + this.currentTestCase
					+ "' is not found in the test datasheet '" + dataSheetName + "'!");
		}

		rowNum = testDataAccess.getRowNum(Integer.toString(this.currentIteration), 1, rowNum);
		if (rowNum == -1) {
			throw new FrameworkException("The iteration number '" + this.currentIteration + " of 'the test case '"
					+ this.currentTestCase + "' is not found in the test datasheet '" + dataSheetName + "'!");
		}
		String dataValue = testDataAccess.getValue(rowNum, fieldName);
		if (dataValue.startsWith(this.dataReferenceIdentifier)) {
			dataValue = getCommonData(fieldName, dataValue);
		}
		return dataValue;
	}

	public String getCommonData(String fieldName, String dataValue) {
		ExcelDataAccess commonDataAccess = new ExcelDataAccess(this.dataTablePath, "Common Testdata");
		commonDataAccess.setDataSheetName("Common_Testdata");
		String dataReferenceId = dataValue.split(this.dataReferenceIdentifier)[1];
		int rowNum = commonDataAccess.getRowNum(dataReferenceId, 0, 1);
		if (rowNum == -1) {
			throw new FrameworkException("The common testdata row identified by '" + dataReferenceId
					+ "' is not found in the common test datasheet!");
		}
		return commonDataAccess.getValue(rowNum, fieldName);
	}

	public void putData(String dataSheetName, String fieldName, String dataValue) {
		ExcelDataAccess testDataAccess = new ExcelDataAccess(this.dataTablePath, this.dataTableName);
		testDataAccess.setDataSheetName(dataSheetName);

		int rowNum = testDataAccess.getRowNum(this.currentTestCase, 0, 1);
		if (rowNum == -1) {
			throw new FrameworkException("The test case '" + this.currentTestCase
					+ "' is not found in the test datasheet '" + dataSheetName + "'!");
		}

		rowNum = testDataAccess.getRowNum(Integer.toString(this.currentIteration), 1, rowNum);
		if (rowNum == -1) {
			throw new FrameworkException("The iteration number '" + this.currentIteration + " of 'the test case '"
					+ this.currentTestCase + "' is not found in the test datasheet '" + dataSheetName + "'!");
		}
		synchronized (AccelerateProcessDataTable.class) {
			testDataAccess.setValue(rowNum, fieldName, dataValue);
		}
	}

	public String getExpectedResult(String fieldName) {
		checkPreRequisites();
		ExcelDataAccess expectedResultsAccess = new ExcelDataAccess(this.dataTablePath, this.dataTableName);
		expectedResultsAccess.setDataSheetName("Parameterized_checkpoints");

		int rowNum = expectedResultsAccess.getRowNum(this.currentTestCase, 0, 1);
		if (rowNum == -1) {
			throw new FrameworkException("The test case '" + this.currentTestCase
					+ "' is not found in the parameterized checkpoints sheet!");
		}
		rowNum = expectedResultsAccess.getRowNum(Integer.toString(this.currentIteration), 1, rowNum);
		if (rowNum == -1) {
			throw new FrameworkException("The iteration number '" + this.currentIteration + " of 'the test case '"
					+ this.currentTestCase + "' is not found in the parameterized checkpoint sheet!");
		}

		return expectedResultsAccess.getValue(rowNum, fieldName);
	}
}
