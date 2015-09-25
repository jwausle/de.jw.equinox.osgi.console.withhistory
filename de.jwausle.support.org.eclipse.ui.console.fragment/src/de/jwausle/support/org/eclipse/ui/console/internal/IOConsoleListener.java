package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.jface.text.BadLocationException;
import org.eclipse.jface.text.IDocument;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Display;

/**
 * The key event handler.
 * 
 * @author winter
 *
 */
public class IOConsoleListener implements KeyListener, CommandWriteCallback {
	private final Logger log = Logger.getLogger(IOConsoleListener.class);

	private final IOConsoleHistory history;
	private final StyledText styledText;
	private final IDocument document;

	private ProposalGetterBundles bundleTracker = null;
	private ProposalGetterCommands cmdTracker = null;
	private QuickAssistant quickAssist = null;

	private String lastcommand = null;

	public IOConsoleListener(final StyledText textWigetRef, IDocument document) {
		this.styledText = textWigetRef;
		this.document = document;
		this.history = new IOConsoleHistory().setStyledText(styledText);
		this.history.registerGogoCommand();
	}

	public void keyPressed(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);

		if (key.isReturn()) {
			lastcommand = new StyledTextHandle(styledText,document).getLastCommandLine();
			log.debug("Catched return=true with last-command: `{0}`.",
					lastcommand);
		}
	}

	public void keyReleased(KeyEvent e) {
		KeyHandle key = new KeyHandle(e);

		if (key.ignore()) {
			log.debug("Ignore key={0}", key);
			return;
		}

		if (key.isCtrlE()) {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					styledText.setSelection(styledText.getCharCount());
				}
			});
			return;
		}

		StyledTextHandle sth = new StyledTextHandle(this.styledText, this.document);
		if (key.isCtrlSpace() || key.isTab()) {
			log.debug("Catched ctrl+space={0} or tab={1}", key.isCtrlSpace(),
					key.isTab());
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

		if (key.isCtrlR()) {
			log.debug("Catched ctrl+r=true");
			ProposalGetterCommandHistroy history = new ProposalGetterCommandHistroy(
					this.history);

			this.quickAssist.show(history, "", this);
			e.doit = false;
			return;
		}

		boolean visible = this.quickAssist.isVisible();
		if (!visible && key.isArrowUp()) {
			log.debug("Catched arrow-up=true");
			String previous = history.previous();
			if (previous.isEmpty()) {
				styledText.setSelection(styledText.getCharCount());
				return;
			}
			sth.replaceLine(previous);
			styledText.setSelection(styledText.getCharCount());
		} else if (!visible && key.isArrowDown()) {
			log.debug("Catched arrow-down=true");
			String next = history.next();
			if (next.isEmpty()) {
				styledText.setSelection(styledText.getCharCount());
				return;
			}
			sth.replaceLine(next);
			styledText.setSelection(styledText.getCharCount());
		} else if (!visible && key.isReturn()) {
			log.debug("Add command to history: {0}", lastcommand);
			this.history.add(lastcommand);
		} else if (key.isEsc()) {
			log.debug("Catched esc=true");
			this.quickAssist.hide();
		}

	}

	void writeRandom() {
		int lineIndex = styledText.getLineCount() - 1;
		String line = styledText.getLine(lineIndex);
		Point selection = styledText.getSelection();
		log.debug("Selection: {0}. Arrow-up get selection regaring one line above:(", selection);
		
		try {
			int offsetAtLine = styledText.getOffsetAtLine(lineIndex);
			if (offsetAtLine == 0) {
				document.replace(offsetAtLine, line.length(),
						"osgi! "
								+ Double.valueOf(Math.random()).toString()
										.substring(0, 10));
			} else {
				document.replace(offsetAtLine + 6, line.length() - 6, Double
						.valueOf(Math.random()).toString().substring(0, 10));
			}
		} catch (BadLocationException e1) {
			e1.printStackTrace();
		}
		styledText.setSelection(styledText.getCharCount());
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
		StyledTextHandle sth = new StyledTextHandle(this.styledText,document);

		String filter = sth.getTokenBeforeCursor();

		sth.replace(filter, command);

		this.quickAssist.hide();
	}

}
