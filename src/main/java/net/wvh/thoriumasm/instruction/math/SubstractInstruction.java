package net.wvh.thoriumasm.instruction.math;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

import java.util.List;

public class SubstractInstruction extends Instruction {
	private static String identifier = "sub";

	public static String getIdentifier() {
		return identifier;
	}

	public SubstractInstruction(Variant destination, Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		if (!hasDestination() && !hasSource()) {
			throw new RuntimeException("The sub instruction requires a destination and a source");
		}

		Variant destination = getDestination();
		Variant source = getSource();

		long arg1, arg2;

		if (destination.getType() == Variant.NUMBER) {
			arg1 = new Long((Integer)destination.getData());
		} else if (destination.getType() == Variant.STANDARD_REGISTER) {
			arg1 = state.getStandardRegisterValue(destination);
		} else {
			throw new RuntimeException("Unsupported operands for sub instruction");
		}

		if (source.getType() == Variant.NUMBER) {
			arg2 = new Long((Integer)source.getData());
		} else if (source.getType() == Variant.STANDARD_REGISTER) {
			arg2 = state.getStandardRegisterValue(source);
		} else {
			throw new RuntimeException("Unsupported operands for sub instruction");
		}

		state.getRegisters().setResultRegister(arg1 - arg2);

		return EXECUTION_OK;
	}
}
