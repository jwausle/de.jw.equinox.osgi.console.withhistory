package de.jwausle.support.org.eclipse.ui.console.internal;

public class _ReplaceCommandWriter implements CommandWriter {
	private final String filter;
	private _StyledTextHandle styledText2;
	private _QuickAssistAssistant assistant;

	_ReplaceCommandWriter(String filter, _StyledTextHandle sth) {
		this.filter = filter;
		this.styledText2 = sth;
	}

	public void write(String command) {
		String _command =command.replace(filter, "");
		styledText2.replace(filter,   command);
		if(assistant != null)
			assistant.setVisible(false);
	}

	public CommandWriter setQuickAssistance(_QuickAssistAssistant quickAssist) {
		this.assistant = quickAssist;
		return this;
	}
}