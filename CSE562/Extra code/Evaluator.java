package edu.buffalo.cse562;

import java.util.Iterator;
import java.util.List;

import net.sf.jsqlparser.schema.Table;
import net.sf.jsqlparser.statement.*;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.*;

/**
 *
 * @author Niccolo' Meneghetti
 */

public class Evaluator
{
	Statement statement;
	
	
	public Evaluator(Statement statement)
	{
		this.statement = statement;
	}
	
	public void Statement()
	{
		if(statement instanceof CreateTable)
			CreateStatement();
		else if(statement instanceof Select)
				SelectStatement();
	}

	public void CreateStatement()
	{
		CreateTable createstatement = (CreateTable)statement;
		Table table = createstatement.getTable();
		String col_def = createstatement.getColumnDefinitions().toString();
			
		//System.out.println("Table====>"+table.getName());
	//	System.out.println("Col Def===>"+col_def);
	}
	
	public void SelectStatement()
	{
		if(statement.toString().contains("UNION") || 
		   statement.toString().contains("Union") ||
		   statement.toString().contains("union"))
			UnionStatement();
		else if(statement.toString().contains("JOIN") || 
		      statement.toString().contains("Join") ||
		      statement.toString().contains("join"))
			JoinStatement();
		else
			PlainSelectStatement((PlainSelect) statement);
	}

	public void/*Rows*/ PlainSelectStatement(PlainSelect plainSelect)
	{
		String fromItem = plainSelect.getFromItem().toString();
		String fieldItems = plainSelect.getSelectItems().toString();
		
		//System.out.println("Table : " + fromItem); 
		//System.out.println("Field Items : " + fieldItems);
		if(plainSelect.getWhere() != null)
	    {
	    	String where = plainSelect.getWhere().toString();
	    	//System.out.println("Where : " + where);
	    }	
		
		//Return the resultant Rows
	}
	
	public void/*Rows*/ UnionStatement()
	{
	    Select selectstatement = (Select)statement;
	    Union union = (Union) selectstatement.getSelectBody();
	    
	    Iterator<PlainSelect> plainselectitr = union.getPlainSelects().iterator();
	    while(plainselectitr.hasNext())
	    {
	    	PlainSelect plainselect = (PlainSelect) plainselectitr.next();
	    	PlainSelectStatement(plainselect);
	    	//System.out.println(plainselect.toString());
	    }
	    
	    /*
		 *Get the rows from PlainSelect for each individual select statement. 
         *Combine the rows here.
		 *Return rows.
		 */
	}
	
	private void/*Rows*/ JoinStatement() 
	{
		Select selectstatement = (Select)statement;
		PlainSelect plainselect=(PlainSelect) selectstatement.getSelectBody();
		String fromItem = plainselect.getFromItem().toString();
		String fieldItems = plainselect.getSelectItems().toString();
		
		//System.out.println("Table : " + fromItem); 
		
		List<Join> joinlist= plainselect.getJoins();
		Iterator<Join> joinitr = joinlist.iterator();
		while(joinitr.hasNext())
		{
			Join join = joinitr.next();
			Table table = (Table) join.getRightItem();
			System.out.println("Table : " + table.getName());
		}
		
		System.out.println("Field Items : " + fieldItems);
		
		/*Create an intermediate table with all Joins. 
		 *Call PlainSelect() on intermedaite table if there is a where clause.
		 *Print the returned result.
		 */
	} 
}