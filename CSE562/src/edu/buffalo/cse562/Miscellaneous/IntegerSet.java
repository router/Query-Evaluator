package edu.buffalo.cse562.Miscellaneous;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;

import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;

public class IntegerSet implements Comparable<IntegerSet>,Serializable{
	
	
	//public ArrayList<Integer> list;
	public int value1;
	public int value2;
	public IntegerSet()
	{
		//list=new ArrayList<Integer>();

	}
	
	private void writeObject(ObjectOutputStream oos) throws IOException
	{
		try
		{
//			oos.writeInt(this.list.get(0));
//			oos.writeInt(this.list.get(1));
			oos.writeInt(value1);
			oos.writeInt(value2);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private void readObject(ObjectInputStream in) throws IOException, ClassNotFoundException
	{
		try
		{
//			if(this.list==null)
//				this.list=new ArrayList<Integer>();
			value1=in.readInt();
			value2=in.readInt();
//			this.list.add(in.readInt());
//			this.list.add(in.readInt());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public String toString()
	{
		return "test";
	}
	
	@Override
	public int compareTo(IntegerSet o2) {
		
//		int val1=0,val2=0;
//		for(int i=0;i<this.list.size();i++)
//		{
//			val1=this.list.get(i);
//			val2=o2.list.get(i);
//			
//			if(val1!=val2)
//				return val1-val2;
//			
//		}
//		return val1-val2;
		if(this.value1!=o2.value1)
			return this.value1-o2.value1;
		
		return this.value2-o2.value2;
		
	}
	public static class IntegerSetSerializer implements Serializer<IntegerSet>
	{
		public void serialize(SerializerOutput out,IntegerSet obj) throws IOException
		{
//			for(Integer i: obj.list)
//			{
//				out.writeInt(i.intValue());
//			}
			out.writeInt(obj.value1);
			out.writeInt(obj.value2);
		}
		public IntegerSet deserialize(SerializerInput in) throws IOException, ClassNotFoundException
		{
//			IntegerSet result=new IntegerSet();
//			int value=0;
//			int byteCount=0;
//			while((byteCount=in.available())>0)
//			{
//				value=in.readInt();
//				System.out.println(value);
//				
//				result.list.add(value);
//			}
//			return result;
//				
//		}
	     
			IntegerSet result=new IntegerSet();
			result.value1=in.readInt();
			result.value2=in.readInt();
			return result;
		}
	}
}