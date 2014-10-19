package edu.buffalo.cse562.QueryHandler;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import jdbm.Serializer;
import jdbm.SerializerInput;
import jdbm.SerializerOutput;
// Used in indexing
public class TupleSerializer implements Serializer<Tuple>{

	ArrayList<fieldDetails> fields;
	SQLTable parentTable;
	public TupleSerializer(SQLTable table)
	{
		this.parentTable=table;
		fields = table.getSchema();
	}
	
	/*
	public TupleSerializer(String type)
	{
		String[] temp = null;
		String[] temp1 = null;
		temp[0] = type;
		temp1[0] = field;
		this.typeArr = (ArrayList<String>) Arrays.asList(temp);
		this.fieldArr = (ArrayList<String>) Arrays.asList(temp1);
		System.out.println(fieldArr.toString());
		System.out.println(typeArr.toString());
	}
	*/
	
	@Override
	public Tuple deserialize(SerializerInput in) throws IOException, ClassNotFoundException
	{
		//Tuple tup = new Tuple(parentTable);
		Iterator<fieldDetails> fieldIterator = fields.iterator();
		ArrayList<String> values=new ArrayList<String>();
		
		while(fieldIterator.hasNext())
		{
			fieldDetails field = fieldIterator.next();
			String fieldType = field.getType();
			
			switch(fieldType)
			{
				case "int":
					values.add(String.valueOf(in.readInt()));
					//tup.setValue(field.getName(), fieldType, String.valueOf(in.readInt()));
					break;
				case "float":
					values.add(String.valueOf(in.readFloat()));
					//tup.setValue(field.getName(), fieldType, String.valueOf(in.readFloat()));
					break;
				case "dec":
					values.add(String.valueOf(in.readDouble()));
					//tup.setValue(field.getName(), fieldType, String.valueOf(in.readDouble()));
					break;
				case "bool":
					values.add(String.valueOf(in.readBoolean()));
					
				//	tup.setValue(field.getName(), fieldType, String.valueOf(in.readBoolean()));
					break;
				case "date":
					values.add(String.valueOf(in.readUTF()));
					//NOT SURE WHAT TO DO HERE, LEFT BLANK INTENTIONALLY
					//tup.setValue(field.getName(), fieldType, String.valueOf(in.readUTF()));
					break;
				case "char":
					values.add(String.valueOf(in.readChar()));
					//tup.setValue(field.getName(), fieldType, in.readUTF());
					break;
				case "str":
					//System.out.println("hey");
					values.add(in.readUTF());
					//tup.setValue(field.getName(), fieldType, in.readUTF());
					break;
				default:
					throw new IOException("Unhandled type!!: " + fieldType);
			}
			
		}
		return parentTable.createNewTupleWithValues(values);
	}

	@Override
	public void serialize(SerializerOutput out, Tuple tup) throws IOException {
		int index = 0;
		ArrayList<String> values = tup.values();
		Iterator<fieldDetails> fieldIterator = fields.iterator();
		
		while(fieldIterator.hasNext())
		{
			fieldDetails field = fieldIterator.next();
			String fieldType = field.getType();
			switch(fieldType)
			{
				case "int":
					out.writeInt(Integer.parseInt(values.get(index)));
					break;
				case "float":
					out.writeFloat(Float.parseFloat(values.get(index)));
					break;
				case "dec":
					out.writeDouble(Double.parseDouble(values.get(index)));
					break;
				case "bool":
					out.writeBoolean(Boolean.parseBoolean(values.get(index)));
					break;
				case "date":
					//String value=;
					
					out.writeUTF(String.valueOf(values.get(index)));
					break;
				case "char":
					out.writeChar(values.get(index).charAt(0));
					//out.writeUTF();
					break;
				case "str":
					out.writeUTF(String.valueOf(values.get(index)));
					break;
				default:
					throw new IOException("Unhandled type!!: " + fieldType);
			}
			index++;
		}
		
		
	}
}