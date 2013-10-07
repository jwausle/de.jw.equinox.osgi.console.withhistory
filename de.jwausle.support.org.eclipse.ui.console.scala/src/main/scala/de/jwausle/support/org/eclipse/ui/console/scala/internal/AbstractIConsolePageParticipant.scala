package de.jwausle.support.org.eclipse.ui.console.scala.internal

import org.eclipse.ui.console.IConsolePageParticipant
import org.eclipse.ui.console.IConsole
import org.eclipse.ui.part.IPageBookViewPage

abstract class AbstractIConsolePageParticipant extends IConsolePageParticipant{
  override def getAdapter(adapter: java.lang.Class[_]) = null

  override def init(page: IPageBookViewPage, console: IConsole) = Unit

  override def dispose() = Unit

  override def activated() = Unit

  override def deactivated() = Unit
}