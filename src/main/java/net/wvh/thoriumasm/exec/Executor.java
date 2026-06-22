package net.wvh.thoriumasm.exec;

import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.RegisterState;

public final class Executor {
	private final InstructionStack stack;
	private final RegisterState registers;

	public Executor(InstructionStack stack, RegisterState registers) {
		this.stack = stack;
		this.registers = registers;
	}

	public void execute() {
		int currentIndex = 0;
		Instruction current = null;

		ExecutionState executionState = new ExecutionState(registers);

		System.out.println("Executing " + stack.getStackLabel());

		try {
			while (!stack.isEmpty()) {
				current = stack.dequeue();
				current.execute(executionState);
				currentIndex++;
				executionState.currentIndex = currentIndex;
			}
		} catch (Throwable e) {
			System.err.println("Executor failed to execute at index " + currentIndex);
			System.err.println("Error message: " + e.getMessage());
			System.err.println("Stack label: " + stack.getStackLabel());
			System.err.println("Instruction: " + current.getReflectedIdentifier());
		}
	}
}
