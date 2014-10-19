package edu.buffalo.cse562;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

public class IntegerSet implements Serializable,Comparator<Integer>{
	
	
	public ArrayList<Integer> list;
	
	@Override
	public int compare(Integer o1, Integer o2) {
		// TODO Auto-generated method stub
		return ((Integer)o1).compareTo(o2);
	}
}