package com.cust.framework;

public class ExcelCellFormatting {

	private String fontName;
	private int fontSize;
	private int backColorIndex;
	private int foreColorIndex;
	public boolean bold = false;
	public boolean italics = false;
	public boolean centered = false;

	public String getFontName() {
		return fontName;
	}

	public void setFontName(String fontName) {
		this.fontName = fontName;
	}

	public int getFontSize() {
		return fontSize;
	}

	public void setFontSize(int fontSize) {
		this.fontSize = fontSize;
	}

	public int getBackColorIndex() {
		return backColorIndex;
	}

	public void setBackColorIndex(int backColorIndex) {
		if (backColorIndex < 8 || backColorIndex > 64) {
			throw new FrameworkException("Valid indexes for the Excel custom palete are from 0x8 to 0x40(inclusive)!");
		}
		this.backColorIndex = backColorIndex;
	}

	public int getForeColorIndex() {
		return foreColorIndex;
	}

	public void setForeColorIndex(int foreColorIndex) {
		if (foreColorIndex < 8 || foreColorIndex > 64) {
			throw new FrameworkException("Valid indexes for the Excel custom palete are from 0x8 to 0x40(inclusive)!");
		}
		this.foreColorIndex = foreColorIndex;
	}

}
