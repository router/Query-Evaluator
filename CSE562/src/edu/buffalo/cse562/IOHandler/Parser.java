package edu.buffalo.cse562.IOHandler;

import java.io.File;
import java.io.FileReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.TreeSet;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.parser.CCJSqlParser;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.parser.ParseException;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.Join;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectBody;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import net.sf.jsqlparser.statement.select.SubSelect;
import net.sf.jsqlparser.statement.select.Union;
import edu.buffalo.cse562.Evaluators.ArithmeticEvaluator;
import edu.buffalo.cse562.Evaluators.ConditionalEvaluators;
import edu.buffalo.cse562.Evaluators.RelationalEvaluator;
import edu.buffalo.cse562.Operators.ASTNode;
import edu.buffalo.cse562.Miscellaneous.IndexScanOperation;
import edu.buffalo.cse562.Miscellaneous.JoinDetails;
import edu.buffalo.cse562.Operators.JoinNode;
import edu.buffalo.cse562.Miscellaneous.OrderDetails;
import edu.buffalo.cse562.Operators.EvaluatorNode;
import edu.buffalo.cse562.Operators.ScannerNode;
import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.fieldDetails;

public class Parser {
	private HashMap<String,SQLTable> tables;
	private String baseDir;
	private String swapDir;
	private String indexDir;
	boolean hasSingleSource;
	private ArrayList<Expression> joinList = new ArrayList<Expression>();
	private ArrayList<Expression> additionalPredicates= new ArrayList<Expression>();
	private String nestedAlias;
	ArrayList<SQLTable> sortedTables;
	public static HashMap<String,Long> tableSizes;
	private HashMap<String, String> fromItemsAliasMap;
	private HashMap<String, String> allFieldsMap;
	public static HashMap<String, String []> foreignIndexMap = new HashMap<String,String[]>();
	
	private boolean isInIndexPhase;
	public Parser()
	{
		sortedTables=new ArrayList<SQLTable>();
		Parser.tableSizes=new HashMap<String,Long>();
		nestedAlias=null;
		new HashMap<String,String>();
		fromItemsAliasMap=new HashMap<String,String>();
		allFieldsMap= new HashMap<String,String>();
		isInIndexPhase=false;
		Parser.tableSizes.put("CUSTOMER", 300000L);
		Parser.tableSizes.put("LINEITEM", 11997996L);
		Parser.tableSizes.put("NATION",25L);
		Parser.tableSizes.put("ORDERS", 3000000L);
		Parser.tableSizes.put("PART", 400000L);
		Parser.tableSizes.put("PARTSUPP", 1600000L);
		Parser.tableSizes.put("REGION", 5L);
		Parser.tableSizes.put("SUPPLIER",20000L);
		Parser.foreignIndexMap.put("CUSTOMER",new String []{"nationkey"});
		Parser.foreignIndexMap.put("LINEITEM",new String []{"partkey","suppkey","receiptdate","shipdate"});
		Parser.foreignIndexMap.put("NATION",new String []{"regionkey"});
		Parser.foreignIndexMap.put("ORDERS", new String []{"custkey","orderdate"});
		Parser.foreignIndexMap.put("PART", new String []{"name"}); //This threw a biiig error. Fuck you part!
		Parser.foreignIndexMap.put("PARTSUPP", new String []{"suppkey"});
		Parser.foreignIndexMap.put("REGION", new String []{"name"});
		Parser.foreignIndexMap.put("SUPPLIER",new String []{"nationkey","suppkey"});
		
	}
	
