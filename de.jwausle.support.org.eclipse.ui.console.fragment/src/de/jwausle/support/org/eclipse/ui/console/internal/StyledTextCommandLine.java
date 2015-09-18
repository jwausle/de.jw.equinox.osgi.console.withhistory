package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;

public class StyledTextCommandLine {
	private final Logger log = Logger.getLogger(StyledTextCommandLine.class);
	
	private final StyledText styledText;
	private final String prefix;

	public StyledTextCommandLine(String prefix, StyledText styledText) {
		this.prefix = prefix;
		this.styledText = styledText;
	}

	public int getStart() {
		boolean startsWithPrefix = startWithPrefix();

		if (startsWithPrefix) {
			int offset = getLastLineOffset() + prefix.length();
			return offset + 1;
		}

		return getLastLineOffset();
	}

	public int getLength() {
		int start = getStart();
		int charCount = styledText.getCharCount();

		if (charCount == start)
			return 0;
		return charCount - start - 1;
	}

	private boolean startWithPrefix() {
		String line = styledText.getLine(styledText.getLineCount()-1);
		if("".isEmpty())
			return line.startsWith(prefix);
			
		int start = getLastLineOffset();
		String textRange = styledText.getTextRange(start, prefix.length());

		boolean startsWithPrefix = prefix.equals(textRange);

		return startsWithPrefix;
	}

	private int getLastLineOffset() {
		int lineCount = styledText.getLineCount() - 1;

		int offsetAtLine = styledText.getOffsetAtLine(lineCount);
		log.debug("Lastline offset: {0}:`{1}`.", offsetAtLine, styledText.getLine(lineCount));
		return offsetAtLine;
	}

	@Override
	public String toString() {
		int start = getStart();
		int length = getLength();
		String textRange = styledText.getTextRange(start, length);
		return textRange;
	}

}
