
package edu.buffalo.cse562.Operators;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Function;
import edu.buffalo.cse562.Evaluators.ArithmeticEvaluator;
import edu.buffalo.cse562.IOHandler.FileHandler;
import edu.buffalo.cse562.QueryHandler.Relation;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.fieldDetails;
import edu.buffalo.cse562.Operators.SelectNode;

public class ScannerNode extends ASTNode
{
	String basePath;
	SQLTable t;
	boolean isMultipleSource;
	FileHandler fh;
	
	public ScannerNode()
	{}
	
	public ScannerNode(String basePath,SQLTable t, String fileName, boolean isMultipleSource)
	{
		this.basePath = basePath;
		this.t = t;
		this.isMultipleSource = isMultipleSource;
		String filePath;
		fh=new FileHandler();
		//brokenLine=new String(); 
		
		if(fileName == null)
			filePath = basePath+"/"+ t.getTableName() +".dat";
		else
			filePath = fileName;
		
		fh.openReadFile(filePath);
		//incompleteLine=false;
	}
	
	private String getNextLine() {
		String line = fh.readFile();
		return line;
	}
	
	public boolean hasNextTuple()
	{
		if(!fh.endOfFile())
			return true;
		else
			return false;
	}
	
	public Tuple readNextTuple()
	{
		String line = getNextLine();
		ArrayList<String> components=new ArrayList<String>(Arrays.asList(line.split("\\|")));
		Tuple tup=new Tuple(t);
 		tup = t.createNewTupleWithValues(components);
		return tup;
	}
	
	public void closeScanner()
	{
		fh.closeFile();
	}
	
	public void handleAlias(Tuple t)
	{
		if(aliasMap != null && !aliasMap.isEmpty())
		{
			Iterator it=aliasMap.keySet().iterator();
			while(it.hasNext())
			{
				String aName=it.next().toString();
				Expression exp=aliasMap.get(aName);
				if(aName.equals(exp.toString()) || exp instanceof Function)
					continue;
				
				if(ArithmeticEvaluator.isArithMeticOperator(exp))
				{
					ArithmeticEvaluator aeva=new ArithmeticEvaluator(t);
					exp.accept(aeva);
					
					// To drop the trailing zeroes 
					double result = aeva.getResult();
					DecimalFormat df=new DecimalFormat("###.#");
					
					//Add Filed Type
					t.setValue(aName, t.getOwner().getTypeFormFieldName(aName), df.format(result));
				}
				else // normal field alias
				{
					//Add Filed Type
					t.setValue(aName, t.getOwner().getTypeFormFieldName(aName), t.valueForField(exp.toString()));
				}
			}
		}	
	}

	public Tuple validateTuple(Tuple newTuple, ArrayList<Expression> filters) {
		SelectNode selectnode = new SelectNode();
		selectnode.setAliasMap(aliasMap);
		boolean isValidTuple = true;
		
		if(filters != null && !filters.isEmpty())
		{
			Iterator<Expression> expressions = filters.iterator();
			Expression expression;
			
			while(expressions.hasNext())
			{
				expression = expressions.next();
				if(!selectnode.filterTuple(newTuple, expression))
				{
					isValidTuple=false;
					break;
				}
			}
		}
		if(isValidTuple)
			return newTuple;
		else
			return null;
	}
}