	public void parseCmdLineArguments(String args[])
	{
		tables=new HashMap<>(); // we will maintain our list of the tables of the concerned here
		ArrayList<String> sqlFiles=new ArrayList<String>();
		baseDir=null;
		for(int i=0;i<args.length;i++)
		{
			if(args[i].equals("--swap"))
			{
				swapDir = new String(args[i+1]);
				i++;
			}
			else if(args[i].equals("--data"))
			{
				baseDir=new String(args[i+1]);
				i++;
			}
			else if(args[i].equals("--index"))
			{
				indexDir=new String(args[i+1]);
				i++;
			}
			else if(args[i].equals("--build"))
			{
				isInIndexPhase=true;
			}
			else
				sqlFiles.add(new String(args[i]));
		}
		// Open the files and read their contents
		for(String file: sqlFiles)
		{
			try 
			{
				Statement st;
				FileReader fr=new FileReader(new File(file));
				CCJSqlParser parser=new CCJSqlParser(fr);
				while((st=parser.Statement())!=null)
				{
					if(st instanceof CreateTable)
					{
						parseCreate(st,tables);
					}
					else 
					{
						if(st instanceof Select && !isInIndexPhase)
							parseSelect(st);
				
						else if(isInIndexPhase)
						{
							IndexBuilder<String> idb= new IndexBuilder<String>(baseDir, indexDir);
							//String for multiple primary keys
							//IndexBuilder<Integer> idb1 = new IndexBuilder<Integer>(baseDir, indexDir);
							//Integer for single primary keys
							//We can take multiple index builders here. one each for integer and string.
							Iterator<String> keyIt=tables.keySet().iterator();
							while(keyIt.hasNext())
							{
								SQLTable table=tables.get(keyIt.next());
								//keyIt.remove();
								if(table.getPrimaryField().size()>1)
								{
									idb.buildIndexOnMultiplePrimaryKey(table);
								}
								else
									idb.buildIndexOnSinglePrimaryKey(table);
							}
						}
						//sc.close();
					}
				}
			}
			catch(Exception e)
			{
				System.out.println(e.getMessage()+e.getStackTrace());
			}
		}
	}
	
	// Author:Kaushal
	
	public Statement parseSQLExpression(String exprStr) throws ParseException, JSQLParserException
	{
		CCJSqlParserManager pm = new CCJSqlParserManager();
		return pm.parse(new StringReader(exprStr));
	}
	
	public void parseCreate(Statement statement, HashMap<String, SQLTable> tables) // parse and store value in the tables map.
	{
		CreateTable createstatement = (CreateTable)statement;
		String tableName = createstatement.getTable().getName();
		Object [] col_def=createstatement.getColumnDefinitions().toArray();
		String[] columns=new String[col_def.length];
		String[] columntypes = new String[col_def.length];
		//Extracting the field names and storing as string
		for(int i=0;i<col_def.length;i++)
		{
			String[] col_name_type = col_def[i].toString().split(" ", 2);  // make it 2 if you want the CHAR(1) as type!
			columns[i] = col_name_type[0];
			
			if(col_name_type[1].toUpperCase().contains("CHAR"))
			{
				if(col_name_type[1].contains("(1)"))
					columntypes[i] = "CHAR"; //for types
				else
					columntypes[i] = "VARCHAR";
			}
			else
				columntypes[i] = col_name_type[1]; //for types
		}
		
		SQLTable table=new SQLTable(tableName,columns, columntypes);
		table.setTupleCount(Parser.tableSizes.get(tableName));
		tables.put(tableName, table);
		table.setDumpedInFile(true);
		table.setFilePath(baseDir +"\\"+ tableName + ".dat");

		List<Index> ls = createstatement.getIndexes();
		if(ls != null)
		{
			/*
			 * Ideally primary key information should be provided before we start building indexes,
			 * so that if we sort on primary key and then make a B+ Tree index on it, we can easily
			 * make a clustered B+ Tree index.
			 */
			Iterator<Index> itr = ls.iterator();
			while(itr.hasNext())
			{
				Index indx = itr.next();
				if(indx.getType().equals("INDEX")) //checks statement for INDEX option.
				{
					//Add indexes to the SQL Table!
					Iterator<String> itrFields = indx.getColumnsNames().iterator();
					
					while(itrFields.hasNext())
						table.addIndexField(itrFields.next());
					
					//Decide what to do here. (Suggestion: go to index building code. Working on that right now)
				}
				else if(indx.getType().equals("PRIMARY KEY")) //checks statement for PRIMARY KEY option.
				{
					Iterator<String> itrFields = indx.getColumnsNames().iterator();
					
					while(itrFields.hasNext())
						table.addPrimaryField(itrFields.next());
					//Decide what to do here. (Suggestion: go to sorting code, provided we need data sorted by primary key)
				}
			}
			String[] itrIndex = foreignIndexMap.get(table.getTableName());
			for(String itrFields:itrIndex)
				table.addIndexField(itrFields);
		}
	}
	
