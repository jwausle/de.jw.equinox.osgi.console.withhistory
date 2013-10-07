package de.jwausle.support.org.eclipse.ui.console.scala.internal.history
import java.util.Stack
import java.lang.Boolean

class HistoryCommandStack(history: Stack[String] = new Stack, historySession: Stack[String] = new Stack) {

  def add(command: String):Unit = {
    val optCommand: Option[String] = Option(command)

    if (optCommand.map(c => equalsLastCommand(c)).getOrElse(Boolean.FALSE)) return

    if (optCommand.getOrElse("").trim().isEmpty()) return

    val trimmedCommand = optCommand //
      .map(command => dropRightIfExist("\r")(command))
      .map(command => dropRightIfExist("\n")(command))
      .getOrElse("").trim()

    if (!historySession.isEmpty()) {
      clearSessionStack()
    }

    history.push(trimmedCommand)
  }

  def popFromHistory() = pushPopIfPopIsNotEmpty(this.historySession,this.history)
  
  def popOneBefore() = pushPopIfPopIsNotEmpty(this.history,this.historySession)
  
  private def pushPopIfPopIsNotEmpty(pushStack:Stack[String],popStack:Stack[String]):String = {
    if(popStack.isEmpty()) return ""
    
    pushStack.push(popStack.pop())
  }
  
  def clearSessionStack() {
    val reverseHistorySession = historySessionAsReverseList(this.historySession)

    import scala.collection.JavaConverters._
    this.history.addAll(reverseHistorySession.asJava)

    this.historySession.clear()
  }

  def equalsLastCommand(command: String):scala.Boolean = {
    if (true_IfHistoryEmpty_Or_CommandNull(command)) return Boolean.FALSE

    
    command.trim().equals(lastCommand())
  }

  private def lastCommand():String = {
    if (!historySession.isEmpty()) return historySessionAsReverseList(historySession).head
    
    if (!history.isEmpty()) return history.peek()
    
    return ""
  }

  private def true_IfHistoryEmpty_Or_CommandNull(command: String):Boolean = {
    if (history.isEmpty()) return Boolean.TRUE
    
    if (command == null) return Boolean.TRUE
    
    Boolean.FALSE
  }

  private def historySessionAsReverseList(list: java.util.Stack[String]) = {
    import scala.collection.JavaConverters._
    list.asScala.reverse
  }

  private def dropRightIfExist(right: String)(cmd: String):String = {
    val trimmedCmd = Option(cmd).getOrElse("").trim()

    if (trimmedCmd.endsWith(right)) {
      return trimmedCmd.dropRight(right.length())
    }
    trimmedCmd
  }
  
  override def toString() = "IOConsoleWithHistoryData [history=" + this.history	+ ", historySession=" + this.historySession + "]"
}