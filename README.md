de.jwausle.support.org.eclipse.ui.console
========================================

Featured IOConsole with history via keys ( 'ARROW_UP' , 'ARROW_DOWN').

Featured TextConsole enable/disable LineWrapping via command ('enableLineWrapping' (default), 'disableLineWrapping')


## HOWTO-install:
- update-site: 

```
https://github.com/jwausle/de.jwausle.support.org.eclipse.ui.console/raw/master/de.jwausle.support.org.eclipse.ui.console.updatesite/update-site/2.1.0
```

- after install on mac 'eclipse -clean' start requiered 

OR

```
download: https://github.com/jwausle/de.jwausle.support.org.eclipse.ui.console/archive/master.zip
unzip: master.zip
eclipse: install as local-update site 'file:/.../de.jw.equinox.osgi.console.withhistory/de.jw.support.org.eclipse.pde.ui.updatesite'
eclipse: restart 
```

![show console/select-first-arrow-down-from-console-toolbar and find 'Host OSGiConsoleWithHistory' beside 'Host OSGiConsole'](https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/raw/master/img/screenshot-successful-installation2.png)

## HOWTO use:

- open Console
- edit command into IOConsole 'return'
- arrow-up  -> get last command (or nothing) 
- arrow-down  -> get next command (or nothing) 

Tested
- for 'Host OSGi Console'
- for 'Bndtools Process Console' : http://bndtools.org/

## HOWTO extend:

- use multiple -Dioconsole.history.pattern.*='pattern'
- I parse for System.properties with prefix 'ioconsole.history.pattern'
- 'pattern' : normal java.util.RegExp
  - group(1) will interprete as command
  - sample-pattern: 'osgi>(.*)(\n[^\n]|\r[^\r])' for 'Host OSGi Console'
  - sample-pattern-sample: 'osgi> ss | grep de.jwausle [return]'


## HOWTO-contribute
- contact me: jan.winter.leipzig+git@gmail.com

```
git clone https://github.com/jwausle/de.jwausle.support.org.eclipse.ui.console.git
```


**Bundle-info**: de.jwausle.support.org.eclipse.ui.console.fragment
- contains implementation to react of Key-Events (arrow-up, arrow-down)
- some parts of code have to copied from org.eclipse.pde.ui.internal.console.OSGiConsole 

**Bundle-info**: de.jwausle.support.org.eclipse.ui.console.feature
- eclipse-pde-feature 

**Bundle-info**: de.jwausle.support.org.eclipse.ui.console.updatesite
- last successful tested update site (for indigo and juno and luna)
