package net.wvh.thoriumasm.exec;

import net.wvh.thoriumasm.core.Variant;
import net.wvh.thoriumasm.state.RegisterState;

/// Encapsulates everything accessible to Instruction derivatives
public final class ExecutionState {
	private final RegisterState registers;

	private String nextSymbol = "";

	public ExecutionState(RegisterState registers) {
		this.registers = registers;
	}

	public RegisterState getRegisters() {
		return registers;
	}

	public long getStandardRegisterValue(Variant variant) {
		return variant.getStandardRegisterValue(registers);
	}

	public byte getShortRegisterValue(Variant variant) {
		return variant.getShortRegisterValue(registers);
	}

	public void setNextSymbol(String symbol) {
		nextSymbol = symbol;
	}

	public String getNextSymbol() {
		return nextSymbol;
	}

	public String formatVariant(Variant variant) {
		return variant.toString(registers);
	}
}
