package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Arrays;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;
/**
 * IPatternMatchListener to extract a 'command' from 'matchFound()' via 'pattern' call. <br />
 * <br />
 * 'pattern'	:	pattern for line<br />
 * 'command'	:=	pattern.matches(command).group(1)<br />
 * 'matchFound'	:	interface impl<br />
 * @author winter
 *
 */
public final class HistoryHandlePatternMatchListener implements
		IPatternMatchListener {
	public static final String OSGI_COMMAND_LINE_PATTERN = "osgi>.*(\n[^\n]|\r[^\r])";

	private final Logger log = Logger
			.getLogger(HistoryHandlePatternMatchListener.class.getName());

	private final HistoryHandle ioConsoleWithHistoryHandle;
	private final IOConsole ioConsole;
	private final Pattern pattern;

	public HistoryHandlePatternMatchListener(IOConsole console,
			HistoryHandle ioConsoleWithHistoryHandle) {
		this(console, ioConsoleWithHistoryHandle, OSGI_COMMAND_LINE_PATTERN);
	}

	public HistoryHandlePatternMatchListener(IOConsole console,
			HistoryHandle handle, String osgiCommandLinePattern) {
		this.ioConsoleWithHistoryHandle = handle;
		this.ioConsole = console;
		this.pattern = Pattern.compile(osgiCommandLinePattern);
	}

	public void matchFound(PatternMatchEvent event) {
		IDocument document = ioConsole.getDocument();

		String command = safeGetNextPatternMatchFromDocument(event, document);

		if ((command == null) || command.isEmpty()) {
			return;
		}
		if (!ioConsoleWithHistoryHandle.data.equalLastCommand(command)) {
			ioConsoleWithHistoryHandle.data.add(command);
		}
		ioConsoleWithHistoryHandle.data.clearSessionStack();
		log.fine("enter:  " + ioConsoleWithHistoryHandle.data);
	}

	private String safeGetNextPatternMatchFromDocument(PatternMatchEvent event,
			IDocument document) {
		int lineOffset = event.getOffset();
		int lineLength = event.getLength();
		String command = "";
		try {
			command = document.get(lineOffset, lineLength);
		} catch (BadLocationException e) {
			log.severe(e.getMessage() + ", stacktrace:\n"
					+ Arrays.toString(e.getStackTrace()));
		}
		
		//pattern-sample: "osgi>(.*)(\n[^\n]|\r[^\r])"
		Matcher matcher = pattern.matcher(command);
		if(matcher.matches()){
			String group1 = matcher.group(1);
			command = group1;
		}
		return command;
	}

	public void disconnect() {

	}

	public void connect(TextConsole console) {

	}

	public String getPattern() {
		return this.pattern.pattern();
	}

	public String getLineQualifier() {
		return this.pattern.pattern();
	}

	public int getCompilerFlags() {
		return 0;
	}
}
