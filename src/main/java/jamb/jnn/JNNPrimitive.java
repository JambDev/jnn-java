package jamb.jnn;

public enum JNNPrimitive {
	String(0x01), Number(0x02), Boolean(0x03), Null(0x04);
	
	public final int identifier;
	JNNPrimitive(int identifier) {
		this.identifier = identifier;
	}
}
