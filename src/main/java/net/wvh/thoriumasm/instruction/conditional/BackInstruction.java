package net.wvh.thoriumasm.instruction.conditional;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

import java.util.List;

public final class BackInstruction extends Instruction {
	private static String identifier = "back";

	public static String getIdentifier() {
		return identifier;
	}

	public BackInstruction(Variant destination, Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		return EXECUTION_BACK;
	}
}
