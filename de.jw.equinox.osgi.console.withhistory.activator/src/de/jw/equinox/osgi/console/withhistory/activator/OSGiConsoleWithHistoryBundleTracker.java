package de.jw.equinox.osgi.console.withhistory.activator;

import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleEvent;
import org.osgi.util.tracker.BundleTracker;
import org.osgi.util.tracker.BundleTrackerCustomizer;

public class OSGiConsoleWithHistoryBundleTracker extends BundleTracker{

	public OSGiConsoleWithHistoryBundleTracker(BundleContext context,
			int stateMask, BundleTrackerCustomizer customizer) {
		super(context, stateMask, customizer);
	}
	
	public Object addingBundle(Bundle bundle, BundleEvent event) {
		System.out.println("\t OSGiConsoleWithHistoryBundleTracker addingBundle: " + bundle.getSymbolicName() + ", event: " + event);
		if(bundle.getSymbolicName().equals("org.eclipse.pde.ui")){
			OSGiConsoleWithHistoryPluginXmlRegistration.register();
		}
		return bundle;
	}
	
	public void modifiedBundle(Bundle bundle, BundleEvent event,
			Object object) {
		System.out.println("\t OSGiConsoleWithHistoryBundleTracker modifyingBundle: " + bundle.getSymbolicName() + ", event: " + event);
		if(bundle.getSymbolicName().equals("org.eclipse.pde.ui")){
			OSGiConsoleWithHistoryPluginXmlRegistration.register();
		}
	}

}
