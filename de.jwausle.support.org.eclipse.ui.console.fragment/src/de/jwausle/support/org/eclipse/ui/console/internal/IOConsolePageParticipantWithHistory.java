package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Hashtable;

import org.apache.felix.service.command.Descriptor;
import org.eclipse.jface.text.source.SourceViewerConfiguration;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.IConsolePageParticipant;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.console.TextConsoleViewer;
import org.eclipse.ui.internal.console.IOConsolePage;
import org.eclipse.ui.part.IPageBookViewPage;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;

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
@SuppressWarnings("rawtypes")
public class IOConsolePageParticipantWithHistory implements
		IConsolePageParticipant {

	public static final String IOCONSOLE_HISTORY_PATTERN = "ioconsole.history.pattern";

	private IOConsolePage ioConsolePage;

	private ProposalGetterCommands commandTracker;

	private ProposalGetterBundles bundleTracker;

	private QuickAssistant assistant;

	private StyledText textWiget;

	public Object getAdapter(Class adapter) {
		return null;
	}

	public void init(IPageBookViewPage page, IConsole console) {
		if (!(console instanceof IOConsole) || !(page instanceof IOConsolePage)) {
			return;
		}
		this.ioConsolePage = (IOConsolePage) page;
		final TextConsoleViewer viewer = this.ioConsolePage.getViewer();

		BundleContext bundleContext = FrameworkUtil.getBundle(getClass())
				.getBundleContext();

		commandTracker = new ProposalGetterCommands(bundleContext);
		bundleTracker = new ProposalGetterBundles(bundleContext);
		assistant = new QuickAssistant();

		SourceViewerConfiguration configuration = assistant.get();
		viewer.configure(configuration);

		registerGogoCommand();
	}

	public void dispose() {
		this.ioConsolePage = null;
		this.bundleTracker = null;
		this.commandTracker = null;
		this.assistant = null;
	}

	public void activated() {
		if ((this.ioConsolePage == null)) {
			return;
		}

		textWiget = (StyledText) this.ioConsolePage.getControl();

		IOConsoleListener listener = new IOConsoleListener(textWiget);
		listener.setCommandTracker(this.commandTracker);
		listener.setBundleTracker(this.bundleTracker);
		listener.setContentAssist(this.assistant);

		textWiget.addKeyListener(listener);
	}

	public void deactivated() {
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
		cmdDesc.put("osgi.command.function", "linewrap");
		cmdDesc.put("osgi.command.scope", "jwausle");
		context.registerService(IOConsolePageParticipantWithHistory.class,
				this, cmdDesc);
	}

	@Descriptor("[--disable/--enable] [--help]")
	public String linewrap(@Descriptor("...\n\t"
			+ "--help   \t: show this help.\n\t"
			+ "--disable\t: disble line console wrapping.\n\t"
			+ "--enable \t: enable console line wrapping.\n") String... args) {
		if (args.length == 0) {
			return "Usage: 'linewrap [--disable/--enable] [--help]'";
		}

		if ("--help".equals(args[0]) || "-h".equals(args[0])) {
			return "Usage: 'linewrap [--disable/--enable] [--help]'";
		}

		if ("--disable".equals(args[0])) {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					IOConsolePageParticipantWithHistory.this.textWiget
							.setWordWrap(false);
				}
			});
			return " --disable console linewrap";
		}

		if ("--enable".equals(args[0])) {
			Display.getDefault().asyncExec(new Runnable() {

				public void run() {
					IOConsolePageParticipantWithHistory.this.textWiget
							.setWordWrap(true);
				}
			});
			return " --enable console linewrap";
		}
		return "";
	}
}
