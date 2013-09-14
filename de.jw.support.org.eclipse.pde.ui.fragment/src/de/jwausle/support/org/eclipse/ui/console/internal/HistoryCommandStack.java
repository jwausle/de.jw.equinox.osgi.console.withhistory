/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

/**
 * @author wij1si
 */
public class HistoryCommandStack {

	private final Stack history;
	private final Stack historySession;

	/** Default constructor */
	public HistoryCommandStack() {
		this.history = new Stack();
		this.historySession = new Stack();
	}

	/**
	 * @param command
	 *            maybe null command.
	 * @return true if command == lastCommand (null return false)
	 */
	public boolean equalLastCommand(final String command) {
		if (this.history.isEmpty()) {
			return false;
		}

		if (command == null) {
			return false;
		}

		String trimedCommand = command.trim();
		String lastCommand = lastCommand();

		boolean equalCommands = lastCommand.equals(trimedCommand);
		return equalCommands;
	}

	/**
	 * @return historySession.list.<b>head</b>(if exist) ||
	 *         history.<b>pop</b>(if exist) || <b>""</b>.
	 */
	public String lastCommand() {
		String lastCommand = "";

		if (!this.historySession.isEmpty()) {
			List historySessionCopy = historySessionAsReverseList();

			lastCommand = (String) historySessionCopy.iterator().next();
			return lastCommand;
		}
		if (!this.history.isEmpty()) {
			lastCommand = (String) this.history.peek();
			return lastCommand;
		}

		return lastCommand;
	}

	/**
	 * @param command
	 *            to append command.
	 */

	public void add(final String command) {
		if (command == null) {
			return;
		}

		if (equalLastCommand(command)) {
			return;
		}

		String trimedCommand = command.trim();
		if (trimedCommand.isEmpty()) {
			return;
		}

		if (trimedCommand.endsWith("\r")) {
			trimedCommand = trimedCommand.substring(0,
					trimedCommand.length() - 1);
		}

		if (this.historySession.isEmpty()) {
			this.history.push(trimedCommand);
			return;
		}

		clearSessionStack();
		this.history.push(trimedCommand);
	}

	/**
   * 
   */
	public void clearSessionStack() {
		List historySessionCopy = historySessionAsReverseList();

		this.history.addAll(historySessionCopy);
		this.historySession.clear();
	}

	private List historySessionAsReverseList() {
		List historySessionCopy = new LinkedList(this.historySession);
		Collections.reverse(historySessionCopy);
		return historySessionCopy;
	}

	/**
	 * @return lastComamnd or "" if history is empty.
	 */
	public String popFromHistory() {
		if (this.history.isEmpty()) {
			return "";
		}

		String lastCommandFromHistory = (String) this.history.pop();

		this.historySession.push(lastCommandFromHistory);
		return lastCommandFromHistory;
	}

	/**
	 * @return lastCommand from session(arrow up) or "" if session is empty.
	 */
	public String popOneBefore() {
		if (this.historySession.isEmpty()) {
			return "";
		}
		String lastSessionCommand = (String) this.historySession.pop();

		this.history.push(lastSessionCommand);

		return lastSessionCommand;
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		return "IOConsoleWithHistoryData [history=" + this.history
				+ ", historySession=" + this.historySession + "]";
	}

}
