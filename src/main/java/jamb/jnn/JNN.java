package jamb.jnn;

import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class JNN {
	public static final String FORMAT_VERSION = "002";
	public static Charset CHARSET = StandardCharsets.UTF_8;
	public static final int CLOSE_ENTRY = 0xFF, CLOSE_KEY = 0xFA, CLOSE_HEADER = 0xFF, END = 0xFF, ESCAPE = 0xFB;
	private static final jnnHeader[] SUPPORTED = new jnnHeader[] {
		new jnnHeader(FORMAT_VERSION, new JNNOptions(false)),
		new jnnHeader("001", new JNNOptions(true))
	};
	public static final byte[] HEADER = SUPPORTED[0].header;

	public static JNNOptions validateHeader(byte[] headerToValidate) {
		// TODO: eventually change this header format >999
		// TODO: validate each part of the header for better error handling (version, etc.)
		for (jnnHeader header : SUPPORTED)
			if (Arrays.equals(headerToValidate, header.header))
				return header.opts;
		return null;
	}

	private static class jnnHeader {
		public final byte[] header;
		public final JNNOptions opts;

		public jnnHeader(String ver, JNNOptions opts) {
			this.header = ("JNN" + ver).getBytes(StandardCharsets.UTF_8);
			this.opts = opts;
		}
	}
}
