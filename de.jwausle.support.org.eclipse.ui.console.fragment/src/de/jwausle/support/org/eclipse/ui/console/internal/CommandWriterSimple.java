package de.jwausle.support.org.eclipse.ui.console.internal;

public class CommandWriterSimple implements CommandWriter {
	private final String filter;
	private StyledTextHandle styledText2;
	private QuickAssistant assistant;

	CommandWriterSimple(String filter, StyledTextHandle sth) {
		this.filter = filter;
		this.styledText2 = sth;
	}

	public void write(String command) {
		styledText2.replace(filter,   command);
		
		if(assistant != null)
			assistant.setVisible(false);
	}

	public CommandWriter setQuickAssistance(QuickAssistant quickAssist) {
		this.assistant = quickAssist;
		return this;
	}
}