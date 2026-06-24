package net.wvh.thoriumasm.instruction.conditional;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

import java.util.List;

public final class CompareInstruction extends Instruction {
	private static String identifier = "cmp";

	public static String getIdentifier() {
		return identifier;
	}

	public CompareInstruction(Variant destination, Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		if (!hasDestination() || !hasSource()) {
			throw new InstructionException("cmp instruction requires two operands", identifier);
		}

		int result = getDestination().compareTo(getSource(), state.getRegisters());

		if (result < 0) {
			state.getRegisters().setConditionalFlags(true, false, false);
		} else if (result == 0) {
			state.getRegisters().setConditionalFlags(false, true, false);
		} else {
			state.getRegisters().setConditionalFlags(false, false, true);
		}

		return EXECUTION_OK;
	}
}
