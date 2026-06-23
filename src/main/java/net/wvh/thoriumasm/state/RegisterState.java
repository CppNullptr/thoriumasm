package net.wvh.thoriumasm.state;

import net.wvh.thoriumasm.core.Pair;

import java.util.Arrays;
import java.util.Vector;

public final class RegisterState {
	// standard registers
	// regA, regB, regC, regD, regE, regF
	private long[] standardRegisters = new long[6];

	// result / return register
	// regR
	private long resultRegister = 0;

	// short registers
	// reg0, reg1, reg2, reg3, reg4, reg5
	private byte[] shortRegisters = new byte[6];

	// returns Integer.MAX_VALUE if failed
	public static int standardRegisterIndexFromString(String str) {
		switch (str) {
			case "regA" -> {
				return 0;
			}
			case "regB" -> {
				return 1;
			}
			case "regC" -> {
				return 2;
			}
			case "regD" -> {
				return 3;
			}
			case "regE" -> {
				return 4;
			}
			case "regF" -> {
				return 5;
			}
		}

		return Integer.MAX_VALUE;
	}

	// returns Integer.MAX_VALUE if failed
	public static int shortRegisterIndexFromString(String str) {
		switch (str) {
			case "reg0" -> {
				return 0;
			}
			case "reg1" -> {
				return 1;
			}
			case "reg2" -> {
				return 2;
			}
			case "reg3" -> {
				return 3;
			}
			case "reg4" -> {
				return 4;
			}
			case "reg5" -> {
				return 5;
			}
		}

		return Integer.MAX_VALUE;
	}

	public void printRegisters() {
		System.out.println("Register state: ");
		System.out.println("Standard registers: " +
			Arrays.toString(standardRegisters));
		System.out.println("regR: " + resultRegister);
		System.out.println("Short registers: " +
			Arrays.toString(shortRegisters));
	}

	public void setStandardRegisters(Long[] values) {
		if (values.length != standardRegisters.length) {
			throw new RuntimeException("The length of the array must be %d"
				.formatted(standardRegisters.length));
		}

		for (int index = 0; index < values.length; index++) {
			if (values[index] == null) {
				continue;
			}

			long value = values[index];

			standardRegisters[index] = value;
		}
	}

	public void setResultRegister(long value) {
		resultRegister = value;
	}

	public void setShortRegisters(Byte[] values) {
		if (values.length != shortRegisters.length) {
			throw new RuntimeException("The length of the array must be %d"
				.formatted(shortRegisters.length));
		}

		for (int index = 0; index < values.length; index++) {
			if (values[index] == null) {
				continue;
			}

			byte value = values[index];

			shortRegisters[index] = value;
		}
	}

	public long[] getStandardRegisters() {
		return standardRegisters;
	}

	public long getResultRegister() {
		return resultRegister;
	}

	public byte[] getShortRegisters() {
		return shortRegisters;
	}
}
