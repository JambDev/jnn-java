package jamb.jnn.io;

import static jamb.jnn.JNN.CLOSE_ENTRY;
import static jamb.jnn.JNN.CLOSE_KEY;
import static jamb.jnn.JNN.END;
import static jamb.jnn.JNN.ESCAPE;

import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStream;

import jamb.jnn.JNN;
import jamb.jnn.JNNObject;
import jamb.jnn.JNNOptions;
import jamb.jnn.JNNPrimitive;
import jamb.jnn.io.exception.JNNInvalidHeaderException;
import jamb.jnn.io.exception.JNNInvalidPrimitiveException;

public class JNNInputStream extends InputStream {
	private final InputStream in;

	public JNNInputStream(InputStream in) {
		this.in = in;
	}

	private JNNPrimitive primitive(int primitiveType) {
		for (JNNPrimitive primitive : JNNPrimitive.values())
			if (primitive.identifier == primitiveType)
				return primitive;
		return null;
	}

	public JNNObject readJNNObject() throws IOException, JNNInvalidHeaderException {
		// read header
		byte[] header = new byte[6];
		in.read(header, 0, header.length);
		JNNOptions opts = JNN.validateHeader(header);
		if (opts == null)
			throw new JNNInvalidHeaderException();
		// close header
		in.read();

		JNNObject jnnObj = new JNNObject();

		int b;
		while ((b = in.read()) != END) {
			JNNPrimitive primitiveType = primitive(b);
			if (primitiveType == null)
				throw new JNNInvalidPrimitiveException();
			// read key
			ByteArrayOutputStream key = new ByteArrayOutputStream();
			while ((b = in.read()) != CLOSE_KEY) {
				if (b == -1)
					throw new EOFException(primitiveType.name() + " entry's key was never closed");
				if (b == ESCAPE)
					b = in.read();
				key.write(b);
			}
			Object val = null;
			switch (primitiveType) {
			case Boolean:
				int boolVal = in.read();
				if (boolVal == -1)
					throw new EOFException("Boolean entry is corrupted #" + key);
				val = Boolean.valueOf(boolVal == 0 ? false : true);
				if(opts.exclusivelyCloseEntry) in.read();
				break;
			case Number:
				byte[] longBytes = new byte[8];
				in.read(longBytes, 0, longBytes.length);
				for (byte b1 : longBytes) {
					if (b1 == -1)
						throw new EOFException("Number entry is corrupted #" + key);
				}
				/* https://stackoverflow.com/a/27610608 */
				long l = ((long) longBytes[7] << 56) | ((long) longBytes[6] & 0xff) << 48
						| ((long) longBytes[5] & 0xff) << 40 | ((long) longBytes[4] & 0xff) << 32
						| ((long) longBytes[3] & 0xff) << 24 | ((long) longBytes[2] & 0xff) << 16
						| ((long) longBytes[1] & 0xff) << 8 | ((long) longBytes[0] & 0xff);
				val = Long.valueOf(l);
				if(opts.exclusivelyCloseEntry) in.read();
				break;
			case String:
				ByteArrayOutputStream valueBytes = new ByteArrayOutputStream();
				while ((b = in.read()) != CLOSE_ENTRY) {
					if (b == -1)
						throw new EOFException("String entry was never closed #" + key);
					if (b == ESCAPE)
						b = in.read();
					valueBytes.write(b);
				}
				val = valueBytes.toString(JNN.CHARSET);
				break;
			case Null:
				if(opts.exclusivelyCloseEntry) {
					while ((b = in.read()) != CLOSE_ENTRY)
						if(b == ESCAPE) b = in.read();
				}
				val = null;
				break;
			case JNN:
				JNNInputStream jis = new JNNInputStream(in);
				val = jis.readJNNObject();
				jis = null;
				if(opts.exclusivelyCloseEntry) in.read();
				break;
			}
			jnnObj.set(key.toString(JNN.CHARSET), val);
		}

		return jnnObj;
	}

	@Override
	public byte[] readAllBytes() throws IOException {
		return in.readAllBytes();
	}

	@Override
	public int readNBytes(byte[] b, int off, int len) throws IOException {
		return in.readNBytes(b, off, len);
	}

	@Override
	public byte[] readNBytes(int len) throws IOException {
		return in.readNBytes(len);
	}

	@Override
	public void close() throws IOException {
		in.close();
	}

	@Override
	public int read() throws IOException {
		return in.read();
	}

	@Override
	public int read(byte[] b) throws IOException {
		return in.read(b);
	}

	@Override
	public int read(byte[] b, int off, int len) throws IOException {
		return in.read(b, off, len);
	}
}
