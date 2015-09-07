package de.jwausle.support.org.eclipse.ui.console.internal;

import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.widgets.Event;

public class KeyHandle {

//	private Event event;
	private char keycode;
	private char character;
	private boolean ctrl;
	private boolean alt;
	private int keycodeInt;

	public KeyHandle(Event e) {
//		this.event = e;
		this.keycode = (char) e.keyCode;
		this.character = e.character;
		this.ctrl = e.stateMask == SWT.CTRL;
		this.alt = e.stateMask == SWT.ALT;
	}

	public KeyHandle(VerifyEvent e) {
		this.keycode = (char) e.keyCode;
		this.keycodeInt = e.keyCode;
		this.character = e.character;
		this.ctrl = e.stateMask == SWT.CTRL;
		this.alt = e.stateMask == SWT.ALT;
	}

	public KeyHandle(KeyEvent e) {
		this.keycode = (char) e.keyCode;
		this.keycodeInt = e.keyCode;
		this.character = e.character;
		this.ctrl = e.stateMask == SWT.CTRL;
		this.alt = e.stateMask == SWT.ALT;
	}

	public boolean isCtrlSpace() {
		boolean isTrue = ctrl && ' ' == keycode;
		return isTrue;
	}

	public boolean isAltSpace() {
		boolean isTrue = alt && ' ' == keycode;
		return isTrue;
	}

	public boolean isSpace() {
		return ' ' == keycode;
	}

	public char get() {
		return character;
	}

	public boolean isDel() {
		if (ctrl || alt)
			return false;

		boolean isDEL = keycode == SWT.DEL || character == '\b';
		return isDEL;
	}

	public boolean isArrowUp() {
		return !ctrl && !alt && SWT.ARROW_UP == keycodeInt;
	}

	public boolean isArrowDown() {
		return !ctrl && !alt && SWT.ARROW_DOWN == keycodeInt;
	}

	public boolean isReturn() {
		return !ctrl && !alt && '\r' == keycode;
	}

	public boolean isTab() {
		if (ctrl || alt)
			return false;

		boolean isTAB = keycode == SWT.TAB || character == '\t';
		return isTAB;
	}

	@Override
	public String toString() {
		if (this.alt) {
			return "alt+'" + keycode + "'" + character + "'";
		}
		if (this.ctrl)
			return "ctrl+'" + keycode + "'" + character + "'";
		return "'" + keycode + "'";
	}

	public boolean ignore() {
		if (!ctrl && keycode == 262144)
			return true;
		if (!alt && keycode == 65536)
			return true;
		return false;
	}

	public boolean isEsc() {
		if (ctrl || alt)
			return false;

		boolean isTAB = keycode == SWT.ESC || keycodeInt == 27 || character == SWT.ESC; 
		return isTAB;

	}
}