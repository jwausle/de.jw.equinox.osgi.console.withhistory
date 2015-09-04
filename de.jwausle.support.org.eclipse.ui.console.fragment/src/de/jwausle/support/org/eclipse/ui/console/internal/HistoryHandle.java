/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.logging.Logger;

import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyAdapter;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.IPatternMatchListener;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * @author wij1si
 */
public final class HistoryHandle extends KeyAdapter implements CommandWriter{
	private final Logger log = Logger.getLogger(HistoryHandle.class.getName());

	/** Ref to io console. */
	private final IOConsole console;
	/** Ref to text widget. */
	private final StyledText styledText;
	/** Ref to calculate area where command will be resetted */
	private final StyledTextEditArea styledTextEditArea;
	/** Ref to [command] list and last added command */
	final HistoryCommandStack data = new HistoryCommandStack();
	@SuppressWarnings("rawtypes")
	final Class dataGuard = HistoryCommandStack.class;

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
		int keyCode = e.keyCode;
		keyReleased(keyCode);
	}

	public void keyReleased(int keyCode) {
		// super.keyReleased(e);
		synchronized (dataGuard) {
			if (SWT.ARROW_UP == keyCode) {
				String lastCommand = popFromHistory();
				writeCommandToCosole(lastCommand);
			} else if (SWT.ARROW_DOWN == keyCode) {
				String lastSessionCommand = popFromOneBefore();
				writeCommandToCosole(lastSessionCommand);
			}
		}
	}

	public String popFromOneBefore() {
		String lastSessionCommand = this.data.popOneBefore();
		return lastSessionCommand;
	}

	public String popFromHistory() {
		String lastCommand = this.data.popFromHistory();
		return lastCommand;
	}

	/**
	 * @param command
	 *            no null command.
	 */
	private synchronized void writeCommandToCosole(final String command) {
		if (command == null) {
			return;
		}

		int consoleCharCountActual = this.styledText.getContent()
				.getCharCount();
		int length = this.styledTextEditArea
				.getDeleteCommandLength(consoleCharCountActual);

		// delete last command in console.
		try {
			this.styledText.replaceTextRange(
					this.styledTextEditArea.consoleCharCountBeforeAdding,
					length, "");
		} catch (IllegalArgumentException e) {
			log.fine("Expected exception by replaceTextRange() catched: "
					+ e.getMessage());
		}

		this.styledText.setText(command);

		this.styledTextEditArea.resetCharCount(this.styledText, command);

		log.fine("write:  " + this.data);
	}

	/**
	 * Delegate to styledTextEditArea.
	 */
	public void clearConsole() {
		this.styledTextEditArea.clearConsole();
	}

	public IPatternMatchListener getPatternMatchHandler() {
		return new HistoryHandlePatternMatchListener(this.console,
				HistoryHandle.this);
	}

	public void add(String command) {
		if (!command.equals(data.lastCommand())) {
			data.add(command);
		}
		data.clearSessionStack();
	}

	public void writeLast() {
		String lastCommand = data.lastCommand();
		writeCommandToCosole(lastCommand);
	}

	@Override
	public String toString() {
		return this.data.toString();
	}

	public void registerGogoCommand() {
		Bundle bundle = FrameworkUtil.getBundle(getClass());
		if (bundle == null) {
			return;
		}
		BundleContext context = bundle.getBundleContext();
		if (context == null)
			return;

		Hashtable<String, String> cmdDesc = new Hashtable<String, String>();
		cmdDesc.put("osgi.command.function", "history");
		cmdDesc.put("osgi.command.scope", "jw");
		context.registerService(HistoryHandle.class, this, cmdDesc);
	}

	public String history(String... args) {
		if (args.length == 0) 
			return this.data.toString();
		
		if ("-show-all".equals(args[0]))
			return this.data.toStringAll();
		if ("-clear".equals(args[0])){
			this.data.clear();
			return "history -clear done.";
		}
		return "Unknow args: " + Arrays.toString(args) + ". Use 'history [-show-all/-clear]'";
	}

	public void write(String command) {
		writeCommandToCosole(command);
	}

}