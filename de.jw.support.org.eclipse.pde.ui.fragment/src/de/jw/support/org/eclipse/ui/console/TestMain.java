package de.jw.support.org.eclipse.ui.console;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class TestMain {
	public static void main(String[] args) {
		Matcher matcher = Pattern.compile("([0-9]{2})/([0-9]{2})/([0-9]{4})").matcher("01/12/2012");
		matcher.matches();
		System.out.println(matcher.group(0));
//		System.out.println(Pattern.compile("osgi.(.*)").matcher("osgi test").group(1));
		
	}
}
