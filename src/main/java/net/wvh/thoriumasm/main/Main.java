package net.wvh.thoriumasm.main;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.exec.ExecutionState;
import net.wvh.thoriumasm.exec.Executor;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.instruction.math.AddInstruction;
import net.wvh.thoriumasm.interpreter.AsmParser;
import net.wvh.thoriumasm.interpreter.ParseException;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.RegisterState;

import java.util.List;

public class Main {
	public static void main(String[] args) {
		String currentVersion = Main.class.getPackage().getImplementationVersion();
		System.out.println("Thorium Assembler Version v%s".formatted(currentVersion));

		List<InstructionStack> parsedStacks = null;

		try {
			AsmParser parser = new AsmParser("C:/Users/ma200/Downloads/test.tasm");

			parsedStacks = parser.parse();
		} catch (ParseException e) {
			System.err.println("Failed to parse file: " + e.getMessage());
		}

		try {
			RegisterState registerState = new RegisterState();
			InstructionStack instructionStack = null;

			for (InstructionStack stack : parsedStacks) {
				if (stack.getStackLabel().equals("_start")) {
					instructionStack = stack;
				}
			}

			Executor executor = new Executor(instructionStack, registerState);

			Thread executionThread = new Thread(() -> {
				executor.execute();
			});

			executionThread.start();

			executionThread.join();
		} catch (Throwable e) {
			System.err.println("Error %s occurred: %s".formatted(e.getClass().getSimpleName(), e.getMessage()));
		}
	}
}
