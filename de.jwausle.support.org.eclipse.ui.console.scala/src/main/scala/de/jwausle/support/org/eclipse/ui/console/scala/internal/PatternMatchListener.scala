package de.jwausle.support.org.eclipse.ui.console.scala.internal

import org.eclipse.ui.console.IPatternMatchListener
import org.eclipse.ui.console.TextConsole
import org.eclipse.ui.console.PatternMatchEvent
import java.util.logging.Logger
import java.util.regex.Pattern
import org.eclipse.jface.text.IDocument
import javax.swing.text.BadLocationException

object PatternMatchListeners {
  def safeGetNextPatternMatchFromDocument(event: PatternMatchEvent, document: IDocument, pattern: Pattern): String = {
    val lineOffset = event.getOffset()
    val lineLength = event.getLength()

    var command = ""
    try {
      command = document.get(lineOffset, lineLength)
    } catch {
      case e: BadLocationException => Logger.getLogger(classOf[PatternMatchListener].getName).severe(e.getMessage())
    }
    val matcher = pattern.matcher(command)
    if (matcher.matches()) {
      command = matcher.group(1)
    }
    command
  }
     
}
class PatternMatchListener(patternString: String, doMatchFunction: (String) => Unit) extends IPatternMatchListener {

  private val log = Logger.getLogger(classOf[PatternMatchListener].getName())

  private val pattern = Pattern.compile(patternString)

  private var ioConsole = Option.empty[TextConsole]

  override def getPattern() = pattern.pattern()

  override def getCompilerFlags() = 0

  override def getLineQualifier() = pattern.pattern()

  override def connect(console: TextConsole) = ioConsole = Option.apply(console)

  override def disconnect() = ioConsole = Option.empty[TextConsole]

  override def matchFound(event: PatternMatchEvent): Unit = {
    if (ioConsole.isEmpty) return

    val document = ioConsole.get.getDocument()

    val command = PatternMatchListeners.safeGetNextPatternMatchFromDocument(event, document, pattern)

    doMatchFunction(command)
  }
}