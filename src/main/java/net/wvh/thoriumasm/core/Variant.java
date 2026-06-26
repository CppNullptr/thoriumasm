package net.wvh.thoriumasm.core;

import net.wvh.thoriumasm.state.RegisterState;
import net.wvh.thoriumasm.state.SpecialLabel;

import java.util.List;

/**
Represents an argument for a instruction (for example print)
It can be either a pointer to object, a user numeral or a register
**/
public final class Variant implements Comparable<Variant> {
	private enum Type {
		NUMBER,
		STANDARD_REGISTER,
		SHORT_REGISTER,
		RETURN_REGISTER,
		LABEL,
		SPECIAL_LABEL,
		STRING_LITERAL,
		CONDITION,
	}

	/*
	corresponds to ifless, ifequal and ifgreater identifiers
	 */
	public static enum ConditionType {
		IF_LESS,
		IF_EQUAL,
		IF_GREATER
	}

	public static final Type NUMBER = Type.NUMBER;
	public static final Type STANDARD_REGISTER = Type.STANDARD_REGISTER;
	public static final Type SHORT_REGISTER = Type.SHORT_REGISTER;
	public static final Type RETURN_REGISTER = Type.RETURN_REGISTER;
	public static final Type LABEL = Type.LABEL;
	public static final Type SPECIAL_LABEL = Type.SPECIAL_LABEL;
	public static final Type STRING_LITERAL = Type.STRING_LITERAL;
	public static final Type CONDITION = Type.CONDITION;

	private final Type type;
	private final Object data;

	private Variant(Type type, Object data) {
		this.type = type;
		this.data = data;
	}

	public static Variant makeNumberVariant(int number) {
		return new Variant(NUMBER, number);
	}

	/// accepts index where 0 = regA, 1 = regB, etc
	public static Variant makeStandardRegisterVariant(int registerIndex) {
		return new Variant(STANDARD_REGISTER, registerIndex);
	}

	/// accepts index where 0 = reg0, 1 = reg1, etc
	public static Variant makeShortRegisterVariant(int registerIndex) {
		return new Variant(SHORT_REGISTER, registerIndex);
	}

	public static Variant makeReturnRegisterVariant() {
		return new Variant(RETURN_REGISTER, null);
	}

	public static Variant makeLabelVariant(String label) {
		return new Variant(LABEL, label);
	}

	public static Variant makeSpecialLabelVariant(SpecialLabel label) {
		return new Variant(SPECIAL_LABEL, label);
	}

	public static Variant makeStringLiteralVariant(String literal) {
		return new Variant(STRING_LITERAL, literal);
	}

	public static Variant makeConditionVariant(ConditionType type) {
		return new Variant(CONDITION, type);
	}

	// supports only registers, user numerals and symbols
	// symbols parameter can be null if you do not want to have symbols from string
	public static Variant deserialize(String str, List<String> symbols,
	                                  List<SpecialLabel> specialLabels) {
		int result;
		if ((result = RegisterState.standardRegisterIndexFromString(str))
			!= Integer.MAX_VALUE) {
			return Variant.makeStandardRegisterVariant(result);
		}
		if ((result = RegisterState.shortRegisterIndexFromString(str))
			!= Integer.MAX_VALUE) {
			return Variant.makeShortRegisterVariant(result);
		}
		if (str.equals("regR")) {
			return Variant.makeReturnRegisterVariant();
		}
		switch (str) {
			case "ifless" -> {
				return makeConditionVariant(ConditionType.IF_LESS);
			}
			case "ifequal" -> {
				return makeConditionVariant(ConditionType.IF_EQUAL);
			}
			case "ifgreater" -> {
				return makeConditionVariant(ConditionType.IF_GREATER);
			}
		}

		if (str.charAt(0) == '"' && str.charAt(str.length() - 1) == '"') {
			return Variant.makeStringLiteralVariant(str.replace("\"", ""));
		}

		if (symbols != null) {
			for (String symbol : symbols) {
				if (str.equals(symbol)) {
					return Variant.makeLabelVariant(symbol);
				}
			}
		}
		if (specialLabels != null) {
			for (SpecialLabel label : specialLabels) {
				if (str.equals(label.getLabel())) {
					return Variant.makeSpecialLabelVariant(label);
				}
			}
		}

		try {
			return Variant.makeNumberVariant(Integer.valueOf(str));
		} catch (Throwable e) {
			System.err.println("Failed to serialize argument from %s!"
				.formatted(str));

			return null;
		}
	}

	public Type getType() {
		return type;
	}

	public Object getData() {
		return data;
	}

