package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.regex.Pattern;

import org.eclipse.ui.console.IPatternMatchListener;
import org.eclipse.ui.console.PatternMatchEvent;
import org.eclipse.ui.console.TextConsole;

final class LineWrappPatternMatchListener implements
		IPatternMatchListener {

	private final IOConsolePageParticipantWithLineWrapp ioConsolePageParticipantWithLineWrapp;

	private final Pattern compile;
	
	private TextConsole textConsole = null;

	public LineWrappPatternMatchListener(IOConsolePageParticipantWithLineWrapp ioConsolePageParticipantWithLineWrapp, String disablePattern) {
		this.ioConsolePageParticipantWithLineWrapp = ioConsolePageParticipantWithLineWrapp;
		this.compile = Pattern.compile(disablePattern);
	}

	public void matchFound(PatternMatchEvent event) {
		
		String notNullGroupOne = HistoryHandlePatternMatchListener.safeGetNextPatternMatchFromDocument(event, textConsole.getDocument(), compile);
		
		if("disableLineWrapping".equals(notNullGroupOne)){
			this.ioConsolePageParticipantWithLineWrapp.disableLineWrapping();
			return;
		}
		if("enableLineWrapping".equals(notNullGroupOne)){
			this.ioConsolePageParticipantWithLineWrapp.enableLineWrapping();
			return;
		}
	}

	public void disconnect() {
		this.textConsole = null;
	}

	public void connect(TextConsole console) {
		this.textConsole = console;
	}

	public String getPattern() {
		return compile.pattern();
	}

	public String getLineQualifier() {
		return compile.pattern();
	}

	public int getCompilerFlags() {
		return 0;
	}
}