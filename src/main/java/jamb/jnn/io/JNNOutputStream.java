package jamb.jnn.io;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map.Entry;

import jamb.jnn.JNN;
import jamb.jnn.JNNObject;
import jamb.jnn.JNNPrimitive;

import static jamb.jnn.JNN.CLOSE_ENTRY;
import static jamb.jnn.JNN.CLOSE_HEADER;
import static jamb.jnn.JNN.CLOSE_KEY;
import static jamb.jnn.JNN.END;
import static jamb.jnn.JNN.ESCAPE;
import static jamb.jnn.JNN.HEADER;

public class JNNOutputStream extends OutputStream {
	private final OutputStream out;

	public JNNOutputStream(OutputStream out) {
		this.out = out;
	}

	protected static final List<Integer> RESERVED = Collections.unmodifiableList(Arrays.asList(
		CLOSE_ENTRY, CLOSE_HEADER, CLOSE_KEY, END, ESCAPE
	));

	protected byte[] escapeBytes(byte[] b) {
		ByteArrayOutputStream arr = new ByteArrayOutputStream();
		for (byte b1 : b) {
			if(RESERVED.contains(b1 & 0xFF))
				arr.write(ESCAPE);
			arr.write(b1);
		}
		return arr.toByteArray();
	}

	public void writeJNNObject(JNNObject obj) throws IOException {
		out.write(HEADER);
		out.write(CLOSE_HEADER);

		for (Entry<String, Object> entry : obj.entries()) {
			JNNPrimitive type = JNNObject.getPrimitive(entry.getValue());
			;
			out.write(type.identifier);
			out.write(escapeBytes(entry.getKey().getBytes(JNN.CHARSET)));
			out.write(CLOSE_KEY);
			switch (type) {
			case Boolean:
				out.write((boolean) entry.getValue() ? 1 : 0);
				break;
			case Number:
				long lng = (long) entry.getValue();
				byte[] b = new byte[] {
					       (byte) lng,
					       (byte) (lng >> 8),
					       (byte) (lng >> 16),
					       (byte) (lng >> 24),
					       (byte) (lng >> 32),
					       (byte) (lng >> 40),
					       (byte) (lng >> 48),
					       (byte) (lng >> 56)};
				out.write(b);
				break;
			case String:
				out.write(escapeBytes(((String) entry.getValue()).getBytes(JNN.CHARSET)));
				break;
			case Null:
				break;
			}
			out.write(CLOSE_ENTRY);
		}
		out.write(END);
	}

	@Override
	public void flush() throws IOException {
		out.flush();
	}

	@Override
	public void close() throws IOException {
		out.close();
	}

	@Override
	public void write(int b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b) throws IOException {
		out.write(b);
	}

	@Override
	public void write(byte[] b, int off, int len) throws IOException {
		out.write(b, off, len);
	}
}
