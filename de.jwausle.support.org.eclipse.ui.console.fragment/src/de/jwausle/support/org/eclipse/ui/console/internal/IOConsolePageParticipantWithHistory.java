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
	private HistoryHandle ioConsoleHistoryHandle;
	private IOConsole ioConsole;
	private static Map<String, String> commandMap = Collections
			.synchronizedMap(new LinkedHashMap<String, String>());

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
			TMP = textWiget;

			InputLineListener listener = new InputLineListener(this.ioConsole,
					textWiget);
			listener.setCommandTracker(this.commandTracker);
			listener.setBundleTracker(this.bundleTracker);
			listener.setContentAssist(this.assistant);
			// textWiget.addKeyListener(listener);
			if ("".isEmpty()) {
				// textWiget.getDisplay().addFilter(SWT.KeyDown, listener);
//				textWiget.addVerifyKeyListener(listener);
				textWiget.addKeyListener(listener);
			}

			// this.ioConsoleHistoryHandle = new HistoryHandle(this.ioConsole,
			// textWiget);
			// textWiget.addKeyListener(this.ioConsoleHistoryHandle);
			//
			// this.ioConsole.addPatternMatchListener(new
			// HistoryHandlePatternMatchListener(ioConsole,
			// ioConsoleHistoryHandle,"osgi>(.*)(\n[^\n]|\r[^\r])"));
			// this.ioConsole.addPatternMatchListener(new
			// HistoryHandlePatternMatchListener(ioConsole,
			// ioConsoleHistoryHandle,"g!(.*)(\n[^\n]|\r[^\r])"));
			//
			// List userPattern = getUserPattern();
			// for (int i=0; i<userPattern.size();i++) {
			// String pattern = userPattern.get(i).toString();
			// this.ioConsole.addPatternMatchListener(new
			// HistoryHandlePatternMatchListener(ioConsole,
			// ioConsoleHistoryHandle,pattern ));
			// }
		}
	}

	private List getUserPattern() {
		String keyPrefix = IOCONSOLE_HISTORY_PATTERN;
		List userPattern = getUserPattern(keyPrefix);
		return userPattern;
	}

	@SuppressWarnings({ "unused", "unchecked", "rawtypes" })
	private ServiceTracker trackOSGiCommands(final BundleContext context)
			throws InvalidSyntaxException {
		Filter filter = context.createFilter("(&("
				+ CommandProcessor.COMMAND_SCOPE + "=*)("
				+ CommandProcessor.COMMAND_FUNCTION + "=*))");

		return new ServiceTracker(context, filter, null) {
			@Override
			public Object addingService(ServiceReference reference) {
				Object scope = reference
						.getProperty(CommandProcessor.COMMAND_SCOPE);
				Object function = reference
						.getProperty(CommandProcessor.COMMAND_FUNCTION);
				Map<String, String> commands = new LinkedHashMap<String, String>();

				if (scope != null && function != null) {
					if (function.getClass().isArray()) {
						for (Object f : ((Object[]) function)) {
							commands.put(scope + ":" + f.toString(),
									"no help for " + f.toString());
						}
					} else {
						commands.put(scope + ":" + function.toString(),
								"no help for " + function.toString());
					}
					commandMap.putAll(commands);
					return commands;
				}
				return null;
			}

			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				Object scope = reference
						.getProperty(CommandProcessor.COMMAND_SCOPE);
				Object function = reference
						.getProperty(CommandProcessor.COMMAND_FUNCTION);

				if (scope != null && function != null) {
					if (!function.getClass().isArray()) {
						commandMap.remove(scope.toString() + ":"
								+ function.toString());
					} else {
						for (Object func : (Object[]) function) {
							commandMap.remove(scope.toString() + ":"
									+ func.toString());
						}
					}
				}

				super.removedService(reference, service);
			}
		};
	}

	private Thread thread;

	private _CommandServiceTracker commandTracker;

	private _BundleServiceTracker bundleTracker;

	private _QuickAssistAssistant assistant;

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ServiceTracker processorTracker(BundleContext context) {
		ServiceTracker t = new ServiceTracker(context,
				CommandProcessor.class.getName(), null) {

			@Override
			public Object addingService(ServiceReference reference) {
				CommandProcessor processor = (CommandProcessor) super
						.addingService(reference);
				startShell(context, processor);
				return processor;
			}

			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				if (thread != null) {
					thread.interrupt();
				}
				super.removedService(reference, service);
			}
		};

		return t;
	}

	private void startShell(BundleContext context, CommandProcessor processor) {
		Set<Entry<String, String>> entrySet = this.commandMap.entrySet();
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (Entry<String, String> entry : entrySet) {
			InputStream in = System.in;
			PrintStream err = System.err;

			Path file = null;
			try {
				file = Files.createTempFile("system", "out");
				PrintStream out = new PrintStream(file.toFile());

				CommandSession session = processor.createSession(in, out, err);
				session.execute("type " + entry.getKey());
				out.append("\nHelp:\n");
				session.execute("help " + entry.getKey());

				String help = new String(Files.readAllBytes(file));
				map.put(entry.getKey(), help);

			} catch (Exception e) {
				map.put(entry.getKey(), e.getMessage());
			} finally {
				if (file != null) {
					file.toFile().delete();
				}
			}
		}
		synchronized (this.commandMap) {
			this.commandMap.putAll(map);
		}

	}

}
