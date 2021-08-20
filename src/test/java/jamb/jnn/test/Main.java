package jamb.jnn.test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
		System.out.println();
		
		reverse_compat_test();
		System.out.println();
		
		test_size_001();
		System.out.println();
	}
	
	private static final byte[] plainText = "Jonathan Monkeys".getBytes(StandardCharsets.UTF_8);
	
	private static boolean checkPlainText(InputStream in) throws IOException {
		byte[] plainTextRead = in.readNBytes(plainText.length);
		if (!Arrays.equals(plainText, plainTextRead)) {
			System.err.println("Invalid plain text: " + new String(plainTextRead));
			return false;
			//System.exit(1);
		}
		return true;
	}
	
	private static JNNObject obj001() {
		JNNObject obj = new JNNObject();
		obj.set("name", "Jonathan Monke" + Character.valueOf((char) JNN.CLOSE_ENTRY) + "y");
		obj.set("age", 18);
		obj.set("isMale", true);
		return obj;
	}
	
	public static void reverse_compat_test() throws IOException {
		JNNObject obj = obj001();
		
		JNNInputStream jis = new JNNInputStream(new FileInputStream(new File("src/test/resources/", "test001.jnn")));
		JNNObject readObj = jis.readJNNObject();
		checkPlainText(jis);
		jis.close();
		System.out.println("reverse compat test (001) " + (readObj.equals(obj) ? "passed" : " failed"));
	}
	
	public static void test_size_001() throws IOException {
		File f001 = new File("src/test/resources", "test001.jnn");
		ByteArrayOutputStream fNew = new ByteArrayOutputStream();
		JNNOutputStream out = new JNNOutputStream(fNew);
		out.writeJNNObject(obj001());
		out.close();
		
		int f001_size = (int) (f001.length() - plainText.length);
		int new_size = fNew.size();
		System.out.println(f001_size + " (001) vs " + new_size + " (" + JNN.FORMAT_VERSION + ")");
		System.out.println(new BigDecimal((double) (f001_size - new_size) / f001_size * 100).setScale(2, RoundingMode.HALF_EVEN) + "% difference");
	}

	public static void read_write_test() throws IOException {
		JNNObject obj = new JNNObject();
		obj.set("name", "Jonathan Monke" + Character.valueOf((char) JNN.CLOSE_ENTRY) + "y");
		obj.set("age", 18);
		obj.set("isMale", true);
		JNNObject testObj = new JNNObject();
		testObj.set("testkey", "testvalue");
		testObj.set("testkey", "testvalue");
		testObj.set("recursive_check", new JNNObject().set("testkey2", 2).set("thisisnull", null));
		obj.set("test", testObj);
		obj.set("monkey", null);


		JNNOutputStream out = new JNNOutputStream(
				new FileOutputStream(new File("src/test/resources/", "test.jnn")));
		out.writeJNNObject(obj);
		out.write(plainText);
		out.close();
		System.out.println("object written");
		
		JNNInputStream in = new JNNInputStream(new FileInputStream(new File("src/test/resources/", "test.jnn")));
		JNNObject obj2 = in.readJNNObject();
		System.out.println("object read");
		checkPlainText(in);
		in.close();
		System.out.println("original	:" + obj.toString());
		System.out.println("read		:" + obj2.toString());
		if (!obj.equals(obj2)) {
			System.err.println("both object aren't equal!");
			System.exit(1);
		} else
			System.out.println("both objects are equal!");
	}
}
