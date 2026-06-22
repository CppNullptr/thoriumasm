package net.wvh.thoriumasm.state;

import net.wvh.thoriumasm.instruction.Instruction;

import java.util.Collections;
import java.util.LinkedList;
import java.util.Queue;

public final class InstructionStack {
	private final Queue<Instruction> instructions =
		new LinkedList<>();

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
		instructions.offer(instruction);
	}

	public Instruction dequeue() {
		return instructions.poll();
	}

	public boolean isEmpty() {
		return instructions.isEmpty();
	}

	public String getStackLabel() {
		return stackLabel;
	}
}
