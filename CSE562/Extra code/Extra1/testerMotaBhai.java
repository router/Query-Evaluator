package edu.buffalo.cse562;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

import edu.buffalo.cse562.Miscellaneous.IndexScanOperation;
import edu.buffalo.cse562.Operators.IndexScanner;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.fieldDetails;

public class testerMotaBhai {

	public testerMotaBhai() throws IOException
	{
		IndexScanOperation<String> iop = new IndexScanOperation<String>();
		iop.key = "shipdate";
		iop.lowerInclusive = true;
		iop.upperInclusive = true;
		iop.lowerLimit = "1994-01-01";
		iop.upperLimit = "1994-01-01";
		iop.type = "SECONDARY";
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
		IndexScanner<String> idX1 = IndexScanner.getInstance("C:/index");
		IndexScanner<String>.InnerScanner InrScanner = idX1.returnClass(tbl1);
		InrScanner.run(iop);
		while(InrScanner.hasNextTuple())	
		{
			Tuple tpl = InrScanner.readNextTuple();
			System.out.println(tpl.finalPrint());
		}
		IndexScanOperation<String> iop1 = new IndexScanOperation<String>();
		iop1.key = "nationkey";
		iop1.type = "PRIMARY";
		ArrayList<String> tblnew = new ArrayList<String>();
		ArrayList<String> tbltype1 = new ArrayList<String>();
		ArrayList<fieldDetails> field1= new ArrayList<fieldDetails>();
		tblnew.add("nationkey");
		tbltype1.add("INT");
		tblnew.add("name");
		tbltype1.add("VARCHAR(79)");
		tblnew.add("regionkey");
		tbltype1.add("INT");
		tbl.add("comment");
		tbltype.add("VARCHAR(152)");
		Iterator<String> itr3 = tblnew.iterator();
		Iterator<String> itr4 = tbltype1.iterator();
		while(itr3.hasNext() && itr3.hasNext())
		{
			String str1 = itr3.next();
			String str2 = itr4.next();
			fieldDetails f = new fieldDetails(str1, str2);
			field1.add(f);
		}
		SQLTable tblnew1 = new SQLTable("NATION",field1);

		IndexScanner<String> idS1 = IndexScanner.getInstance("C:/index");
		IndexScanner<String>.InnerScanner IndexScannerInner = idS1.returnClass(tblnew1);
		IndexScannerInner.run(iop1);
		while(IndexScannerInner.hasNextTuple())
		{
			Tuple newtup = IndexScannerInner.readNextTuple();
			System.out.println(newtup.finalPrint());
		}
	}
	
	public static void main(String args[]) throws IOException
	{
		testerMotaBhai tmB = new testerMotaBhai();
	}
}
