package edu.buffalo.cse562.Operators;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.SortedMap;
import java.util.TreeMap;

import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryTreeMap;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.Evaluators.ArithmeticEvaluator;
import edu.buffalo.cse562.Miscellaneous.CustomSecondaryKeyExtractor;
import edu.buffalo.cse562.Miscellaneous.IndexScanOperation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.TupleSerializer;

public class IndexScanner<PrimaryKeyType extends Comparable> extends ASTNode{
	private static IndexScanner<String> idS;
	static RecordManager lRecordManager;
	static String indexDir;
	protected IndexScanner(String dir) throws IOException
	{
		indexDir = dir;
		lRecordManager = RecordManagerFactory.createRecordManager(indexDir+"/Index");
	}
	public static IndexScanner<String> getInstance(String dir) throws IOException
	{
		if(idS == null && indexDir == null)
		{
			idS = new IndexScanner<String>(dir);
			indexDir = dir;
		}
		return idS;
	}
	
	public InnerScanner returnClass(SQLTable parent)
	{
		return new InnerScanner(parent);
	}
	public class InnerScanner
	{
		SQLTable parentTable;
		IndexScanOperation operation;
		Iterator<String> primaryTupleIterator;
		Iterator<PrimaryKeyType> secondaryIterator;
		Iterator<String> primaryTableIterator;
		SortedMap<String,Iterable<PrimaryKeyType>> resultMap;
		PrimaryTreeMap<PrimaryKeyType,Tuple> primaryMap;
		int flag = 0;
		boolean shouldContinue;
		public InnerScanner(SQLTable parent)
		{
			parentTable=parent;
			primaryTupleIterator=null;
			secondaryIterator=null;
			primaryTableIterator = null;
			resultMap=null; 
			
			String primaryMapName=parentTable.getTableName().toLowerCase()+"_primary";
			primaryMap=(PrimaryTreeMap<PrimaryKeyType, Tuple>) lRecordManager.treeMap(primaryMapName,
					new TupleSerializer(parentTable));
		}
		
