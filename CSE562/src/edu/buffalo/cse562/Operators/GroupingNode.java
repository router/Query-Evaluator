package edu.buffalo.cse562.Operators;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.TreeSet;

import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.Evaluators.ArithmeticEvaluator;
import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.fieldDetails;

public class GroupingNode extends ASTNode{
	
	String inDirectory;
	String outDirectory;
	String groupDir;
	SQLTable table; //join output , input to groupby
	int bucketsDumped;
	
	boolean jugaad;

	ScannerNode scn;
	
	public GroupingNode()
	{
		scn=null;
	}

	public double evaluateEXpressionOnTuple(Tuple tup,Expression exp )
	{
	
		ArithmeticEvaluator aeva=new ArithmeticEvaluator(tup);
		exp.accept(aeva);
		double s=aeva.getResult();
		return s;
	}
	public double evaluateSum(Relation input,ExpressionList expl)
	{
		double sum = 0;
		
		Iterator it=expl.getExpressions().iterator();
		Expression sumParam =(Expression)it.next();
		Iterator relIt=input.getTuples().iterator();
		
		
		while(relIt.hasNext())
		{
			sum=sum + evaluateEXpressionOnTuple((Tuple)relIt.next(), sumParam);
			
			
		}
		return sum;
	}
	
	
	public String evaluateAggregateOnTuple(String key,String value,Tuple input)
	{
		//Add the fields for all alias
		String previousResult=key;
		
		if(value==null)
		{
			value="";
			//previousResult=new String();
			Iterator<fieldDetails> it=ASTNode.groupOutputTable.getSchema().iterator();
			while(it.hasNext())
			{
				
				fieldDetails field=it.next();
				if(input.getOwner().getIndexFromFieldName(field.getName())==-1) // new fields for the agg
					value+="0|";
			}
		}
		previousResult=previousResult+value;
		value="";
		
		// iterate over all the aliases and evaluate the functions
		Iterator itAlias=aliasMap.keySet().iterator();
		//Tuple newTuple= ASTNode.baseDir ;//new Tuple();
		while(itAlias.hasNext())
		{
			String aKey=itAlias.next().toString();
			Expression exp=aliasMap.get(aKey);
			//DecimalFormat df=new DecimalFormat("$0.00");
			if(exp instanceof Function)
			{
				ArrayList<String> values=new ArrayList<String>(Arrays.asList(previousResult.split("\\|")));
				Tuple tup=ASTNode.groupOutputTable.createNewTupleWithValues(values);
//				tup.readValuesForSchema(t.getProjectedFields(), values, null);
				
				String fName=((Function)exp).getName();
				ExpressionList param=((Function)exp).getParameters();
				if(fName.toUpperCase().equals("COUNT"))
				{
					
					// The following code is commented since it will never get called .. count(distinct) queries are handled seperately
					if(((Function) exp).isDistinct())
					{}
					else
					{
						int count=Integer.parseInt(tup.valueForField(aKey))+1;
						tup.setValue(aKey,"int",String.valueOf(count)); // count will always be an integer
					}
				}
				
				else if(fName.toUpperCase().equals("SUM"))
				{
					Iterator it=param.getExpressions().iterator();
					Expression sumParam =(Expression)it.next();
					
					double value1 = Double.parseDouble(tup.valueForField(aKey));
					double nValue=evaluateEXpressionOnTuple(input,sumParam);
					value1 = value1 + nValue;
					
					value+=String.valueOf(value1)+"|";
					tup.setValue(aKey, "decimal",String.valueOf(value1));
					
				}
				else if(fName.toUpperCase().equals("AVG"))
				{
					String[] value1;
					if(tup.valueForField(aKey).equals("0"))
					{
						value1=new String[2];
						value1[0]="0";
						value1[1]="0";
					}
					else
						value1=tup.valueForField(aKey).split(",");
					
					Iterator it=param.getExpressions().iterator();
					Expression sumParam =(Expression)it.next();
					
					double newSum=Double.parseDouble(value1[0]) + evaluateEXpressionOnTuple(input, sumParam);//+Double.valueOf();
					int newCount=Integer.parseInt(value1[1])+1;
					tup.setValue(aKey,"decimal", String.valueOf(newSum)+","+String.valueOf(newCount));
				}	
			}
		}
		return (value!=null)?value:"";
	}

