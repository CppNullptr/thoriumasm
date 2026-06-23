package net.wvh.thoriumasm.interpreter;

import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.SpecialLabel;

import java.io.File;
import java.io.FileNotFoundException;
import java.security.UnresolvedPermission;
import java.util.*;

// Parses a file and outputs a list of instruction stacks!
public final class AsmParser {
	private String source;
	private int index = 0;
	private int currentLine = 0;

	private Vector<InstructionStack> stack = null;
	private Vector<SpecialLabel> specialLabels = null;

	private String lastSymbol = "";
	private List<String> symbols = null;

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

	public void parse() {
		String[] lines = source.split("\n");

		stack = new Vector<>();
		specialLabels = new Vector<>();

		Vector<Token> tokens = new Vector<>();

		symbols = parseLabels(lines);

		for (String line : lines) {
			currentLine++;

			String result = prepareLine(line);
			if (result == null) {
				continue;
			}

			tokens.add(parseLine(result));
		}

		if (tokens.size() <= 1) {
			throw new ParseException("No instructions found");
		}

		interpretTokens(tokens);

		System.out.println(specialLabels);
	}

	private void interpretTokens(List<Token> tokens) {
		for (Token token : tokens) {
			if (token.getType() == Token.INSTRUCTION) {
				Instruction instruction = Instruction.deserialize((String)token.getData(), symbols, specialLabels);

				stack.lastElement().enqueue(instruction);
			} else if (token.getType() == Token.SYMBOL_DECL) {
				stack.add(new InstructionStack((String)token.getData()));
			} else if (token.getType() == Token.SPECIAL_LABEL_DECL) {
				specialLabels.add(SpecialLabel.makeEmpty((String)token.getData()));
			} else if (token.getType() == Token.SPECIAL_LABEL_PROPERTY) {
				specialLabels.lastElement().deserialize((String)token.getData());
			} else {
				throw new ParseException("Unknown token %s at line %d"
					.formatted(token.toString(), currentLine));
			}
		}
	}

	private Token parseLine(String line) {
		if (!(lastSymbol = parseSymbol(line)).isEmpty()) {
			return Token.makeSymbolDeclaration(lastSymbol, currentLine);
		}

		// special label declaration!
		if (line.charAt(0) == '%') {
			String label = line.substring(1, line.length() - 1);

			return Token.makeSpecialLabelDeclaration(label, currentLine);
		}

		// special label property!
		if (line.charAt(0) == '.') {
			return Token.makeSpecialLabelProperty(line, currentLine);
		}

		String firstIdentifier = line.split(" ")[0];

		if (Instruction.isValidSymbol(firstIdentifier)) {
			return Token.makeInstruction(line, currentLine);
		} else {
			throw new ParseException("Unknown identifier at line " + currentLine);
		}
	}

	private List<String> parseLabels(String[] lines) {
		List<String> result = new ArrayList<>();

		for (String line : lines) {
			if (line.isEmpty() || line.isBlank()) {
				continue;
			}

			String parsed = parseSymbol(line);

			if (!parsed.isEmpty()) {
				result.add(parsed);
			}
		}

		return result;
	}

	private String parseSymbol(String line) {
		if (line.charAt(0) == '%') {
			return "";
		}

		if (line.charAt(line.length() - 1) == ':') {
			String result = line.substring(0, line.length() - 1);

			return result;
		} else {
			return "";
		}
	}

	// returns null if resulting string is empty
	private String prepareLine(String line) {
		String[] splitted = line.split(";");

		String str = splitted[0].trim();

		if (str.isEmpty() || str.isBlank()) {
			return null;
		}

		return str;
	}

	private String[] prepareLines(String[] lines) {
		if (lines == null || lines.length == 0) {
			throw new ParseException("Source text is empty");
		}

		ArrayList<String> result = new ArrayList<>();

		for (String line : lines) {
			String str = prepareLine(line);

			if (str != null) {
				result.add(str);
			}
		}

		return result.toArray(new String[0]);
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

	public List<InstructionStack> getStack() {
		return stack;
	}

	public List<SpecialLabel> getSpecialLabels() {
		return specialLabels;
	}

	public List<String> getSymbols() {
		return symbols;
	}
}
