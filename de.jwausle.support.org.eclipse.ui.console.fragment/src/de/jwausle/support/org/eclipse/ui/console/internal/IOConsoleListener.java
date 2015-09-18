package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.widgets.Display;
/**
 * The key event handler.
 * @author winter
 *
 */
public class IOConsoleListener implements KeyListener, CommandWriteCallback {
	private final Logger log = Logger.getLogger(IOConsoleListener.class);
	
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
			log.debug("Catched return=true with last-command: `{0}`.", lastcommand);
		}
	}

	public void keyReleased(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);
		
		if (key.ignore()) {
			log.debug("Ignore key={0}", key);
			return;
		}
		
		if(key.isCtrlE()){
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					styledText.setSelection(styledText.getCharCount());
				}
			});
			log.debug("====> {0}", new StyledTextCommandLine("osgi> ", styledText));
			return;
		}

		StyledTextHandle sth = new StyledTextHandle(this.styledText);
		if (key.isCtrlSpace() || key.isTab()) {
			log.debug("Catched ctrl+space={0} or tab={1}", key.isCtrlSpace(),key.isTab());
			String tokenBeforeCursor = sth.getTokenBeforeCursor();
			String lastWord = tokenBeforeCursor;
			this.quickAssist.show(this.cmdTracker, lastWord, this);
			e.doit = false;
			return;
		}

		if (key.isAltSpace()) {
			log.debug("Catched alt+space=true");
			String lastWord = sth.getTokenBeforeCursor();
			this.quickAssist.show(this.bundleTracker, lastWord, this);
			e.doit = false;
			return;
		}

		boolean visible = this.quickAssist.isVisible();
		if (!visible && key.isArrowUp()) {
			log.debug("Catched arrow-up=true");
			String previous = history.previous();
			sth.replaceLine(previous);
		} else if (!visible && key.isArrowDown()) {
			log.debug("Catched arrow-down=true");
			String next = history.next();
			sth.replaceLine(next);
		} else if (!visible && key.isReturn()) {
			this.history.add(lastcommand);
		} else if (key.isEsc()) {
			log.debug("Catched esc=true");
			this.quickAssist.hide();
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

		this.quickAssist.hide();
	}

}
