package net.wvh.thoriumasm.exec;

import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.RegisterState;

import java.util.ArrayList;
import java.util.List;

public final class Executor {
	private List<StackFrame> stackTrace;
	private List<InstructionStack> program;

	private RegisterState registers;

	private byte lastFlag = Instruction.JUMP_LABEL;
	private int currentIndex = 0;

	public Executor(List<InstructionStack> program, RegisterState registers) {
		this.stackTrace = new ArrayList<>();
		this.program = program;

		this.registers = registers;
	}

	public void executeAll() {
		try {
			execute("_start", 0);
		} catch (Throwable e) {
			logExecutionError("unknown", e.getMessage(), Integer.MAX_VALUE);
		}
	}

	private void execute(String symbol, int index) {
		InstructionStack stack = findStack(symbol);
		stackTrace.add(new StackFrame(stack, index));

		boolean executing = true;

		Instruction current = null;
		ExecutionState executionState = new ExecutionState(registers);

		while (executing) {
			try {
				current = nextInstruction();

				byte flag = current.execute(executionState, currentFrame().getStack().getStackLabel(),
					currentFrame().getIndex());

				currentFrame().incrementIndex();

				if (flag == Instruction.JUMP_LABEL) {
					pushFrame(findStack(executionState.getNextSymbol()), 0);
				} else if (flag == Instruction.EXECUTION_RET) {
					if (!popFrame()) {
						executing = false;
					}
				} else if (flag == Instruction.SKIP_NEXT) {
					currentFrame().incrementIndex();
				}
			} catch (Throwable e) {
				logExecutionError("unknown", e.getMessage(), currentFrame().getIndex());
			}
		}
	}

	private Instruction nextInstruction() {
		return currentFrame().getCurrentInstruction();
	}

	private void pushFrame(StackFrame frame) {
		stackTrace.add(frame);
	}

	private void pushFrame(InstructionStack stack, int index) {
		pushFrame(new StackFrame(stack, index));
	}

	// returns false if no more frames can be popped
	private boolean popFrame() {
		if (stackTrace.size() > 1) {
			StackFrame frame = stackTrace.remove(stackTrace.size() - 1);
			return true;
		}

		return false;
	}

	private StackFrame currentFrame() {
		try {
			return stackTrace.getLast();
		} catch (Throwable e) {
			return null;
		}
	}

	public void setIndex(int index) {
		currentIndex = index;
	}

	private void logExecutionError(String instructionIdentifier, String errorMessage, int currentIndex) {
		System.err.println("Executor failed to execute at index " + currentIndex);
		System.err.println("Error message: " + errorMessage);
		System.err.println("Stack label: " + currentFrame().getStack().getStackLabel());
		System.err.println("Instruction: " + instructionIdentifier);
	}

	private InstructionStack findStack(String symbol) {
		return InstructionStack.findStack(program, symbol);
	}
}
