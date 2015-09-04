package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public interface _CompletionProposalGetter {
		
		public ICompletionProposal[] getCompletionProposal(String filter, CommandWriter writer);
}
