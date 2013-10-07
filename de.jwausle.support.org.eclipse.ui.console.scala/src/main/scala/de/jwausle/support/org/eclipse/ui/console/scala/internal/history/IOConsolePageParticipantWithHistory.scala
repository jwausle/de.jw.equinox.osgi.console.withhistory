package de.jwausle.support.org.eclipse.ui.console.scala.internal.history

import java.util.Properties
import scala.collection.mutable.LinkedList
import scala.collection.immutable.List
import org.eclipse.ui.part.IPageBookViewPage
import org.eclipse.ui.console.IOConsole
import org.eclipse.ui.console.IConsole
import org.eclipse.swt.custom.StyledText
import de.jwausle.support.org.eclipse.ui.console.scala.internal.PatternMatchListener
import org.eclipse.ui.console.IConsolePageParticipant
import de.jwausle.support.org.eclipse.ui.console.scala.internal.AbstractIConsolePageParticipant

object IOConsolePageParticipantWithHistorys {
  val javaProperty_ioconsole_history_pattern = "ioconsole.history.pattern"

  def getDefaultUserPattern() = getUserPattern(javaProperty_ioconsole_history_pattern)

  def getUserPattern(prefix: String): List[String] = {
    val systemOverrideEnvMap = new Properties
    systemOverrideEnvMap.putAll(System.getenv())
    systemOverrideEnvMap.putAll(System.getProperties())

    import scala.collection.JavaConverters._
    systemOverrideEnvMap.keys().asScala
      .filter(p => p != null) //
      .filter(p => p.toString().startsWith(prefix)) //
      .map(f => systemOverrideEnvMap.getProperty(f.toString()))
      .toList
  }
}
class IOConsolePageParticipantWithHistory(//
    var ioConsolePage: IPageBookViewPage, var ioConsoleHistoryHandle: HistoryHandle, var ioConsole: IOConsole) extends AbstractIConsolePageParticipant{
  def this(){
    this(null,null,null)
  }
  override def init(page: IPageBookViewPage, console: IConsole) = if (console.isInstanceOf[IOConsole]) {
    this.ioConsolePage = page
    this.ioConsole = console.asInstanceOf[IOConsole]
  } 

  override def dispose() = {
    this.ioConsole = null
    this.ioConsolePage = null
    this.ioConsoleHistoryHandle = null
  }

  override def activated() = {
	  val initIsTrue = (this.ioConsole != null) && (this.ioConsoleHistoryHandle == null)
	  
	  lazyInit(initIsTrue)
  }

  private def lazyInit(initIfTrue: Boolean): Unit = {
    if (!initIfTrue) return

    val textWidget = this.ioConsolePage.getControl().asInstanceOf[StyledText]

    this.ioConsoleHistoryHandle = new HistoryHandle(this.ioConsole, textWidget)
    textWidget.addKeyListener(this.ioConsoleHistoryHandle)

    this.ioConsole.addPatternMatchListener(new PatternMatchListener("osgi>(.*)(\n[^\n]|\r[^\r])", ioConsoleHistoryHandle.doMatchCommand))
    this.ioConsole.addPatternMatchListener(new PatternMatchListener("g!(.*)(\n[^\n]|\r[^\r])", ioConsoleHistoryHandle.doMatchCommand))

    IOConsolePageParticipantWithHistorys.getDefaultUserPattern.foreach(
      pattern => new PatternMatchListener(pattern, ioConsoleHistoryHandle.doMatchCommand))
  }
}