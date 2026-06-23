package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

public final class CallInstruction extends Instruction {
	private static final String identifier = "call";

	public static String getIdentifier() {
		return identifier;
	}

	public CallInstruction(Variant destination,
			       Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, String currentSymbol, int currentIndex) throws InstructionException {
		if (!hasDestination() ||
			getDestination().getType() != Variant.LABEL) {
			throw new InstructionException("call instruction requires a label to jump", identifier);
		}

		state.setNextSymbol((String)getDestination().getData());

		return JUMP_LABEL;
	}
}
