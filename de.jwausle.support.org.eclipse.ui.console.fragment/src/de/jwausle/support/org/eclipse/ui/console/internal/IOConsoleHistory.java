package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Stack;

import org.apache.felix.service.command.Descriptor;
import org.eclipse.swt.custom.StyledText;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

/**
 * Command history. To handle ARROWUP/ARROWDOWN session.
 * 
 * @author winter
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked" })
public class IOConsoleHistory {
	private Stack history = new Stack();

	private ListIterator session = null;

	private StyledText styledText;

	/**
	 * Add a command to history.
	 * 
	 * @param command
	 *            ignore null and empty commands.
	 * @return this.
	 */
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

	/**
	 * Return the
	 * 
	 * @return
	 */
	public String next() {
		if (session == null)
			session = newSession();

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
			session = newSession();
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

	public IOConsoleHistory setStyledText(StyledText styledText) {
		this.styledText = styledText;
		return this;
	}

	private ListIterator newSession() {
		List stackList = new LinkedList(history);

		LinkedList filtered = new LinkedList();
		if (styledText != null) {
			String prefix = new StyledTextHandle(styledText).getCommandName();
			for (Object object : stackList) {
				if (object == null)
					continue;

				String string = object.toString();
				if (!string.startsWith(prefix)) {
					continue;
				}

				filtered.add(string);
			}
		} else {
			filtered.addAll(stackList);
		}

		ListIterator session2 = filtered.listIterator();
		while (session2.hasNext())
			session2.next();

		return session2;
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
		cmdDesc.put("osgi.command.scope", "jwausle");
		context.registerService(IOConsoleHistory.class, this, cmdDesc);
	}
	@Descriptor("[--show-all/--clear]")
	public String history(@Descriptor("...\n\t"//
			+ "[]        \t: show short history.\n\t"//
			+ "--show-all\t: show complete history.\n\t"//
			+ "--clear   \t: clear history.\n\t"//
			+ "--help    \t: show help.\n\t"//
			) String... args) {
		if (args.length == 0)
			return this.toString();
		
		if ("--show-all".equals(args[0]))
			return this.history.toString();
		if ("--clear".equals(args[0])) {
			this.history.clear();
			return "history --clear done.";
		}
		if ("--help".equals(args[0])) {
			this.history.clear();
			return "Use 'history [--show-all/--clear]'";
		}
		
		return "Unknow args: " + Arrays.toString(args)
				+ ". Use 'history [--show-all/--clear]'";
	}

	/**
	 * {@inheritDoc}
	 */
	public String toString() {
		switch (history.size()) {
		case 0:
			return "[]";
		case 1:
		case 2:
		case 3:
			return history.toString();
		default:
			String first = history.elementAt(0).toString();
			String last = history.elementAt(history.size() - 1).toString();
			return "[" + first + ", ..(" + history.size() + ").. ,  " + last
					+ "]";
		}
	}

}
