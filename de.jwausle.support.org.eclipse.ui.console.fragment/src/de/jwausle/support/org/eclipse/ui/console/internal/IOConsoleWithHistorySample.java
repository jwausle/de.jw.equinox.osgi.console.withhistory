/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.console.IOConsole;
import org.eclipse.ui.part.IPageBookViewPage;

/**
 * Try to clone: org.eclipse.pde.internal.ui.util.OSGiConsole <br />
 * - OSGiConsoleFactory (as extension for point=org.eclipse.ui.console.consoleFactories) <br />
 * - OSGiConsolePageParticipant (as for point=org.eclipse.ui.console.consolePageParticipants) <br />
 * 
 * @author wij1si
 */
public final class IOConsoleWithHistorySample extends IOConsole {

  private IPageBookViewPage ioConsolePage;
  private HistoryHandle historyHandle;

  /** Default constructor */
  public IOConsoleWithHistorySample() {
    super(IOConsoleWithHistorySample.class.getSimpleName(), null);
  }

  /**
   * {@inheritDoc}
   */
  public IPageBookViewPage createPage(final IConsoleView view) {
    this.ioConsolePage = super.createPage(view);
    return this.ioConsolePage;
  }

  /**
   * {@inheritDoc}
   */
  public void addPropertyChangeListener(final IPropertyChangeListener listener) {
    super.addPropertyChangeListener(listener);

    boolean pageExist_AND_historyHandleIsNull = (this.ioConsolePage != null) && (this.historyHandle == null);

    lazyInit(pageExist_AND_historyHandleIsNull);
  }

  private void lazyInit(final boolean initIfTrue) {
    if (initIfTrue) {
      StyledText textWiget = (StyledText) this.ioConsolePage.getControl();

      this.historyHandle = new HistoryHandle(this, textWiget);
      textWiget.addKeyListener(this.historyHandle);
    }
  }

  /**
   * {@inheritDoc}
   */
  public void clearConsole() {
    super.clearConsole();

    if (this.historyHandle != null) {
      this.historyHandle.clearConsole();
    }
  }


}