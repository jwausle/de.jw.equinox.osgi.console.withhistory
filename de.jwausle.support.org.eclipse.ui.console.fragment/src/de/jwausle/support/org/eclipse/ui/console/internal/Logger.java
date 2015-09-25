package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Hashtable;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.TreeMap;

import org.apache.felix.service.command.Descriptor;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;
import org.osgi.service.log.LogService;

import com.ibm.icu.text.MessageFormat;

/**
 * Use #MessageFormat. E.g.: log.info("log line {0},{1}, "0-param", "1-param");
 * 
 * @author winter
 *
 */
public class Logger {
	private static final Map<String, Integer> config = Collections
			.synchronizedMap(new TreeMap<String, Integer>());

	public static Logger getLogger(Class<?> clazz) {
		return new Logger(clazz);
	}

	private final Class<?> clazz;

	private Logger(final Class<?> clazz) {
		this.clazz = clazz;
	}

	public void error(String string, Object... args) {
		if (!isErrorable())
			return;
		syserr("ERROR " + string + " [" + clazz + "]", args);
	}

	public boolean isErrorable() {
		if (config.isEmpty())
			return false;
		if (config.containsKey("--silent")) {
			return false;
		}
		if (config.containsKey("--verbose")) {
			return true;
		}
		String logger = existLoggerConfig(clazz.getName());
		if (!"".equals(logger)) {
			Integer integer = config.get(logger);
			return integer >= LogService.LOG_ERROR;
		}
		if (config.containsKey("log.level")) {
			Integer integer = config.get("log.level");
			return integer >= LogService.LOG_ERROR;
		}

		return false;
	}

	private String existLoggerConfig(String name) {
		Set<String> logger = config.keySet();
		for (String log : logger) {
			if (name.startsWith(log)) {
				return log;
			}
		}
		return "";
	}

	public void warn(String string, Object... args) {
		if (!isWarnable())
			return;
		syserr("WARN  " + string + " [" + clazz + "]", args);
	}

	public boolean isWarnable() {
		if (config.isEmpty())
			return false;
		if (config.containsKey("--silent")) {
			return false;
		}
		if (config.containsKey("--verbose")) {
			return true;
		}
		String logger = existLoggerConfig(clazz.getName());
		if (!"".equals(logger)) {
			Integer integer = config.get(logger);
			return integer >= LogService.LOG_WARNING;
		}
		if (config.containsKey("log.level")) {
			Integer integer = config.get("log.level");
			return integer >= LogService.LOG_WARNING;
		}
		return false;
	}

	public void info(String string, Object... args) {
		if (!isInfonable())
			return;
		syserr("INFO  " + string + " [" + clazz + "]", args);
	}

	public boolean isInfonable() {
		if (config.isEmpty())
			return false;
		if (config.containsKey("--silent")) {
			return false;
		}
		if (config.containsKey("--verbose")) {
			return true;
		}
		String logger = existLoggerConfig(clazz.getName());
		if (!"".equals(logger)) {
			Integer integer = config.get(logger);
			return integer >= LogService.LOG_INFO;
		}
		if (config.containsKey("log.level")) {
			Integer integer = config.get("log.level");
			return integer >= LogService.LOG_INFO;
		}

		return false;
	}

	public void debug(String string, Object... args) {
		if (!isDebugable())
			return;
		syserr("DEBUG " + string + " [" + clazz + "]", args);
	}

	public boolean isDebugable() {
		if (config.isEmpty())
			return false;
		if (config.containsKey("--silent")) {
			return false;
		}
		if (config.containsKey("--verbose")) {
			return true;
		}
		String logger = existLoggerConfig(clazz.getName());
		if (!"".equals(logger)) {
			Integer integer = config.get(logger);
			return integer >= LogService.LOG_DEBUG;
		}
		if (config.containsKey("log.level")) {
			Integer integer = config.get("log.level");
			return integer >= LogService.LOG_DEBUG;
		}
		return false;
	}

	@SuppressWarnings("unused")
	private static void sysout(String string, Object... args) {
		String format = format(string, args);
		System.out.println(format);
	}

	private static String format(String string, Object... args) {
		return MessageFormat.format(string, args);
	}

