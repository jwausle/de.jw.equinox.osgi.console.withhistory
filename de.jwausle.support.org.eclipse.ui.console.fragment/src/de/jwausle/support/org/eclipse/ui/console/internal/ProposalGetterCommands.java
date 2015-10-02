package de.jwausle.support.org.eclipse.ui.console.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.felix.service.command.CommandProcessor;
import org.apache.felix.service.command.CommandSession;
import org.apache.felix.service.command.Descriptor;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Filter;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;

public class ProposalGetterCommands implements ProposalGetter {
	private final Logger log = Logger.getLogger(ProposalGetterCommands.class);
	private Map<String, String> commandMap = new LinkedHashMap<String, String>();
	private CommandProcessor processor = null;

	public ProposalGetterCommands() {
		this(FrameworkUtil.getBundle(ProposalGetterCommands.class)
				.getBundleContext());
	}

	public ProposalGetterCommands(BundleContext bundleContext) {
		try {
			@SuppressWarnings("rawtypes")
			ServiceTracker serviceTracker = trackOSGiCommands(bundleContext);
			serviceTracker.open();

			@SuppressWarnings("rawtypes")
			ServiceTracker processorTracker = processorTracker(bundleContext);
			processorTracker.open();
			
			registerGogoCommand(bundleContext);
		} catch (InvalidSyntaxException e) {
			log.error(e.getMessage());
		}
	}

	public ICompletionProposal[] getCompletionProposal(String filter,
			CommandWriteCallback writer) {
		ICompletionProposal[] _return;
		if (filter == null)
			_return = QuickAssistant
					.newICompletionProposals(commandMap, writer);
		else if (filter.isEmpty())
			_return = QuickAssistant
					.newICompletionProposals(commandMap, writer);
		else {
			Set<Entry<String, String>> entrySet = commandMap.entrySet();
			Map<String, String> commandMap2 = new LinkedHashMap<String, String>();
			for (Entry<String, String> entry : entrySet) {
				String command = QuickAssistant.commandWithoutScope(entry
						.getKey());
				// System.err.println("===> " + command + "," + filter);
				if (!command.startsWith(filter))
					continue;

				commandMap2.put(entry.getKey(), entry.getValue());
			}
			log.info("===> filtered map: " + commandMap2.keySet());
			_return = QuickAssistant.newICompletionProposals(commandMap2,
					writer);
		}
		log.info("===> callback cmd-proposals: {0}/{1} for filter=´{2}´",
				_return.length, commandMap.size(), filter.trim());
		return _return;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
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
							String cmd = scope + ":" + f.toString();
							String help = getHelp(cmd);

							commands.put(cmd, help);
						}
					} else {
						String cmd = scope + ":" + function.toString();
						String help = getHelp(cmd);

						commands.put(cmd, help);
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

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private ServiceTracker processorTracker(BundleContext context) {
		ServiceTracker t = new ServiceTracker(context,
				CommandProcessor.class.getName(), null) {

			@Override
			public Object addingService(ServiceReference reference) {
				ProposalGetterCommands.this.processor = (CommandProcessor) super
						.addingService(reference);

				helps(context, ProposalGetterCommands.this.processor);
				
				return ProposalGetterCommands.this.processor;
			}

			@Override
			public void removedService(ServiceReference reference,
					Object service) {
				ProposalGetterCommands.this.processor = null;
				if (thread != null) {
					thread.interrupt();
				}
				super.removedService(reference, service);
			}
		};

		return t;
	}

	private void helps(BundleContext context, CommandProcessor processor) {
		Set<Entry<String, String>> entrySet = this.commandMap.entrySet();
		Map<String, String> map = new LinkedHashMap<String, String>();
		for (Entry<String, String> entry : entrySet) {
			String help = null;
			help = help(processor, entry);
			map.put(entry.getKey(), help);
		}
		synchronized (this.commandMap) {
			this.commandMap.putAll(map);
		}
	}

	private String help(CommandProcessor processor, Entry<String, String> entry) {
		String key = entry.getKey();

		return help(processor, key);
	}

	private String help(CommandProcessor processor, String key) {
		if ("false".isEmpty())
			return "";
		String help;
		InputStream in = System.in;

		ByteArrayOutputStream stream = new ByteArrayOutputStream();
		PrintStream out = new PrintStream(stream);

		CommandSession session = processor.createSession(in, out, out);
		try {
			session.execute("type " + key);
			out.append("\n===== Help ====\n");
			session.execute("help " + key);

			help = stream.toString();
		} catch (Exception e) {
			help = e.getMessage() + "\n\n" + Arrays.toString(e.getStackTrace());
		} finally {
			session.close();
			try {
				stream.close();
			} catch (IOException e) {
				// ignore
			}
			out.close();
		}
		return help;
	}

	private String getHelp(String cmd) {
		if (this.processor == null)
			return "no help for " + cmd;
		return help(processor, cmd);
	}

	private void registerGogoCommand(BundleContext context) {
		Hashtable<String, Object> cmdDesc = new Hashtable<String, Object>();
		cmdDesc.put("osgi.command.function", new String[] { "showhelp" });
		cmdDesc.put("osgi.command.scope", "jwausle");
		context.registerService(ProposalGetterCommands.class, this, cmdDesc);
	}

	@Descriptor(" <command>")
	public String showhelp(
			@Descriptor("...\n"
					+ "Show help of command like [ctrl+space]\n\t"
					+ "<command>\t: command to show (e.g 'showhelp showhelp')\n")CommandSession session, String command) throws Exception {
		System.out.println("--------------------------------");		
		session.execute("help " + command);
		System.out.println("--------------------------------");
		
		CommandProcessor processor = this.processor;
		String help = help(processor,command);
		return help;
	}
}
