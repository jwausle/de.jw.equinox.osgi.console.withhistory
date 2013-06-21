package de.jw.equinox.osgi.console.withhistory.activator;

import java.io.ByteArrayInputStream;
import java.util.Arrays;
import java.util.logging.Logger;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class OSGiConsoleWithHistoryPluginXmlRegistration {
	private static final Logger log = Logger
			.getLogger(OSGiConsoleWithHistoryPluginXmlRegistration.class
					.getName());

	public static void addExtension(Bundle bundle, String xml) throws Exception {
		// use Eclipse Dynamic Extension API
		IExtensionRegistry registry = Platform.getExtensionRegistry();
		log.fine("Got exension registry: " + registry);

		Object registryUserToken = ((ExtensionRegistry) registry)
				.getTemporaryUserToken();
		log.fine("Got exension registry/temporaryUserToken: "
				+ registryUserToken);

		IContributor contributor = ContributorFactoryOSGi
				.createContributor(bundle);
		log.fine("Created OSGiContributor: '" + contributor + "' for bundle: "
				+ bundle);

		ByteArrayInputStream xmlByteStream = new ByteArrayInputStream(xml.getBytes());
		log.finer("Created xml byte stream: " + xmlByteStream);

		if (!registry.addContribution(xmlByteStream, contributor, false, null, null,
				registryUserToken)) {

			throw new Exception("Error while adding contribution: " + xml);
		}
		log.info("Registered extension: " + xml);
	}

	public static String osgiConsoleWithHistoryExtension_consoleFactories = "<plugin><extension point=\"org.eclipse.ui.console.consoleFactories\"> "
			+ "<consoleFactory "
			+ "class=\"org.eclipse.pde.internal.ui.util.OSGiConsoleWithHistoryFactory\" "
			+ "icon=\"$nl$/icons/eview16/osgiconsole.gif\" "
			+ "label=\"Host OSGi ConsoleWithHistory\"> "
			+ "</consoleFactory> "
			+ "</extension></plugin>";
	public static String osgiConsoleWithHistoryExtension_consolePageParticipants = "<plugin>"
			+ "<extension point=\"org.eclipse.ui.console.consolePageParticipants\"> "
			+ "<consolePageParticipant "
			+ "class=\"org.eclipse.pde.internal.ui.util.OSGiConsoleWithHistoryPageParticipant\" "
			+ "id=\"org.eclipse.pde.ui.OSGiConsoleParticipant2\"> "
			+ "<enablement> "
			+ "<test property=\"org.eclipse.ui.console.consoleTypeTest\" value=\"osgiConsole\"/> "
			+ "</enablement> "
			+ "</consolePageParticipant> "
			+ "</extension>"
			+ "</plugin>";

	public static void register() {

		Bundle org_eclipse_pde_ui = Platform.getBundle("org.eclipse.pde.ui");
		Bundle bundle = org_eclipse_pde_ui;
		try {
			addExtension(bundle,
					osgiConsoleWithHistoryExtension_consoleFactories);
			addExtension(bundle,
					osgiConsoleWithHistoryExtension_consolePageParticipants);
		} catch (Exception e) {
			log.severe(e.getMessage() + ", stacktrace:\n"
					+ Arrays.toString(e.getStackTrace()));
		}
	}

}
