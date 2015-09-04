package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Arrays;
import java.util.Hashtable;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

import com.ibm.icu.text.MessageFormat;

public class _BufferHandle {
	private StringBuffer buffer = new StringBuffer();
	private boolean showCommands = true;

	public boolean isNotEmpty() {
		return !isEmpty();
	}

	public boolean isEmpty() {
		return buffer.length() == 0;
	}

	public void deleteLastChar() {
		if (isEmpty())
			return;
		buffer.deleteCharAt(buffer.length() - 1);
		System.err.println("===> deleted last char of " + buffer + " with length=" + buffer.length());
	}

	public void append(char c) {
		if (c == ' ' && showCommands)
			showCommands = false;
		else if (c == '(' && !showCommands)
			showCommands = true;
		else if (c == ')' && showCommands)
			showCommands = false;
		else if (c == '|' && !showCommands)
			showCommands = true;
		buffer.append(c);
		System.err.println("===> appended '" + c + "' to " + buffer + " with length=" + buffer.length());
	}

	public void clear() {
		buffer = new StringBuffer();
	}

	@Override
	public String toString() {
		return buffer.toString();
	}

	public boolean isCommandAssistRequested() {
		return showCommands;
	}

	protected boolean isLastNonSpaceEquals(char... chars) {
		char lastNonSpace = getLastNonSpace(this.buffer);
		int binarySearch = Arrays.binarySearch(chars, lastNonSpace);
		return binarySearch >= 0;
	}

	private char getLastNonSpace(StringBuffer buf) {
		StringBuffer reverse = buf.reverse();
		char[] charArray = reverse.toString().toCharArray();
		for (char c : charArray) {
			if (c != ' ') {
				return c;
			}
		}
		return '\0';
	}

	public void update(String command) {
		this.buffer = new StringBuffer();
		this.buffer.append(command);
	}

	public String getLastWord() {
		char[] charArray = this.buffer.reverse().toString().toCharArray();
		StringBuffer _buffer = new StringBuffer();
		for (char c : charArray) {
			if (c == ' ')
				break;
			_buffer.append(c);
		}
		return _buffer.toString();
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
		cmdDesc.put("osgi.command.function", "buffer");
		cmdDesc.put("osgi.command.scope", "jw");
		context.registerService(_BufferHandle.class, this, cmdDesc);
	}

	public String buffer(String... args) {
		if (args.length == 0)
			return this.toString();

		if ("-show-all".equals(args[0]))
			return MessageFormat.format("buffer=%s, c=%s, word=%s",
					buffer.toString(), getLastNonSpace(this.buffer),
					getLastWord());
		if ("-clear".equals(args[0])) {
			this.buffer = new StringBuffer();
			return "buffer -clear done.";
		}
		return "Unknow args: " + Arrays.toString(args)
				+ ". Use 'buffer [-show-all/-clear]'";
	}

}
