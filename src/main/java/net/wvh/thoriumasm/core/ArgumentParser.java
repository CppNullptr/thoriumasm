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
		this(String.join(" ", strings));
	}

	public ArgumentParser(String source) {
		super(source);
	}

	@Override
	public void parse() {
		String alphaNumericBuffer = "";
		boolean insideParameter = false;

		String[] split = source.split(" ");

		for (String arg : split) {
			if (arg.startsWith("--")) {
				parseParameter(arg);
			} else {
				if (!filePath.isEmpty()) {
					throw new IllegalArgumentException("Cannot specify file to interpret " +
						"more than once");
				} else {
					filePath = arg;
				}
			}
		}
	}

	private void parseParameter(String parameter) {
		switch (parameter) {
			case "--verbose" -> {
				verbose = true;
			}
			default -> {
				throw new IllegalArgumentException("Unknown parameter: " + parameter);
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
