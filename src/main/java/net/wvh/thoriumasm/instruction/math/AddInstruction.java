package net.wvh.thoriumasm.instruction.math;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.instruction.Instruction;

public final class AddInstruction extends Instruction {
	private static String identifier = "add";

	public static String getIdentifier() {
		return identifier;
	}

	public AddInstruction(Variant destination, Variant source) {
		super(destination, source);
	}

	@Override
	public void execute(ExecutionState state) {
		if (!hasDestination() && !hasSource()) {
			throw new RuntimeException("The add instruction requires a destination and a source");
		}

		Variant destination = getDestination();
		Variant source = getSource();

		long arg1, arg2;

		if (destination.getType() == Variant.NUMBER) {
			arg1 = new Long((Integer)destination.getData());
		} else if (destination.getType() == Variant.STANDARD_REGISTER) {
			arg1 = state.getStandardRegisterValue(destination);
		} else {
			throw new RuntimeException("Unsupported operands for add instruction");
		}

		if (source.getType() == Variant.NUMBER) {
			arg2 = new Long((Integer)source.getData());
		} else if (source.getType() == Variant.STANDARD_REGISTER) {
			arg2 = state.getStandardRegisterValue(source);
		} else {
			throw new RuntimeException("Unsupported operands for add instruction");
		}

		state.setResultRegister(arg1 + arg2);
	}
}
