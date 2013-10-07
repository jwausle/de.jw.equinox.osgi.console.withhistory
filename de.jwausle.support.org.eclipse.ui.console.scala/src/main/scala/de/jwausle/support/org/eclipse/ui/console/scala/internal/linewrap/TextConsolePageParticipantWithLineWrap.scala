package de.jwausle.support.org.eclipse.ui.console.scala.internal.linewrap

import de.jwausle.support.org.eclipse.ui.console.scala.internal.AbstractIConsolePageParticipant
import org.eclipse.ui.part.IPageBookViewPage
import org.eclipse.ui.console.TextConsole
import org.eclipse.swt.custom.StyledText
import org.eclipse.ui.console.IConsole
import de.jwausle.support.org.eclipse.ui.console.scala.internal.PatternMatchListener
import org.eclipse.swt.widgets.Display

class TextConsolePageParticipantWithLineWrap( //
  var page: IPageBookViewPage = null, var console: TextConsole = null, var styledText: StyledText = null) extends AbstractIConsolePageParticipant {

  def this() = this(null, null, null)

  override def init(page: IPageBookViewPage, iConsole: IConsole) = {
    this.page = page
    if (iConsole.isInstanceOf[TextConsole])
      this.console = iConsole.asInstanceOf[TextConsole]
  }

  override def activated() = {
    if (this.page != null && this.page.getControl().isInstanceOf[StyledText]) {
      this.styledText = this.page.getControl().asInstanceOf[StyledText]
    }

    if (this.console != null) {
      console.addPatternMatchListener(new PatternMatchListener(".*(disableLineWrapping).*(\n[^\n]|\r[^\r])", resetLineWrapping(false)))
      console.addPatternMatchListener(new PatternMatchListener(".*(enableLineWrapping).*(\n[^\n]|\r[^\r])", resetLineWrapping(true)))
    }
  }

  private def resetLineWrapping(isTrueThanEnable: Boolean)(ignored: String): Unit = {
    Display.getDefault().asyncExec(new Runnable() {
      def run() = {
        if (styledText != null)
          styledText.setWordWrap(isTrueThanEnable)
      }
    })
  }
}