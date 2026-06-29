package net.wvh.thoriumasm.main;

import net.wvh.thoriumasm.core.ArgumentParser;
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
		int exitCode = 0;

		ArgumentParser argParser = new ArgumentParser(args);
		argParser.parse();
		filePath = argParser.getFilePath();

		if (filePath.isEmpty()) {
			System.out.println(messageBuilder.toString());

			System.out.println("No file specified for interpretation");

			System.exit(0);
		} else {
			messageBuilder.append(" interpreting \"");
			messageBuilder.append(filePath);
			messageBuilder.append('\"');

			if (argParser.isVerbose()) {
				messageBuilder.append('\n');
				messageBuilder.append("VERBOSE MODE");
			}
		}

		System.out.println(messageBuilder.toString());

		List<InstructionStack> parsedStacks = null;
		List<SpecialLabel> specialLabels = null;

		String entryPoint = null;

		try {
			AsmParser parser = new AsmParser(filePath, argParser.isVerbose());
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
			if (argParser.isVerbose()) executor.makeVerbose();

			executor.executeAll(entryPoint);
			exitCode = executor.getExitCode();
		} catch (Throwable e) {
			System.err.println("Error %s occurred: %s".formatted(e.getClass().getSimpleName(), e.getMessage()));
		}

		System.exit(exitCode);
	}
}
