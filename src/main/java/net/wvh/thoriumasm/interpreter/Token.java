package net.wvh.thoriumasm.interpreter;

public final class Token {
	private enum Type {
		SYMBOL_DECL,

		// for example %message
		SPECIAL_LABEL_DECL,

		INSTRUCTION,

		// for example .object
		SPECIAL_LABEL_PROPERTY;

		@Override
		public String toString() {
			return this.name().toLowerCase();
		}
	}

	public static final Type SYMBOL_DECL = Type.SYMBOL_DECL;
	public static final Type SPECIAL_LABEL_DECL = Type.SPECIAL_LABEL_DECL;
	public static final Type INSTRUCTION = Type.INSTRUCTION;
	public static final Type SPECIAL_LABEL_PROPERTY = Type.SPECIAL_LABEL_PROPERTY;

	private Type type;
	private Object data;
	private int line;

	private Token(Type type, Object data, int line) {
		this.type = type;
		this.data = data;
		this.line = line;
	}

	public static Token makeSymbolDeclaration(String symbol, int line) {
		return new Token(Type.SYMBOL_DECL, symbol, line);
	}

	public static Token makeSpecialLabelDeclaration(String label, int line) {
		return new Token(Type.SPECIAL_LABEL_DECL, label, line);
	}

	public static Token makeInstruction(String str, int line) {
		return new Token(Type.INSTRUCTION, str, line);
	}

	public static Token makeSpecialLabelProperty(String str, int line) {
		return new Token(SPECIAL_LABEL_PROPERTY, str, line);
	}

	public Type getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

	public int getLine() {
		return line;
	}

	@Override
	public String toString() {
		return type.toString();
	}
}
