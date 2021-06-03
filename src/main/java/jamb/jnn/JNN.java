package jamb.jnn;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JNN {
	public static final String VERSION = "001";
	public static Charset CHARSET = StandardCharsets.UTF_8;
	public static final int CLOSE_ENTRY = 0xFF,
			CLOSE_KEY = 0xFA,
			CLOSE_HEADER = 0xFF,
			END = 0xFF,
			ESCAPE = 0xFB;
	public static final byte[] HEADER = ("JNN" + VERSION).getBytes(StandardCharsets.UTF_8);

	public static boolean validateHeader(byte[] headerToValidate) {
		// TODO: validate each part of the header for better error handling (version, etc.)
		return Arrays.equals(headerToValidate, HEADER);
	}
}
