package net.wvh.thoriumasm.exec;

import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.InstructionException;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.RegisterState;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Executor {
	private List<StackFrame> stackTrace;
	private StackFrame currentFrame;
	private Map<String, InstructionStack> program;

	private RegisterState registers;

	private byte lastFlag = Instruction.JUMP_LABEL;
	private int currentIndex = 0;

	public Executor(List<InstructionStack> program, RegisterState registers) {
		this.stackTrace = new ArrayList<>();
		this.program = InstructionStack.toMap(program);

		this.registers = registers;
	}

	public void executeAll(String entryPoint) {
		execute(entryPoint, 0);
	}

	private void execute(String symbol, int index) {
		InstructionStack stack = findStack(symbol);
		if (stack == null) {
			throw new RuntimeException("Failed to call symbol '%s'"
				.formatted(symbol));
		}

		StackFrame entryPoint = new StackFrame(stack, index);
		stackTrace.add(entryPoint);
		currentFrame = entryPoint;

		boolean executing = true;

		Instruction current = null;
		ExecutionState executionState = new ExecutionState(registers);

		while (executing) {
			try {
				current = nextInstruction();

				byte flag = current.execute(executionState, stackTrace);

				currentFrame.incrementIndex();

				if (flag == Instruction.EXECUTION_OK) {
					continue;
				}

				if (flag == Instruction.JUMP_LABEL) {
					pushFrame(findStack(executionState.getNextSymbol()), 0);
				} else if (flag == Instruction.EXECUTION_BACK) {
					boolean result = popFrame();

					if (!result) {
						System.err.println("Cannot use back instruction on entry function!");
						executing = false;
					}

					currentFrame.decrementIndex();
					currentFrame.decrementIndex();
				} else if (flag == Instruction.EXECUTION_RET) {
					if (!popFrame()) {
						executing = false;
					}
				} else if (flag == Instruction.SKIP_NEXT) {
					currentFrame.incrementIndex();
				}
			} catch (IndexOutOfBoundsException e) {
				// probably somebody forgot to return from _start!

				System.err.println("_start has no 'ret' instruction!");

				executing = false;
			} catch (InstructionException e) {
				logExecutionError(e.getInstructionIdentifier(), e.getMessage(), currentFrame.getIndex());
				currentFrame.incrementIndex();
			} catch (Throwable e) {
				logExecutionError("unknown", e.getMessage(), currentFrame.getIndex());
				currentFrame.incrementIndex();
			}
		}
	}

	private Instruction nextInstruction() {
		return currentFrame.getCurrentInstruction();
	}

	private void pushFrame(StackFrame frame) {
		stackTrace.add(frame);
		currentFrame = stackTrace.getLast();
	}

	private void pushFrame(InstructionStack stack, int index) {
		pushFrame(new StackFrame(stack, index));
	}

	// returns false if no more frames can be popped
	private boolean popFrame() {
		if (stackTrace.size() > 1) {
			StackFrame frame = stackTrace.remove(stackTrace.size() - 1);
			currentFrame = stackTrace.getLast();
			return true;
		}

		return false;
	}

	public void setIndex(int index) {
		currentIndex = index;
	}

	private void logExecutionError(String instructionIdentifier, String errorMessage, int currentIndex) {
		System.err.println("Executor failed to execute at index " + currentIndex);
		System.err.println("Error message: " + errorMessage);
		System.err.println("Stack label: " + currentFrame.getStack().getStackLabel());
		System.err.println("Instruction: " + instructionIdentifier);
	}

	private InstructionStack findStack(String symbol) {
		return InstructionStack.findStack(program, symbol);
	}
}
