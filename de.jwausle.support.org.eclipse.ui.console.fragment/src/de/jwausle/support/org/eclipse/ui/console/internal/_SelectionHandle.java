package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

class _SelectionHandle {

	private StyledText styledText2;

	public _SelectionHandle(StyledText st) {
		styledText2 = st;
	}

	public boolean noSelection() {
		Point selection = styledText2.getSelection();
		boolean same = selection.x == selection.y;
		return same && charBefore(selection.x) == ' ';
	}

	public boolean noSelectionWithPrecender() {
		Point selection = styledText2.getSelection();
		boolean same = selection.x == selection.y;
		return same && charBefore(selection.x) != ' ';
	}

	public boolean hasSelection() {
		Point selection = styledText2.getSelection();
		boolean same = selection.x == selection.y;
		return !same;
	}

	public int getStart() {
		int x = styledText2.getSelection().x;
		if (noSelectionWithPrecender()) {
			int precenderCount = getPrecenderUntrimmedCount();
			int start = x - precenderCount;
			return start;
		}
		return x;
	}

	private int getPrecenderUntrimmedCount() {
		return getPrecender(false).length();
	}

	public int getLenght() {
		if (hasSelection())
			return styledText2.getSelectionCount();

		int length = getPrecenderTrimmedCount();
		char c = 0;
		try {
			c = styledText2.getContent()
					.getTextRange(styledText2.getSelection().y - 1, 1)
					.toCharArray()[0];
		} catch (Exception e) {
			// ignore
		}

		if (_StyledTextHandle.isTabOrNonSpaceBreak(c))
			length++;

		return length;
	}

	private int getPrecenderTrimmedCount() {
		String precender = getPrecenderTrimmed();
		return precender.length();
	}
	
	public String getPrecenderTrimmed() {
		String precender = getPrecender(true);
		return precender;
	}

	private String getPrecender(boolean ignore) {
		Point cursorLocation = this.styledText2.getSelection();

		int offsetAtLine = this.styledText2.getOffsetAtLine(styledText2
				.getLineCount() - 1);
		if (cursorLocation.x < offsetAtLine + _StyledTextHandle.OSGI.length())
			return "";
		String line = getLine();
		String substring = line.substring(0, cursorLocation.x - offsetAtLine);
		substring = substring.replace(_StyledTextHandle.OSGI, "");

		StringBuffer reverse = new StringBuffer(substring).reverse();
		StringBuffer buffer = new StringBuffer();
		char[] charArray = reverse.toString().toCharArray();
		for (char c : charArray) {
			if (ignore && _StyledTextHandle.isTabOrNonSpaceBreak(c))
				continue;
			if (_StyledTextHandle.isSpace(c)) {
				return buffer.reverse().toString();
			}
			buffer.append(c);
		}
		return buffer.toString();
	}

	private char charBefore(int x) {
		char charAt = styledText2.getContent().getTextRange(x - 1, 1).charAt(0);
		return charAt;
	}

	private String getLine() {
		int lineCount = styledText2.getLineCount();
		String line = styledText2.getLine(lineCount - 1);
		return line;
	}

}