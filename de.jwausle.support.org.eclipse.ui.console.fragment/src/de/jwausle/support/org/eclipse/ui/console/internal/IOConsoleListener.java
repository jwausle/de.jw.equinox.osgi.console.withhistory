package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class IOConsoleListener implements KeyListener {

	private StyledText styledText;

	private IOConsoleHistory history2;

	private ProposalGetterCommands cmdTracker;
	private ProposalGetterBundles bundleTracker;
	private QuickAssistant quickAssist;
	private String lastcommand;

	public IOConsoleListener(final StyledText textWigetRef) {
		this.styledText = textWigetRef;
		this.history2 = new IOConsoleHistory(textWigetRef);
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

	public static String getLastWord(StyledText styledText2) {
		String tokenBeforeCursor = new StyledTextHandle(styledText2)
				.getTokenBeforeCursor();
		System.err.printf("==> filter: %s\n", tokenBeforeCursor);
		return tokenBeforeCursor;
	}

	public void setCommandTracker(ProposalGetterCommands commandTracker) {
		this.cmdTracker = commandTracker;
	}

	public void setContentAssist(QuickAssistant assistant) {
		this.quickAssist = assistant;
	}

	public void setBundleTracker(ProposalGetterBundles bundleTracker) {
		this.bundleTracker = bundleTracker;
	}

	public void keyPressed(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);
		if (key.isReturn()) {
			lastcommand = new StyledTextHandle(styledText)
					.getLastCommandLine();
			// addBufferToHistory();
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
			CommandWriterSimple writer = new CommandWriterSimple(lastWord,
					new StyledTextHandle(this.styledText));
			writer.setQuickAssistance(quickAssist);
			this.quickAssist.show(this.cmdTracker, lastWord, writer);
			e.doit = false;
			return;
		}

		if (key.isAltSpace()) {
			String lastWord = getLastWord(this.styledText);
			System.err.printf("===> capture alt+' ' with ´%s´\n", lastWord);
			this.quickAssist.show(this.bundleTracker, lastWord,
					new CommandWriterSimple(lastWord, new StyledTextHandle(
							this.styledText)));
			e.doit = false;
			return;
		}

		boolean visible = this.quickAssist.isVisible();
		if (!visible && key.isArrowUp()) {
			String previous = history2.previous();
			new StyledTextHandle(styledText).replaceLine(previous);
		} else if (!visible && key.isArrowDown()) {
			String next = history2.next();
			new StyledTextHandle(styledText).replaceLine(next);
		} else if (!visible && key.isReturn()) {
			this.history2.add(lastcommand);
		}

	}

}
