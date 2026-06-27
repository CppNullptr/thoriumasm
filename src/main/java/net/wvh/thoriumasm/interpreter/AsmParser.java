package net.wvh.thoriumasm.interpreter;

import net.wvh.thoriumasm.core.Pair;
import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.SpecialLabel;

import java.io.*;
import java.util.*;

// Parses a file and outputs a list of instruction stacks!
public final class AsmParser {
	private String source;
	private int index = 0;
	private int currentLine = 0;

	private Vector<InstructionStack> stack = null;
	private Vector<SpecialLabel> specialLabels = null;
	private Map<String, Variant> constants = null;

	private String lastSymbol = "";
	private List<String> symbols = null;

	private boolean isInGlobalSpace = true;

	private String entryPoint = "_start";
	private File originalFile = null;

	public AsmParser(File file) {
		originalFile = file;
		source = readFile(file);

		if (source == null) {
			System.exit(0);
		}
	}

	public AsmParser(String filePath) {
		this(new File(filePath));
	}

	public void parse() {
		parse(source);
	}

	public void parse(String text) {
		String[] lines = text.split("\n");

		if (stack == null) {
			stack = new Vector<>();
		}

		if (specialLabels == null) {
			specialLabels = new Vector<>();
		}

		Vector<Token> tokens = new Vector<>();
		Set<String> importedFiles = new HashSet<>();

		if (symbols == null) {
			symbols = new ArrayList<>();
		}

		if (constants == null) {
			constants = new HashMap<>();
		}

		preParse(lines);

		for (String line : lines) {
			currentLine++;

			String result = prepareLine(line);
			if (result == null) {
				continue;
			}

			Token token = parseLine(result, importedFiles);

			if (token != null) {
				tokens.add(token);
			}
		}

		if (tokens.size() <= 1) {
			throw new ParseException("No instructions found");
		}

		for (String file : importedFiles) {
			String importedSource = readFile(new File(file));

			if (importedSource == null) {
				throw new ParseException("Failed to import file " + file);
			}

			parse(importedSource);
		}

		importedFiles = null;

		interpretTokens(tokens);
	}

	private void interpretTokens(List<Token> tokens) {
		for (Token token : tokens) {
			if (token.getType() == Token.INSTRUCTION) {
				String[] splitString = splitInstructionToken(token);

				Instruction instruction = Instruction.deserialize(splitString[0], splitString[1],
					splitString[2], symbols, specialLabels, constants);

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

	private Token parseLine(String line, Set<String> importedFiles) {
		if (!(lastSymbol = parseSymbol(line)).isEmpty()) {
			isInGlobalSpace = false;
			return Token.makeSymbolDeclaration(lastSymbol, currentLine);
		}

		// special label declaration!
		if (line.charAt(0) == '%') {
			String label = line.substring(1, line.length() - 1);

			isInGlobalSpace = false;
			return Token.makeSpecialLabelDeclaration(label, currentLine);
		}

		if (line.charAt(0) == '.') {
			// special label property!
			if (!isInGlobalSpace) {
				return Token.makeSpecialLabelProperty(line, currentLine);
			}

			// global property!
			String[] separated = line.split(" ");

			if (separated.length == 1) {
				throw new ParseException("Failed to parse global property: "
				+ line);
			}

			String property = separated[0].substring(1, separated[0].length());
			String content = "";

			for (int i = 0; i < separated.length; i++) {
				if (i == 0) continue;

				content += separated[i];
				content += ' ';
			}

			parseGlobalProperty(property, content.trim(),
				importedFiles);

			return null;
		}

		String firstIdentifier = line.split(" ")[0];

		if (firstIdentifier.equals("const")) {
			return null;
		}

		if (Instruction.isValidSymbol(firstIdentifier)) {
			return Token.makeInstruction(line, currentLine);
		} else {
			throw new ParseException("Unknown identifier '%s' at line %d"
				.formatted(firstIdentifier, currentLine));
		}
	}

	private void preParse(String[] lines) {
		for (String line : lines) {
			if (line.split(" ")[0].equals("const")) {
				Pair<String, Variant> constant = parseConstant(line);

				constants.put(constant.left(), constant.right());
			} else {
				if (line.isEmpty() || line.isBlank()) {
					continue;
				}

				String parsed = parseSymbol(line);

				if (!parsed.isEmpty()) {
					symbols.add(parsed);
				}
			}
		}
	}

	private Pair<String, Variant> parseConstant(String line) {
		String[] split = line.split(" ");
		String identifier = split[1];
		String rest = "";

		for (int i = 0; i < split.length; i++) {
			String str = split[i];

			if (i < 2) {
				continue;
			}

			rest += str;
			rest += ' ';
		}

		rest = rest.trim();

		if (rest.charAt(0) == '\"' && rest.charAt(rest.length() - 1) == '\"') {
			rest = rest.substring(1, rest.length() - 1);

			return new Pair<>(identifier,
				Variant.makeStringLiteralVariant(rest));
		}

		try {
			return new Pair<>(identifier,
				Variant.makeNumberVariant(Integer.valueOf(rest)));
		} catch (Throwable e) {}

		throw new UnsupportedOperationException("Failed to parse constant '%s' with value '%s'"
			.formatted(identifier, rest));
	}

	private void parseGlobalProperty(String property, String content,
					 Set<String> importedFiles) {
		switch (property) {
			case "entry" -> {
				entryPoint = content;
			}
			case "import" -> {
				if (importedFiles == null) {
					importedFiles = new HashSet<>();
				}

				importedFiles.add(content);
			}
			default -> {
				throw new IllegalArgumentException("Unknown global property '%s'"
					.formatted(property));
			}
		}
	}

	private void importFile(String file) {
		if (new File(file) == originalFile) {
			throw new ParseException("Trying to import file that is currently being interpreted");
		}

		parse(file);
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

	/**
	 * Splits an instruction token into three parts: instruction itself,
	 * left hand side and right hand side. The latter two can be null in the resulting array
	 * @param[in] token A token of type <code>Token.INSTRUCTION</code>, cannot be null
	 * @return A String array with length always equal to 3
	 */
	private String[] splitInstructionToken(Token token) {
		Object data = token.getData();

		if (!(data instanceof String)) {
			throw new IllegalArgumentException("Token's data must be of String type");
		}

		String line = (String)data;

		String[] splitByWhitespace = line.split(" ");
		String instr = splitByWhitespace[0].trim();

		if (splitByWhitespace.length == 1) {
			return new String[] { instr, null, null };
		}

		String rest = new String();
		String[] args = Arrays.copyOfRange(splitByWhitespace, 1, splitByWhitespace.length);

		for (String arg : args) {
			rest += arg;
			rest += ' ';
		}

		// parsing operands (destination, source)

		List<String> operands = new ArrayList<>();

		StringBuilder builder = new StringBuilder();
		boolean inString = false;

		for (int i = 0; i < rest.length(); i++) {
			char c = rest.charAt(i);

			if (c == '"') {
				inString = !inString;
				builder.append(c);
				continue;
			}

			if (c == ',' && !inString) {
				operands.add(builder.toString().trim());
				builder.setLength(0);
				continue;
			}

			builder.append(c);
		}

		if (!builder.isEmpty()) {
			operands.add(builder.toString().trim());
		}

		if (operands.size() == 1) {
			return new String[] { instr, operands.get(0), null };
		} else {
			return new String[] { instr, operands.get(0), operands.get(1) };
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

	// returns null if failed
	private String readFile(File file) {
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

	public String getEntryPoint() {
		return entryPoint;
	}
}
