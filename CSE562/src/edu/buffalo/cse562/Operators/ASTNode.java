package edu.buffalo.cse562.Operators;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeMap;

import edu.buffalo.cse562.Miscellaneous.OrderDetails;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import net.sf.jsqlparser.expression.Expression;

public abstract class ASTNode {
	
	public static long TUPLE_LIMIT = 5000;
	public static long STRING_LIMIT = 500;
	
	public enum OperationType 
	{
		SELECT,
		PROJECT,
		JOIN,
		GROUPBY,
		UNION,
		SUM,
		COUNT,
		MAX,
		MIN,
		ADDITION,
		SUBTRACTION,
		MULTIPLICATION,
		DIVISION,
		LESSTHAN,
		LESSTHANEQUAL,
		GREATERTHAN,
		GREATHEREQUAL,
		EQUAL,
		COMPLEMENT
	};
	
	public static HashMap<String, Expression> aliasMap = new HashMap<String, Expression>();
	public void setAliasMap(HashMap<String, Expression> aMap)
	{
		ASTNode.aliasMap=aMap;
	}
	
	public static String baseDir;
	public static String swapDir;
	public static String indexDir;
	
	public static ArrayList<SQLTable> joinTables = new ArrayList<SQLTable>();
	public static SQLTable joinOutputTable = null;
	public static SQLTable groupOutputTable = null;
	public static SQLTable orderOutputTable = null;
	
	public static ArrayList<Expression> extraPredicates=new ArrayList<Expression>();
	public static ArrayList<Expression> joinPredicates = new ArrayList<Expression>();
	public static ArrayList<String> groupFields = new ArrayList<String>();
	public static ArrayList<String> projectFields = new ArrayList<String>();
	public static ArrayList<String> innerProjectFields = new ArrayList<String>();
	public static ArrayList<OrderDetails> orderFields = new ArrayList<OrderDetails>();
	public static ArrayList<String> outerprojectFields = new ArrayList<String>();
	
	public static String distinctField = null;
	public static long limit = -1;
	public static boolean isNestedQuery = false;
	public static boolean isOuter = false;
	public static long startTime=0;
	public static TreeMap<String, Tuple> groupBucketsAggOuter = new TreeMap<String,Tuple>();
	public static HashMap<String, String> groupBucketsAgg = new HashMap<String,String>();
	public static HashMap<String, ArrayList<String>> groupBucketsDis = new HashMap<String, ArrayList<String>>();
	public static TreeMap<String, ArrayList<String>> groupBucketsDisOuter  = new TreeMap<String,ArrayList<String>>();
}