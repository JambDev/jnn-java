package jamb.jnn;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map.Entry;

import jamb.jnn.exception.JNNDoesNotExistException;
import jamb.jnn.exception.JNNWrongTypeException;

public class JNNObject {
	private LinkedHashMap<String, Object> jnnMap;

	public JNNObject() {
		this.jnnMap = new LinkedHashMap<String, Object>();
	}

	public JNNObject(JNNObject obj) {
		this.jnnMap = obj.jnnMap;
	}

	public static JNNPrimitive getPrimitive(Object obj) {
		if(obj == null)
			return JNNPrimitive.Null;
		else if (obj.getClass() == String.class)
			return JNNPrimitive.String;
		else if (obj.getClass() == Long.class)
			return JNNPrimitive.Number;
		else if (obj.getClass() == Boolean.class)
			return JNNPrimitive.Boolean;
		// maybe throw a null pointer?
		return null;
	}

	public Collection<Entry<String, Object>> entries() {
		return jnnMap.entrySet();
	}

	public Collection<Object> values() {
		return jnnMap.values();
	}

	public Collection<String> keys() {
		return jnnMap.keySet();
	}

	public boolean has(String key) {
		return jnnMap.containsKey(key);
	}

	public void delete(String key) {
		jnnMap.remove(key);
	}

	public void set(String key, Object val) {
		if (key == null)
			throw new NullPointerException();
		if (val instanceof Number) {
			if (!(val instanceof Long))
				val = ((Number)val).longValue();
		} else if (val instanceof String)
			;
		else if (val instanceof Boolean)
			;
		else if (val == null)
			val = null;
		else
			val = val.toString();
		jnnMap.put(key, val);
	}

	public Object getObject(String key) {
		Object obj = jnnMap.get(key);
		if (obj != null || has(key))
			return obj;
		throw new JNNDoesNotExistException(key);
	}

	public long getNumber(String key) {
		Object obj = getObject(key);
		if (obj instanceof Long)
			return ((Long) obj).longValue();
		throw new JNNWrongTypeException(key, JNNPrimitive.Number, getPrimitive(obj));
	}

	public String getString(String key) {
		Object obj = getObject(key);
		if (obj instanceof String)
			return (String) obj;
		throw new JNNWrongTypeException(key, JNNPrimitive.String, getPrimitive(obj));
	}

	public boolean getBoolean(String key) {
		Object obj = getObject(key);
		if (obj instanceof Boolean)
			return ((Boolean) obj).booleanValue();
		throw new JNNWrongTypeException(key, JNNPrimitive.Boolean, getPrimitive(obj));
	}
	
	@Override
	public String toString() {
		return jnnMap.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj instanceof JNNObject)
			return jnnMap.keySet().equals(((JNNObject) obj).jnnMap.keySet());
		return jnnMap.equals(obj);
	}
}
