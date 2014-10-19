package edu.buffalo.cse562.Operators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import edu.buffalo.cse562.Miscellaneous.OrderDetails;
import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.fieldDetails;

public class JoinNode extends ASTNode{
	
	long joinOutputTupleCount;
	private HashMap<String, Relation> rightSideHash;
	private GroupingNode gNode;
	
	public JoinNode()
	{
		gNode=new GroupingNode();
		rightSideHash=new HashMap<String,Relation>();
	}
	
	private Tuple mergeTuples(Tuple t1,Tuple t2,SQLTable parentTable)
	{
		ArrayList<String> values=new ArrayList<String>();
		Iterator<fieldDetails> schemaItr = parentTable.getSchema().iterator();
		while(schemaItr.hasNext())
		{
			fieldDetails field = schemaItr.next();
			if(t1.getOwner().getSchema().contains(field))
				values.add(t1.valueForField(field.getName()));
			else if(t2.getOwner().getSchema().contains(field))
				values.add(t2.valueForField(field.getName()));
		}
		
		Tuple newTuple=parentTable.createNewTupleWithValues(values);
		
		if(ASTNode.joinTables.size()==2 && ASTNode.isNestedQuery==true)
		{
			try
			{
				IndexScanner<String> sc=IndexScanner.getInstance(ASTNode.indexDir);
				sc.setAliasMap(aliasMap);
				sc.handleAlias(newTuple);
			}
			catch(Exception e)
			{
				System.out.println("Merge Tuple Crashed!");
				e.printStackTrace();
			}
				
		}
			
		return newTuple;
	}

	/*
	 * Add this SQLTable to the tables HashMap in Parser.java
	 */
	private SQLTable createSQLTable(SQLTable t1, String k1, SQLTable t2, String k2)
	{
		ArrayList<fieldDetails> newSchema = new ArrayList<fieldDetails>();//.getSchema();//t1.getProjectedFields();
		newSchema.addAll(t1.getSchema());
		newSchema.addAll(t2.getSchema());
		
		SQLTable newTable = new SQLTable(t1.getTableAlias()+t2.getTableAlias(), newSchema);
		
		if(ASTNode.joinTables.size()==2 && ASTNode.isNestedQuery==true)
		{
			ArrayList<fieldDetails> finalSchema=new ArrayList<fieldDetails>();
			for(String s:ASTNode.innerProjectFields)
			{
				Expression exp=aliasMap.get(s);
				if(exp instanceof Function || exp instanceof BinaryExpression)
					finalSchema.add(new fieldDetails(s, newTable.getTypeFormExpression(exp)));
				else
					finalSchema.add(new fieldDetails(s, newTable.getTypeFormFieldName(exp.toString())));
				
			}
			
			newSchema.addAll(finalSchema);
			newTable.setSchema(newSchema);
		}		
		//newSql.setTupleCount(joinOutputTupleCount);
		
		ArrayList<fieldDetails> filteredSchema = filterFields(t1,t2);
		newTable.setSchema(filteredSchema);
		//newTable.setSchema(newSchema);
		newTable.setisJoinoutput(true);
		return newTable;
	}
	
	
	private ArrayList<fieldDetails> filterFields(SQLTable t1, SQLTable t2)
	{
		ArrayList<fieldDetails> oldSchema = new ArrayList<fieldDetails>(t1.getSchema());
		oldSchema.addAll(t2.getSchema());
		
		ArrayList<fieldDetails> newSchema = new ArrayList<fieldDetails>();
		
		Iterator<fieldDetails> schemaItr = oldSchema.iterator();
		while(schemaItr.hasNext())
		{
			fieldDetails field = schemaItr.next();
			
			if(distinctField != null && distinctField.equals(field.getName()))
			{
				if(!newSchema.contains(field))
				{	
					newSchema.add(field);
					continue;
				}
			}
			
			else if(projectFields != null)
			{
				if(projectFields.contains(field.getName()))
				{
					if(!newSchema.contains(field))
					{	
						newSchema.add(field);
						continue;
					}
				}
				
				else
				{
					for(String alias : projectFields)
					{
						if(aliasMap.containsKey(alias))
						{
							String exp = aliasMap.get(alias).toString();
							if(exp.contains(field.getName()))
							{
								if(!newSchema.contains(field))
								{	
									newSchema.add(field);
									continue;
								}
							}
						}
					}
				}
			}
			
			if(innerProjectFields != null && innerProjectFields.contains(field.getName()))
			{
				if(!newSchema.contains(field))
				{	
					newSchema.add(field);
					continue;
				}
				
			}
			
			if(groupFields != null && !groupFields.isEmpty() && groupFields.contains(field.getName()))
			{
				if(!newSchema.contains(field))
				{	
					newSchema.add(field);
					continue;
				}
			}
			
			if(orderFields != null && !orderFields.isEmpty() && orderFields.contains(new OrderDetails(field.getName(), "ASC")) || orderFields.contains(new OrderDetails(field.getName(), "DESC")))
			{
				if(!newSchema.contains(field))
				{	
					newSchema.add(field);
					continue;
				}
			}
			
			if(joinPredicates != null && !joinPredicates.isEmpty())
			{
				Iterator<Expression> joinItr = joinPredicates.iterator();
				while(joinItr.hasNext())
				{
					String joinExp = joinItr.next().toString();
					if(joinExp.contains(field.getName()))
					{
						if(!newSchema.contains(field))
						{	
							newSchema.add(field);
							continue;
						}
					}
				}
			}
			
			if(extraPredicates != null && !extraPredicates.isEmpty())
			{
				Iterator<Expression> joinItr = extraPredicates.iterator();
				while(joinItr.hasNext())
				{
					String joinExp = joinItr.next().toString();
					if(joinExp.contains(field.getName()))
					{
						if(!newSchema.contains(field))
						{	
							newSchema.add(field);
							continue;
						}
					}
				}
			}
		}	
		
		return newSchema;
	}
	
	
	public String projectWithoutDistinct(Tuple tup,ArrayList<String> projectCols, HashMap<String, Expression> aMap)
	{
		String output="";
		Iterator<String> it=projectCols.iterator();
		while(it.hasNext())
		{
			String field=it.next();
			String field1 = aMap.get(field).toString();
			if(!field1.toUpperCase().contains("DISTINCT"))
				output=output+tup.valueForField(field)+"|";
		} 
		return output;
	}
	
