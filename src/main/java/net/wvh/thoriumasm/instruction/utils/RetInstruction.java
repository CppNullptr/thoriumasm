package net.wvh.thoriumasm.instruction.utils;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.StackFrame;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;

import java.util.List;

public final class RetInstruction extends Instruction {
	private static final String identifier = "ret";

	public static String getIdentifier() {
		return identifier;
	}

	public RetInstruction(Variant destination,
			      Variant source) {
		super(destination, source);
	}

	@Override
	public byte execute(ExecutionState state, List<StackFrame> frames) throws InstructionException {
		state.getRegisters().setExitCode((int) state.getRegisters().getResultRegister());
		return Instruction.EXECUTION_RET;
	}
}
