package edu.buffalo.cse562.Operators;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;

public class EvaluatorNode extends ASTNode
{	
	JoinNode jNode = new JoinNode();
	GroupingNode gNode = new GroupingNode();
	OrderbyNode oNode = null;
	ProjectNode pNode = null;
	
	public void evaluate() throws IOException
	{
		//Does join and group!
		if(ASTNode.joinTables != null && !ASTNode.joinTables.isEmpty())
			joinEvaluate();
		
		//orders and project!
		if(orderFields != null && !orderFields.isEmpty())
		{
			orderEvaluate();
			project();
		}		
	}
	
	public void joinEvaluate() throws IOException
	{
		
		while(ASTNode.joinTables.size() >= 2)   //Condition checks if a join is left or not!
		{	
			/*
			 * All join functions will get two SQLTable objects table1 and table2.
			 * Based on various factors it should decide the left table and the right table!
			 * Also check for the size of jointables to see if it is the last join!
			 */
				
			SQLTable table1 = ASTNode.joinTables.get(0);
			SQLTable table2 = ASTNode.joinTables.get(1);
				
			
			try 
			{
				
				/*
				 * tpch07 stack trace after 209s
				 */
				SQLTable resultTable = jNode.Join(table1, table2);
				if(ASTNode.joinTables.size()>2)
					resultTable.addToJoinSet(table2.getJoinSet().pollFirst());
				ASTNode.joinTables.remove(table1);
				ASTNode.joinTables.remove(table2);
				
				ASTNode.joinTables.add(0, resultTable);
			}
			catch(IOException e)
			{
				e.printStackTrace();
			}
		}
		
		if(groupFields != null && !isOuter)
			jNode.finalizeGrouping();
	}

	
	public void groupEvaluate()
	{
	 	//This is called only when there is no join but directly group by. (Outer query for tpch7)
		SQLTable groupTable = joinTables.get(0);
		gNode.initializeGrouping(groupTable);
		ArrayList<Tuple> tuples = groupTable.getRelation().getTuples();
		
		Iterator<Tuple> tupleItr = tuples.iterator();
		
		while(tupleItr.hasNext())
		{
			gNode.groupTuple(tupleItr.next());
		}
		
		gNode.processGroupOutput();
	}
	
	
	public void orderEvaluate()
	{
		oNode = new OrderbyNode();
		Relation orderedRelation = oNode.orderBy(groupOutputTable.getRelation(), orderFields);
		orderOutputTable = groupOutputTable;
		orderOutputTable.setRelation(orderedRelation);
	}
	
	public void project()
	{
		//TODO
		if(ASTNode.limit>0)
		{
			Relation rel=new Relation();
			for(int i=0;i<ASTNode.limit;i++)
				rel.addTuple(ASTNode.orderOutputTable.getRelation().getTuples().get(i));
			
			ASTNode.orderOutputTable.getRelation().clearRelation();
			ASTNode.orderOutputTable.setRelation(rel);
		}
		
		Iterator itr  = ASTNode.orderOutputTable.getRelation().getTuples().iterator();
		while(itr.hasNext())
		{
			ArrayList<String> projFields = null;
			Tuple tuple= (Tuple)itr.next();
			if(outerprojectFields != null && !outerprojectFields.isEmpty())
				projFields = outerprojectFields;
			else
				projFields = projectFields;
			String temp = ProjectNode.projectTuple(tuple, projFields).finalPrint();
			System.out.println(temp);
		}
		//long endTime   = System.currentTimeMillis();
		//long totalTime = endTime - ASTNode.startTime;
		//System.out.println(totalTime/100);	
		
		System.exit(0);	
	}
}