	public void parseSelect(Statement st )
	{
		SelectBody stb=((Select)st).getSelectBody();
		if(stb instanceof Union)
			parseUnion((Union)stb);
		else if(stb instanceof PlainSelect)
		{
			parsePlainSelect((PlainSelect)stb);
		}
		
	}
	
	private void parseUnion(Union stb) {
		System.out.println("Not Implemented!");
	}

	public void parsePlainSelect(PlainSelect st)
	{	
		HashMap<String, Expression> aliasMap = parseAlias(st);
		
		try
		{
			String fromSource=st.getFromItem().toString();
			FromItem fr1 = st.getFromItem();
			List<Join> extrasources=st.getJoins();
			String selectalias = null;
			if(fr1 instanceof SubSelect)
			{
				selectalias = fr1.getAlias();
				fr1.setAlias(null);
				fr1.toString();
			}
			else if(fr1 instanceof Table)
			{
				parseTableAlias(fr1);
				for(Join table:extrasources)
				{
 					FromItem f=table.getRightItem();
					parseTableAlias(f);
				}
			}
			Expression whereClause=st.getWhere();
			fromSource = fr1.toString();
			
			parseOperators(st,aliasMap);
			
			if(fromSource.startsWith("(") && fromSource.endsWith(")"))
			{	
				ASTNode.outerprojectFields = parseSelectionList(st.getSelectItems(),aliasMap);
				fromSource=fromSource.substring(1, fromSource.length()-1);
				Statement nestedSt=parseSQLExpression(fromSource);
				if(nestedSt!=null)
				{
					if(selectalias == null)
						nestedAlias = "defautkey";
					else
						nestedAlias = selectalias;
					
					ASTNode.isNestedQuery = true;
					parsePlainSelect((PlainSelect)(((Select)nestedSt).getSelectBody()));
				}
			}
		
			else
			{		
				traverseExpressionTree(whereClause);
				translateExpressionsToIndexLookups();
				queryRewritePlan();
					
				if(additionalPredicates!=null && !additionalPredicates.isEmpty())
					ASTNode.extraPredicates=additionalPredicates;
				
				EvaluatorNode evaluator = new EvaluatorNode();
				evaluator.evaluate();
					
			}
		}
		catch(Exception e)
		{
			System.out.println(e.getStackTrace());
		}
	}
	
	private void queryRewritePlan() 
	{
		TreeSet<String> orderedJoinTables= new TreeSet<>(new Comparator<String>() {
			public int compare(String s1,String s2)
			{
				
				int rValue= (int)(Parser.tableSizes.get(tables.get(s1).getTableName().toUpperCase()) - Parser.tableSizes.get(tables.get(s2).getTableName().toUpperCase()));
				if(rValue==0)
					return s1.compareTo(s2);
				else
					return rValue; 
			}
		});
		
		Iterator<Expression> it=joinList.iterator();
		while(it.hasNext())
		{ 
			BinaryExpression exp=(BinaryExpression)it.next();
			String lhs=exp.getLeftExpression().toString();
			String rhs=exp.getRightExpression().toString();
			
			String table1=Parser.getTableNameFromKey(lhs);
			String table2=Parser.getTableNameFromKey(rhs);
				
			Parser.getFieldFromKey(lhs);
			
			SQLTable t1 = tables.get(table1);
			SQLTable t2 = tables.get(table2);
			t1.addToJoinSet(new JoinDetails(table2,lhs));
			t2.addToJoinSet(new JoinDetails(table1, rhs));
			orderedJoinTables.add(table1);
			orderedJoinTables.add(table2);	
		}
		
		ArrayList<SQLTable> joinFields = new ArrayList<>();	
		joinFields.add(tables.get(orderedJoinTables.pollFirst()));
		
		SQLTable prev = joinFields.get(0);
		
		while(true)
		{
			//SQLTable next = tables.get(prev.getJoinSet().pollFirst().table);
			SQLTable next = tables.get(prev.getJoinSet().first().table);
			
			if(!joinFields.contains(next))
				joinFields.add(next);
			else
			{
				prev.getJoinSet().pollFirst();
				if(prev.getJoinSet().size()<=0)
					break;
					
				next = tables.get(prev.getJoinSet().first().table);
				if(!joinFields.contains(next))
					joinFields.add(next);
				else
					break;
			}
			prev = next;
		}
		
		if(!joinFields.contains(prev))
			joinFields.add(prev);
		ASTNode.joinTables.addAll(joinFields);
	}

