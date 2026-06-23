package net.wvh.thoriumasm.exec;

import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.state.InstructionStack;

public final class StackFrame {
	private InstructionStack stack;
	private int index;

	public StackFrame(InstructionStack stack,
			  int index) {
		this.stack = stack;
		this.index = index;
	}

	public void setIndex(int newIndex) {
		index = newIndex;
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

	public Instruction getCurrentInstruction() {
		return stack.elementAt(index);
	}
}
