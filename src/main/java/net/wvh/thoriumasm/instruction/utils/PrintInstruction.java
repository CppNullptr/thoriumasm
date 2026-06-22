package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.instruction.Instruction;

public class PrintInstruction extends Instruction {
	private static String identifier = "print";

	public static String getIdentifier() {
		return identifier;
	}

	public PrintInstruction(Variant destination,
	                        Variant source) {
		super(destination, source);
	}

	@Override
	public void execute(ExecutionState state) {
		if (!hasDestination()) {
			throw new RuntimeException("No operand provided for print instruction");
		}

		System.out.println("<Message>@%d: %s".formatted(state.getCurrentIndex(),
			state.formatVariant(getDestination())));
	}
}
