package edu.buffalo.cse562.Operators;

import java.util.ArrayList;
import java.util.Iterator;

import edu.buffalo.cse562.IOHandler.FileHandler;
import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.fieldDetails;

public class ProjectNode extends ASTNode{
	
		public Relation projectRelationOnFields(Relation input,ArrayList<String> fields)
		{
			Relation output=new Relation();
			
			Iterator it=input.getTuples().iterator();
			while(it.hasNext())
			{
				Tuple t=(Tuple)(it.next());
				Tuple otp = projectTuple(t, fields); 
				output.addTuple(otp);
			}
			return output;
		}
		
		public static Tuple projectTuple(Tuple t, ArrayList<String> fields)
		{
			if(t==null)
				return null;
			
			ArrayList<fieldDetails> fieldDetails = new ArrayList<fieldDetails>();
			ArrayList<String> values = new ArrayList<String>();
			Iterator<String> fieldsItr = fields.iterator();
			
			while(fieldsItr.hasNext())
			{
				String fieldName = fieldsItr.next();
				fieldDetails.add(new fieldDetails(fieldName, "int"));
				values.add(t.valueForField(fieldName));
			}
				
			SQLTable projectTable = new SQLTable("projectTable", fieldDetails);
			Tuple tuple = projectTable.createNewTupleWithValues(values);
			return tuple;
		}
		
		public void externalProjectOnFields(String inPath, String outPath, SQLTable t, ArrayList<String> fields)
		{
			FileHandler fh = new FileHandler();
			String output = null;
			
			ScannerNode scan = new ScannerNode(null, t, inPath, true);
			while (scan.hasNextTuple())
			{
				Tuple tuple = scan.readNextTuple();
				Tuple outTuple = ProjectNode.projectTuple(tuple, fields);
				if(outPath == null)
				{
					String temp = outTuple.finalPrint();

					//temp = temp.substring(0, temp.length()-1);

					System.out.println(temp);
					outTuple = null;
					//counter++;
				}
				else
				{
					output += outTuple.convertToString() + "\n";
					
					if(output.length()%STRING_LIMIT == 0)
					{
						fh.writeFile(outPath,output);
						output = null;
					}
				}
			}
			//System.out.println(counter+" values dumped");
			fh = null;
		}
}
