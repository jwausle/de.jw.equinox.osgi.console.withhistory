de.jw.equinox.osgi.console.withhistory
======================================

Enhancement for org.eclipse.pde.ui.internal.util.OSGiConsole ( Host OSGi Console) with command history

--------------------------------------
HOWTO-install:
- 

--------------------------------------
HOWTO-contribute
- contact me: jan.winter.leipzig@gmail.com
--------------------------------------
- git clone ...
- feel free to change 

bundle: ...
- Contains implementation to react of Key-Events (arrow-up, arrow-down)
- Some parts of code have to copied from org.eclipse.pde.ui.internal.console.OSGiConsole 

bundle: ...
- contains the activation logic 
- register/extend eclipse.extensionpoint (...) 
- as a combination of (org.eclipse.ui.IStartup, org.osgi.util.tracker.BundleTracker)

