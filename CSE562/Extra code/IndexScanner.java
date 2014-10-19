package edu.buffalo.cse562;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.SortedMap;

import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.TupleSerializer;
import jdbm.PrimaryMap;
import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;
import jdbm.btree.BTree;


public class IndexScanner
{
	static RecordManager recMan;
	//static PrimaryTreeMap<Integer,Tuple> t1;
	static Set<Integer> ptrKeys;
	IndexHandler ih = null;
	
	public IndexScanner(SQLTable tbl)
	{
		try
		{
			recMan = RecordManagerFactory.createRecordManager(tbl.getTableAlias().toUpperCase());
			
			long recid = recMan.getNamedObject("orders_primary");
			PrimaryTreeMap<Integer,Tuple> t1 = recMan.treeMap(
							"orders_primary",
							new TupleSerializer(tbl.getSchema(),tbl.getTypes())
					);
			
			System.out.println("hey");
			SecondaryTreeMap<String,Integer,Tuple> sMap = t1.secondaryTreeMap("newOrdersSecondary", 
					new SecondaryKeyExtractor<String, Integer, Tuple>() {
						@Override
						public String extractSecondaryKey(Integer key,
								Tuple value) {
							// TODO Auto-generated method stub
							return value.valueForField("orderdate");
						}
				}
			);
			
			System.out.println("oho");
/*			PrimaryTreeMap<String,ArrayList<Tuple>> t3 = recMan.treeMap("orders_datenew");*/
			//System.out.println(tp);
			//ptrKeys = t1.keySet();
			//ih = new IndexHandler(recMan, t1,ptrKeys);
			//System.out.println(t1.size());
			/*for(Entry<String, Tuple> entry : t1.entrySet())
			{
				System.out.println(entry.getValue().finalPrint());
			}*/
			Set<String> dateKeySet = sMap.keySet();
			SortedMap<String,Iterable<Integer>> tpmap =	sMap.subMap("1994-01-01", "1994-02-01");
			for(String st : tpmap.keySet())
			{
				Iterable<Integer> itr = tpmap.get(st);
				for(int i : itr)
				{
					System.out.println(t1.get(i).finalPrint());
				}
				//System.out.println("hey");
			}
			
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
		return ih.endOfIndex();
	}
	
	public static void main(String args[])
	{
		ArrayList<String> tbl = new ArrayList<String>();
		ArrayList<String> tbltype = new ArrayList<String>();
		tbl.add("orderkey");
		tbltype.add("INT");
		tbl.add("custkey");
		tbltype.add("INT");
		tbl.add("orderstatus");
		tbltype.add("CHAR(1)");
		tbl.add("totalprice");
		tbltype.add("DECIMAL");
		tbl.add("orderdate");
		tbltype.add("DATE");
		tbl.add("orderpriority");
		tbltype.add("CHAR(15)");
		tbl.add("clerk");
		tbltype.add("CHAR(15)");
		tbl.add("shippriority");
		tbltype.add("INT");
		tbl.add("comment");
		tbltype.add("VARCHAR(79)");
		SQLTable tbl1 = new SQLTable("ORDERS",tbl,tbltype);
		IndexScanner idS = new IndexScanner(tbl1);
	}
}