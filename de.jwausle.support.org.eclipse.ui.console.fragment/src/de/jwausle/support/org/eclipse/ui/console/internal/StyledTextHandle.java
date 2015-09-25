package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Point;

/**
 * Wrapper to handle command line token an d replacements.
 * 
 * @author winter
 *
 */
public class StyledTextHandle {
	public static final String OSGI = "osgi> ";

	private final Logger log = Logger.getLogger(StyledTextHandle.class);

	private final StyledText styledText;

	private final IDocument document;

	private String linestart;

	public StyledTextHandle(StyledText text, IDocument document) {
		this.styledText = text;
		this.document = document;
		this.linestart = OSGI;
	}

	public StyledTextHandle(StyledText styledText2) {
		this(styledText2, null);
	}

	/**
	 * 
	 * @return
	 */
	public String getTokenBeforeCursor() {
		StyledTextSelectionHandle selection = new StyledTextSelectionHandle(
				styledText);
		String precender = selection.getPrecenderTrimmed();
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
		if (document == null)
			return;
		if (replacement == null)
			return;

		int lineCount = styledText.getLineCount();
		int offsetAtLine = styledText.getOffsetAtLine(lineCount - 1);

		Point selection = styledText.getSelection();
		if (offsetAtLine + 6 <= selection.x && selection.x != selection.y) {
			try {
				document.replace(selection.x, selection.y - selection.x,
						replacement);
				styledText.setSelection(selection.x + replacement.length());
			} catch (BadLocationException e) {
				log.error(e.getMessage());
			}
		} else if (offsetAtLine + 6 <= selection.x) {
			try {
				document.replace(selection.x - match.length(),
						0 + match.length(), replacement);
				styledText.setSelection(selection.x + replacement.length());
			} catch (BadLocationException e) {
				log.error(e.getMessage());
			}
		} else {
			try {
				document.replace(styledText.getCharCount(), 0, replacement);
				styledText.setSelection(styledText.getCharCount());
			} catch (BadLocationException e) {
				log.error(e.getMessage());
			}
		}
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
		if (document == null)
			return;

		int lineIndex = styledText.getLineCount() - 1;
		String line2 = styledText.getLine(lineIndex);
		Point selection = styledText.getSelection();
		log.debug("Selection: {0} ", selection);
		try {
			int offsetAtLine = styledText.getOffsetAtLine(lineIndex);
			if (offsetAtLine == 0) {
				document.replace(offsetAtLine, line2.length(), "osgi! " + line);
			} else {
				document.replace(offsetAtLine + 6, line2.length() - 6, line);
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
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
