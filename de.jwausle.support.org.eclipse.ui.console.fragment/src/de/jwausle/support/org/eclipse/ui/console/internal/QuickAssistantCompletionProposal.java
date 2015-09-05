package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Map.Entry;

import org.eclipse.jface.text.IDocument;
import org.eclipse.jface.text.contentassist.ICompletionProposal;
import org.eclipse.jface.text.contentassist.IContextInformation;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;

class QuickAssistantCompletionProposal implements ICompletionProposal {
	private final CommandWriteCallback writer;
	private final Entry<String, String> entry;

	QuickAssistantCompletionProposal(CommandWriteCallback writer,
			Entry<String, String> entry) {
		this.writer = writer;
		this.entry = entry;
	}

	public Point getSelection(IDocument document) {
		// System.err.println("==> GetSelection(): " +
		// entry.getKey() );
		// return new Point(0, 0);
		return null;
	}

	public Image getImage() {
		return null;
	}

	public String getDisplayString() {
		String key = entry.getKey();
		return QuickAssistant.commandWithoutScope(key);
	}

	public IContextInformation getContextInformation() {
		return null;
	}

	public String getAdditionalProposalInfo() {
		return entry.getValue();
	}

	public void apply(IDocument document) {
		System.err.println("==> Apply to document: "
				+ entry.getKey());
		String key = entry.getKey();
		String commandWithoutScope = QuickAssistant.commandWithoutScope(key);
		writer.write(commandWithoutScope);
	}
}