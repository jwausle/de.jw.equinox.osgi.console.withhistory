package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public interface ProposalGetter {
		
		public ICompletionProposal[] getCompletionProposal(String filter, CommandWriteCallback writer);
}
