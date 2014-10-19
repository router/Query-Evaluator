package edu.buffalo.cse562;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import edu.buffalo.cse562.Miscellaneous.SpecialStringComparator;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.TupleSerializer;
import edu.buffalo.cse562.QueryHandler.fieldDetails;
import jdbm.PrimaryMap;
import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;
import jdbm.btree.BTree;


public class testerBhai
{
	static RecordManager recMan;
	//static PrimaryTreeMap<Integer,Tuple> t1;
	static Set<Integer> ptrKeys;
	//IndexHandler ih = null;
	
	public testerBhai(SQLTable tbl)
	{
		try
		{
			recMan = RecordManagerFactory.createRecordManager(tbl.getTableAlias().toUpperCase());
			
			long recid = recMan.getNamedObject("orders_primary");
			RecordManager lRecordManager = RecordManagerFactory.createRecordManager("C:/index/lineitem");
			Tuple tpl;
			String primaryMapName=tbl.getTableAlias().toLowerCase()+"_primary";
			PrimaryTreeMap<String,Tuple> primaryMap=lRecordManager.treeMap(primaryMapName,
//					new SpecialStringComparator(), 	
					new TupleSerializer(tbl)
						);
			
			
			//System.out.println("hey");
//			SecondaryTreeMap<String,Integer,Tuple> sMap = t1.secondaryTreeMap("newOrdersSecondary", 
//					new SecondaryKeyExtractor<String, Integer, Tuple>() {
//						@Override
//						public String extractSecondaryKey(Integer key,
//								Tuple value) {
//							// TODO Auto-generated method stub
//							return value.valueForField("orderdate");
//						}
//				}
//			);
			
			System.out.println("oho");
/*			PrimaryTreeMap<String,ArrayList<Tuple>> t3 = recMan.treeMap("orders_datenew");*/
			//System.out.println(tp);
			//ptrKeys = t1.keySet();
			//ih = new IndexHandler(recMan, t1,ptrKeys);
			//System.out.println(t1.size());
			for(Entry<String, Tuple> entry : primaryMap.entrySet())
			{
				System.out.println(entry.getValue().finalPrint());
			}
			/*Set<String> dateKeySet = sMap.keySet();
			SortedMap<String,Iterable<Integer>> tpmap =	sMap.subMap("1994-01-01", "1994-02-01");
			for(String st : tpmap.keySet())
			{
				Iterable<Integer> itr = tpmap.get(st);
				for(int i : itr)
				{
					System.out.println(t1.get(i).finalPrint());
				}
				//System.out.println("hey");
			}*/
		}
		catch(IOException e)
		{
			e.printStackTrace();
		}
	}
	
	private Tuple getNextTuple()
	{
		return null;
	}
	
	public boolean hasNextTuple()
	{
		return false;
		//return ih.endOfIndex();
	}
	
	public static void main(String args[])
	{
		ArrayList<String> tbl = new ArrayList<String>();
		ArrayList<String> tbltype = new ArrayList<String>();
		ArrayList<fieldDetails> field = new ArrayList<fieldDetails>();
		tbl.add("orderkey");
		tbltype.add("INT");
		tbl.add("partkey");
		tbltype.add("INT");
		tbl.add("suppkey");
		tbltype.add("INT");
		tbl.add("linenumber");
		tbltype.add("INT");
		tbl.add("quantity");
		tbltype.add("DECIMAL");
		tbl.add("extendedprice");
		tbltype.add("DECIMAL");
		tbl.add("discount");
		tbltype.add("DECIMAL");
		tbl.add("tax");
		tbltype.add("DECIMAL");
		tbl.add("returnflag");
		tbltype.add("CHAR");
		tbl.add("linestatus");
		tbltype.add("CHAR");
		tbl.add("shipdate");
		tbltype.add("DATE");
		tbl.add("commitdate");
		tbltype.add("DATE");
		tbl.add("receiptdate");
		tbltype.add("DATE");
		tbl.add("shipinstruct");
		tbltype.add("VARCHAR(29)");
		tbl.add("shipmode");
		tbltype.add("VARCHAR(79)");
		Iterator<String> itr1 = tbl.iterator();
		Iterator<String> itr2 = tbltype.iterator();
		while(itr1.hasNext() && itr2.hasNext())
		{
			String str1 = itr1.next();
			String str2 = itr2.next();
			fieldDetails f = new fieldDetails(str1, str2);
			field.add(f);
		}
		SQLTable tbl1 = new SQLTable("LINEITEM",field);
		testerBhai idS = new testerBhai(tbl1);
	}
}
