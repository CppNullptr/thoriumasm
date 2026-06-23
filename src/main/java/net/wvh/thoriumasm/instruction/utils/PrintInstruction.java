package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

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
	public byte execute(ExecutionState state, String currentSymbol, int currentIndex) throws InstructionException {
		if (!hasDestination()) {
			throw new InstructionException("No operand provided for print instruction",
				identifier);
		}

		if (getDestination().getType() == Variant.LABEL) {
			throw new InstructionException("'print' instruction does not support labels as operands",
				identifier);
		}

		System.out.println("<Message>@%d: %s".formatted(currentIndex,
			state.formatVariant(getDestination())));

		return EXECUTION_OK;
	}
}