		public void run(IndexScanOperation op)
		{
			try
			{
				//parentTable=table;
				shouldContinue=true;
				operation=op;
				if(operation == null)
				{
					return;
				}
				if(operation.type.equals("SCAN"))
				{
					flag = 2; //help I am primary!
					/*
					 * 0 : Secondary
					 * 1 : Primary
					 * 2 : Scan
					 */
                                }
				else if(operation.type.equals("PRIMARY"))
				{
					flag = 1;
				}
				//RecordManager lRecordManager=RecordManagerFactory.createRecordManager(indexDir+"/"+parentTable.getTableName().toLowerCase());
				
				//long tp = lRecordManager.getNamedObject(primaryMapName);
				
				
//				for(PrimaryKeyType s:primaryMap.keySet())
//				{
//					System.out.println(s+"--->"+primaryMap.get(s).convertToString());
//				}
//				
				String fKeyMapName=parentTable.getTableName().toLowerCase()+"_"+operation.key;
				SecondaryTreeMap<String,PrimaryKeyType,Tuple> secondaryMap=primaryMap.secondaryTreeMap(fKeyMapName,new CustomSecondaryKeyExtractor<String,PrimaryKeyType,Tuple>(operation.key));
				SortedMap<String, Iterable<PrimaryKeyType>> tempResult=secondaryMap.subMap(String.valueOf(operation.lowerLimit), String.valueOf(operation.upperLimit));
				TreeMap<String, Iterable<PrimaryKeyType>> tempResultOne = new TreeMap<String,Iterable<PrimaryKeyType>>();
				if(operation.upperInclusive)
				{
					Iterable<PrimaryKeyType> result = secondaryMap.get(String.valueOf(operation.upperLimit));
					if(result != null)
						tempResultOne.put(String.valueOf(operation.upperLimit), result); //Copy upper limit.
					//This is tested and working!!!
					
				}
				/*
				 * This is actually not required, but just to keep the type of map consistent, and to display in the loop below. 
				 */
				/*SortedMap<String,Iterable<PrimaryKeyType>> tempResultOnenew = (SortedMap<String,Iterable<PrimaryKeyType>>) tempResultOne;
				for(Entry<String,Iterable<PrimaryKeyType>> entry : tempResultOnenew.entrySet())
				{
					Iterable<PrimaryKeyType> itr = entry.getValue();
					for(PrimaryKeyType p : itr)
					{
						System.out.println(primaryMap.find(p).finalPrint());
						//primaryMap.find(p);
					}
				}
				resultMap=tempResultOnenew;*/
				tempResultOne.putAll(tempResult); //Copy rest of values from tempResult
				resultMap=(SortedMap<String,Iterable<PrimaryKeyType>>)tempResultOne;
				primaryTupleIterator=resultMap.keySet().iterator();
				primaryTableIterator = (Iterator<String>) primaryMap.keySet().iterator();
				if(operation.lowerInclusive==false)
				{
					if(!primaryTupleIterator.hasNext()) // null set return
						return;
					
					primaryTupleIterator.next();
				}
				
				if(flag == 0 && primaryTupleIterator.hasNext())
					secondaryIterator=resultMap.get(primaryTupleIterator.next()).iterator();
				else
				{
					//primary map scan
					resultMap.clear();
				}
				secondaryMap=null;
				//tempResult.clear();
				tempResult=null;
				tempResultOne=null;
				//tempResultOne.clear();
				System.gc();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
		public boolean hasNextTuple()
		{
			boolean value=primaryTupleIterator.hasNext() || (secondaryIterator!=null && secondaryIterator.hasNext()) || (primaryTableIterator.hasNext() && flag == 2) || (!primaryMap.isEmpty() && flag == 1 && shouldContinue);
			if(!value)
			{
			//	primaryMap.clear();primaryMap=null;
				resultMap.clear();
				//resultMap=null;
				
			}
			return value;
		}
		
		public Tuple readNextTuple()
		{
			try {
			if(secondaryIterator != null && secondaryIterator.hasNext())
				//Subhranil, yahan se comparator call ho raha hai.
			{
				
				//TODO
				/*
				 * Integer being casted to String here!!!!
				 * Causing crash in tpch10!!!
				 */
				String val=(String)secondaryIterator.next();
				Tuple tupe=primaryMap.get(val);
				return tupe;
			}
			if(hasNextTuple())
			{
				if(flag == 1 && !primaryMap.isEmpty())
				{
					//System.out.println(primaryMap.keySet().getClass());
					
				
//					for(PrimaryKeyType k:primaryMap.keySet())
//					{
//						System.out.println(k.getClass());
//						break;
//					}
					//System.out.println(primaryMap.find((PrimaryKeyType)).convertToString());
					if(primaryMap.containsKey((PrimaryKeyType)operation.lowerLimit))
					{

						if(operation.lowerLimit != operation.upperLimit)
							throw new Exception("ABEY EQUAL NAHI HAI!");
					

						Tuple t= primaryMap.get(operation.lowerLimit);
						boolean b = t.setOwner(parentTable);
						shouldContinue=false;
						return t;
					}
					else
						return null;
				}
				else if(flag == 0 && primaryTupleIterator.hasNext() )
				{
					secondaryIterator=resultMap.get(primaryTupleIterator.next()).iterator();
					return readNextTuple();
				}
				else if(primaryTableIterator.hasNext() && flag == 2)
				{
//					String key=null;
//					if(!primaryTableIterator.hasNext())
//						System.out.println("asdsd");
//					//else
//						//key=.getClass().toString();
					Tuple t=primaryMap.get(primaryTableIterator.next());
					boolean b = t.setOwner(parentTable);
					return t;
				}
			}
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			return null;
		}
		public void close()
		{
			primaryMap.clear();
			primaryMap=null;
			System.gc();
		}
		public Tuple validateTuple(Tuple newTuple, ArrayList<Expression> filters) {
			SelectNode selectnode = new SelectNode();
			selectnode.setAliasMap(aliasMap);
			boolean isValidTuple = true;
			
			if(filters != null && !filters.isEmpty())
			{
				Iterator<Expression> expressions = filters.iterator();
				Expression expression;
				
				while(expressions.hasNext())
				{
					expression = expressions.next();
					if(!selectnode.filterTuple(newTuple, expression))
					{
						isValidTuple=false;
						break;
					}
				}
			}
			if(isValidTuple)
				return newTuple;
			else
				return null;
		}	
	}
		
	public void handleAlias(Tuple t)
	{
		//System.out.println("Handle Alias!");
		
		//System.out.println("Value Size: " + t.values().size());
		//System.out.println("Schema Size: " + t.getOwner().getSchema().size());
		
		if(aliasMap != null && !aliasMap.isEmpty())
		{
			if(true)
			{
				Iterator it=aliasMap.keySet().iterator();
				while(it.hasNext()) //&& t.values().size() == t.getOwner().getSchema().size())
				{
					String aName=it.next().toString();
					Expression exp = aliasMap.get(aName);
					if(aName.equals(exp.toString()) || exp instanceof Function)
						continue;
					
					if(ArithmeticEvaluator.isArithMeticOperator(exp))
					{
						ArithmeticEvaluator aeva=new ArithmeticEvaluator(t);
						exp.accept(aeva);
						
						// To drop the trailing zeroes 
						double result = aeva.getResult();
						DecimalFormat df=new DecimalFormat("###.#");
						
						//Add Filed Type
						t.setValue(aName, "dec", df.format(result));
					}
					else // normal field alias
					{
						//Add Filed Type
						
						Expression exp1 = aliasMap.get(aName);
						String type = null;
						if(exp1 instanceof Column) {
							type = "str";
						}
						else {
							type = "dec";
						}
						t.setValue(aName, type, t.valueForField(exp.toString()));
					}
				}
			}
		}	
	}

	public Tuple validateTuple(Tuple newTuple, ArrayList<Expression> filters) {
		SelectNode selectnode = new SelectNode();
		selectnode.setAliasMap(aliasMap);
		boolean isValidTuple = true;
		
		if(filters != null && !filters.isEmpty())
		{
			Iterator<Expression> expressions = filters.iterator();
			Expression expression;
			
			while(expressions.hasNext())
			{
				expression = expressions.next();
				if(!selectnode.filterTuple(newTuple, expression))
				{
					isValidTuple=false;
					break;
				}
			}
		}
		if(isValidTuple)
			return newTuple;
		else
			return null;
	}
}
