# !txt
======================================
 HOWTO recompile with new version
--------------------------------------

1) Udpate version:
- ${de.jwausle.support.org.eclipse.ui.console.fragment}/META-INF/MANIFEST.MF.Bundle-Version
- ${de.jwausle.support.org.eclipse.ui.console.feature}/META-INF/MANIFEST.MF.Bundle-Version
- ${de.jwausle.support.org.eclipse.ui.console.feature}/feature.xml
	- all related versions
- ${de.jwausle.support.org.eclipse.ui.console.update-site}/category.xml

2) Export 'de.jw.support.org.eclipse.ui.console.feature' as 'Deployable Feature'
- [Destination]/Directory: ${de.jwausle.support.org.eclipse.pde.ui.updatesite}/update-site/X.Y.Z
- [Options]/Catagorize Repository: ${de.jwausle.support.org.eclipse.pde.ui.updatesite}/category.xml 



