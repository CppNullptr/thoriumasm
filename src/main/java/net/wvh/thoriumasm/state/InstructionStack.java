package net.wvh.thoriumasm.state;

import net.wvh.thoriumasm.instruction.Instruction;

import java.util.*;

public final class InstructionStack {
	private final List<Instruction> instructions = new ArrayList<>();

	private final String stackLabel;

	public InstructionStack(String label) {
		stackLabel = label;
	}

	public void printInstructions() {
		for (Instruction instruction : instructions) {
			System.out.println("Instruction in stack: " + instruction.toString());
		}
	}

	public void enqueue(Instruction instruction) {
		instructions.addLast(instruction);
	}

	public Instruction lastElement() {
		return instructions.getLast();
	}

	public Instruction dequeue() {
		Instruction instr = instructions.removeLast();
		return instr;
	}

	public Instruction get(int index) {
		return instructions.get(index);
	}

	public boolean isEmpty() {
		return instructions.isEmpty();
	}

	public String getStackLabel() {
		return stackLabel;
	}

	@Override
	public String toString() {
		return stackLabel + ':';
	}

	public static Map<String, InstructionStack> toMap(List<InstructionStack> list) {
		Map<String, InstructionStack> result = new HashMap<>();

		for (InstructionStack stack : list) {
			result.put(stack.stackLabel, stack);
		}

		return result;
	}

	// returns null if not found
	public static InstructionStack findStack(Map<String, InstructionStack> map, String label) {
		return map.get(label);
	}
}
