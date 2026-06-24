package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

import java.util.List;

public final class PrintInstruction extends Instruction {
	private static String identifier = "print";

	public static String getIdentifier() {
		return identifier;
	}

	public PrintInstruction(Variant destination,
	                        Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		if (!hasDestination()) {
			throw new InstructionException("No operand provided for print instruction",
				identifier);
		}

		if (getDestination().getType() == Variant.LABEL) {
			throw new InstructionException("'print' instruction does not support labels as operands",
				identifier);
		}

		StackFrame current = frames.getLast();

		System.out.println("<Message> %s@%d: %s".formatted(current.getLabel(),
			current.getIndex(),
			state.formatVariant(getDestination())));

		return EXECUTION_OK;
	}
}
