package com.cust.framework;

public class ReportThemeFactory {

	public static enum Theme {
		CLASSIC, MYSTIC, AUTUMN, OLIVE, REBEL, RETRO, SERENE, CUSTOM
	}

	public static ReportTheme getReportsTheme(Theme theme) {
		ReportTheme reportTheme = new ReportTheme();
		switch (theme) {
		case AUTUMN:
			reportTheme.setHeadingBackColor("#62536D");
			reportTheme.setHeadingForeColor("#DAD0E1");
			reportTheme.setSectionBackColor("#AD96BD");
			reportTheme.setSectionForeColor("#413748");
			reportTheme.setContentBackColor("#F3F0F6");
			reportTheme.setContentForeColor("#000000");
			break;

		case CLASSIC:
			reportTheme.setHeadingBackColor("#334C00");
			reportTheme.setHeadingForeColor("#FFD98C");
			reportTheme.setSectionBackColor("#849366");
			reportTheme.setSectionForeColor("#1E2D00");
			reportTheme.setContentBackColor("#FFE7B7");
			reportTheme.setContentForeColor("#000000");
			break;

		case CUSTOM:
			reportTheme.setHeadingBackColor("#006B42");
			reportTheme.setHeadingForeColor("#C2DF62");
			reportTheme.setSectionBackColor("#C2DF62");
			reportTheme.setSectionForeColor("#333300");
			reportTheme.setContentBackColor("#FAFAC5");
			reportTheme.setContentForeColor("#000000");
			break;

		case MYSTIC:
			reportTheme.setHeadingBackColor("#4D7C7B");
			reportTheme.setHeadingForeColor("#FFFF95");
			reportTheme.setSectionBackColor("#89B6B5");
			reportTheme.setSectionForeColor("#333300");
			reportTheme.setContentBackColor("#FAFAC5");
			reportTheme.setContentForeColor("#000000");
			break;

		case OLIVE:
			reportTheme.setHeadingBackColor("#333300");
			reportTheme.setHeadingForeColor("#DED05E");
			reportTheme.setSectionBackColor("#70704C");
			reportTheme.setSectionForeColor("#001F00");
			reportTheme.setContentBackColor("#E8DEBA");
			reportTheme.setContentForeColor("#003326");
			break;

		case REBEL:
			reportTheme.setHeadingBackColor("#333300");
			reportTheme.setHeadingForeColor("#DED05E");
			reportTheme.setSectionBackColor("#70704C");
			reportTheme.setSectionForeColor("#001F00");
			reportTheme.setContentBackColor("#E8DEBA");
			reportTheme.setContentForeColor("#003326");
			break;

		case RETRO:
			reportTheme.setHeadingBackColor("#5E5661");
			reportTheme.setHeadingForeColor("#FFE4B5");
			reportTheme.setSectionBackColor("#9E99A0");
			reportTheme.setSectionForeColor("#252226");
			reportTheme.setContentBackColor("#FFF5EE");
			reportTheme.setContentForeColor("#413F49");
			break;
		case SERENE:
			reportTheme.setHeadingBackColor("#005B96");
			reportTheme.setHeadingForeColor("#B1DAFB");
			reportTheme.setSectionBackColor("#669CC0");
			reportTheme.setSectionForeColor("#1E2D35");
			reportTheme.setContentBackColor("#D8ECFD");
			reportTheme.setContentForeColor("#000000");
			break;
		}
		return reportTheme;
	}

}
