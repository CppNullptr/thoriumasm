package net.wvh.thoriumasm.exec;

import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.state.InstructionStack;

import java.util.Map;

public final class StackFrame {
	private final InstructionStack stack;
	private int index;

	public StackFrame(InstructionStack stack,
			  int index) {
		this.stack = stack;
		this.index = index;
	}

	public void setIndex(int newIndex) {
		index = newIndex;
	}

	public void decrementIndex() {
		index--;
	}

	public void incrementIndex() {
		index++;
	}

	public InstructionStack getStack() {
		return stack;
	}

	public int getIndex() {
		return index;
	}

	public String getLabel() {
		return stack.getStackLabel();
	}

	public Instruction getCurrentInstruction() {
		return stack.get(index);
	}
}
