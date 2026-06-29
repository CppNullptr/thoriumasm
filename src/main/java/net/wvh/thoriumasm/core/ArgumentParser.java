package net.wvh.thoriumasm.core;

import java.util.Vector;

/**
 * Contains functionality for the argument parser, which parses user input,
 * such as "--verbose"
 */
public final class ArgumentParser extends BasicParser {
	private String filePath = "";
	private boolean verbose = false;

	// concatenates the array into single string
	public ArgumentParser(String[] strings) {
		super(String.join(" ", strings));
	}

	public ArgumentParser(String source) {
		super(source);
	}

	@Override
	public void parse() {
		while (peek().isPresent()) {
			while (peek().isPresent() && peek().get() != '-') {
				filePath += consume();
			}
		}
	}

	@Override
	public Vector<?> getResult() {
		throw new UnsupportedOperationException("This parser uses getters instead of getResult");
	}

	public String getFilePath() {
		return filePath;
	}

	public boolean isVerbose() {
		return verbose;
	}
}
