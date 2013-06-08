package org.eclipse.pde.internal.ui.util;

import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IOConsole;

/** Copied from OSGiConsoleFactory */
public class OSGiConsoleWithHistoryFactory extends OSGiConsoleFactory{
	private IOConsole fConsole = null;
	private IConsoleManager fConsoleManager;

	public OSGiConsoleWithHistoryFactory() {
		fConsoleManager = ConsolePlugin.getDefault().getConsoleManager();
	}

	/* (non-Javadoc)
	 * @see org.eclipse.ui.console.IConsoleFactory#openConsole()
	 */
	public void openConsole() {
		IOConsole console = getConsole();

		IConsole[] existing = fConsoleManager.getConsoles();
		boolean exists = false;
		for (int i = 0; i < existing.length; i++) {
			if (console == existing[i])
				exists = true;
		}
		if (!exists)
			fConsoleManager.addConsoles(new IConsole[] {console});
		fConsoleManager.showConsoleView(console);
	}

	private synchronized IOConsole getConsole() {
		if (fConsole != null)
			return fConsole;
		fConsole = new OSGiConsoleWithHistory(this);
		return fConsole;
	}

	void closeConsole(OSGiConsole console) {
		synchronized (this) {
			if (console != fConsole)
				throw new IllegalArgumentException("Wrong console instance!"); //$NON-NLS-1$
			fConsole = null;
		}
		fConsoleManager.removeConsoles(new IConsole[] {console});
	}}
