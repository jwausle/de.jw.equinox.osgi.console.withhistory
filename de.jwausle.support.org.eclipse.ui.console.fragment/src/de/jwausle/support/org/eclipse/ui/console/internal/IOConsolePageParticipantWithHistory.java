package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Properties;
import java.util.Set;

import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * A IConsolePageParticipant to register all [pattern] for a IOConsole. <br />
 * <br />
 * [pattern] 	:	[default-pattern] ++ [-Dioconsole.history.pattern.*=pattern]<br />
 * [default-pattern]	:	['osgi>(.*)(\n[^\n]|\r[^\r])', 'g!(.*)(\n[^\n]|\r[^\r])']<br />
 * [-Dioconsole.history.pattern.*=pattern]	:	pattern.matches(command).group(1) is the pattern<br />
 * @author winter
 *
 */
public class IOConsolePageParticipantWithHistory implements
		IConsolePageParticipant {
	
	public static final String IOCONSOLE_HISTORY_PATTERN = "ioconsole.history.pattern";

	/**
	 * Get a listOf [System.properties.values] where -D${keyPrefix}.*=value
	 * @param keyPrefix not null prefix
	 * @return listOf [System.properties.values] where -D${keyPrefix}.*=value
	 */
	public static List getUserPattern(String keyPrefix) {
		Properties properties = System.getProperties();
		final Set keys = properties.keySet();

		final LinkedList patternList = new LinkedList();
		final Iterator keysIterator = keys.iterator();
		while(keysIterator.hasNext()){
			final Object next = keysIterator.next();
			if(next == null)
				continue;
			if(next.toString().startsWith(keyPrefix)){
				String catchedKey = next.toString();
				Object value = properties.get(catchedKey);
				if(value != null){
					patternList.add(value);
				}
			}
		}
		return patternList;
	}
	private IPageBookViewPage ioConsolePage;
	private HistoryHandle ioConsoleHistoryHandle;
	private IOConsole ioConsole;

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void init(IPageBookViewPage page, IConsole console) {
		if (console instanceof IOConsole) {
			this.ioConsole = (IOConsole) console;
			this.ioConsolePage = page;
		}
	}

	public void dispose() {
		this.ioConsole = null;
		this.ioConsolePage = null;
		this.ioConsoleHistoryHandle = null;
	}

	public void activated() {
		boolean pageExist_AND_historyHandleIsNull = (this.ioConsolePage != null)
				&& (this.ioConsoleHistoryHandle == null);

		lazyInit(pageExist_AND_historyHandleIsNull);
	}

	public void deactivated() {
	}

	private void lazyInit(final boolean initIfTrue) {
		if (initIfTrue) {
			StyledText textWiget = (StyledText) this.ioConsolePage.getControl();
//			textWiget.setWordWrap(true);

			this.ioConsoleHistoryHandle = new HistoryHandle(this.ioConsole, textWiget);
			textWiget.addKeyListener(this.ioConsoleHistoryHandle);
			
			this.ioConsole.addPatternMatchListener(new HistoryHandlePatternMatchListener(ioConsole, ioConsoleHistoryHandle,"osgi>(.*)(\n[^\n]|\r[^\r])"));
			this.ioConsole.addPatternMatchListener(new HistoryHandlePatternMatchListener(ioConsole, ioConsoleHistoryHandle,"g!(.*)(\n[^\n]|\r[^\r])"));
			
			List userPattern = getUserPattern();
			for (int i=0; i<userPattern.size();i++) {
				String pattern = userPattern.get(i).toString();
				this.ioConsole.addPatternMatchListener(new HistoryHandlePatternMatchListener(ioConsole, ioConsoleHistoryHandle,pattern ));
			}
		}
	}

	private List getUserPattern() {
		String keyPrefix = IOCONSOLE_HISTORY_PATTERN;
		List userPattern = getUserPattern(keyPrefix);
		return userPattern;
	}

}