	public long getStandardRegisterValue(RegisterState registers) {
		if (type != STANDARD_REGISTER) {
			throw new UnsupportedOperationException("Cannot get standard register value from this variant");
		}

		return registers.getStandardRegisters()[(Integer)data];
	}

	public byte getShortRegisterValue(RegisterState registers) {
		if (type != SHORT_REGISTER) {
			throw new UnsupportedOperationException("Cannot get short register value from this variant");
		}

		return registers.getShortRegisters()[(Integer)data];
	}

	public long getReturnRegisterValue(RegisterState registers) {
		return registers.getResultRegister();
	}

	@Override
	public int compareTo(Variant other) {
		return compareTo(other, null);
	}

	/// registers can be null
	public int compareTo(Variant other, RegisterState registers) {
		Type thisType = this.type;
		Type otherType = other.type;

		long thisValue, otherValue;

		switch (thisType) {
			case NUMBER -> {
				thisValue = (int)this.data;
			}
			case STANDARD_REGISTER -> {
				if (registers != null) {
					thisValue = getStandardRegisterValue(registers);
				} else {
					throw new IllegalArgumentException("Cannot compare this variant without register state");
				}
			}
			case SHORT_REGISTER -> {
				if (registers != null) {
					thisValue = getShortRegisterValue(registers);
				} else {
					throw new IllegalArgumentException("Cannot compare this variant without register state");
				}
			}
			case RETURN_REGISTER -> {
				if (registers != null) {
					thisValue = getReturnRegisterValue(registers);
				} else {
					throw new IllegalArgumentException("Cannot compare this variant without register state");
				}
			}
			case SPECIAL_LABEL -> {
				// allow comparison only if the special label is a number

				Object labelData = ((SpecialLabel)getData()).getData();

				if (labelData instanceof Number) {
					thisValue = (long)labelData;
				} else {
					throw new IllegalArgumentException("%s is not a number!"
						.formatted(((SpecialLabel) getData()).getLabel()));
				}
			}
			default -> {
				throw new IllegalArgumentException("Failed to compare %s with %s"
					.formatted(toString(registers), other.toString(registers)));
			}
		}

		switch (otherType) {
			case NUMBER -> {
				otherValue = (int)this.data;
			}
			case STANDARD_REGISTER -> {
				if (registers != null) {
					otherValue = other.getStandardRegisterValue(registers);
				} else {
					throw new IllegalArgumentException("Cannot compare this variant without register state");
				}
			}
			case SHORT_REGISTER -> {
				if (registers != null) {
					otherValue = other.getShortRegisterValue(registers);
				} else {
					throw new IllegalArgumentException("Cannot compare this variant without register state");
				}
			}
			case RETURN_REGISTER -> {
				if (registers != null) {
					otherValue = other.getReturnRegisterValue(registers);
				} else {
					throw new IllegalArgumentException("Cannot compare this variant without register state");
				}
			}
			case SPECIAL_LABEL -> {
				Object labelData = ((SpecialLabel)other.getData()).getData();

				if (labelData instanceof Number) {
					otherValue = (long)labelData;
				} else {
					throw new IllegalArgumentException("%s is not a number!"
						.formatted(((SpecialLabel)other.getData()).getLabel()));
				}
			}
			default -> {
				throw new IllegalArgumentException("Failed to compare %s with %s"
					.formatted(other.toString(registers), toString(registers)));
			}
		}

		return Long.compare(thisValue, otherValue);
	}

	@Override
	public String toString() {
		return toString(null);
	}

	public String toString(RegisterState registers) {
		switch (type) {
			case NUMBER -> {
				return String.valueOf((Integer)data);
			}
			case STANDARD_REGISTER -> {
				if (registers == null) {
					return "ST%d".formatted((Integer)data);
				} else {
					return String.valueOf(getStandardRegisterValue(registers));
				}
			}
			case SHORT_REGISTER -> {
				if (registers == null) {
					return "SH%d".formatted((Integer)data);
				} else {
					return String.valueOf(getShortRegisterValue(registers));
				}
			}
			case RETURN_REGISTER -> {
				if (registers == null) {
					return "Rval";
				} else {
					return String.valueOf(getReturnRegisterValue(registers));
				}
			}
			case LABEL -> {
				return (String)data;
			}
			case SPECIAL_LABEL -> {
				SpecialLabel label = (SpecialLabel)data;

				if (label.getData() != null) {
					return label.getData().toString();
				} else {
					return "null";
				}
			}
			case STRING_LITERAL -> {
				return (String)data;
			}
		}

		return "null";
	}
}
