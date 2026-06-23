package net.wvh.thoriumasm.state;

/**
 * A class that represents a label that has special behavior,
 * for example heap-allocated String label 'message'
 */
public final class SpecialLabel {
	private enum LabelType {
		OBJECT,
		NATIVE_FUNC,
		NATIVE_TYPE
	}

	public static final LabelType OBJECT = LabelType.OBJECT;
	public static final LabelType NATIVE_FUNC = LabelType.NATIVE_FUNC;
	public static final LabelType NATIVE_TYPE = LabelType.NATIVE_TYPE;

	private LabelType type;
	private Object data;

	private String label;

	private SpecialLabel(LabelType type, Object data, String label) {
		this.type = type;
		this.data = data;
		this.label = label;
	}

	public static SpecialLabel makeEmpty(String label) {
		return new SpecialLabel(null, null, label);
	}

	// null by default, can be assigned later using 'assignObject' method
	public void setObject() {
		type = OBJECT;
		data = null;
	}

	public void setNativeFunc() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void setNativeType() {
		throw new UnsupportedOperationException("Not implemented");
	}

	public void deserialize(String str) {
		switch (str) {
			case ".object" -> {
				setObject();
			} case ".nativefunc" -> {
				setNativeFunc();
			} case ".nativetype" -> {
				setNativeType();
			} default -> {
				throw new RuntimeException("Unknown special label property");
			}
		}
	}

	/// Allowed only on OBJECT type labels
	public void assignObject(Object object) {
		if (type != OBJECT) {
			throw new UnsupportedOperationException("Cannot assign object to non-OBJECT type label");
		}

		data = object;
	}

	public LabelType getType() {
		return type;
	}

	public Object getData() {
		if (type != OBJECT) {
			throw new UnsupportedOperationException("Cannot retrieve data from non-OBJECT type label");
		}

		return data;
	}

	public String getLabel() {
		return label;
	}

	@Override
	public String toString() {
		return type.name() + '#' + label;
	}
}
