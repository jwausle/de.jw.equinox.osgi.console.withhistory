package de.jwausle.support.org.eclipse.ui.console.internal;

public class KeyHandles {

	public static boolean isTabOrNonSpaceBreak(char c) {
		return KeyHandles.ignore(c);
	}

	public static boolean ignore(char c) {
		boolean nonBreakSpace = KeyHandles.isNonBreakSpace(c);
		boolean tab = KeyHandles.isTab(c);
		boolean tabOrNonBreakSpace = nonBreakSpace || tab;
		return tabOrNonBreakSpace;
	}

	public static boolean isNonBreakSpace(char c) {
		boolean nonBreakSpace = c == '\u00A0';
		return nonBreakSpace;
	}

	public static boolean isSpace(char c) {
		return c == ' ';
	}

	public static boolean isTab(char c) {
		return c == '\t'/* non break space */;
	}

}
