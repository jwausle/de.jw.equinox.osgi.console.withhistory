de.jwausle.support.org.eclipse.ui.console
========================================

Featured IOConsole with history via keys ( 'ARROW_UP' , 'ARROW_DOWN').

Featured TextConsole enable/disable LineWrapping via command ('linewrap -enable' (default), 'linewrap --disable')


## Installation:
- update-site: 

```
https://github.com/jwausle/de.jwausle.support.org.eclipse.ui.console/raw/master/de.jwausle.support.org.eclipse.ui.console.updatesite/update-site/3.0.0
```

- after install on mac 'eclipse -clean' start requiered 

OR

```
download: https://github.com/jwausle/de.jwausle.support.org.eclipse.ui.console/archive/master.zip
unzip: master.zip
eclipse: install as local-update site 'file:/.../de.jw.equinox.osgi.console.withhistory/de.jw.support.org.eclipse.pde.ui.updatesite/updatesite/3.0.0'
eclipse: restart 
```

## Features

**[Ctrl]+[Space]** ... quickfix for osgi-commands
> ![Screenshot-ctrl-space.png](https://github.com/jwausle/de.jw.equinox.osgi.console.withhistory/raw/master/img/Screenshot-ctrl-space.png)

**[Alt]+[Space]** ... quickfix for installed bundles
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

**[cls]** ... clear console

**[logger] ... set log-level (default=OFF)

## Contribute

download https://wiki.eclipse.org/Eclipse_Installer (oomph)

```
unzip+start Eclipse_Installer
```

setup 'Eclipse-IDE' (next)

... TODO screenshot

edit '.eclipse/org.eclipse.oomph.setup/setups/com.github.projects.setup'

```
<?xml version="1.0" encoding="UTF-8"?>
<setup:Project
    xmi:version="2.0"
    xmlns:xmi="http://www.omg.org/XMI"
    xmlns:setup="http://www.eclipse.org/oomph/setup/1.0"
    name="user.project"
    label="&lt;User>">
  <project href="https://github.com/jwausle/de.jwausle.support.org.eclipse.ui.console/raw/master/de.jwausle.support.org.eclipse.ui.console.oomph/oomph-dev.setup#/"/>
  <description>A container project for local user projects that are virtual members of the Github.com project catalog</description>
</setup:Project>
```

setup 'Eclispe workspace'

... TODO screenshot



**Bundle-info**: de.jwausle.support.org.eclipse.ui.console.fragment
- contains implementation to react of Key-Events (arrow-up, arrow-down)
- some parts of code have to copied from org.eclipse.pde.ui.internal.console.OSGiConsole 

**Bundle-info**: de.jwausle.support.org.eclipse.ui.console.feature
- eclipse-pde-feature 

**Bundle-info**: de.jwausle.support.org.eclipse.ui.console.updatesite
- last successful tested update site (for indigo and juno and luna)
