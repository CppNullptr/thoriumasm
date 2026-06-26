package net.wvh.thoriumasm.main;

import net.wvh.thoriumasm.exec.Executor;
import net.wvh.thoriumasm.instruction.Instruction;
import net.wvh.thoriumasm.interpreter.AsmParser;
import net.wvh.thoriumasm.interpreter.ParseException;
import net.wvh.thoriumasm.state.InstructionStack;
import net.wvh.thoriumasm.state.RegisterState;
import net.wvh.thoriumasm.state.SpecialLabel;
import java.util.List;

public class Main {
	public static void main(String[] args) {
		StringBuilder messageBuilder = new StringBuilder();
		messageBuilder.append("TASM Ver v");
		messageBuilder.append(Main.class.getPackage().getImplementationVersion());

		String filePath = null;

		if (args.length >= 1) {
			filePath = args[0];

			messageBuilder.append(" interpreting \"");
			messageBuilder.append(filePath);
			messageBuilder.append('\"');
		} else {
			System.out.println(messageBuilder.toString());

			System.out.println("No file specified for interpretation");

			System.exit(0);
		}

		System.out.println(messageBuilder.toString());

		List<InstructionStack> parsedStacks = null;
		List<SpecialLabel> specialLabels = null;

		String entryPoint = null;

		try {
			AsmParser parser = new AsmParser(filePath);
			parser.parse();

			parsedStacks = parser.getStack();
			specialLabels = parser.getSpecialLabels();
			entryPoint = parser.getEntryPoint();
		} catch (ParseException e) {
			System.err.println("Failed to parse file: " + e.getMessage());
			System.exit(0);
		} catch (Throwable e) {
			System.err.println("%s: %s"
				.formatted(e.getClass().getSimpleName(), e.getMessage()));
			System.exit(0);
		}

		try {
			RegisterState registerState = new RegisterState();

			Executor executor = new Executor(parsedStacks, registerState);

			executor.executeAll(entryPoint);
		} catch (Throwable e) {
			System.err.println("Error %s occurred: %s".formatted(e.getClass().getSimpleName(), e.getMessage()));
		}
	}
}
