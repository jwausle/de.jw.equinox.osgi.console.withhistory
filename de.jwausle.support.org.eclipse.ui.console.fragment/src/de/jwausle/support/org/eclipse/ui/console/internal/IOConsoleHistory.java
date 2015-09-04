package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import org.eclipse.swt.custom.StyledText;

@SuppressWarnings({ "rawtypes", "unchecked" })
public class IOConsoleHistory {
	private final StyledText styledText;

	private Stack history = new Stack();

	private ListIterator session = null;

	public IOConsoleHistory(StyledText st) {
		styledText = st;
	}

	public IOConsoleHistory add(String command) {
		session = null;

		if (command == null)
			return this;

		if (command.trim().isEmpty())
			return this;

		Object lastCommand = history.isEmpty() ? "" : history.peek();
		if (command.equals(lastCommand)) {
			return this;
		}

		history.add(command);

		return this;
	}

	public String next() {
		if (session == null)
			session = new LinkedList(history).listIterator();

		Object next = null;
		try {
			next = session.next();
		} catch (Exception e) {
			// ignore
		}
		String string = next == null ? "" : next.toString();
		return string;
	}

	public String previous() {
		if (session == null) {
			List stackList = new LinkedList(history);
//			Collections.reverse(stackList);
			session = stackList.listIterator();
			while(session.hasNext())
				session.next();
		}

		Object previous = null;
		try {
			previous = session.previous();
		} catch (Exception e) {
			// ignore
		}
		String string = previous == null ? "" : previous.toString();
		return string;
	}

}
