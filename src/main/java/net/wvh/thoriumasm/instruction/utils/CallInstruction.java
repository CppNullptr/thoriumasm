package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

import static net.wvh.thoriumasm.core.Variant.ConditionType;

import java.util.List;

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
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		if (!hasDestination() ||
			getDestination().getType() != Variant.LABEL) {
			throw new InstructionException("call instruction requires a label to jump", identifier);
		}

		if (!hasSource()) {
			state.setNextSymbol((String)getDestination().getData());

			return JUMP_LABEL;
		}

		if (getSource().getType() != Variant.CONDITION) {
			throw new InstructionException("Second operand of call instruction should be a condition",
				identifier);
		}

		ConditionType condition = (ConditionType)getSource().getData();

		boolean flag;

		switch (condition) {
			case IF_LESS -> {
				flag = state.getRegisters().getConditionalFlags()[0];
			} case IF_GREATER -> {
				flag = state.getRegisters().getConditionalFlags()[2];
			} case IF_EQUAL -> {
				flag = state.getRegisters().getConditionalFlags()[1];
			} default -> {
				throw new InstructionException("Unsupported condition for call instruction!",
					identifier);
			}
		}

		if (flag) {
			state.setNextSymbol((String)getDestination().getData());

			return JUMP_LABEL;
		} else {
			return EXECUTION_OK;
		}
	}
}
