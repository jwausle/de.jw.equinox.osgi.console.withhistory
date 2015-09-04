package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.graphics.Point;

public class _StyledTextHandle {
	public static boolean isTabOrNonSpaceBreak(char c) {
		return ignore(c);
	}

	public static boolean ignore(char c) {
		boolean nonBreakSpace = isNonBreakSpace(c);
		boolean tab = isTab(c);
		boolean tabOrNonBreakSpace = nonBreakSpace || tab;
		return tabOrNonBreakSpace;
	}

	public static final String OSGI = "osgi>";
	private StyledText styledText;
	private String linestart;

	public _StyledTextHandle(StyledText text) {
		this(text, OSGI);
	}

	public _StyledTextHandle(StyledText text, String linestart) {
		this.styledText = text;
		this.linestart = linestart;
	}

	public String getTokenBeforeCursor() {
		String precender = new _SelectionHandle(styledText)
				.getPrecenderTrimmed();
		return precender;
	}

	public static boolean isSpace(char c) {
		return c == ' ';
	}

	public static boolean isTab(char c) {
		return c == '\t'/* non break space */;
	}

	public static boolean isNonBreakSpace(char c) {
		boolean nonBreakSpace = c == '\u00A0';
		return nonBreakSpace;
	}

	public String getCommandLine() {
		String commandLine = getLine();

		commandLine = commandLine//
				.replace(linestart, "")//
				.trim();
		return commandLine;
	}

	public void replace(String match, String replacement) {
		_SelectionHandle selection2 = new _SelectionHandle(styledText);
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

		if (isTab(c) || isNonBreakSpace(c))
			length++;

		// this.styledText.setSelection(new Point(start, start + length -1));
		// this.styledText.setWordWrap(true);
		// this.styledText.setBlockSelection(false);
		// this.styledText.insert(replacement);
		StyledTextContent content = this.styledText.getContent();
		content.replaceTextRange(start, length, replacement);
		System.err.println("Replaced " + line + " with " + getLine());
		// this.styledText.replaceTextRange(start, length, replacement);
	}

	private String getLine() {
		int lineCount = styledText.getLineCount();
		String line = styledText.getLine(lineCount - 1);
		return line;
	}

	/**
	 * Append the command-string to styledText area (setText()). <br />
	 * <br />
	 * If command startsWith prefix <br />
	 * Then remove prefix before append.
	 * 
	 * @param command
	 * @param prefix
	 */
	public void appendText(String command, String prefix) {
		String _prefix = prefix;
		if (prefix == null)
			_prefix = "";

		Point point = calculatePoint(prefix);

		String _command = command;
		if (_command.startsWith(_prefix)) {
			_command = _command.replaceFirst(prefix, "");
		}
		this.styledText.setText(_command);
	}

	private Point calculatePoint(String prefix) {
		int lineIndex = styledText.getLineCount() - 1;

		String line = styledText.getLine(lineIndex);

		int lineOffest = styledText.getOffsetAtLine(lineIndex);

		int findPrefix = line.lastIndexOf(prefix);
		if (findPrefix == -1)
			return null;

		Point point = new Point(styledText.getCharCount() - 1,
				styledText.getCharCount() - 1);
		return point;
	}

	public void appendText(String command) {
		styledText.setText(command);
	}

	public void replaceLine(String line) {
		if ("false".isEmpty()) {
			Point linePoint = new Point(styledText.getOffsetAtLine(styledText
					.getLineCount() - 1), styledText.getCharCount());
			try {
				styledText.getContent().replaceTextRange(linePoint.x,
						linePoint.y - linePoint.x - 1, OSGI + " " + line);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		int end = this.styledText.getContent().getCharCount();
		int start = this.styledText
				.getOffsetAtLine(styledText.getLineCount() - 1);

		// delete last command in console.
		try {
			this.styledText.getContent().replaceTextRange(start, end - start, OSGI + " ");
		} catch (Exception e) {
			// ignore exception
		}
		this.styledText.setText(line);
	}

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
}
