package helpers;

import java.util.ArrayList;
import java.util.List;

public final class Singleton {

	private static Singleton instance;
	private static List<String> values;

	private Singleton(List<String> values) {
		Singleton.values = values;
	}

	public static Singleton getInstance() {
		if (instance == null) {
			List<String> values = new ArrayList<String>();
			instance = new Singleton(values);
		}
		return instance;
	}

	public static boolean checkIfExists(String value) {
		if (values.contains(value)) {
			return true;
		} else {
			values.add(value);
			return false;
		}
	}

	public static boolean checkIfExists(Integer value) {
		String val = value.toString();
		return checkIfExists(val);		
	}

}
