package jamb.jnn.test;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

import jamb.jnn.JNN;
import jamb.jnn.JNNObject;
import jamb.jnn.io.JNNInputStream;
import jamb.jnn.io.JNNOutputStream;

public class Main {
	private static final char[] HEX_ARRAY = "0123456789ABCDEF".toCharArray();

	public static String bytesToHex(byte[] bytes) {
		char[] hexChars = new char[bytes.length * 2];
		for (int j = 0; j < bytes.length; j++) {
			int v = bytes[j] & 0xFF;
			hexChars[j * 2] = HEX_ARRAY[v >>> 4];
			hexChars[j * 2 + 1] = HEX_ARRAY[v & 0x0F];
		}
		return new String(hexChars);
	}

	public static void printBytes(byte[] arr) {
		System.out.println(Arrays.toString(bytesToHex(arr).split("(?<=\\G.{2})")));
	}

	public static void main(String[] args) throws IOException {
		read_write_test();
	}

	public static void read_write_test() throws IOException {
		JNNObject obj = new JNNObject();
		obj.set("name", "Jonathan Monke" + JNN.CLOSE_ENTRY + "y");
		obj.set("age", 18);
		obj.set("isMale", true);
		obj.set("monkey", null);

		final byte[] plainText = "Jonathan Monkeys".getBytes(StandardCharsets.UTF_8);

		JNNOutputStream out = new JNNOutputStream(
				new FileOutputStream(new File("src/test/resources/", "test.jnn")));
		out.writeJNNObject(obj);
		out.write(plainText);
		out.close();
		
		JNNInputStream in = new JNNInputStream(new FileInputStream(new File("src/test/resources/", "test.jnn")));
		JNNObject obj2 = in.readJNNObject();
		byte[] plainTextRead = in.readNBytes(plainText.length);
		if (!Arrays.equals(plainText, plainTextRead)) {
			System.err.println("Invalid plain text!");
			System.exit(1);
		}
		System.out.println(obj.toString());
		System.out.println(obj2.toString());
		if (!obj.equals(obj2)) {
			System.err.println("Read/write test failed!");
			System.exit(1);
		} else System.out.println("Read/write test passed");
	}
}
