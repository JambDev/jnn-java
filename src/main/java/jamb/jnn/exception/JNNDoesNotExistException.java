package jamb.jnn.exception;

public class JNNDoesNotExistException extends NullPointerException {
	public JNNDoesNotExistException(String key) {
		super("Key \"" + key + "\" could not be found. ");
	}
}
