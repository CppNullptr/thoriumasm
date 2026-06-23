package net.wvh.thoriumasm.interpreter;

import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.state.InstructionStack;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

// Parses a file and outputs a list of instruction stacks!
public final class AsmParser {
	private String source;
	private int index = 0;
	private int currentLine = 1;

	private Vector<InstructionStack> stack = null;
	private String lastSymbol = "";

	public AsmParser(File file) {
		try (Scanner scanner = new Scanner(file)) {
			source = new String();

			while (scanner.hasNextLine()) {
				source = source.concat(scanner.nextLine() + '\n');
			}
		} catch (FileNotFoundException e) {
			source = null;
		}
	}

	public AsmParser(String filePath) {
		this(new File(filePath));
	}

	public List<InstructionStack> parse() {
		String[] lines = source.split("\n");
		if (lines.length == 0) {
			throw new ParseException("Source text is empty");
		}

		stack = new Vector<>();

		Vector<Token> tokens = new Vector<>();

		for (String line : lines) {
			if (line.isBlank()) {
				continue;
			}

			tokens.add(parseLine(line.trim()));
			currentLine++;
		}

		if (tokens.size() <= 1) {
			throw new ParseException("No instructions found");
		}

		for (Token token : tokens) {
			if (token.getType() == Token.INSTRUCTION) {
				Instruction instruction = Instruction.deserialize((String)token.getData());

				stack.lastElement().enqueue(instruction);
			} else if (token.getType() == Token.SYMBOL_DECL) {
				stack.add(new InstructionStack((String)token.getData()));
			} else {
				throw new ParseException("Unknown token at line " + currentLine);
			}
		}

		return stack;
	}

	private Token parseLine(String line) {
		if (line.charAt(line.length() - 1) == ':') {
			lastSymbol = line.substring(0, line.length() - 1);

			return Token.makeSymbolDeclaration(lastSymbol, currentLine);
		}

		String firstIdentifier = line.split(" ")[0];

		if (firstIdentifier.charAt(firstIdentifier.length() - 1) == ';') {
			firstIdentifier = firstIdentifier.substring(0, firstIdentifier.length() - 1);
		}

		if (Instruction.isValidSymbol(firstIdentifier)) {
			return Token.makeInstruction(line, currentLine);
		} else {
			throw new ParseException("Unknown identifier at line " + currentLine);
		}
	}

	private Optional<Character> peek() {
		return peek(0);
	}

	private Optional<Character> peek(int offset) {
		if (index + offset >= source.length()) {
			return Optional.empty();
		}

		return Optional.of(source.charAt(index + offset));
	}

	private Character consume() {
		return source.charAt(index++);
	}
}
