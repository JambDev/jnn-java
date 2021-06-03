package jamb.jnn.exception;

import jamb.jnn.JNNPrimitive;

public class JNNWrongTypeException extends RuntimeException {
	public JNNWrongTypeException(String key, JNNPrimitive requestedPrimitive, JNNPrimitive realPrimitive) {
		// TODO: "a" Object isn't proper grammar
		super("Key \"" + key + "\" is a " + realPrimitive.name() + ", not a " + (requestedPrimitive == null ? "Object"
				: requestedPrimitive.name()));
	}
}
