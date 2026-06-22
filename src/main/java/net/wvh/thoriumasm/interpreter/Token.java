package net.wvh.thoriumasm.interpreter;

public final class Token {
	private enum Type {
		SYMBOL_DECL,
		INSTRUCTION
	}

	public static final Type SYMBOL_DECL = Type.SYMBOL_DECL;
	public static final Type INSTRUCTION = Type.INSTRUCTION;

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

	public static Token makeInstruction(String str, int line) {
		return new Token(Type.INSTRUCTION, str, line);
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
}
