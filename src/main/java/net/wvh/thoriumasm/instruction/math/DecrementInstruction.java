package net.wvh.thoriumasm.instruction.math;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

import java.util.List;

public final class DecrementInstruction extends Instruction {
	private static String identifier = "dec";

	public static String getIdentifier() {
		return identifier;
	}

	public DecrementInstruction(Variant destination, Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		if (!hasDestination()) {
			throw new InstructionException("inc instruction requires an operand", identifier);
		}

		Variant operand = getDestination();

		if (operand.getType() == Variant.NUMBER) {
			Integer integer = (Integer)operand.getData();
			integer--;
		} else if (operand.getType() == Variant.STANDARD_REGISTER) {
			Long[] result = new Long[6];

			Long newValue = state.getStandardRegisterValue(operand) - 1;
			int registerIndex = (Integer)operand.getData();

			result[registerIndex] = newValue;

			state.setStandardRegisters(result);
		} else if (operand.getType() == Variant.SHORT_REGISTER) {
			Byte[] result = new Byte[6];

			Byte newValue = state.getShortRegisterValue(operand);
			newValue--;
			int registerIndex = (Integer)operand.getData();

			result[registerIndex] = newValue;

			state.setShortRegisters(result);
		} else {
			throw new InstructionException("This operand is not supported for incrementing",
				identifier);
		}

		return EXECUTION_OK;
	}
}
