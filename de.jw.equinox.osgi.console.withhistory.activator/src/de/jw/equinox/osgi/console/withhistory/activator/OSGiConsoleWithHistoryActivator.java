package de.jw.equinox.osgi.console.withhistory.activator;


import org.eclipse.ui.IStartup;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;

/**
 * The activator class controls the plug-in life cycle
 */
public class OSGiConsoleWithHistoryActivator implements BundleActivator, IStartup{
	/*
	 * (non-Javadoc)
	 * 
	 * @see
	 * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
	 * )
	 */
	public void start(BundleContext context) throws Exception {
		System.out.println("\t OSGiConsoleWithHistoryActivator start:" + context.getBundle().getVersion());
		
//		int stateMask = Bundle.STARTING | Bundle.STOPPING | Bundle.RESOLVED | Bundle.INSTALLED | Bundle.UNINSTALLED;
		int stateMask = BundleEvent.INSTALLED| BundleEvent.LAZY_ACTIVATION | BundleEvent.RESOLVED| BundleEvent.STARTED| BundleEvent.STARTING| BundleEvent.STOPPED| BundleEvent.STOPPING| BundleEvent.UNINSTALLED| BundleEvent.UNRESOLVED | BundleEvent.UPDATED;
		OSGiConsoleWithHistoryBundleTracker osGiConsoleWithHistoryBundleTracker = new OSGiConsoleWithHistoryBundleTracker(context, stateMask, null);
		osGiConsoleWithHistoryBundleTracker.open();
	}

	public void stop(BundleContext context) throws Exception {
		// doNothing.
	}

	public void earlyStartup() {
		System.out.println("\t OSGiConsoleWithHistoryActivator early startup.");
	}
}
