package edu.buffalo.cse562;
import java.util.List;
import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Properties;
import java.util.Set;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.ExpressionList;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.parser.CCJSqlParserManager;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.create.table.Index;
import net.sf.jsqlparser.statement.select.FromItem;
import net.sf.jsqlparser.statement.select.PlainSelect;
import net.sf.jsqlparser.statement.select.Select;
import net.sf.jsqlparser.statement.select.SelectExpressionItem;
import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.recman.*;

public class newclass
{
    CCJSqlParserManager parserManager = new CCJSqlParserManager();

    @SuppressWarnings("null")
	public newclass() throws JSQLParserException
    {
        String statement = "CREATE TABLE LINEITEM (orderkey INT, PRIMARY KEY (orderkey, linenumber),INDEX shipidx (shipdate));";
        /*
        Properties p1 = new Properties();
        try {
			RecordManager recMan = RecordManagerFactory.createRecordManager("OMFG");
			RecordManager recMan1 = RecordManagerFactory.createRecordManager("Shail", p1);
			PrimaryTreeMap<Integer, String> treeMap = recMan.treeMap("heya");
			recMan.commit();
			System.out.println(treeMap.keySet());
			treeMap.remove(2);
			System.out.println(treeMap.keySet());
			recMan.rollback();
			System.out.println(treeMap.keySet());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
        */
        CreateTable st1 = (CreateTable) (parserManager.parse(new StringReader(statement)));
        List<Index> ls = st1.getIndexes();
        Iterator<Index> itr = ls.iterator();
        while(itr.hasNext())
        {
        	Index indx = itr.next();
        	if(indx.getType().equals("INDEX"))
        	{
        		System.out.println();
        	}
        }
        /*PlainSelect st = (PlainSelect) ((Select) parserManager.parse(new StringReader(statement))).getSelectBody();
        
        
        //while(true)
        //{
        	ExpressionList el = new ExpressionList();
        	ArrayList<Expression> l1 = new ArrayList<Expression> ();
        	Expression s = st.getWhere();
        	JoinChecker (s);
        	FromItem fr1 = st.getFromItem();
        	if(fr1 instanceof Table)
        	{
        		System.out.println(fr1.toString());
        	}
       */ //parseJoins(st.getWhere());
        	
        	
        	
        	//break;
        //}
        /*String[] tokens = null;
        String str = st.toString();
        HashMap<String,String> map1 = new HashMap<String,String> ();
        int tokenflag = 0;
        tokens = str.split(" ");
        
        for(int i = 0;i<tokens.length;i++)
        {
        	if(tokens[i].equals("AS")||tokens[i].equals("as"))
        	{
        		tokenflag = 1;
        	}
        }
        if (tokenflag == 0)
        {
        	
        	//System.out.println("NO AS HERE! YOU HAVE NO POWER HERE!");
        }
        else
        {
        	for(int p=0;p<st.getSelectItems().size();p++)
            {
            	//System.out.println(st.getSelectItems().get(p).toString());
            	SelectExpressionItem s1=(SelectExpressionItem) st.getSelectItems().get(p);
            	Expression expr = s1.getExpression();
            	System.out.println(expr.toString());
            	//String fnname = ((Function)s1.getExpression()).getParameters().toString();
            	String alias = s1.getAlias();
            	if (alias!=null)
            	{
            		System.out.println(alias);
            	}
            	else if (expr instanceof Column)
            	{
            		//System.out.println("HEYA");
            		System.out.println(((Column) expr).getColumnName());
            	}
            	else
            	{
            		System.out.println(expr.toString());
            	}
            }
        }*/
        //System.out.format("%s is function call? %s",
         //       plainSelect.getSelectItems().get(0),
           //     ((Function)((SelectExpressionItem) plainSelect.getSelectItems().get(0)).getExpression()).isAllColumns());
        //System.out.println(plainSelect.getSelectItems().get(0));
        //System.out.println(((Function)((SelectExpressionItem) plainSelect.getSelectItems().get(0)).getExpression()).getParameters());
        /*String[] fn = new String[20];
        String[] fn1 = new String[20];
        for(int o=0;o<st.getSelectItems().size();o++)
		{
        	String str = ((Function)((SelectExpressionItem) st.getSelectItems().get(o)).getExpression()).getParameters().toString();
        	String str1 = ((Function)((SelectExpressionItem) st.getSelectItems().get(o)).getExpression()).getName();
        	fn1[o]=str1;
        	fn[o]=str.substring(1,str.length()-1);
        	System.out.println(fn1[o]+" "+fn[o]);
        	//System.out.println(str.substring(1, str.length()-1));
		}*/
        /*HashMap<String,Expression> result = parseAlias(st);
        if(result == null)
        {
        	System.out.println("NULL");
        }
        Set<String> keys = result.keySet();
        for(String key : keys)
        {
       		Expression value = result.get(key);
       		System.out.println(key+" "+value.toString());
       	}*/
        
    }
    public static void main(String[] args) throws JSQLParserException
    {

        new newclass();

    }
    public void JoinChecker(Expression p)
    {
    	if(p instanceof AndExpression)
    	{
    		parseJoins((AndExpression) p);
    	}
    	else if(p instanceof MinorThan)
    	{
    		parseJoins((MinorThan) p);
    	}
    	else if(p instanceof MinorThanEquals)
    	{
    		parseJoins((MinorThanEquals) p);
    	}
    	else if(p instanceof GreaterThan)
    	{
    		parseJoins((GreaterThan) p);
    	}
    	else if(p instanceof OrExpression)
    	{
    		parseJoins((OrExpression) p);
    	}
    	else if(p instanceof GreaterThanEquals)
    	{
    		parseJoins((GreaterThanEquals) p);
    	}
    	else if(p instanceof EqualsTo)
    	{
    		parseJoins((EqualsTo) p);
    	}
    	else if(p instanceof NotEqualsTo)
    	{
    		parseJoins((NotEqualsTo) p);
    	}
    }
    private void parseJoins(NotEqualsTo p) {
		// TODO Auto-generated method stub
    	p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
	}
	private void parseJoins(EqualsTo p) {
		// TODO Auto-generated method stub
		p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
	}
	private void parseJoins(GreaterThanEquals p) {
		// TODO Auto-generated method stub
		p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
	}
	private void parseJoins(OrExpression p) {
		// TODO Auto-generated method stub
		p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
	}
	private void parseJoins(GreaterThan p) {
		// TODO Auto-generated method stub
		p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
	}
	private void parseJoins(MinorThan p) {
		// TODO Auto-generated method stub
		p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
	}
	private void parseJoins(MinorThanEquals p) {
		// TODO Auto-generated method stub
		p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
	}
	public void parseJoins(AndExpression p)
    {
    	p.getRightExpression();
    	Expression e = p.getLeftExpression();
    	System.out.println(p.getRightExpression().toString());
    	JoinChecker(e);
    }
    public HashMap<String,Expression> parseAlias(PlainSelect st)
	{
		HashMap<String,Expression> map1 = new HashMap<String, Expression>();
		String str=st.toString();
		//String[] tokens = null;
		int tokenflag = 0;
        //tokens = str.split(" ");
        try
        {
        	/*
        	for(int i = 0;i<tokens.length;i++)
        	{
        		if(tokens[i].equals("AS")||tokens[i].equals("as"))
        		{
        			tokenflag = 1;
        		}
        	}
        	if(tokenflag == 0)
        	{
        		return null;
        	}
        	else
        	{*/
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
    	        		tokenflag = 1;
    	        	}
    	        }

        	//}
        }
        catch(NullPointerException e)
        {
        	System.out.println(e.getStackTrace());
        }
        if(tokenflag == 0)
        {
        	return null;
        }
        else
        {
        	return map1;
        }
	}
}