	private void parseOperators(PlainSelect st, HashMap<String, Expression> aliasMap) 
	{
		long limitval;
		String distinctField;
		ASTNode.indexDir=indexDir;
		ASTNode.baseDir=baseDir;
		ASTNode.swapDir=swapDir;
		
		ASTNode.aliasMap = aliasMap;
		if(ASTNode.groupFields==null || ASTNode.groupFields.size()==0) //optimisation for nested query
			ASTNode.groupFields = parseGroupingList(st.getGroupByColumnReferences(),aliasMap);
		
		if(ASTNode.orderFields==null || ASTNode.orderFields.size()==0)	
			ASTNode.orderFields = parseOrderByList(st.getOrderByElements(),aliasMap);
		
		ASTNode.projectFields = parseSelectionList(st.getSelectItems(),aliasMap);
		//ASTNode.joinFields = parseJoinList(st.getJoins());
		distinctField = hasDistinctInQuery(aliasMap);
		if(distinctField != null && !distinctField.isEmpty())
		{
			if(distinctField.contains("("))
				distinctField = distinctField.replace("(", "");
			if(distinctField.contains(")"))
				distinctField = distinctField.replace(")", "");
			ASTNode.distinctField = distinctField.trim();
		}
		else
			distinctField = "";
		if(st.getLimit() != null)
			limitval = st.getLimit().getRowCount();
		else
			limitval = -1;
		
		ASTNode.limit = limitval;
	}


	private ArrayList<String> parseJoinList(List joins) 
	{
		ArrayList<String> joinList = new ArrayList<String>();
		int i = 0;
		while(joins != null && i<joins.size())
		{
			Join n1 = (Join) joins.get(i);
			FromItem newfr = (FromItem) n1.getRightItem();
			if(newfr.getAlias() != null)
				joinList.add(newfr.getAlias());
			else
				joinList.add(newfr.toString());
			parseTableAlias(newfr);
			i++;
		}
		return joinList;
	}
	
	private ArrayList<String> parseSelectionList(List fields, HashMap<String, Expression> aliasMap)
	{
		ArrayList<String> result=new ArrayList<String>();
		//String[] result=new String[];
		Iterator it=fields.iterator();
		
		while(it.hasNext())
		{
			SelectExpressionItem set=(SelectExpressionItem)it.next();
			if(set.getAlias() != null)
				result.add(set.getAlias());
			else
				result.add(set.toString());
		}
		
		return result;
	}
	
	private ArrayList<String> parseGroupingList(List fields, HashMap<String, Expression> aliasMap)
	{
		if(fields==null)
			return null;
		ArrayList<String> result=new ArrayList<String>();
		//String[] result=new String[];
		Iterator it=fields.iterator();
		
		while(it.hasNext())
		{
			result.add(it.next().toString());
		}
		
		return result;
	}
	private ArrayList<OrderDetails> parseOrderByList(List l, HashMap<String, Expression> aliasMap)
	{
		ArrayList<OrderDetails> output=new ArrayList<OrderDetails>();
		if(l==null || l.isEmpty())
			return output;
		Iterator it=l.iterator();
		String type="DESC";
		while(it.hasNext())
		{
			String value=it.next().toString();
			if(value.contains(type))
			{
				OrderDetails oe = new OrderDetails(value.split(" ")[0], "DESC");
				output.add(oe);
			}
			else
			{
				OrderDetails oe = new OrderDetails(value.split(" ")[0], "ASC");
				output.add(oe);
			}
		}
		return output;
	}
	
