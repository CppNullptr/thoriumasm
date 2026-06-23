package net.wvh.thoriumasm.instruction;

public class InstructionException extends Exception {
	private final String instructionIdentifier;

	public InstructionException(String message, String identifier) {
		super(message);
		instructionIdentifier = identifier;
	}

	public final String getInstructionIdentifier() {
		return instructionIdentifier;
	}
}
