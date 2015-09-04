package de.jwausle.support.org.eclipse.ui.console.internal;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public class Main2 {
	public static class FilterList {
		private final List<String> original = new LinkedList<String>();
		private final List<String> tmp = new LinkedList<String>();

		public FilterList create() {
			original.addAll(Arrays.asList("aaa", "bbb", "ccc", "ddd"));
			return this;
		}

		public FilterList listIterator() {
			synchronized (this) {
				tmp.addAll(original);
			}
			return this;
		}

		public List<String> filter(String matches) {
			LinkedList<String> filtered = new LinkedList<String>();
			for (String t : tmp) {
				if (t == null)
					continue;
				if (!t.matches(matches)) {
					continue;
				}
				filtered.add(t);
			}
			return filtered;
		}
	}

	public static void main(String[] args) {
		FilterList filter = new FilterList().create().listIterator();
		System.out.println(filter.filter("a.*"));
	}
}
