package edu.buffalo.cse562.Miscellaneous;

import java.io.Serializable;
import java.util.Comparator;

public class SpecialStringComparator implements Comparator<String>,Serializable{

	@Override
	public int compare(String o1, String o2) {
		// TODO Auto-generated method stub
		String[] str1 = o1.split("#");
		String[] str2 = o2.split("#");
		int val1 = Integer.parseInt(str1[0]);
		int val2 = Integer.parseInt(str1[1]);
		int val3 = Integer.parseInt(str2[0]);
		int val4 = Integer.parseInt(str2[1]);
		
		if(val1 != val3)	
			return val1 - val3;
		
		return val2 - val4;
	}
}
