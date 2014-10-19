package edu.buffalo.cse562.Operators;

import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;

public class CartesianProductNode extends ASTNode {
	
	
	// This is an extremely heavy operation. Please God forgive me :(
	
	public Relation cartesianProduct(Relation rel1,SQLTable table1,Relation rel2,SQLTable table2)
	{
		Relation output=new Relation();
		try
		{
			for(Tuple tup1: rel1.getTuples())
			{
				for(Tuple tup2: rel2.getTuples())
				{
					//Tuple otp=new Tuple(tup1);
					while(/*String field: table2.getSchema()*/ true)
					{
						//otp.readValuesForSchema(, values);
						if(/*tup1.hasField(field)*/true)// field exists in both the tables
						{
							
							// The following 6 lines willget executed for each and every pair. Redundant ?? Can be optimized. HELP !!
							//String nf1= table1.getTableName()+"."+field;
							//String nf2= table2.getTableName()+"."+field;
	//						table1.removeField(field);
	//						table2.removeField(field);
	//						table1.addField(nf1);
	//						table2.addField(nf2);
							
	
							//otp.setValue(nf1, tup1.valueForField(field));
							//otp.setValue(nf2 , tup2.valueForField(field));
							//tup1.removeField(field);
						}
						else
						{
							//otp.setValue(field, tup2.valueForField(field));
							
						}
						
						
					}
					//output.addTuple(otp);
				}
			}
			
		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace());
		}
		return output;
	}

}
