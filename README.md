de.jwausle.support.org.eclipse.ui.console
========================================

Featured IOConsole with history via keys ( 'ARROW_UP' , 'ARROW_DOWN').

Featured TextConsole enable/disable LineWrapping via command ('linewrap -enable' (default), 'linewrap --disable')


## Installation:
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

## Features

**[Ctrl]+[Space]**
> ![Screenshot-ctrl-space.png](https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/raw/master/img/Screenshot-ctrl-space.png)

**[Alt]+[Space]**
> ![Screenshot-alt-space.png](https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/raw/master/img/Screenshot-alt-space.png)

**[Arrow Up]** 
> iterate backward through the history

**[Arrow Down]** 
> iterate forward through the history


## Commands

**[linewrap]**
> ![Screenshot-linewrap.png](https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/raw/master/img/Screenshot-linewrap.png)

**[history]**
> ![Screenshot-history.png](https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/raw/master/img/Screenshot-history.png)


## Contribute
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