	public void initializeGrouping(SQLTable t)
	{
		if(ASTNode.distinctField==null || ASTNode.distinctField.isEmpty())
		{
			if(outerprojectFields == null || outerprojectFields.isEmpty())
				ASTNode.groupBucketsAgg = new HashMap<String,String>();
			else
			{
				ASTNode.groupBucketsAggOuter = new TreeMap<String,Tuple>();
			}
		}
		else
		{
			if(!orderFields.equals(groupFields))
				ASTNode.groupBucketsDis = new HashMap<String, ArrayList<String>>();
			else
				ASTNode.groupBucketsDisOuter = new TreeMap<String,ArrayList<String>>();
		}
		
		this.table = t;
		ArrayList<fieldDetails> groupTableschema=new ArrayList<fieldDetails>();
		//schema.addAll(ASTNode.groupFields);
		//Addthe grouping cols from the input table
		
		for(fieldDetails col:this.table.getSchema())
		{
			 if(ASTNode.groupFields.contains(col.getName()))
			 {
				 if(!groupTableschema.contains(col))
				 	groupTableschema.add(col);
			 }
		}

		
			 // search for aliases
		Iterator<Entry<String, Expression>> aliasItr = aliasMap.entrySet().iterator();
		while(aliasItr.hasNext()) 
		{
			Entry<String, Expression> map = aliasItr.next();
			Expression exp = map.getValue();
			String colAlias = map.getKey();
			boolean isAdded = false;
			
			for(fieldDetails fields : groupTableschema)
			{
				if(colAlias.equals(fields.getName()))
				{
					isAdded = true;
					break;
				}
			}
			
			if(!isAdded)
			{	
				if(exp instanceof Function) // || exp instanceof BinaryExpression)
				{
					//String type=this.table.getTypeFormExpression(exp);
					String type=null;
					String fName=((Function)exp).getName();
					if(fName.toUpperCase().equals("COUNT"))
						type="int";
					else if(fName.toUpperCase().equals("SUM"))
						type="decimal";
						 
					if(!groupTableschema.contains(new fieldDetails(colAlias, type)))
						groupTableschema.add(new fieldDetails(colAlias, type)); 
				}
					 
				else if(exp instanceof Column)
					groupTableschema.add(new fieldDetails(colAlias, "str"));
					 
				else if(exp instanceof BinaryExpression)
					groupTableschema.add(new fieldDetails(colAlias, "dec"));
			}
		}	 
		ASTNode.groupOutputTable=new SQLTable("GroupByOutput",groupTableschema);
	}

	public void groupTuple(Tuple tuple)
	{
 
		if(scn==null)
			scn=new ScannerNode();
		
		if(ASTNode.extraPredicates!=null && !ASTNode.extraPredicates.isEmpty())
			tuple=scn.validateTuple(tuple,ASTNode.extraPredicates);
		
		if(tuple != null)
		{
			Iterator<fieldDetails> it1 = ASTNode.groupOutputTable.getSchema().iterator();
			String key="";
			
			while(it1.hasNext())
			{
				String field=it1.next().getName();
				if(ASTNode.groupFields.contains(field))
					key=key+tuple.valueForField(field)+"|";
			}

			//check if we already have a bucket with this key
			if(outerprojectFields == null || outerprojectFields.isEmpty())
			{
				if(!ASTNode.groupBucketsAgg.containsKey(key))
						groupBucketsAgg.put(key, evaluateAggregateOnTuple(key,null,tuple));
				else
					groupBucketsAgg.put(key, evaluateAggregateOnTuple(key,groupBucketsAgg.get(key),tuple));
			}
				
			else
			{
				for(String projFields : outerprojectFields)
				{
					if(aliasMap.get(projFields) instanceof Function)
					{
						ArrayList<fieldDetails> schema = tuple.getOwner().getSchema();
						boolean isPresent = false;
						fieldDetails lastField = schema.get(schema.size()-1);
						if(lastField.getName().equals(projFields))
							isPresent = true;
						if(!isPresent)
						{
							tuple.getOwner().getSchema().add(new fieldDetails(projFields, "dec"));
							break;
						}
					}
				}
				
				if(!ASTNode.groupBucketsAggOuter.containsKey(key))
				{
					groupBucketsAggOuter.put(key, evaluateAggregateOnTupleNestedQuery(null, tuple));
				}
				else
					groupBucketsAggOuter.put(key, evaluateAggregateOnTupleNestedQuery(groupBucketsAggOuter.get(key),tuple));
			}
		}
	}
	
	private Tuple evaluateAggregateOnTupleNestedQuery(Tuple previousTuple, Tuple tuple) 
	{
		ArrayList<fieldDetails> oldSchema = tuple.getOwner().getSchema();
		String aliasField = oldSchema.get(oldSchema.size()-1).getName();
		Expression exp = aliasMap.get(aliasField);
		String sumparam = exp.toString();
		if(exp instanceof Function)
			sumparam = ((Function)exp).getParameters().toString();
		sumparam = sumparam.replace("(", "");		
		sumparam = sumparam.replace(")", "");
		
		if(previousTuple == null)
		{
			tuple.setValue(aliasField, "dec", tuple.valueForField(sumparam));
		}
		
		else
		{
			double oldVal = Double.parseDouble(previousTuple.valueForField(aliasField));
			double newVal = Double.parseDouble(tuple.valueForField(sumparam));
			newVal += oldVal; 
			tuple.setValue(aliasField, "dec", String.valueOf(newVal));
		}
		
		
		return tuple;
	}

