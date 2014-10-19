package edu.buffalo.cse562.QueryHandler;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.TreeSet;

import edu.buffalo.cse562.IOHandler.Parser;
import edu.buffalo.cse562.Miscellaneous.IndexScanOperation;
import edu.buffalo.cse562.Miscellaneous.JoinDetails;
import net.sf.jsqlparser.expression.BinaryExpression;
import net.sf.jsqlparser.expression.Expression;

public class SQLTable{
	private String tablename;
	private String tableAlias;
	private ArrayList<fieldDetails> schema;
	private ArrayList<Expression> filters;
	private long totalTupleCount;
	private int aliasflag;
	private TreeSet<JoinDetails> joinSet;
	private boolean isJoinoutput; 
	private ArrayList<fieldDetails> projectedFields;
	private Relation tuples;
	private String filePath;
	private boolean isDumpedInFile;
	private ArrayList<String> primaryField;
	private ArrayList<String> indexField;
	private IndexScanOperation<String> indexLookup;
	
	public SQLTable()
	{
		this.tablename=null;
		this.schema=new ArrayList<fieldDetails>();
		//this.tempSchema=new ArrayList<String>(t.getSchema());
		this.filters=new ArrayList<Expression>();
		this.tableAlias=tablename;
		this.totalTupleCount=0;
		this.isDumpedInFile=false;
		this.filePath="";
		this.indexLookup=null;
		joinSet=new TreeSet<JoinDetails>(new Comparator<JoinDetails>() {
			public int compare(JoinDetails s1,JoinDetails s2)
			{
				int rValue=(int)(Parser.tableSizes.get(s1.table) - Parser.tableSizes.get(s2.table));
				if(rValue==0)
					return s1.table.compareTo(s2.table);
				else
					return rValue; 
			}
		});
		
		this.primaryField = new ArrayList<String>();
		this.indexField = new ArrayList<String>();
		this.tuples=new Relation();		
	}
	public SQLTable(String name,ArrayList<fieldDetails> sch) 
	{
		tablename=name;
		schema=sch;
		//tempSchema=new ArrayList<String>(sch);
		filters=new ArrayList<Expression>();
		tableAlias=tablename;
		aliasflag = 0;
		totalTupleCount=-1;
		isJoinoutput=false;
		
		joinSet=new TreeSet<JoinDetails>(new Comparator<JoinDetails>() {
			public int compare(JoinDetails s1,JoinDetails s2)
			{
				int value=(int)(Parser.tableSizes.get(s1.table) - Parser.tableSizes.get(s2.table));
				if(value==0)
					return s1.table.compareTo(s2.table);
				else
					return value; 
			}
		});
		
		this.primaryField = new ArrayList<String>();
		this.indexField = new ArrayList<String>();
		projectedFields=new ArrayList<fieldDetails>(sch);
		this.tuples=new Relation();
	}
	
	public SQLTable(SQLTable t) // copy constructor
	{
		this.tablename=t.getTableName();
		this.schema=new ArrayList<fieldDetails>(t.getSchema());
		this.filters=new ArrayList<Expression>();
		this.tableAlias=tablename;
		this.totalTupleCount=t.getTupleCount();
		this.indexLookup=t.getIndexLookupOperation();
		
		joinSet=new TreeSet<JoinDetails>(new Comparator<JoinDetails>() {
			public int compare(JoinDetails s1,JoinDetails s2)
			{
				int rValue=(int)(Parser.tableSizes.get(s1.table) - Parser.tableSizes.get(s2.table));
				if(rValue==0)
					return s1.table.compareTo(s2.table);
				else
					return rValue; 
			}
		});
		this.isJoinoutput=t.isJoinoutput();
		this.filePath=t.filePath;
		this.isDumpedInFile=t.isDumpedInFile;
		
		this.primaryField = new ArrayList<String>();
		this.indexField = new ArrayList<String>();
		setPrimaryField(t.getPrimaryField());
		setIndexField(t.getIndexField());
	
		projectedFields=new ArrayList<fieldDetails>(t.getProjectedFields());
	}
	
