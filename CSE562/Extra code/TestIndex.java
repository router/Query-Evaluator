package edu.buffalo.cse562;

import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Properties;

import com.sun.rowset.internal.Row;

import jdbm.PrimaryMap;
import jdbm.PrimaryStoreMap;
import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;
import net.sf.jsqlparser.schema.Column;
import net.sf.jsqlparser.schema.Table;
import edu.buffalo.cse562.Operators.ScannerNode;
import edu.buffalo.cse562.QueryHandler.CollectionSerializer;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.TupleSerializer;

public class TestIndex
{
	String basePath = "C:/Users/Shail/Downloads/LittleBigDataEvaluation";
	static String FileName = "C:/Users/Shail/Desktop/tpch-dataset/ORDERS.dat";
	public static void IndexBuilderClass(SQLTable tbl,Column col,Column col1)
	{
		try {
				RecordManager recMan = RecordManagerFactory.createRecordManager(tbl.getTableAlias().toUpperCase());
				//	PrimaryTreeMap<Integer, Integer> t1 = recMan.treeMap("odr_primary");
				Tuple tpl;
				PrimaryTreeMap<Integer,Tuple> t1 = recMan.treeMap("orders_primary",
						new IntegerComparator(),
						new TupleSerializer(tbl.getSchema(),tbl.getTypes()));
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
				/*PrimaryTreeMap<String,Tuple> t2 = recMan.treeMap("orders_date",
						new StringComparator(),
						new TupleSerializer(tbl.getSchema(),tbl.getTypes())
						);
				*/
				//PrimaryTreeMap<String,ArrayList<Tuple>> t3 = recMan.treeMap("orders_datenew", new CollectionSerializer());
				//PrimaryMap<String, Tuple> t3 = recMan.treeMap("orders_date");
				
				/*PrimaryTreeMap<String,Tuple> t1 = recMan.treeMap
						(
								tbl.getTableAlias()+"_primary",
								new StringComparator(tpl.valueForField("orderkey")),
								new TupleSerializer(tbl.getSchema(),tbl.getTypes())
						);
				*/
				
				//	SQLTable tbl = new SQLTable();
				ScannerNode scn = new ScannerNode
				(
					null,tbl,FileName,false
				);
				int count = 0;
				while(scn.hasNextTuple())
				{
					//	Optimization possible, Tuple to Integer
					tpl = scn.readNextTuple();
					String key = tpl.valueForField(col.getColumnName());
					//String keyforSecond = tpl.valueForField(col1.getColumnName());
					int key1 = Integer.parseInt(key);
					t1.put(key1,tpl);
					//t2.put(keyforSecond, tpl);
					/*if(t3.containsKey(keyforSecond))
					{
						ArrayList<Tuple> arr = t3.get(keyforSecond);
						arr.add(tpl);
						t3.put(keyforSecond, arr);
					}
					else
					{
						ArrayList<Tuple> arr = new ArrayList<Tuple>();
						arr.add(tpl);
						t3.put(keyforSecond, arr);
					}*/
					count++;
					if(count % 5000 == 0) //depends on how many tuples you want to commit.
					{
						recMan.commit();
					}
					//System.out.println(count);
				}
				recMan.commit();
				
			}
			catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		public static void main(String args[])
		{
			Column c1 = new Column();
			c1.setColumnName("orderkey");
			Column c2 = new Column();
			c2.setColumnName("orderdate");
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
			long startTime = System.currentTimeMillis();
			IndexBuilderClass(tbl1,c1,c2);
			long endTime   = System.currentTimeMillis();
			long totalTime = endTime - startTime;
			System.out.println(totalTime/1000);
		}
		
	}

