package edu.buffalo.cse562.QueryHandler;

import java.io.Serializable;
import java.util.ArrayList;

public class Tuple implements Serializable, Comparable<Tuple>{
	
	// Since we do not need to support insert/update queries, we can safely keep the type of values open. EDIT: Kill the person who wrote the previous comment. EDIT: That was me
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	//private ArrayList<String> schema;
	private ArrayList<String> values;
	private SQLTable ownerTable;
	
//	//Constructors
	
	public Tuple(SQLTable table)
	{
		ownerTable = table;
		values=new ArrayList<String>();
	}
	
	public Tuple(SQLTable table,ArrayList<String> val)
	{
		//Values=new LinkedHashMap<String,Object>();
		//schema=new ArrayList<String>();
		this.ownerTable=table;
		if(val==null)
			this.values=new ArrayList<String>();
		else
			this.values=val;
		
	}
	
	
	public Tuple(Tuple t)
	{
		//Values=	new LinkedHashMap<String,Object>();
		//this.readValuesForSchema(t.fields(), t.values(), null);
		//this.schema=new ArrayList<String>(t.fields());
		this.values=new ArrayList<String>(t.values());
		this.ownerTable=t.ownerTable;
	}
//	public Tuple(String[] schema) {}
//	
//	// Other member functions
//	public void setSchema(String[] schema) {}
//	public void readValues(String line){}
//	public void readValues(String[] values) {}
//	public void readValuesForSchema(ArrayList<String> schema, ArrayList<String> values, String table_name)
//	{
////		for(int i=0;i<schema.size();i++)
////		{
////			Values.put(schema.get(i),values.get(i));
////		}
////		
//		//this.schema=schema;
//		this.values=values;
////		if(table_name != null)
////		{
////			String tablename = table_name.concat(".");
////			for(int i=0;i<schema.size();i++)
////			{
////				String key=tablename.concat(schema.get(i));
////				Values.put(key,schema.get(i));
////			}
////		}
////		else
////		{
////			for(int i=0;i<schema.size();i++)
////			{
////				Values.put(schema.get(i),values.get(i));
////			}
////		}
//	}
	
	public void writeValues()
	{
		String temp = this.convertToString();
		if(temp.endsWith("|"))
			temp = temp.substring(0, temp.length()-1);
		
		System.out.println(temp);
	}
	
	public SQLTable getOwner()
	{
		return this.ownerTable;
	}
	
	public boolean setOwner(SQLTable tbl)
	{
		this.ownerTable = tbl;
		if(this.ownerTable.equals(tbl))
			return true;
		else
			return false;
	}
	public String valueForField(String fieldName)
	{
		int index = this.ownerTable.getIndexFromFieldName(fieldName);
		String ret=values.get(index);
		return ret.trim();
	}
	
	public void setValue(String fieldName, String fieldType, String fieldValue)
	{ 
		int index = this.ownerTable.getIndexFromFieldName(fieldName);
		if(index != -1)
		{
			values.add(index, fieldValue);
		}
		else
		{
			this.ownerTable.getSchema().add(new fieldDetails(fieldName, fieldType));
			values.add(fieldValue);
		}
	}
	
	
	public void removeField(String fieldName)
	{
		int index = this.ownerTable.getIndexFromFieldName(fieldName);
		this.ownerTable.removeField(fieldName);
		values.remove(index);
	}
//	public boolean hasField(String field)
//	{
//		return schema.contains(field);
//		//return Values.containsKey(field);
//	}
	
//	public ArrayList<String> fields()
//	{
//		return schema;
////		Object[] k= Values.keySet().toArray(); 
////		ArrayList<String> keys=new ArrayList<String>();
////		for (Object o:k)
////		{
////			keys.add(o.toString());
////		}
////		
////		return keys;
//	}
	
	public ArrayList<String> values()
	{
		return values;
		
//		Object[] v= Values.values().toArray(); 
//		ArrayList<String> values=new ArrayList<String>();
//		for (Object o:v)
//		{
//			values.add(o.toString());
//		}
//		
//		return values;
	}
	public String convertToString()
	{
	
		String output=new String();
		for( Object value : values)
		{
//			if(!start)
//				output=output+"|";
//			else
//				start=false;
			
			output=output+value+"|";
		}
//		
//		if(output.trim().endsWith("|"))
//			output = output.substring(0, output.length()-1);
		
		return output;
	}
	
	public String finalPrint()
	{
		String output = convertToString();

		if(output.endsWith("|"))
			output = output.substring(0, output.length()-1);
		
		return output;
	}

	@Override
	public int compareTo(Tuple arg0) {
		// TODO Auto-generated method stub
		boolean b1 = this.equals(arg0);
		if(b1 == true)
			return 1;
		else
			return 0;
	}	
}