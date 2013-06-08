/*
 * Copyright (c) Robert Bosch GmbH. All rights reserved.
 */
package de.jw.equinox.osgi.console.withhistory;

import org.eclipse.swt.custom.StyledText;

public class StyledTextEditArea {

  int consoleCharCountBeforeAdding = -1;
  int consoleCharCountAfterAdding = -1;

  /**
   * @param charCountBeforeAdding
   */
  public StyledTextEditArea(final int charCountBeforeAdding) {
    this.consoleCharCountBeforeAdding = charCountBeforeAdding;
  }

  public void setCharCountBeforeWrite(final String command) {
    this.consoleCharCountBeforeAdding = this.consoleCharCountAfterAdding - command.length();
  }

  public void setCharCountAfterWrite(final StyledText styledText) {
    this.consoleCharCountAfterAdding = styledText.getContent().getCharCount();
  }

  public int getDeleteCommandLength(final int consoleCharCountActual) {
    int maxFromAfterAddingOrActual = Math.max(this.consoleCharCountAfterAdding, consoleCharCountActual);

    int length = maxFromAfterAddingOrActual - this.consoleCharCountBeforeAdding;
    return length;
  }

  public void resetCharCount(final StyledText styledText, final String command) {

    setCharCountAfterWrite(styledText);

    setCharCountBeforeWrite(command);
  }

  /** What should be done if console cleared. */
  public void clearConsole() {
    this.consoleCharCountBeforeAdding = 0;
    this.consoleCharCountAfterAdding = 0;
  }
}