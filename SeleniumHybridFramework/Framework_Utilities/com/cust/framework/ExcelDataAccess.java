package com.cust.framework;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.FormulaEvaluator;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class ExcelDataAccess {

	private final String filePath;
	private final String fileName;
	private String dataSheetName;

	public ExcelDataAccess(String filePath, String fileName) {
		this.filePath = filePath;
		this.fileName = fileName;
	}

	public void setDataSheetName(String dataSheetName) {
		this.dataSheetName = dataSheetName;
	}

	public String getDataSheetName() {
		return this.dataSheetName;
	}

	private void checkPreRequisites() {
		if (dataSheetName == null) {
			throw new FrameworkException("ExcelDataAccess.dataSheetName is not set!");
		}
	}

	private XSSFWorkbook openFileForReading() {
		String absolutePath = this.filePath + Util.getFileSeparator() + this.fileName + ".xlsx";
		FileInputStream fileInputStream;
		try {
			fileInputStream = new FileInputStream(absolutePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FrameworkException("The specified file \"" + absolutePath + "\" does not exist!");
		}
		XSSFWorkbook workbook;
		try {
			workbook = new XSSFWorkbook(fileInputStream);
		} catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException("Error while opening the specified Excel workbook \"" + absolutePath + "\" ");
		}
		return workbook;
	}

	private void writeIntoFile(XSSFWorkbook workbook) {
		String absolutePath = this.filePath + Util.getFileSeparator() + this.fileName + ".xlsx";
		FileOutputStream fileOutputStream;
		try {
			fileOutputStream = new FileOutputStream(absolutePath);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new FrameworkException("The specified file \"" + absolutePath + "\" does not exist!");
		}
		try {
			workbook.write(fileOutputStream);
			fileOutputStream.close();
		} catch (IOException e) {
			e.printStackTrace();
			throw new FrameworkException(
					"Error while writing into the specified Excel workbook \"" + absolutePath + "\" ");
		}
	}

	public XSSFSheet getWorkSheet(XSSFWorkbook workbook) {
		XSSFSheet workSheet = workbook.getSheet(this.dataSheetName);
		if (workSheet == null) {
			throw new FrameworkException("The specified sheet \"" + this.dataSheetName
					+ "\" does not exist within the workbook \"" + this.fileName + "\".xlsx");
		}
		return workSheet;
	}

	private String getCellValueAsString(XSSFCell cell, FormulaEvaluator formulaEvaluator) {
		if ((cell == null) || (cell.getCellType() == CellType.BLANK)) {
			return "";
		}
		if (formulaEvaluator.evaluate(cell).getCellType() == CellType.ERROR) {
			throw new FrameworkException(
					"Error in formula within this cell! Error code: " + cell.getErrorCellValue() + "");
		}
		DataFormatter dataFormatter = new DataFormatter();
		return dataFormatter.formatCellValue(formulaEvaluator.evaluateInCell(cell));
	}

	public int getRowNum(String key, int columnNum, int startRowNum) {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

		for (int currentRowNum = startRowNum; currentRowNum <= worksheet.getLastRowNum();) {
			XSSFRow row = worksheet.getRow(currentRowNum);
			XSSFCell cell = row.getCell(columnNum);
			String currentValue = getCellValueAsString(cell, formulaEvaluator);

			if (currentValue.equals(key)) {
				return currentRowNum;
			}
			currentRowNum++;
		}
		return -1;
	}

	public int getRowNum(String key, int columnNum) {
		return getRowNum(key, columnNum, 0);
	}

	public int getLastRowNum() {
		checkPreRequisites();
		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		return worksheet.getLastRowNum();
	}

	public int getRowCount(String key, int columnNum, int startRowNum) {
		this.checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

		int rowCount = 0;
		boolean keyFound = false;

		for (int currentRowNum = startRowNum; currentRowNum <= worksheet.getLastRowNum(); ++currentRowNum) {
			XSSFRow row = worksheet.getRow(currentRowNum);
			XSSFCell cell = row.getCell(columnNum);
			String currentValue = getCellValueAsString(cell, formulaEvaluator);

			if (currentValue.equals(key)) {
				++rowCount;
				keyFound = true;
			} else if (keyFound) {
				break;
			}
		}
		return rowCount;
	}

	public int getRowCount(String key, int columnNum) {
		return getRowCount(key, columnNum, 0);
	}

	public int getColumnNum(String key, int rowNum) {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

		XSSFRow row = worksheet.getRow(rowNum);

		for (int currentColumnNum = 0; currentColumnNum <= row.getLastCellNum();) {
			XSSFCell cell = row.getCell(currentColumnNum);
			String currentValue = getCellValueAsString(cell, formulaEvaluator);

			if (currentValue.equals(key)) {
				return currentColumnNum;
			} else {
				currentColumnNum++;
			}
		}
		return -1;
	}

	public String getValue(int rowNum, int columnNum) {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

		XSSFRow row = worksheet.getRow(rowNum);
		XSSFCell cell = row.getCell(columnNum);
		return getCellValueAsString(cell, formulaEvaluator);
	}

	public String getValue(int rowNum, String columnHeader) {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

		XSSFRow row = worksheet.getRow(0);
		int columnNum = -1;

		for (int currentColumnNum = 0; currentColumnNum <= row.getLastCellNum();) {
			XSSFCell cell = row.getCell(currentColumnNum);
			String currentValue = getCellValueAsString(cell, formulaEvaluator);

			if (currentValue.equals(columnHeader)) {
				columnNum = currentColumnNum;
				break;
			}
			currentColumnNum++;
		}

		if (columnNum == -1) {
			throw new FrameworkException("The specified column header \"" + columnHeader
					+ "\" is not found in the sheet \"" + this.dataSheetName + "\"!");
		}

		row = worksheet.getRow(rowNum);
		XSSFCell cell = row.getCell(columnNum);
		return getCellValueAsString(cell, formulaEvaluator);
	}

	public void setValue(int rowNum, int columnNum, String value) {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		XSSFRow row = worksheet.getRow(rowNum);
		XSSFCell cell = row.getCell(columnNum);

		cell.setCellType(CellType.STRING);
		cell.setCellValue(value);
		writeIntoFile(workbook);
	}

	public void setValue(int rowNum, String columnHeader, String value) {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);
		FormulaEvaluator formulaEvaluator = workbook.getCreationHelper().createFormulaEvaluator();

		XSSFRow row = worksheet.getRow(0);
		int columnNum = -1;

		for (int currentColumnNum = 0; currentColumnNum <= row.getLastCellNum();) {
			XSSFCell cell = row.getCell(currentColumnNum);
			String currentValue = getCellValueAsString(cell, formulaEvaluator);

			if (currentValue.equals(columnHeader)) {
				columnNum = currentColumnNum;
				break;
			}
			currentColumnNum++;
		}

		if (columnNum == -1) {
			throw new FrameworkException("The specified column header \"" + columnHeader
					+ "\" is not found in the sheet \"" + this.dataSheetName + "\"!");
		}

		row = worksheet.getRow(rowNum);
		XSSFCell cell = row.createCell(columnNum);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(value);
		writeIntoFile(workbook);
	}

	public void createWorkbook() {
		XSSFWorkbook workbook = new XSSFWorkbook();
		writeIntoFile(workbook);
	}

	public void addSheet(String sheetName) {
		XSSFWorkbook workbook = new XSSFWorkbook();
		XSSFSheet worksheet = workbook.createSheet(sheetName);
		worksheet.createRow(0);
		writeIntoFile(workbook);
		this.dataSheetName = sheetName;
	}

	public int addRow() {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);

		int newRowNum = worksheet.getLastRowNum() + 1;
		worksheet.createRow(newRowNum);
		writeIntoFile(workbook);
		return newRowNum;
	}

	public void addColumn(String columnHeader) {
		checkPreRequisites();

		XSSFWorkbook workbook = openFileForReading();
		XSSFSheet worksheet = getWorkSheet(workbook);

		XSSFRow row = worksheet.getRow(0);
		int lastCellNum = row.getLastCellNum();

		if (lastCellNum == -1) {
			lastCellNum = 0;
		}

		XSSFCell cell = row.createCell(lastCellNum);
		cell.setCellType(CellType.STRING);
		cell.setCellValue(columnHeader);
		writeIntoFile(workbook);
	}

}
