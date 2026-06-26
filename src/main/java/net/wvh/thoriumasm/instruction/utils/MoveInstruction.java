package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;
import net.wvh.thoriumasm.state.SpecialLabel;

import java.util.List;

public final class MoveInstruction extends Instruction {
	private static String identifier = "mov";

	public static String getIdentifier() {
		return identifier;
	}

	public MoveInstruction(Variant destination, Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		if (!hasDestination() || !hasSource()) {
			throw new InstructionException("mov instruction requires two operands",
				identifier);
		}

		if (getDestination().getType() != Variant.SPECIAL_LABEL) {
			throw new InstructionException("Destination should be an object special label",
				identifier);
		}

		SpecialLabel destination = (SpecialLabel)getDestination().getData();

		destination.assignObject(getSource().getData(state.getRegisters()));

		return EXECUTION_OK;
	}
}