	private String hasDistinctInQuery(HashMap<String, Expression>aMap)
	{
		Iterator<String> it=aMap.keySet().iterator();
		while(it.hasNext())
		{
			String key=it.next();
			Expression exp=aMap.get(key);
			if(exp instanceof Function)
			{
				String fname=((Function) exp).getName();
				if(fname.toUpperCase().equals("COUNT") && ((Function) exp).isDistinct())
					return ((Function) exp).getParameters().toString();
			}
		}
		return null;	
	}
	
	public ArrayList<Expression> aggregateFunctions(PlainSelect st)
	{
		ArrayList<Expression> expressions = new ArrayList<Expression>();
		for(int o=0 ; o<st.getSelectItems().size() ; o++)
		{
        	if(((SelectExpressionItem)st.getSelectItems().get(o)).getExpression() instanceof Function)
        	{
        		Function expression = ((Function)((SelectExpressionItem) st.getSelectItems().get(o)).getExpression());
        		expressions.add(expression);
        	}
		}
		return expressions;
	}

	public HashMap<String,Expression> parseAlias(PlainSelect st)
	{
		HashMap<String,Expression> map1 = new HashMap<String, Expression>();
		
		if(ASTNode.aliasMap != null && !ASTNode.aliasMap.isEmpty())
			map1 = ASTNode.aliasMap;
	
		try
        {
        	for(int p=0;p<st.getSelectItems().size();p++)
    	    {
    	       	//System.out.println(st.getSelectItems().get(p).toString());
    	       	SelectExpressionItem s1=(SelectExpressionItem) st.getSelectItems().get(p);
    	       	Expression expr = s1.getExpression();
    	       	//String columnname = expr.toString();
    	       	String alias = s1.getAlias();
    	       	if (alias!=null)
    	       	{
    	       		//alias is key and the function to be converted to alias is value
    	       		map1.put(alias, expr);
    	       		//tokenflag = 1;
    	       	}
    	       	else// if(expr instanceof Column)
    	       	{
    	       		//map1.put(((Column) expr).getColumnName(), expr);
    	       //	}
    	       	//else
    	       	//{
    	       		map1.put(expr.toString(), expr);
    	       	}
    	    }
        }
        catch(NullPointerException e)
        {
        	System.out.println(e.getStackTrace());
        }
        return map1;
	}
	
	public boolean isAlias(PlainSelect st)
	{
		String selectst = st.toString();
		if(selectst.contains("AS")||selectst.contains("as"))
		{
			return true;
		}
		else
		{
			return false;
		}
	}
	
