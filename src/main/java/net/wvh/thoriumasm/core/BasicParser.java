package net.wvh.thoriumasm.core;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Optional;
import java.util.Vector;

/**
 * Provides base for parsers
 */
public abstract class BasicParser {
	protected final String source;
	protected final File originalFile;
	protected int index = 0, currentLine = 0;

	protected BasicParser(String source) {
		this.source = source;
		this.originalFile = null;
	}

	protected BasicParser(File file) {
		this.source = readFile(file);
		this.originalFile = file;
	}

	protected final int getIndex() {
		return index;
	}

	protected final int getCurrentLine() {
		return currentLine;
	}

	protected final Optional<Character> peek() {
		return peek(0);
	}

	protected final Optional<Character> peek(int offset) {
		if (index + offset >= source.length()) {
			return Optional.empty();
		}

		return Optional.of(source.charAt(index + offset));
	}

	protected final Character consume() {
		return source.charAt(index++);
	}

	// returns null if failed
	public static String readFile(File file) {
		String result = "";

		try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
			String line;

			while ((line = reader.readLine()) != null) {
				result = result.concat(line + '\n');
			}
		} catch (IOException e) {
			result = null;

			System.err.println("Failed reading %s: %s"
				.formatted(file.getName(), e.getMessage()));
		}

		return result;
	}

	public abstract void parse();

	// should throw OperationNotSupportedException if this parser uses getters
	// instead of vector with the result
	public abstract Vector<?> getResult();
}
