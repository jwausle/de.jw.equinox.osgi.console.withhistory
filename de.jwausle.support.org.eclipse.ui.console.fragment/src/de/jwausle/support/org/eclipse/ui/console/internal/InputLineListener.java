package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.custom.StyledTextContent;
import org.eclipse.swt.custom.VerifyKeyListener;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.console.IOConsole;

public class InputLineListener implements KeyListener,
		org.eclipse.swt.widgets.Listener, VerifyKeyListener {

	@SuppressWarnings("unused")
	private IOConsole console;
	@SuppressWarnings("unused")
	private StyledTextEditArea styledTextEditArea;

	private StyledText styledText;

	private _BufferHandle buffer = new _BufferHandle();
	private HistoryHandle history;
	private _HistoryHandle history2;

	private boolean ctrlSpace = false;
	private boolean altSpace = false;

	private _CommandServiceTracker cmdTracker;
	private _BundleServiceTracker bundleTracker;
	private _QuickAssistAssistant quickAssist;
	private String lastcommand;

	public InputLineListener(final IOConsole consoleRef,
			final StyledText textWigetRef) {
		this.console = consoleRef;
		this.styledText = textWigetRef;
		this.styledTextEditArea = new StyledTextEditArea(this.styledText
				.getContent().getCharCount());

		this.history = new HistoryHandle(consoleRef, textWigetRef);
		this.history.registerGogoCommand();

		this.history2 = new _HistoryHandle(textWigetRef);

		this.buffer.registerGogoCommand();
	}

	public void handleEvent(Event e) {
		if (e.widget != this.styledText)
			return;

		KeyHandle key = new KeyHandle(e);
		System.err.printf("%s\n", key);

		if (key.ignore()) {
			System.err.println("==> ignore: " + key);
			return;
		}

		if (key.isCtrlSpace() || key.isTab()) {
			String lastWord = getLastWord(this.styledText);
			this.quickAssist.show(this.cmdTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
			return;
		}

		if (key.isAltSpace()) {
			String lastWord = getLastWord(this.styledText);
			this.quickAssist.show(this.bundleTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
			return;
		}

		// if (key.isDel()) {
		// buffer.deleteLastChar();
		// } else
		if (key.isArrowUp()) {
			history.keyReleased(e.keyCode);
			// String command = history.popFromHistory();
			// new
			// StyledTextHandle(styledText).appendText(command,buffer.toString());
			buffer.update(history.data.lastCommand());
		} else if (key.isArrowDown()) {
			// String command = history.popFromOneBefore();
			// new StyledTextHandle(styledText).appendText(command);
			history.keyReleased(e.keyCode);
			buffer.update(history.data.lastCommand());
		} else if (key.isReturn()) {
			addBufferToHistory();
		} else {
			// StyledTextHandle handle = new StyledTextHandle(this.styledText);
			// handle.appendText("command");
			// handle.replace("command", "aaa");
			// handle.replace("md", "ommand");
			// System.err.println("==> add to buffer.");
			// buffer.append(key.get());
		}
	}

	private void addBufferToHistory() {
		synchronized (this.buffer) {

			String cmd = buffer.toString();
			String line = this.styledText.getLine(this.styledText
					.getLineCount() - 1);
			int indexOf = line.indexOf(" ");
			cmd = line.substring(indexOf + 1);
			{// hot
				cmd = new _StyledTextHandle(styledText).getLastCommandLine();
				this.history2.add(cmd);
			}
			this.history.add(cmd);
			this.buffer.clear();
		}

	}

	public static String replaceLast(String string, String toReplace,
			String replacement) {
		int pos = string.lastIndexOf(toReplace);
		if (pos > -1) {
			return string.substring(0, pos)
					+ replacement
					+ string.substring(pos + toReplace.length(),
							string.length());
		} else {
			return string;
		}
	}

	public void verifyKey(VerifyEvent event) {
		KeyHandle key = new KeyHandle(event);
		System.err.printf("%s\n", key);

		if (key.ignore()) {
			System.err.println("==> ignore: " + key);
			return;
		}

		if (key.isCtrlSpace() || key.isTab()) {
			String lastWord = getLastWord(this.styledText);
			this.quickAssist.show(this.cmdTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
			event.doit = false;
			return;
		}

		if (key.isAltSpace()) {
			String lastWord = getLastWord(this.styledText);
			this.quickAssist.show(this.bundleTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
			event.doit = false;
			return;
		}

		if ("".isEmpty()) {
			return;
		}
		if (!this.ctrlSpace || !this.altSpace) {
			return;
		}
		String lastWord = getLastWord(this.styledText);
		System.err.printf("==> word='%s'", lastWord);

		if (this.ctrlSpace) {
			this.quickAssist.show(this.cmdTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
			this.ctrlSpace = false;

		} else if (this.altSpace) {
			this.quickAssist.show(this.bundleTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
		}

		event.doit = false;
		this.ctrlSpace = false;
	}

	public static String getLastWord(StyledText styledText2) {
		String tokenBeforeCursor = new _StyledTextHandle(styledText2)
				.getTokenBeforeCursor();
		System.err.printf("==> filter: %s\n", tokenBeforeCursor);
		return tokenBeforeCursor;
		// StyledTextContent content = styledText2.getContent();
		// char[] charArray = content.toString().toCharArray();
		//
		// StringBuffer buffer = new StringBuffer();
		// int charCount = charArray.length;
		// for(int index = charCount -1; charCount > 0; index--){
		// char c = charArray[index];
		// switch (c) {
		// case '\n': return buffer.reverse().toString().trim();
		// case ' ': continue;
		// default:
		// buffer.append(c);
		// }
		//
		// }
		// return null;
	}

	public void setCommandTracker(_CommandServiceTracker commandTracker) {
		this.cmdTracker = commandTracker;
	}

	public void setContentAssist(_QuickAssistAssistant assistant) {
		this.quickAssist = assistant;
	}

	public void setBundleTracker(_BundleServiceTracker bundleTracker) {
		this.bundleTracker = bundleTracker;
	}

	public void keyPressed(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);
		if(key.isReturn()){
			lastcommand = new _StyledTextHandle(styledText).getLastCommandLine();
//			addBufferToHistory();
		}
	}

	public void keyReleased(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);
		System.err.printf("%s (%s/%s)\n", key, styledText.getSelection(),
				styledText.getCharCount());

		if (key.ignore()) {
			System.err.println("==> ignore: " + key);
			return;
		}

		if (key.isCtrlSpace() || key.isTab()) {
			String lastWord = getLastWord(this.styledText);
			System.err.printf("===> capture alt+' ' with ´%s´\n", lastWord);
			this.quickAssist.show(this.cmdTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
			e.doit = false;
			return;
		}

		if (key.isAltSpace()) {
			String lastWord = getLastWord(this.styledText);
			System.err.printf("===> capture alt+' ' with ´%s´\n", lastWord);
			this.quickAssist.show(this.bundleTracker, lastWord,
					new _ReplaceCommandWriter(lastWord, new _StyledTextHandle(
							this.styledText)));
			e.doit = false;
			return;
		}

		if (key.isArrowUp()) {
			String previous = history2.previous();
			new _StyledTextHandle(styledText).replaceLine(previous);
//			history.keyReleased(e.keyCode);
		} else if (key.isArrowDown()) {
			String next = history2.next();
			new _StyledTextHandle(styledText).replaceLine(next);
//			history.keyReleased(e.keyCode);
		} else if (key.isReturn()) {
			this.history2.add(lastcommand);
		}
		if ("".isEmpty()) {
			return;
		}

	}

}
