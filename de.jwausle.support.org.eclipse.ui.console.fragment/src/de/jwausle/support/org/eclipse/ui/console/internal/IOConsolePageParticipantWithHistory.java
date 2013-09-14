package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.part.IPageBookViewPage;


public class IOConsolePageParticipantWithHistory implements
		IConsolePageParticipant {
	private IPageBookViewPage ioConsolePage;
	private HistoryHandle ioConsoleHistoryHandle;
	private IOConsole ioConsole;

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void init(IPageBookViewPage page, IConsole console) {
		if (console instanceof IOConsole) {
			this.ioConsole = (IOConsole) console;
			this.ioConsolePage = page;
		}
	}

	public void dispose() {
		this.ioConsole = null;
		this.ioConsolePage = null;
		this.ioConsoleHistoryHandle = null;
	}

	public void activated() {
		boolean pageExist_AND_historyHandleIsNull = (this.ioConsolePage != null)
				&& (this.ioConsoleHistoryHandle == null);

		lazyInit(pageExist_AND_historyHandleIsNull);
	}

	public void deactivated() {
	}

	private void lazyInit(final boolean initIfTrue) {
		if (initIfTrue) {
			StyledText textWiget = (StyledText) this.ioConsolePage.getControl();

			this.ioConsoleHistoryHandle = new HistoryHandle(this.ioConsole, textWiget);
			textWiget.addKeyListener(this.ioConsoleHistoryHandle);
			
			this.ioConsole.addPatternMatchListener(new HistoryHandlePatternMatchListener(ioConsole, ioConsoleHistoryHandle,"osgi>(.*)(\n[^\n]|\r[^\r])"));
			this.ioConsole.addPatternMatchListener(new HistoryHandlePatternMatchListener(ioConsole, ioConsoleHistoryHandle,"g!(.*)(\n[^\n]|\r[^\r])"));
		}
	}

}