	public boolean isJoin(Expression ex)
	{
		if(hasSingleSource)
			return false;
		if(ex instanceof EqualsTo) // ASSUMPTION: in CHK1, we are only working with equijoins (Subhro) 
		{
			return (((EqualsTo) ex).getLeftExpression() instanceof Column) && (((EqualsTo) ex).getRightExpression() instanceof Column);
		}
		return false;
	}
	public void addFilterToTable(Expression e)
	{
		String leftChild=((BinaryExpression)e).getLeftExpression().toString();
		String tablename=leftChild.split("\\.")[0];
		
		//Handle dates
		Expression rht=((BinaryExpression)e).getRightExpression();
		if(rht instanceof Function) // we only handle dates for the time being
		{
			String dateValue=((Function) rht).getParameters().toString();
			dateValue=dateValue.substring(1, dateValue.length()-1);

			((BinaryExpression)e).setRightExpression(new StringValue(dateValue));
		}
			
		
		
		tables.get(tablename).addFilter(e);
		
	}
	public void traverseExpressionTree(Expression ex)
	{
		if(!(ArithmeticEvaluator.isArithMeticOperator(ex) || RelationalEvaluator.isRelationalOperator(ex) || ConditionalEvaluators.isConditionalOperator(ex)))
		{
			if(ex instanceof Parenthesis ) // right now we only handle parathesis
			{
				additionalPredicates.add(((Parenthesis)ex).getExpression());
			}
			return;
		}
		if(hasSingleSource)
		{
			((SQLTable)(tables.values().toArray()[0])).addFilter(ex); // only single table so whatever we have is for the same table.
			return;
		}
		if(isJoin(ex))
		{
			joinList.add(ex);
			return;
		}
		else if(RelationalEvaluator.isRelationalOperator(ex))
		{
			addFilterToTable(ex);
			return;
		}
		else if(ex instanceof AndExpression) 
		{
			traverseExpressionTree(((AndExpression)ex).getLeftExpression());
			traverseExpressionTree(((AndExpression)ex).getRightExpression());
		}
		
		if(joinList != null && !joinList.isEmpty())
			ASTNode.joinPredicates.addAll(joinList);
	}
	
	
	public void parseTableAlias(FromItem aliasfrom)
	{
		/*
		 * Check for instance of plainselect statement, confused about what to do in this case.
		 * In case of table, check instance and put in tables (HashMap)
		 * get SQLTable from the hashmap according to its key and put new key and corresponding
		 * SQLTable and let the previous entry remain. Big hashmap is okay.
		 */
		
		String aliasStr = aliasfrom.getAlias();
		if(aliasStr != null) 
		{
			aliasfrom.setAlias(null);
			if(aliasfrom instanceof Table)
			{
				String tablename = aliasfrom.toString();
				if(!tables.containsKey(tablename.toUpperCase()))
				{

					 //Table is null, not sure what to do here. (shail)

					// This case is only possible if we have multiple aliases on the same table. (subhro)
					
					// Find any of the other aliases
					Iterator<String> it=tables.keySet().iterator();
					while(it.hasNext())
					{
						SQLTable t= tables.get(it.next());
						if(t.getTableName().equals(tablename.toUpperCase()))
						{
							// copy object and add. It is necessary to clone coz ther emight be seperate filters  for both aliases
							tables.put(aliasStr,new SQLTable(t));
							break;
						}
					}
					
				}
				else
				{
					
					SQLTable t=new SQLTable(tables.get(tablename.toUpperCase()));
					
					t.setAlias(aliasStr);
					//tables.remove(tablename.toUpperCase());
					tables.put(aliasStr, t);
					
				}
				
				
				fromItemsAliasMap.put(aliasStr,tablename);
				Parser.tableSizes.put(aliasStr, Parser.tableSizes.get(tablename.toUpperCase()));
				alterTable(tables.get(aliasStr));
			}
			else if(aliasfrom instanceof SubSelect)
			{
				fromItemsAliasMap.put(aliasStr, aliasfrom.toString());
			}
				
		}
		else // no alias
		{
			String tablename=aliasfrom.toString();
			SQLTable t=tables.get(tablename.toUpperCase());
			t.setAlias(tablename); // alias = tablename
			
			// The table names in the select query might be in lower case and so the next two lines do this conversion in the tables map.
			tables.remove(tablename.toUpperCase());
			tables.put(tablename,t);
			alterTable(tables.get(tablename));
			Parser.tableSizes.put(tablename, Parser.tableSizes.get(tablename.toUpperCase()));
			fromItemsAliasMap.put(tablename, tablename);
			
		}
		
	}
	public void alterTable(SQLTable t)
	{
		if(hasSingleSource)
			return;
		
		ArrayList<fieldDetails> schema = new ArrayList<fieldDetails>();
		
		Iterator<fieldDetails> schIt=t.getSchema().iterator();
		while(schIt.hasNext())
		{
			fieldDetails oldFieldDetail=schIt.next();
			String newKey = t.getTableAlias() + "." + oldFieldDetail.getName();
			fieldDetails newFieldDetails = new fieldDetails(newKey, oldFieldDetail.getType()) ;
			schema.add(newFieldDetails);
			
			allFieldsMap.put(newKey, t.getTableAlias());			
		}
		t.setSchema(schema);
	}
	
	
	public void removeFromprojectFields(String key)
	{
		key=key. replace("(","");
		key=key.replace(")","");
		if(allFieldsMap.containsKey(key))
			allFieldsMap.remove(key);
	}
	
