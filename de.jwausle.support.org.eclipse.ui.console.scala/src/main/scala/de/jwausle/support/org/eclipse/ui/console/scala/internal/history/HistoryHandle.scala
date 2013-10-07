package de.jwausle.support.org.eclipse.ui.console.scala.internal.history

import org.eclipse.ui.console.IOConsole
import org.eclipse.swt.custom.StyledText
import org.eclipse.swt.events.KeyEvent
import org.eclipse.swt.events.KeyAdapter
import org.eclipse.swt.SWT
import java.util.logging.Logger

class HistoryHandle(console: IOConsole, styledText: StyledText) extends KeyAdapter {
  private val log = Logger.getLogger(classOf[HistoryHandle].getName())
  val data = new HistoryCommandStack

  private val syledTextEditArea = new StyledTextEditArea(styledText.getContent().getCharCount())

  override def keyReleased(e: KeyEvent): Unit = {
    e.keyCode match {
      case SWT.ARROW_UP => writeCommandToConsole(this.data.popFromHistory)
      case SWT.ARROW_DOWN => writeCommandToConsole(this.data.popOneBefore)
      case _ => Unit
    }
  }

  private def writeCommandToConsole(command: String): Unit = {
    if (command == null) return

    this.synchronized {
      val consoleCharCountActual = styledText.getContent().getCharCount();
      val length = syledTextEditArea.getDeleteCommandLength(consoleCharCountActual)

      try {
        this.styledText.replaceTextRange(this.syledTextEditArea.consoleCharCountBeforeAdding, length, "")
      } catch {
        case e: IllegalArgumentException => log.fine("Expected exception by replaceTextRange() catched: " + e.getMessage())
      }

      this.styledText.setText(command)

      this.syledTextEditArea.resetCharCount(styledText)(command)

      log.fine("write: " + this.data)
    }
  }

  def clearConsole() = this.syledTextEditArea.clearConsole

  def getPatternMatchHandler() = ""

  def doMatchCommand(command: String): Unit = {
    if (command == null || command.isEmpty()) return

    if (!data.equalsLastCommand(command)) data.add(command)

    data.clearSessionStack
    log.fine("enter: " + data)
  }
}