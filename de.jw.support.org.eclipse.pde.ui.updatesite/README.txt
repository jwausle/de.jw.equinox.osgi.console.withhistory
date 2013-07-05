# txt

-- HOWTO recompile --

delete 
 - features/
 - plugins/
 - artifact.jar
 - content.jar
 
edit site.xml 
 - replace 'last-build-version-part' with 'qualifier'
 - sample-from: <feature url="features/de.jw.support.org.eclipse.pde.ui.feature_1.0.0.201307051412.jar" id="de.jw.support.org.eclipse.pde.ui.feature" version="1.0.0.201307051412">
 - sample-to  : <feature url="features/de.jw.support.org.eclipse.pde.ui.feature_1.0.0.qualifier.jar" id="de.jw.support.org.eclipse.pde.ui.feature" version="1.0.0.qualifier">

build All again => recreate
 - features/
 - plugins/
 - artifact.jar
 - content.jar
