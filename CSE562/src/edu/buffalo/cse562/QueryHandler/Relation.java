package edu.buffalo.cse562.QueryHandler;

import java.util.ArrayList;

public class Relation {
	
	private ArrayList<Tuple> tupleSet;
	public Relation()
	{
		tupleSet= new ArrayList<Tuple>();
	}
	
	public int tupleCount() 
	{
		return tupleSet.size();
	}
	public ArrayList<Tuple> getTuples() 
	{ 
		return tupleSet;
	}
	public void addTuple(Tuple t) 
	{
		tupleSet.add(t);
	}
	public void addTuples(ArrayList<Tuple> t)
	{
		tupleSet.addAll(t);
		// Check if tupleset size if exceeding the memory limit. If yes append to the member file fileToDump and flush remove all contents from arraylist
	}
	
	public void clearRelation()
	{
		tupleSet.clear();
		System.gc();
	}
}
