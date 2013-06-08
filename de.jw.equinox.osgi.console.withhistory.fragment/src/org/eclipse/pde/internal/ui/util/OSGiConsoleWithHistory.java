package org.eclipse.pde.internal.ui.util;
import org.eclipse.jface.util.IPropertyChangeListener;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.ui.console.IConsoleView;
import org.eclipse.ui.part.IPageBookViewPage;

import de.jw.equinox.osgi.console.withhistory.HistoryHandle;



public class OSGiConsoleWithHistory extends OSGiConsole {
	private IPageBookViewPage ioConsolePage;
	private HistoryHandle historyHandle;

	public OSGiConsoleWithHistory(final OSGiConsoleFactory factory) {
		super(factory);
	}

	protected void init() {
		super.init();
	}

	protected void dispose() {
		super.dispose();
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
	      this.addPatternMatchListener(this.historyHandle.getPatternMatchHandler());
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
