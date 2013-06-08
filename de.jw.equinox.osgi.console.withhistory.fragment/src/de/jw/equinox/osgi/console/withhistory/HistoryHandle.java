/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package de.jw.equinox.osgi.console.withhistory;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IPatternMatchListener;

/**
 * @author wij1si
 */
public final class HistoryHandle extends KeyAdapter {

	/** Ref to io console. */
	private final IOConsole console;
	/** Ref to text widget. */
	private final StyledText styledText;
	/** Ref to calculate area where command will be resetted */
	private final StyledTextEditArea styledTextEditArea;
	/** Ref to [command] list and last added command */
	final HistoryCommandStack data = new HistoryCommandStack();

	/**
	 * @param consoleRef
	 *            not null console ref.
	 * @param textWigetRef
	 *            not null ref to text widget.
	 */
	public HistoryHandle(final IOConsole consoleRef,
			final StyledText textWigetRef) {
		this.console = consoleRef;
		this.styledText = textWigetRef;
		this.styledTextEditArea = new StyledTextEditArea(this.styledText
				.getContent().getCharCount());
	}

	/**
	 * {@inheritDoc}
	 */
	public void keyReleased(final KeyEvent e) {
		// super.keyReleased(e);
		if (SWT.ARROW_UP == e.keyCode) {
			String lastCommand = this.data.popFromHistory();
			writeCommandToCosole(lastCommand);
		} else if (SWT.ARROW_DOWN == e.keyCode) {
			String lastSessionCommand = this.data.popOneBefore();
			writeCommandToCosole(lastSessionCommand);
		} 
	}

	/**
	 * @param command
	 *            no null command.
	 */
	private void writeCommandToCosole(final String command) {
		if (command == null) {
			return;
		}

		int consoleCharCountActual = this.styledText.getContent()
				.getCharCount();
		int length = this.styledTextEditArea
				.getDeleteCommandLength(consoleCharCountActual);

		// delete last command in console.
		this.styledText.replaceTextRange(
				this.styledTextEditArea.consoleCharCountBeforeAdding, length,
				"");

		this.styledText.setText(command);

		this.styledTextEditArea.resetCharCount(this.styledText, command);

		System.out.println("write:  " + this.data);
	}

	/**
	 * Delegate to styledTextEditArea.
	 */
	public void clearConsole() {
		this.styledTextEditArea.clearConsole();
	}

	public IPatternMatchListener getPatternMatchHandler() {
		return new HistoryHandlePatternMatchListener(this.console, HistoryHandle.this);
	}
}