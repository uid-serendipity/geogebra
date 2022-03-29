package com.himamis.retex.editor.share.editor;

import java.util.List;
import java.util.stream.Collectors;

public class SyntaxHint {

	private final String command;
	private final List<String> placeholders;
	private final int index;

	/**
	 * @param command command name
	 * @param placeholders argument placeholders
	 * @param index index of active placeholder; must be 0 &lt;= index &lt; placeholders.length
	 */
	public SyntaxHint(String command, List<String> placeholders, int index) {
		this.command = command;
		this.placeholders = placeholders;
		this.index = index;
	}

	/**
	 * @return parts before the active placeholder
	 */
	public String getPrefix() {
		return command + "(" + placeholders.stream().limit(index)
				.collect(Collectors.joining(", ")) + (index > 0 ? ", " : "");
	}

	/**
	 * @return parts after the active placeholder
	 */
	public String getSuffix() {
		return (index < placeholders.size() - 1 ? ", " : "")
				+ placeholders.stream().skip(index + 1).collect(Collectors.joining(",")) + ")";
	}

	/**
	 * @return active placeholder
	 */
	public String getActive() {
		return placeholders.get(index);
	}

}
