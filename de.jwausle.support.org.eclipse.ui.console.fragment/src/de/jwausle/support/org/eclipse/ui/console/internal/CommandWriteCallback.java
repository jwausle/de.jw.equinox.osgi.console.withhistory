package de.jwausle.support.org.eclipse.ui.console.internal;

/**
 * Callback to mark a command writer.
 * 
 * @author winter
 *
 */
public interface CommandWriteCallback {
	/**
	 * Callback to write the command.
	 * 
	 * @param command
	 */
	void write(String command);
}
