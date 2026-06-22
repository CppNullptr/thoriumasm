package net.wvh.thoriumasm.main;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.Executor;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.math.AddInstruction;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.RegisterState;

public class Main {
	public static void main(String[] args) {
		String currentVersion = Main.class.getPackage().getImplementationVersion();
		System.out.println("Thorium Assembler Version v%s".formatted(currentVersion));

		Instruction instruction = Instruction.deserialize("add regA, regB");
		System.out.println(instruction);

		Instruction.logSymbols();

		try {
			RegisterState registerState = new RegisterState();

			InstructionStack instructionStack = new InstructionStack("_start");
			instructionStack.enqueue(instruction);

			Executor executor = new Executor(instructionStack, registerState);

			Thread executionThread = new Thread(() -> {
				executor.execute();
			});

			executionThread.start();

			executionThread.join();

			registerState.printRegisters();
		} catch (Throwable e) {
			System.err.println("Error %s occurred: %s".formatted(e.getClass().getSimpleName(), e.getMessage()));
		}
	}
}
