package de.jw.equinox.osgi.console.withhistory.activator;

import java.io.ByteArrayInputStream;

import org.eclipse.core.internal.registry.ExtensionRegistry;
import org.eclipse.core.runtime.ContributorFactoryOSGi;
import org.eclipse.core.runtime.IContributor;
import org.eclipse.core.runtime.IExtensionRegistry;
import org.eclipse.core.runtime.Platform;
import org.osgi.framework.Bundle;

public class OSGiConsoleWithHistoryPluginXmlRegistration {

	public static void addExtension(Bundle bundle, String xmlsrc) throws Exception {
		// use Eclipse Dynamic Extension API
		IExtensionRegistry reg = Platform.getExtensionRegistry();
	
		Object key = ((ExtensionRegistry) reg).getTemporaryUserToken();
	
		IContributor contributor = ContributorFactoryOSGi
				.createContributor(bundle);
	
		ByteArrayInputStream is = new ByteArrayInputStream(xmlsrc.getBytes());
	
		if (!reg.addContribution(is, contributor, false, null, null, key)) {
	
			throw new Exception("Error while adding contribution: " + xmlsrc);
		}
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
			addExtension(bundle, osgiConsoleWithHistoryExtension_consoleFactories);
			addExtension(bundle,
					osgiConsoleWithHistoryExtension_consolePageParticipants);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
