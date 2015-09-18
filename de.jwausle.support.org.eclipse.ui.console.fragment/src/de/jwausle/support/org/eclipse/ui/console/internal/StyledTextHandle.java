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

	private Logger log = Logger.getLogger(StyledTextHandle.class);

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
		int length2 = selection2.getLength();

		StyledTextCommandLine cmdline = new StyledTextCommandLine(OSGI,
				styledText);
		int start = cmdline.getStart();
		int length = cmdline.getLength();

		log.debug("selection({0},{1}) vs. cmdline({2},{3}) ", start2, length2,
				start, length);
		start2 = start;
		length2 = length;

		if ("".isEmpty()) {
			log.info("Replace string: `{0}` -> `{1}`", selection2, replacement);
			styledText.getContent().replaceTextRange(start2, length2,
					replacement);
			styledText.setSelection(start2 + replacement.length());
			return;
		}

//		Point selection = styledText.getSelection();
//		if (selection.x == selection.y) {
//			log.info("Replace selection: " + selection);
//			styledText.getContent().replaceTextRange(selection.x,
//					selection.y - selection.x, replacement);
//			styledText.setSelection(selection.x + replacement.length());
//			return;
//		}
//
//		String line = getLine();
//		Point linePoint = new Point(styledText.getOffsetAtLine(styledText
//				.getLineCount() - 1), styledText.getCharCount());
//		log.info("Replace line {0} on point {1}", line, linePoint);
//		int startLastMatch = line.lastIndexOf(match);
//		int start = linePoint.x + startLastMatch;
//		int length = match.length();
//
//		char c = 0;
//		try {
//			c = line.toCharArray()[startLastMatch + length];
//		} catch (Exception e) {
//			// ignore
//		}
//
//		if (KeyHandles.isTab(c) || KeyHandles.isNonBreakSpace(c))
//			length++;
//
//		StyledTextContent content = this.styledText.getContent();
//		content.replaceTextRange(start, length, replacement);
//
//		styledText.setSelection(start + replacement.length());
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
		StyledTextCommandLine cmdline = new StyledTextCommandLine(
				this.linestart, styledText);

		int start = cmdline.getStart();
		int length = cmdline.getLength();
		String actual = cmdline.toString();

		log.info("replacing line: Start={0} -> end={1}: `{2}` -> `{3}`", start,
				length, actual, line);
		String spaceLine = " " + line.trim();
		this.styledText.getContent().replaceTextRange(start, length, spaceLine);
		log.info("replaced.");

		this.styledText.setSelection(this.styledText.getCharCount());
	}

	/**
	 * Try find first line who starts with 'linestart' from bottom to top.
	 * 
	 * @return command without 'linestart=osgi>' or ""
	 */
	public String getLastCommandLine() {
		String commandLine = getCommandLine();
		if (!commandLine.isEmpty()) {
			log.debug("Command line is empty. Return default ''.");
			return "";
		}

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
		if (commandLine == null) {
			log.debug("Command line not exist. Return default ''.");
			return "";
		}

		String commandline = commandLine.replace(this.linestart, "").trim();
		log.debug("Command line found: Return '" + commandLine + "'.");
		return commandline;
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