	public SQLTable(String tableName, String[] columns, String[] columntypes) 
	{
		this.tablename = tableName;
		this.schema=new ArrayList<fieldDetails>();
		//this.tempSchema=new ArrayList<String>(t.getSchema());
		this.filters=new ArrayList<Expression>();
		this.tableAlias=tablename;
		this.totalTupleCount=0;
		this.isDumpedInFile=false;
		this.filePath="";
		joinSet=new TreeSet<JoinDetails>(new Comparator<JoinDetails>() {
			public int compare(JoinDetails s1,JoinDetails s2)
			{
				int rValue=(int)(Parser.tableSizes.get(s1.table) - Parser.tableSizes.get(s2.table));
				if(rValue==0)
					return s1.table.compareTo(s2.table);
				else
					return rValue; 
			}
		});
		
		this.primaryField = new ArrayList<String>();
		this.indexField = new ArrayList<String>();
		this.tuples=new Relation();
		
		int counter = 0;
		while(counter < columns.length)
		{
			this.schema.add(new fieldDetails(columns[counter], columntypes[counter]));
			counter++;
		}
		
		projectedFields=new ArrayList<fieldDetails>(schema);
	}
	
	public void setJoinSet(TreeSet<JoinDetails> treeSet)
	{
		joinSet = treeSet;
	}
	public String getTableName()
	{
		return tablename;
	}
	
	public ArrayList<fieldDetails> getSchema()
	{
		return schema;
	}
	
	public void setSchema(ArrayList<fieldDetails> sch)
	{
		schema=sch;
	}
	public ArrayList<fieldDetails> getProjectedFields()
	{
		return projectedFields;
	}
	
	public void setProjectedFields(ArrayList<fieldDetails> sch)
	{
		projectedFields=new ArrayList<fieldDetails>(sch);
	}
	
	public void addField(String fieldName, String fieldType) {
		schema.add(new fieldDetails(fieldName, fieldType));
	}
	
	public void addField(fieldDetails field) {
		schema.add(field);
	}
	
	public void removeField(String fieldName) {
		int index = getIndexFromFieldName(fieldName);
		schema.remove(index);
	}
	
	public void removeField(fieldDetails field) {
		schema.remove(field);
	}
	
	public ArrayList<Expression> getFilters() {
		return filters;
	}
	
	public void setFilters(ArrayList<Expression> filters) {
		this.filters = filters;
	}
	
	public void addFilter(Expression e) {
		this.filters.add(e);
	}
	
	public String getTableAlias() {
		return tableAlias;
	}
	
	public void setAlias(String a) {
		tableAlias=a;
		aliasflag = 1;
	}
	
	public long getTupleCount() {
		if(!isDumpedInFile)
			return tuples.tupleCount();
		
		return totalTupleCount;
	}
	
	public void setTupleCount(long s) {
		totalTupleCount=s;
	}
	
	public String isRelated(SQLTable t) {
		ArrayList<fieldDetails> sch1=this.getSchema();
		ArrayList<fieldDetails> sch2=t.getSchema();
		Iterator<fieldDetails> field=sch1.iterator();
		// Assumption , each pair of relations have only one common attribute
		while(field.hasNext())
		{
			fieldDetails f=field.next();
			if((sch2.indexOf(f))!=-1)
				return f.getName();
		}
		
		return null;	
	}
	
	public boolean hasExistingAlias() {
		if(aliasflag == 1)
			return true;
		else
			return false;
	}
	
	public TreeSet<JoinDetails> getJoinSet() {
		return joinSet;
	}
	
	public void addToJoinSet(JoinDetails t) {
		if(joinSet==null)
			System.out.println("nullllllllllllllllllll");
		else
			joinSet.add(t);
	}
	
	public void removeFromJoinSet(JoinDetails t) {
		joinSet.remove(t);
	}
	
	public boolean isJoinoutput() {
		return isJoinoutput;
	}
	
	public void setisJoinoutput(boolean v) {
		isJoinoutput=v;
	}
	
	
	public JoinDetails searchForTableInJoinSet(String k) {
		Iterator<JoinDetails> it=joinSet.iterator();
		while(it.hasNext())
		{
			JoinDetails j=it.next();
			if(j.table.equals(k))
				return j;
		}
		return null;
	}
	
	public void searchForTableAndRemoveFromJoinSet(String k) {
		joinSet.remove(searchForTableInJoinSet(k));
	}
//	
//	public ArrayList<String> getTempSchema()
//	{
//		//return tempSchema;
//	}
	
	public void removefromprojectionlist(String key) {
		projectedFields.remove(key);
	}
	
	public Tuple createNewTupleWithValues(ArrayList<String> values) {
		Tuple tuple=new Tuple(this,values);
		//tuples.addTuple(tuple);
		return tuple;
	}
	