	/*
	 * This function will be called by all the join methods!
	 * Used to find the join key!
	 */
	
	private String findRightJoinKey(String leftKey) 
	{
		Iterator<Expression> joinItr = joinPredicates.iterator();
		while(joinItr.hasNext())
		{
			String joinExp = joinItr.next().toString();
			if(joinExp.contains(leftKey))// && joinExp.contains(tableName2))
			{
				String[] comp=joinExp.split("=");
				if(comp[0].trim().equals(leftKey))
					return comp[1].trim();
				else
					return comp[0].trim();
			}
		}
 		return null;
	}
	
	public SQLTable Join(SQLTable table1, SQLTable table2) throws IOException {
		
		SQLTable resultTable = null;
		
		String key1=table1.getJoinSet().first().key;
		String key2=findRightJoinKey(key1);
		
		String fKey=null;
		if(!table2.getJoinSet().isEmpty())
			fKey=table2.getJoinSet().first().key;
		if(!rightSideHash.isEmpty())
			resultTable = HashJoin(table2,key2, table1,key1,fKey);
		else
			resultTable = HashJoin(table1,key1, table2,key2,fKey);

		return resultTable;
	}

	private SQLTable HashJoin(SQLTable table1,String key1, SQLTable table2,String key2,String futureKey) // the right hand side table has to be the smaller table or the previous join output
	{
		
		SQLTable resultTable=createSQLTable(table1, key1, table2, key2);//new SQLTable(table1.getTableAlias()+table2.getTableAlias(),newSchema);
		if(ASTNode.joinTables.size()==2  && groupFields != null)
			initializeGrouping(resultTable);
		
		//HashMap<String, Relation> rightSideHash=new HashMap<String,Relation>();
		SQLTable lTable=table1,rTable=table2;
		String lKey=key1,rKey=key2;
		if(table1.getTupleCount()<table2.getTupleCount()&& rightSideHash.isEmpty())//&& table2.getIndexLookupOperation()==null)
		{
			lTable=table2;
			lKey=key2;
			rTable=table1;
			rKey=key1;
		}

		
		//scanning the right relation
		try 
		{
			IndexScanner<String>.InnerScanner iscnInner=null;
			Iterator<Tuple> tupleIt=null;
			ScannerNode scn1=null;
			
			if(rightSideHash.isEmpty())
			{

				if(rTable.getIndexLookupOperation()!=null)
				{
		
					IndexScanner<String>iscn=IndexScanner.getInstance(ASTNode.indexDir);
					iscnInner=iscn.returnClass(rTable);
					
					iscnInner.run( rTable.getIndexLookupOperation());
				}
				else
					scn1=new ScannerNode(ASTNode.baseDir, rTable, null, false);
				//else
					//tupleIt=rTable.getRelation().getTuples().iterator();
			   	while((iscnInner!=null && iscnInner.hasNextTuple()) || (scn1!=null && scn1.hasNextTuple()) ||(tupleIt!=null &&tupleIt.hasNext() ))
				{
					Tuple tuple=null;
					if(rTable.getIndexLookupOperation()!=null)
					{	
						tuple=iscnInner.readNextTuple();
						tuple = iscnInner.validateTuple(tuple, rTable.getFilters());
					}
					else
					{
						tuple=scn1.readNextTuple();
						tuple = scn1.validateTuple(tuple, rTable.getFilters());
					}
					
				//	tuple = scn1.validateTuple(tuple, rTable.getFilters());
					if(tuple==null)
						continue;
					
					String value=tuple.valueForField(rKey);
					if(!rightSideHash.containsKey(value))
						rightSideHash.put(value, new Relation());
					
					rightSideHash.get(value).addTuple(tuple);
				}

			}
			
			//System.out.println("");
			//scanning the left relation
			
			ScannerNode scn2=null;
			if(lTable.getIndexLookupOperation()!=null && !lTable.getTableName().equals("LINEITEM"))
			{
	
					IndexScanner<String>iscn=IndexScanner.getInstance(ASTNode.indexDir);
					iscnInner=iscn.returnClass(lTable);
					iscnInner.run(lTable.getIndexLookupOperation());
			}
			else
				scn2=new ScannerNode(ASTNode.baseDir, lTable, null,false);
			
			HashMap<String,Relation>tempResult=new HashMap<String,Relation>();
			while((iscnInner!=null && iscnInner.hasNextTuple())|| (scn2 !=null && scn2.hasNextTuple()) || (tupleIt!=null && tupleIt.hasNext()))
			{
				Tuple tuple=null;
				if(iscnInner!=null && lTable.getIndexLookupOperation()!=null)
				{
					tuple=iscnInner.readNextTuple();
					tuple = iscnInner.validateTuple(tuple, lTable.getFilters());
				}
				else
				{
					tuple=scn2.readNextTuple();
					if(lTable.getIndexLookupOperation()!=null)// spl case for lineitem
					{
						String value=tuple.valueForField(lTable.getTableAlias()+"."+lTable.getIndexLookupOperation().key);
						String low=lTable.getIndexLookupOperation().lowerLimit;
						String high=lTable.getIndexLookupOperation().upperLimit;
						boolean finalResult=false;
						if(lTable.getIndexLookupOperation().lowerInclusive)
							finalResult=(value.compareTo(low)>=0);
						else
							finalResult=(value.compareTo(low)>0);
						
						if(finalResult)
						{
							if(lTable.getIndexLookupOperation().upperInclusive)
								finalResult=finalResult && (value.compareTo(high)<=0);
							else
								finalResult= finalResult && (value.compareTo(high)<0);
						}
						if(!finalResult)
							continue;
					}
					
					tuple = scn2.validateTuple(tuple, lTable.getFilters());
				}
				
				if(tuple==null)
					continue;
				String value=tuple.valueForField(lKey);
				if(rightSideHash.containsKey(value))
				{
					Iterator<Tuple> itRel=rightSideHash.get(value).getTuples().iterator();
					while(itRel.hasNext())
					{
						Tuple tup=mergeTuples(tuple,(Tuple)itRel.next(),resultTable);
						if(tup!=null)
							//output += tup.convertToString() + "\n";
						{
				
							if(ASTNode.joinTables.size()==2  && groupFields != null) // last join
							{
//								System.out.println(tup.finalPrint());
								if(ASTNode.distinctField==null)
									gNode.groupTuple(tup);
								else
									gNode.addToMap(tup);
							}
							else
							{
								//resultTable.getRelation().addTuple(tup);
								String value1=tup.valueForField(futureKey);
								if(!tempResult.containsKey(value1))
									tempResult.put(value1, new Relation());
								
								tempResult.get(value1).addTuple(tup);
								
							}
							
							joinOutputTupleCount++;
						}
					}
				}
			}
			//End of join
			rightSideHash.clear();
			rightSideHash=null;
			System.gc();
			rightSideHash=tempResult;
		}
		catch(Exception e)
		{
			System.out.println("HashJoin Causes Exception!" + table1.getTableName() + " " + table2.getTableName());
			e.printStackTrace();
		}
		return resultTable;
	}

private void  initializeGrouping(SQLTable resultTable)
	{
		gNode=new GroupingNode();
		gNode.initializeGrouping(resultTable);
	}
	public void finalizeGrouping()
	{
		gNode.processGroupOutput();
	}
}


