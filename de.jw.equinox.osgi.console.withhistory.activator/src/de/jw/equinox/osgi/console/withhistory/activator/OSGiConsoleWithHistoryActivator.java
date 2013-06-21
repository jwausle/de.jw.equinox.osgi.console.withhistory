package de.jw.equinox.osgi.console.withhistory.activator;


import java.util.logging.Logger;

import org.eclipse.ui.IStartup;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

/**
 * The activator class controls the plug-in life cycle
 */
public class OSGiConsoleWithHistoryActivator implements BundleActivator, IStartup{
	private final Logger log = Logger.getLogger(OSGiConsoleWithHistoryActivator.class.getName());
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		log.info("OSGiConsoleWithHistoryActivator starting:" + context.getBundle().getVersion());
		
		int stateMask = BundleEvent.INSTALLED| BundleEvent.LAZY_ACTIVATION | BundleEvent.RESOLVED| BundleEvent.STARTED| BundleEvent.STARTING| BundleEvent.STOPPED| BundleEvent.STOPPING| BundleEvent.UNINSTALLED| BundleEvent.UNRESOLVED | BundleEvent.UPDATED;
		OSGiConsoleWithHistoryBundleTracker osGiConsoleWithHistoryBundleTracker = new OSGiConsoleWithHistoryBundleTracker(context, stateMask, null);
		osGiConsoleWithHistoryBundleTracker.open();
		log.fine("OSGiConsoleWithHistoryActivator started bundle tracker.");
	}

	public void stop(BundleContext context) throws Exception {
		log.info("OSGiConsoleWithHistoryActivator do nothing while stopping bundle.");
	}

	public void earlyStartup() {
		log.info("OSGiConsoleWithHistoryActivator early startup.");
	}
}
