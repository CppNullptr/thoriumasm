package net.wvh.thoriumasm.main;

import net.wvh.thoriumasm.exec.Executor;
import net.wvh.thoriumasm.instruction.Instruction;
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

			Executor executor = new Executor(parsedStacks, registerState);

			executor.executeAll();
		} catch (Throwable e) {
			System.err.println("Error %s occurred: %s".formatted(e.getClass().getSimpleName(), e.getMessage()));
		}
	}
}
