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

		Object sourceData = getSource().getData(state.getRegisters());

		if (getDestination().getType() == Variant.SPECIAL_LABEL) {
			SpecialLabel destination = (SpecialLabel)getDestination().getData();

			destination.assignObject(sourceData);
		} else if (getDestination().getType() == Variant.STANDARD_REGISTER) {
			int registerIndex = (Integer)getDestination().getData();
			Long[] newValues = { null, null, null, null, null, null };
			try {
				newValues[registerIndex] = ((Number)sourceData).longValue();
			} catch (ClassCastException e) {
				throw new InstructionException("Failed to convert non-number value to place into standard integer",
					identifier);
			}

			state.getRegisters().setStandardRegisters(newValues);
		} else if (getDestination().getType() == Variant.SHORT_REGISTER) {
			int registerIndex = (Integer)getDestination().getData();
			Byte[] newValues = { null, null, null, null, null, null };
			try {
				newValues[registerIndex] = ((Number)sourceData).byteValue();
			} catch (ClassCastException e) {
				throw new InstructionException("Failed to convert non-number value to place into a short integer",
					identifier);
			}
		} else if (getDestination().getType() == Variant.RETURN_REGISTER) {
			try {
				state.getRegisters().setResultRegister(((Number)sourceData).longValue());
			} catch (ClassCastException e) {
				throw new InstructionException("Failed to put non-number value to place into the return register",
					identifier);
			}
		} else {
			throw new InstructionException("This operand is not supported for mov instruction",
				identifier);
		}

		return EXECUTION_OK;
	}
}
