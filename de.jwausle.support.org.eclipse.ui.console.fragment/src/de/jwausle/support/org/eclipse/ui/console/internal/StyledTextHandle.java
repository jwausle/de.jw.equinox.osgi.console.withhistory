package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.graphics.Point;

/**
 * Wrapper to handle command line token an d replacements.
 * 
 * @author winter
 *
 */
public class StyledTextHandle {
	public static final String OSGI = "osgi>";

	private StyledText styledText;

	private String linestart;

	public StyledTextHandle(StyledText text) {
		this(text, OSGI);
	}

	public StyledTextHandle(StyledText text, String linestart) {
		this.styledText = text;
		this.linestart = linestart;
	}

	/**
	 * 
	 * @return
	 */
	public String getTokenBeforeCursor() {
		String precender = new StyledTextSelectionHandle(styledText)
				.getPrecenderTrimmed();
		return precender;
	}

	/**
	 * @return get command after 'linestart=osgi>' or "".
	 */
	public String getCommandLine() {
		String commandLine = getLine();

		commandLine = commandLine//
				.replace(linestart, "")//
				.trim();
		return commandLine;
	}

	/**
	 * Replace the 'match' with the 'replacement' dependend where the
	 * cursor/selection is.
	 * 
	 * @param match
	 *            not null match token.
	 * @param replacement
	 *            not null token.
	 */
	public void replace(String match, String replacement) {
		if (replacement == null)
			return;

		StyledTextSelectionHandle selection2 = new StyledTextSelectionHandle(
				styledText);
		int start2 = selection2.getStart();
		int length2 = selection2.getLenght();

		if ("".isEmpty()) {
			styledText.getContent().replaceTextRange(start2, length2,
					replacement);
			return;
		}

		Point selection = styledText.getSelection();
		if (selection.x == selection.y) {
			styledText.getContent().replaceTextRange(selection.x,
					selection.y - selection.x, replacement);
			return;
		}

		String line = getLine();
		Point linePoint = new Point(styledText.getOffsetAtLine(styledText
				.getLineCount() - 1), styledText.getCharCount());

		int startLastMatch = line.lastIndexOf(match);
		int start = linePoint.x + startLastMatch;
		int length = match.length();

		char c = 0;
		try {
			c = line.toCharArray()[startLastMatch + length];
		} catch (Exception e) {
			// ignore
		}

		if (KeyHandles.isTab(c) || KeyHandles.isNonBreakSpace(c))
			length++;

		StyledTextContent content = this.styledText.getContent();
		content.replaceTextRange(start, length, replacement);
	}

	private String getLine() {
		int lineCount = styledText.getLineCount();
		String line = styledText.getLine(lineCount - 1);
		return line;
	}

	/**
	 * Replace the hole present command line <br />
	 * - 'osgi> [any text]' <br />
	 * to <br />
	 * - 'osgi> [line]'
	 * 
	 * @param line
	 */
	public void replaceLine(String line) {
		int end = this.styledText.getContent().getCharCount();
		int start = this.styledText
				.getOffsetAtLine(styledText.getLineCount() - 1);

		// delete last command in console.
		try {
			this.styledText.getContent().replaceTextRange(start, end - start,
					OSGI + " ");
		} catch (Exception e) {
			// ignore exception
		}
		this.styledText.setText(line);
	}

	/**
	 * Try find first line who starts with 'linestart' from bottom to top.
	 * 
	 * @return command without 'linestart=osgi>' or ""
	 */
	public String getLastCommandLine() {
		String commandLine = getCommandLine();
		if (!commandLine.isEmpty())
			return commandLine;

		int lineIndex = styledText.getLineCount();
		commandLine = null;
		while (true) {
			String line = styledText.getLine(lineIndex - 1);
			if (line.startsWith(linestart)) {
				commandLine = line;
				break;
			}
			lineIndex--;

		}
		if (commandLine == null)
			return "";

		return commandLine.replace(this.linestart, "").trim();
	}

	public String getCommandName() {
		String line = getCommandLine();
		if (line == null)
			return "";
		if (line.isEmpty())
			return "";
		int indexOf = line.indexOf(' ');
		if (indexOf == -1)
			return line;
		return line.substring(0, indexOf);
	}
}
