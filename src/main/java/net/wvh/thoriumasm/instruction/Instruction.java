package net.wvh.thoriumasm.instruction;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.state.SpecialLabel;
import org.reflections.Reflections;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public abstract class Instruction {
	private static final Map<String, Class<? extends Instruction>> instructionSymbols;

	static {
		instructionSymbols = new HashMap<>();

		Reflections reflections = new Reflections("net.wvh.thoriumasm.instruction");
		Set<Class<? extends Instruction>> classes = reflections.getSubTypesOf(Instruction.class);

		for (Class<? extends Instruction> type : classes) {
			try {
				Method identifierMethod = type.getMethod("getIdentifier");

				String identifier = (String)identifierMethod.invoke(null);

				instructionSymbols.put(identifier, type);
			} catch (NoSuchMethodException e) {
				throw new RuntimeException("Class %s does not have an identifier!"
					.formatted(type.getSimpleName()));
			} catch (IllegalAccessException | InvocationTargetException e) {
				throw new RuntimeException("Failed to retrieve identifier from " + type.getSimpleName());
			} catch (ClassCastException e) {
				throw new RuntimeException("%s should have identifier as String!"
					.formatted(type.getSimpleName()));
			}
		}
	}

	// is returned when the instruction did not make any errors
	public static final byte EXECUTION_OK = 0x0;

	// signals that the next instruction should be skipped
	// this can be used in if, or, xor instructions
	public static final byte SKIP_NEXT = 0x1;

	// means that instead of the next instruction, executor
	// should jump to a label specified in RegisterState
	public static final byte JUMP_LABEL = 0x2;

	// returned by the 'ret' instruction
	public static final byte EXECUTION_RET = 0x3;

	private Variant destination, source;

	protected Instruction(Variant destination, Variant source) {
		this.destination = destination;
		this.source = source;
	}

	// returns a code
	public abstract byte execute(ExecutionState state, String currentLabel,
				     int currentIndex) throws InstructionException;

	public final boolean hasDestination() {
		return destination != null;
	}

	public final Variant getDestination() {
		if (destination == null) {
			throw new UnsupportedOperationException("No destination provided for this instruction");
		}

		return destination;
	}

	public final boolean hasSource() {
		return source != null;
	}

	public final Variant getSource() {
		if (source == null) {
			throw new UnsupportedOperationException("No source provided for this instruction");
		}

		return source;
	}

	/// Very slow because of reflection, searches for "getIdentifier" method
	/// that returns the identifier as String without arguments.
	/// Returns null if no public 'getIdentifier()' method is present
	public final String getReflectedIdentifier() {
		try {
			Method getIdentifier = this.getClass().getMethod("getIdentifier");
			String identifier = (String)getIdentifier.invoke(null);
			return identifier;
		} catch (Throwable e) {
			return null;
		}
	}

	@Override
	public String toString() {
		return getReflectedIdentifier();
	}

	// symbols & specialLabels parameter can be null
	public static final Instruction deserialize(String str, List<String> symbols,
						    List<SpecialLabel> specialLabels) {
		String[] tokens = str.split(" ");

		if (tokens.length == 0 || tokens.length > 3) {
			return null;
		}

		String symbol = tokens[0];
		String arg1 = null, arg2 = null;

		if (tokens.length >= 2) {
			arg1 = tokens[1];

			if (arg1.charAt(arg1.length() - 1) == ',') {
				arg1 = arg1.substring(0, arg1.length() - 1);
			}
		}

		if (tokens.length == 3) {
			arg2 = tokens[2];
		}

		Variant destination = null, source = null;

		if (arg1 != null) {
			destination = Variant.deserialize(arg1, symbols, specialLabels);
		}

		if (arg2 != null) {
			source = Variant.deserialize(arg2, symbols, specialLabels);
		}

		return Instruction.constructFromSymbol(symbol, destination, source);
	}

	public final String serialize() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public static boolean isValidSymbol(String symbol) {
		return instructionSymbols.containsKey(symbol);
	}

	private static Instruction constructFromSymbol(String symbol, Variant destination, Variant source) {
		Class<? extends Instruction> type = instructionSymbols.get(symbol);

		if (type == null) {
			System.err.println("Symbol not found: " + symbol);

			return null;
		}

		try {
			Instruction result = type
				.getDeclaredConstructor(Variant.class, Variant.class)
				.newInstance(destination, source);

			return result;
		} catch (Throwable e) {
			System.err.println("Failed to construct from symbol: " + symbol);

			return null;
		}
	}

	public static void logSymbols() {
		System.out.println("Total symbols: " + instructionSymbols.size());
	}
}