	public int getIndexFromFieldName(String fieldName) {
		fieldName = fieldName.trim();
		Iterator<fieldDetails> fieldDetailIterator = this.schema.iterator();
		int index = 0;
		
		while(fieldDetailIterator.hasNext())
		{
			fieldDetails fieldDetail = fieldDetailIterator.next();
			if(fieldDetail.getName().equals(fieldName))
				return index;
			
			index++;
		}
		return -1;
	}
	
	public String getTypeFormFieldName(String fieldName) {
		Iterator<fieldDetails> fieldDetailIterator = this.schema.iterator();
		
		while(fieldDetailIterator.hasNext())
		{
			fieldDetails fieldDetail = fieldDetailIterator.next();
			if(fieldDetail.getName().equals(fieldName))
				return fieldDetail.getType();
		}
		return null;
	}
	
	public String getTypeFormExpression(Expression e)
	{
		String leftField= ((BinaryExpression)e).getLeftExpression().toString();
		String rightField=((BinaryExpression)e).getRightExpression().toString();
		int lIndex=this.getIndexFromFieldName(leftField);
		int rIndex=this.getIndexFromFieldName(rightField);
		if(lIndex!=-1)
			return schema.get(lIndex).getType();
		if(rIndex!=-1)
			return schema.get(rIndex).getType();
		
		return "";
	}
	
	public ArrayList<String> getPrimaryField() {
		return this.primaryField;
	}
	
	public ArrayList<fieldDetails> getPrimaryFieldDetails() {
		ArrayList<fieldDetails> primaryFieldDetails = new ArrayList<fieldDetails>();
		Iterator<String> primaryIterator = this.primaryField.iterator();
		
		while(primaryIterator.hasNext())
		{
			String fieldName = primaryIterator.next();
			primaryFieldDetails.add(new fieldDetails(fieldName, getTypeFormFieldName(fieldName)));
		}
		
		return primaryFieldDetails;
	}
	
	
	public void setPrimaryField(ArrayList<String> primaryField) {
		if(primaryField!=null)
			this.primaryField.addAll(primaryField);
	}
	
	public void addPrimaryField(String primaryField) {
		if(primaryField!=null)
			this.primaryField.add(primaryField);
	}
	
	public void removePrimaryField(String primaryField) {
		if(primaryField!=null)
			this.primaryField.remove(primaryField);
	}
	
	public ArrayList<String> getIndexField() {
		return indexField;
	}
	
	public ArrayList<fieldDetails> getIndexFieldDetails() {
		ArrayList<fieldDetails> indexFieldDetails = new ArrayList<fieldDetails>();
		Iterator<String> indexIterator = this.indexField.iterator();
		
		while(indexIterator.hasNext())
		{
			String fieldName = indexIterator.next();
			indexFieldDetails.add(new fieldDetails(fieldName, getTypeFormFieldName(fieldName)));
		}
		
		return indexFieldDetails;
	}
	public void setIndexField(ArrayList<String> indexField) {
		if(indexField!=null)
			this.indexField.addAll(indexField);
	}
	
	public void addIndexField(String indexField) {
		if(indexField!=null && !this.indexField.contains(indexField))
		this.indexField.add(indexField);
	}
	
	public void removeIndexField(String indexField) {
		this.indexField.remove(indexField);
	}
	
	public void setDumpedInFile(boolean isDumpedInFile) {
		this.isDumpedInFile = isDumpedInFile;
	}
	
	public boolean getDumpedInFile() {
		return this.isDumpedInFile;
	}
	
	public void setFilePath( String filePath) {
		this.filePath = filePath;
	}
	
	public String getFilePath() {
		return this.filePath;
	}
	
	public IndexScanOperation<String> getIndexLookupOperation()
	{
		return indexLookup;
	}
	public void setIndexLookupOperation(IndexScanOperation<String> in)
	{
		indexLookup=in;
	}
	
	public Relation getRelation() {
		return this.tuples;
	}
	
	public void setRelation(Relation tuples) {
		this.tuples = tuples;
	}
	
	public boolean hasUsableIndex()
	{
		return (this.getPrimaryField().size()>0 || this.getIndexField().size()>0) && (this.getIndexLookupOperation()==null);
	}

	public boolean isInSchema(String fieldName)
	{
		boolean isPresent = false;
		
		for(fieldDetails fields : this.schema)
		{
			if(fieldName.equals(fields.getName()))
			{	
				isPresent = true;
				break;
			}
		}
		
		return isPresent;
	}
}
