package edu.buffalo.cse562.Operators;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.TreeSet;

import edu.buffalo.cse562.Miscellaneous.OrderDetails;
import edu.buffalo.cse562.Evaluators.OrderbyComparator;
import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;

public class OrderbyNode extends ASTNode {
	
	//private String inDirectory;
	//private String outDirectory;
	static int file_count = 1;
	
	public Relation orderBy(Relation rel, ArrayList<OrderDetails> fields)
	{
		OrderbyComparator oc;
		if(fields != null && !fields.isEmpty())
			oc=new OrderbyComparator(fields);
		else
			oc=new OrderbyComparator(orderFields);
		
		Collections.sort(rel.getTuples(),oc );
		return rel;
	}
	
	
	public Relation externalOrderByLimit(String filePath, SQLTable t, ArrayList<OrderDetails> fields, long limitVal, ArrayList<String> projection)
	{
		OrderbyComparator oc = new	 OrderbyComparator(fields);
		TreeSet<Tuple> tupleset = new TreeSet<Tuple>(oc);
		Relation output = new Relation();
		
		ScannerNode scan = new ScannerNode(null, t, filePath, true);
		
		while(scan.hasNextTuple())
		{
			Tuple tuple = scan.readNextTuple();
			if(tupleset.size() < limitVal)
				tupleset.add(tuple);
			
			else
			{
				int compare = oc.compare(tuple, tupleset.last());
				if(compare < 0)
				{
					tupleset.add(tuple);
					tupleset.pollLast();
				}
			}
		}
		
		Iterator<Tuple> itrTuple = tupleset.iterator();
		
		while(itrTuple.hasNext())
		{
			Tuple tup = ProjectNode.projectTuple(itrTuple.next(), projection);
			output.addTuple(tup);

		}
		
		return output;
	}
}
