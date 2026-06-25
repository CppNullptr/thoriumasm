package net.wvh.thoriumasm.state;

import net.wvh.thoriumasm.instruction.Instruction;

import java.util.*;

public final class InstructionStack {
	private final Map<Integer, Instruction> instructions =
		new HashMap<>();
	private int index = 0;

	private final String stackLabel;

	public InstructionStack(String label) {
		stackLabel = label;
	}

	public void printInstructions() {
		for (Instruction instruction : instructions.values()) {
			System.out.println("Instruction in stack: " + instruction.toString());
		}
	}

	public void enqueue(Instruction instruction) {
		instructions.put(index, instruction);
		index++;
	}

	public Instruction lastElement() {
		return instructions.get(index);
	}

	/// Returns an immutable copy of the map
	public Map<Integer, Instruction> getMap() {
		return Collections.unmodifiableMap(instructions);
	}

	public Instruction dequeue() {
		Instruction instr = instructions.get(index);
		index--;
		return instr;
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