	// Optimisations 
	public void combineFilters()
	{
		
		
		for(SQLTable table:tables.values())
		{
			ArrayList<Expression> filters=table.getFilters();
			if(filters.size()<=1)
				continue;
			
			Iterator<Expression> expIt=filters.iterator();
			AndExpression finalExp=null;
			
			while(expIt.hasNext())
			{
				
				if(finalExp==null)
					finalExp=new AndExpression(expIt.next(),expIt.next());
				else
					finalExp=new AndExpression(finalExp,expIt.next());
			}
			table.getFilters().clear();
			table.getFilters().add(finalExp);
		}
	}
	public void translateExpressionsToIndexLookups()
	{
		// Assumptions :1) Each query will comprise only a single range lookup on a table 2) REange lookup predicates usually occure together
		Iterator<String> keyIt=tables.keySet().iterator();
		while(keyIt.hasNext())
		{
			SQLTable table=tables.get(keyIt.next());
			ArrayList<Expression> filters=table.getFilters();
			Iterator<Expression> expIt=filters.iterator();
			IndexScanOperation<String> scan=null;
			Expression exp1=null,exp2=null;
			while(expIt.hasNext())
			{
				 exp1=expIt.next();
				 
				if(exp1 instanceof MinorThan || exp1 instanceof MinorThanEquals || exp1 instanceof GreaterThan || exp1 instanceof GreaterThanEquals)
				{
					
					if(((BinaryExpression)exp1).getRightExpression() instanceof Column)
						continue;
					
					scan=new IndexScanOperation<String>();
					if(exp1 instanceof MinorThan || exp1 instanceof MinorThanEquals)
					{
						scan.key=Parser.getFieldFromKey(((BinaryExpression)exp1).getLeftExpression().toString());
						scan.upperLimit=((BinaryExpression)exp1).getRightExpression().toString();
						scan.upperLimit = scan.upperLimit.substring(1,scan.upperLimit.length()-1);
						if(exp1 instanceof MinorThanEquals)
							scan.upperInclusive=true;
						else
							scan.upperInclusive=false;
						
						exp2=expIt.next();
					
						
						scan.lowerLimit=((BinaryExpression)exp2).getRightExpression().toString();
						scan.lowerLimit = scan.lowerLimit.substring(1,scan.lowerLimit.length()-1);
						if(exp2 instanceof GreaterThanEquals)
							scan.lowerInclusive=true;
						else
							scan.lowerInclusive=false;
					
						
					}
					else
					{
						scan.key=Parser.getFieldFromKey(((BinaryExpression)exp1).getLeftExpression().toString());
						scan.lowerLimit=((BinaryExpression)exp1).getRightExpression().toString();
						scan.lowerLimit = scan.lowerLimit.substring(1,scan.lowerLimit.length()-1);
						if(exp1 instanceof GreaterThanEquals)
							scan.lowerInclusive=true;
						else
							scan.lowerInclusive=false;
						
						
						
						 exp2=expIt.next();
						
						scan.upperLimit=((BinaryExpression)exp2).getRightExpression().toString();
						scan.upperLimit = scan.upperLimit.substring(1,scan.upperLimit.length()-1);
						if(exp2 instanceof MinorThanEquals)
							scan.upperInclusive=true;
						else
							scan.upperInclusive=false;
						
					}
						
					
				}
				

			}
			if(scan!=null)
			{
				scan.type="SECONDARY";
				table.setIndexLookupOperation(scan);
				table.getFilters().remove(exp1);
				table.getFilters().remove(exp2);
			}

		}
		
	}
		
	// Extra Functions *******************************************************************************
	public static String getTableNameFromKey(String key)
	{
		return key.split("\\.")[0];
	}
	public static String getFieldFromKey(String key)
	{
		return key.split("\\.")[1];
	}
}
	