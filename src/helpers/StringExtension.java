package helpers;

public class StringExtension {

	public static String repeat(String value, Integer quantity) {
		 String rep = "";
		 for(int i=0;i<quantity;i++) {
		    rep += value;
		 }
		 return rep;
	}

}
