package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;
import net.wvh.thoriumasm.state.SpecialLabel;

import java.util.List;

/**
 * Creates a new java object from string literal and moves it into
 * some object special label
 */
public final class AllocInstruction extends Instruction {
	private static final String identifier = "alloc";

	public static String getIdentifier() {
		return identifier;
	}

	public AllocInstruction(Variant destination,
	                        Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		if (getDestination().getType() != Variant.SPECIAL_LABEL) {
			throw new InstructionException("Alloc instruction only works on object labels!",
				identifier);
		}

		if (getSource().getType() != Variant.STRING_LITERAL) {
			throw new InstructionException("Alloc instruction requires string literal as type!",
				identifier);
		}

		String className = (String)getSource().getData();

		try {
			Class<?> resultClass = classFromString(className);
			Object allocated = resultClass.getDeclaredConstructor().newInstance();

			((SpecialLabel)getDestination().getData()).assignObject(allocated);
		} catch (Throwable e) {
			System.err.println("%s Failed to allocate an instance of %s: %s"
				.formatted(e.getClass().getSimpleName(), className, e.getMessage()));
		}

		return EXECUTION_OK;
	}

	private Class<?> classFromString(String classname) throws ClassNotFoundException {
		Class<?> type = Class.forName(classname);

		return type;
	}
}
