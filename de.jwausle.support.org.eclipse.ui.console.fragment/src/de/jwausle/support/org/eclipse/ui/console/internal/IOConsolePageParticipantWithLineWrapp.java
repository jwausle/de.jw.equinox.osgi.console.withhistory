package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.TextConsole;
import org.eclipse.ui.part.IPageBookViewPage;

public class IOConsolePageParticipantWithLineWrapp implements
		IConsolePageParticipant {

	private IPageBookViewPage page;
	private TextConsole console;
	private StyledText styledText;

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void init(IPageBookViewPage page, IConsole console) {
		this.page = page;

		if (console instanceof TextConsole) {
			this.console = (TextConsole) console;
		}
	}

	public void dispose() {

	}

	public void activated() {
		boolean pageExist = this.page != null;

		if (pageExist) {
			Control control = this.page.getControl();
			if (control instanceof StyledText) {
				this.styledText = (StyledText) control;

				enableLineWrapping();
			}
		}

		boolean consoleExist = this.console != null;
		if (consoleExist) {
			console.addPatternMatchListener(new LineWrappPatternMatchListener(
					this, ".*(disableLineWrapping).*(\n[^\n]|\r[^\r])"));
			console.addPatternMatchListener(new LineWrappPatternMatchListener(
					this, ".*(enableLineWrapping).*(\n[^\n]|\r[^\r])"));
		}
	}

	protected void enableLineWrapping() {
		Display.getDefault().syncExec(new Runnable() {
			
			public void run() {
				IOConsolePageParticipantWithLineWrapp.this.styledText.setWordWrap(true);
			}
		});
	}

	protected void disableLineWrapping() {
		Display.getDefault().asyncExec(new Runnable() {
			
			public void run() {
				IOConsolePageParticipantWithLineWrapp.this.styledText.setWordWrap(false);
			}
		});
	}

	public void deactivated() {
		this.styledText = null;
	}

}