	private static void syserr(String string, Object... args) {
		String msg = format(string, args);
//		java.util.logging.Logger.getLogger("").severe(msg);
		System.err.println(msg);
	}

	public static void registerGogoCommand() {
		Bundle bundle = FrameworkUtil.getBundle(Logger.class);
		if (bundle == null) {
			return;
		}
		BundleContext context = bundle.getBundleContext();
		if (context == null)
			return;

		Hashtable<String, String> cmdDesc = new Hashtable<String, String>();
		cmdDesc.put("osgi.command.function", "logger");
		cmdDesc.put("osgi.command.scope", "jwausle");
		context.registerService(Logger.class, new Logger(Logger.class), cmdDesc);

		// set default log.level=INFO (1=ERROR, 2=WARN, 3=IFO, 4=DEBUG)
		config.put("log.level", 1);
	}

	@Descriptor("[<logger>=<level> | --verbose | --silent | --clear | --show-all | --logger]")
	public String logger(@Descriptor("...\n\t"//
			+ "<logger>=<level>  \t: show short history.\n\t"//
			+ "--verbose  \t: set to verbose( and not --silent).\n\t"//
			+ "--silent   \t: set to silent (and not --verbose).\n\t"//
			+ "--clear    \t: clear the complete config (not recoverable).\n\t"//
			+ "--show-all \t: show the actual config.\n\t"//
	) String... args) {
		String help = "Usage: 'logger [<logger>=<level> | --verbose | --silent | --clear | --show-all]'";
		if (args.length == 0) {
			return help;
		}

		if (Arrays.binarySearch(args, "--help") >= 0) {
			return help;
		}

		if (Arrays.binarySearch(args, "--show-all") >= 0) {
			return config.toString().replace(", ", ",\n ");
		}

		if (Arrays.binarySearch(args, "--clear") >= 0) {
			System.out.println("Clear logger config:\n"
					+ config.toString().replace(", ", ",\n "));
			return "... cleared.";
		}

		if (Arrays.binarySearch(args, "--logger") >= 0) {
			Bundle bundle = FrameworkUtil.getBundle(getClass());
			BundleWiring wiring = bundle.adapt(BundleWiring.class);
			Collection<String> classes = wiring.listResources("/de/jwausle/",
					"*.class", BundleWiring.LISTRESOURCES_LOCAL
							| BundleWiring.LISTRESOURCES_RECURSE);
			List<String> noSubclasses = new LinkedList<String>();
			for (String string : classes) {
				if (string.contains("$")) {
					continue;
				}
				noSubclasses.add(string);
			}
			String classesString = noSubclasses//
					.toString()//
					.replace(".class", "")//
					.replace("/", ".")//
					.replace(", ", ",\n ");
			return classesString;
		}

		if (Arrays.binarySearch(args, "--verbose") >= 0) {
			config.put("--verbose", 5);
			config.remove("--silent");
			return "Setted logger to --verbose.";
		}

		if (Arrays.binarySearch(args, "--silent") >= 0) {
			config.put("--silent", -1);
			config.remove("--verbose");
			return "Setted logger to --silent.";
		}

		if (!args[0].contains("=")) {
			return "Unknown args " + Arrays.toString(args) + ". " + help;
		}

		String[] split = args[0].split("=");
		switch (split.length) {
		case 0:
			return "logger [<logger>=<level>]";
		case 1:
			return "Configed logger '" + split[0] + "=DEBUG";
		default:
			String logger = split[0];
			Integer level = parseLevel(split[1]);
			if (level < 0)
				config.remove(logger);
			else
				config.put(logger, level);
			return "Configed logger: " + Arrays.toString(args);
		}
	}

	/**
	 * 
	 * @param string
	 * @return
	 */
	private Integer parseLevel(String string) {
		try {
			Integer valueOf = Integer.valueOf(string);
			return valueOf;
		} catch (NumberFormatException e) {
			// ignore;
		}
		if ("OFF".equals(string.toUpperCase()))
			return -1;
		if ("ERROR".equals(string.toUpperCase()))
			return 0;
		if ("WARN".equals(string.toUpperCase()))
			return 1;
		if ("INFO".equals(string.toUpperCase()))
			return 2;
		if ("DEBUG".equals(string.toUpperCase()))
			return 3;
		return /* ALL */4;
	}

}
