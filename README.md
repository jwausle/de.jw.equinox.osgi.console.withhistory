de.jw.equinox.osgi.console.withhistory
======================================

Enhancement for org.eclipse.pde.ui.internal.util.OSGiConsole ( Host OSGi Console) with command history


HOWTO-install:
- install via update site:
- install vial local-update-site: 

HOWTO-contribute
- contact me: jan.winter.leipzig@gmail.com
- git clone ...


bundle-info: de.jw.equinox.osgi.console.withhistory.fragment
- Contains implementation to react of Key-Events (arrow-up, arrow-down)
- Some parts of code have to copied from org.eclipse.pde.ui.internal.console.OSGiConsole 

bundle-info: de.jw.equinox.osgi.console.withhistory.activator
- contains the activation logic 
- register/extend eclipse.extensionpoint (...) 
- as a combination of (org.eclipse.ui.IStartup, org.osgi.util.tracker.BundleTracker)

bundle-info: de.jw.equinox.osgi.console.withhistory.feature
- eclipse-pde-feature 

bundle-info: de.jw.equinox.osgi.console.withhistory.updatesite
- last successful tested update site (for indigo and juno)