	public void processGroupOutput()
	{
		if(distinctField != null && !distinctField.isEmpty())
		{
			if(groupBucketsDisOuter == null || groupBucketsDisOuter.isEmpty())
			{
				Iterator<Entry<String, ArrayList<String>>> distinctItr = groupBucketsDis.entrySet().iterator(); 
				while(distinctItr.hasNext())
				{
					Entry<String, ArrayList<String>> valTupSet = distinctItr.next();
					ArrayList<String> values = new ArrayList<>();
						
					String[] temp = null;
					temp = valTupSet.getKey().split("\\|");
					
					int count = 0; 
					while(count < temp.length)
					{
						values.add(temp[count]);
						count++;
					}
						
					Integer val = valTupSet.getValue().size();
					//System.out.println(val);
					values.add(val.toString());
						
					Tuple tuple = groupOutputTable.createNewTupleWithValues(values);
					groupOutputTable.getRelation().addTuple(tuple);
				}
			}
			
			else
			{
				Iterator<Entry<String, ArrayList<String>>> distinctItr = groupBucketsDisOuter.entrySet().iterator(); 
				while(distinctItr.hasNext())
				{
					Entry<String, ArrayList<String>> valTupSet = distinctItr.next();
					Integer val = valTupSet.getValue().size();
					String val1 = val.toString();
					
					System.out.println(valTupSet.getKey() + val1);
				}
				System.exit(0);
			}
		}
		else
		{
			if(outerprojectFields == null || outerprojectFields.isEmpty())
			{
				ArrayList<String> keySet= new ArrayList<>(ASTNode.groupBucketsAgg.keySet());
				//Relation groupOutput=new Relation();
				for(String s : keySet)
				{
					ArrayList<String> values=new ArrayList<String>(Arrays.asList((s+groupBucketsAgg.get(s)).split("\\|")));
					Tuple tup=ASTNode.groupOutputTable.createNewTupleWithValues(values);
					ASTNode.groupOutputTable.getRelation().addTuple(tup);
						
					ASTNode.groupBucketsAgg.remove(s);
					//groupOutput.addTuple(tup);
				}
			}
				
			else
			{
				Iterator<Tuple> itr = groupBucketsAggOuter.values().iterator();
				
				while(itr.hasNext())
					System.out.println(ProjectNode.projectTuple(itr.next(), outerprojectFields).finalPrint());
//				long endTime   = System.currentTimeMillis();
//				long totalTime = endTime - ASTNode.startTime;
//				System.out.println(totalTime/100);	
				System.exit(0);
			}
		}
			
		groupBucketsDis.clear();
		groupBucketsAgg.clear();
		groupBucketsAggOuter.clear();
		System.gc();
		return;	
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
	 * Methods to Handle Distinct on Groups!
	 */
	
	public void addToMap(Tuple tuple) 
	{
		ScannerNode scanner = new ScannerNode();
		tuple=scanner.validateTuple(tuple, extraPredicates);
		
		if(tuple != null)
		{
			Iterator<String> it1=groupFields.iterator();
			String key="";
				
			while(it1.hasNext())
				key=key+tuple.valueForField(it1.next().toString())+"|";
			
			if(orderFields.equals(groupFields))
			{
				if(groupBucketsDis.containsKey(key))
				{
					ArrayList<String> distinctValues = groupBucketsDis.get(key);
					if(!distinctValues.contains(tuple.valueForField(distinctField)))
						distinctValues.add(tuple.valueForField(distinctField));
					groupBucketsDis.put(key, distinctValues);
				}
				
				else
				{
					ArrayList<String> distinctValues = new ArrayList<String>();
					distinctValues.add(tuple.valueForField(distinctField));
					groupBucketsDis.put(key,distinctValues);
				}
			}
			
			else
			{
				if(groupBucketsDisOuter.containsKey(key))
				{
					ArrayList<String> distinctValues = groupBucketsDisOuter.get(key);
					if(!distinctValues.contains(tuple.valueForField(distinctField)))
						distinctValues.add(tuple.valueForField(distinctField));
					groupBucketsDisOuter.put(key, distinctValues);
				}
				
				else
				{
					ArrayList<String> distinctValues = new ArrayList<String>();
					distinctValues.add(tuple.valueForField(distinctField));
					groupBucketsDisOuter.put(key,distinctValues);
				}
			}
		}
	}
}
