package de.jwausle.support.org.eclipse.ui.console.internal
import java.util.Stack
import java.lang.Boolean

class HistoryCommandStack(history: Stack[String] = new Stack, historySession: Stack[String] = new Stack) {

  def add(command: String) = {
    val optCommand: Option[String] = Option(command)

    if (optCommand.map(c => equalsLastCommand(c)).getOrElse(Boolean.FALSE))
      Unit

    if (optCommand.getOrElse("").trim().isEmpty())
      Unit

    val trimmedCommand = optCommand //
      .map(command => dropRightIfExist("\r")(command))
      .map(command => dropRightIfExist("\n")(command))
      .getOrElse("").trim()

    if (!historySession.isEmpty()) {
      clearSessionStack()
    }

    history.push(trimmedCommand)
    Unit
  }

  def popFromHistory() = pushPopIfPopIsNotEmpty(this.historySession,this.history)
  
  def popOneBefore() = pushPopIfPopIsNotEmpty(this.history,this.historySession)
  
  private def pushPopIfPopIsNotEmpty(pushStack:Stack[String],popStack:Stack[String]):String = {
    if(popStack.isEmpty())
      ""
    pushStack.push(popStack.pop())
  }
  
  def clearSessionStack() {
    val reverseHistorySession = historySessionAsReverseList(this.historySession)

    import scala.collection.JavaConverters._
    this.history.addAll(reverseHistorySession.asJava)

    this.historySession.clear()
  }

  def equalsLastCommand(command: String) = {
    if (true_IfHistoryEmpty_Or_CommandNull(command))
      Boolean.FALSE

    command.trim().equals(lastCommand())
  }

  private def lastCommand() {
    if (!historySession.isEmpty())
      historySessionAsReverseList(historySession).head
    if (!history.isEmpty())
      history.peek()
    ""
  }

  private def true_IfHistoryEmpty_Or_CommandNull(command: String) = {
    if (history.isEmpty())
      Boolean.TRUE
    if (command == null)
      Boolean.TRUE
    Boolean.FALSE
  }

  private def historySessionAsReverseList(list: java.util.Stack[String]) = {
    import scala.collection.JavaConverters._
    list.asScala.reverse
  }

  private def dropRightIfExist(right: String)(cmd: String) = {
    val trimmedCmd = Option(cmd).getOrElse("").trim()

    if (trimmedCmd.endsWith(right)) {
      trimmedCmd.dropRight(right.length())
    }
    ""
  }
  
  override def toString() = "IOConsoleWithHistoryData [history=" + this.history	+ ", historySession=" + this.historySession + "]"
}