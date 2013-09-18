de.jwausle.support.org.eclipse.ui.console
========================================

Featured IOConsole with history via keys ( 'ARROW_UP' , 'ARROW_DOWN').


## HOWTO-install:
- update-site: 

```
https://github.com/jwausle/de.jwausle.support.org.eclipse.ui.console/raw/master/de.jwausle.support.org.eclipse.ui.console.updatesite/update-site/2.0.0
```

- after install on mac 'eclipse -clean' start requiered 

OR

```
download: https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/archive/master.zip
unzip: master.zip
eclipse: install as local-update site 'file:/.../de.jw.equinox.osgi.console.withhistory/de.jw.support.org.eclipse.pde.ui.updatesite'
eclipse: restart 
```

![show console/select-first-arrow-down-from-console-toolbar and find 'Host OSGiConsoleWithHistory' beside 'Host OSGiConsole'](https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/raw/master/img/screenshot-successful-installation.png)

## HOWTO-contribute
- contact me: jan.winter.leipzig+git@gmail.com

```
git clone https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory.git
```


**Bundle-info**: de.jw.support.org.eclipse.pde.ui.fragment
- contains implementation to react of Key-Events (arrow-up, arrow-down)
- some parts of code have to copied from org.eclipse.pde.ui.internal.console.OSGiConsole 

**Bundle-info**: de.jw.support.org.eclipse.pde.ui.feature
- eclipse-pde-feature 

**Bundle-info**: de.jw.support.org.eclipse.pde.ui.updatesite
- last successful tested update site (for indigo and juno)
- to recompile: delete all(without site.xml), edit site.xml(replace version feature_x.x.x.bla with feature_x.x.x.qualifier), build
