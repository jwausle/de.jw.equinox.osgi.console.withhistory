package de.jwausle.support.org.eclipse.ui.console.scala.internal.history

import org.eclipse.swt.custom.StyledText

class StyledTextEditArea(var consoleCharCountBeforeAdding: Integer = new Integer(-1), var consoleCharCountAfterAdding: Integer = new Integer(-1)) {
  
  def setCharCountBeforeWrite(command: String) =
    consoleCharCountBeforeAdding = consoleCharCountAfterAdding - command.length()

  def setCharCountAfterWrite(styledText: StyledText) =
    consoleCharCountAfterAdding = styledText.getContent().getCharCount()

  def getDeleteCommandLength(consoleCharCountActual: Integer) =
    math.max(consoleCharCountAfterAdding, consoleCharCountActual) - consoleCharCountBeforeAdding
  
  def resetCharCount(styledText:StyledText)(command:String) = { 
    setCharCountAfterWrite(styledText)
    setCharCountBeforeWrite(command)
  }
  
  def clearConsole() = {
    consoleCharCountBeforeAdding = 0
    consoleCharCountAfterAdding = 0
  }
  
}