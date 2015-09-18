package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.eclipse.jface.text.contentassist.ICompletionProposal;

public class ProposalGetterCommandHistroy implements ProposalGetter {

	private final IOConsoleHistory history;

	public ProposalGetterCommandHistroy(IOConsoleHistory history) {
		this.history = history;
	}

	public ICompletionProposal[] getCompletionProposal(String filter,
			CommandWriteCallback writer) {
		Map<String, String> commandMap = toMap(history.reverse());
		
		ICompletionProposal[] proposals = QuickAssistant
				.newICompletionProposals(commandMap, writer);
		
		return proposals;
	}

	private Map<String, String> toMap(List<String> commands) {
		LinkedHashMap<String, String> map = new LinkedHashMap<String, String>();
		
		for (String command : commands) {
			map.put(command, command);
		}
		return map;
	}

}
