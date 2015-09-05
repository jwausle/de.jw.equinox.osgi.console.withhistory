package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;

public class IOConsoleListener implements KeyListener, CommandWriteCallback {

	private ProposalGetterBundles bundleTracker;
	private ProposalGetterCommands cmdTracker;

	private QuickAssistant quickAssist;

	private IOConsoleHistory history;
	private StyledText styledText;

	private String lastcommand;

	public IOConsoleListener(final StyledText textWigetRef) {
		this.styledText = textWigetRef;
		this.history = new IOConsoleHistory().setStyledText(styledText);
		this.history.registerGogoCommand();
	}

	public void keyPressed(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);

		if (key.isReturn()) {
			lastcommand = new StyledTextHandle(styledText).getLastCommandLine();
		}
	}

	public void keyReleased(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);
		
		if (key.ignore()) {
			return;
		}

		StyledTextHandle sth = new StyledTextHandle(this.styledText);
		if (key.isCtrlSpace() || key.isTab()) {
			String tokenBeforeCursor = sth.getTokenBeforeCursor();
			String lastWord = tokenBeforeCursor;
			this.quickAssist.show(this.cmdTracker, lastWord, this);
			e.doit = false;
			return;
		}

		if (key.isAltSpace()) {
			String lastWord = sth.getTokenBeforeCursor();
			this.quickAssist.show(this.bundleTracker, lastWord, this);
			e.doit = false;
			return;
		}

		boolean visible = this.quickAssist.isVisible();
		if (!visible && key.isArrowUp()) {
			String previous = history.previous();
			sth.replaceLine(previous);
		} else if (!visible && key.isArrowDown()) {
			String next = history.next();
			sth.replaceLine(next);
		} else if (!visible && key.isReturn()) {
			this.history.add(lastcommand);
		}

	}

	public void setBundleTracker(ProposalGetterBundles bundleTracker) {
		this.bundleTracker = bundleTracker;
	}

	public void setCommandTracker(ProposalGetterCommands commandTracker) {
		this.cmdTracker = commandTracker;
	}

	public void setContentAssist(QuickAssistant assistant) {
		this.quickAssist = assistant;
	}
	
	public void write(String command) {
		StyledTextHandle sth = new StyledTextHandle(this.styledText);

		String filter = sth.getTokenBeforeCursor();

		sth.replace(filter, command);

		this.quickAssist.setVisible(false);
	}

}
