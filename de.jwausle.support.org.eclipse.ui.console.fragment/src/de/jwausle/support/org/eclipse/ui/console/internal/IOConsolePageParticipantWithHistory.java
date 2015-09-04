package de.jwausle.support.org.eclipse.ui.console.internal;

import java.io.InputStream;
import java.io.PrintStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.eclipse.jface.text.AbstractReusableInformationControlCreator;
import org.eclipse.jface.text.DefaultInformationControl;
import org.eclipse.jface.text.IInformationControl;
import org.eclipse.jface.text.ITextViewer;
import org.eclipse.jface.text.contentassist.ContentAssistant;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContentAssistProcessor;
import org.eclipse.jface.text.contentassist.IContentAssistant;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.jface.text.contentassist.IContextInformationValidator;
import org.eclipse.jface.text.quickassist.IQuickAssistAssistant;
import org.eclipse.jface.text.quickassist.IQuickAssistInvocationContext;
import org.eclipse.jface.text.quickassist.IQuickAssistProcessor;
import org.eclipse.jface.text.quickassist.QuickAssistAssistant;
import org.eclipse.jface.text.source.Annotation;
import org.eclipse.jface.text.source.ISourceViewer;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.internal.console.IOConsolePage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

/**
 * A IConsolePageParticipant to register all [pattern] for a IOConsole. <br />
 * <br />
 * [pattern] : [default-pattern] ++ [-Dioconsole.history.pattern.*=pattern]<br />
 * [default-pattern] : ['osgi>(.*)(\n[^\n]|\r[^\r])', 'g!(.*)(\n[^\n]|\r[^\r])']<br />
 * [-Dioconsole.history.pattern.*=pattern] : pattern.matches(command).group(1)
 * is the pattern<br />
 * 
 * @author winter
 *
 */
public class IOConsolePageParticipantWithHistory implements
		IConsolePageParticipant {
	public static StyledText TMP = null;
	public static final String IOCONSOLE_HISTORY_PATTERN = "ioconsole.history.pattern";

	/**
	 * Get a listOf [System.properties.values] where -D${keyPrefix}.*=value
	 * 
	 * @param keyPrefix
	 *            not null prefix
	 * @return listOf [System.properties.values] where -D${keyPrefix}.*=value
	 */
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static List getUserPattern(String keyPrefix) {
		Properties properties = System.getProperties();
		final Set keys = properties.keySet();

		final LinkedList patternList = new LinkedList();
		final Iterator keysIterator = keys.iterator();
		while (keysIterator.hasNext()) {
			final Object next = keysIterator.next();
			if (next == null)
				continue;
			if (next.toString().startsWith(keyPrefix)) {
				String catchedKey = next.toString();
				Object value = properties.get(catchedKey);
				if (value != null) {
					patternList.add(value);
				}
			}
		}
		return patternList;
	}

	public static QuickAssistAssistant quickAssistAssistant;

	private IOConsolePage ioConsolePage;
	private IOConsole ioConsole;

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void init(IPageBookViewPage page, IConsole console) {
		if (!(console instanceof IOConsole) || !(page instanceof IOConsolePage)) {
			return;
		}
		this.ioConsole = (IOConsole) console;
		this.ioConsolePage = (IOConsolePage) page;
		final TextConsoleViewer viewer = this.ioConsolePage.getViewer();

		BundleContext bundleContext = FrameworkUtil.getBundle(getClass())
				.getBundleContext();

		commandTracker = new _CommandServiceTracker(bundleContext);
		bundleTracker = new _BundleServiceTracker(bundleContext);
		assistant = new _QuickAssistAssistant();

		SourceViewerConfiguration configuration = assistant.get();
		viewer.configure(configuration);
	}

	public void dispose() {
		this.ioConsole = null;
		this.ioConsolePage = null;
	}

	public void activated() {
		boolean pageExist_AND_historyHandleIsNull = (this.ioConsolePage != null);

		lazyInit(pageExist_AND_historyHandleIsNull);
	}

	public void deactivated() {
	}

	private void lazyInit(final boolean initIfTrue) {
		if (initIfTrue) {
			StyledText textWiget = (StyledText) this.ioConsolePage.getControl();
			TMP = textWiget;

			InputLineListener listener = new InputLineListener(textWiget);
			listener.setCommandTracker(this.commandTracker);
			listener.setBundleTracker(this.bundleTracker);
			listener.setContentAssist(this.assistant);
			
			textWiget.addKeyListener(listener);
		}
	}

	private _CommandServiceTracker commandTracker;

	private _BundleServiceTracker bundleTracker;

	private _QuickAssistAssistant assistant;


}
