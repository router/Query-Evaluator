package edu.buffalo.cse562.Evaluators;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import edu.buffalo.cse562.Miscellaneous.OrderDetails;

//import com.sun.org.apache.xerces.internal.impl.xpath.regex.ParseException;

import edu.buffalo.cse562.QueryHandler.Tuple;

public class OrderbyComparator implements Comparator<Tuple> {
	
	private ArrayList<OrderDetails> fieldsForComparison;
	//private boolean isAsc;
	public OrderbyComparator(ArrayList<OrderDetails> fields)
	{
		fieldsForComparison=fields;
	}
	
	public int compare(Tuple a,Tuple b)
	{
		if(a == null || b == null)
			System.out.println("OC tuples empty!!");
		
		Iterator<OrderDetails> it = fieldsForComparison.listIterator();
	
		while(it.hasNext())
		{
			OrderDetails fields=it.next();
			String orderType=fields.orderType;
			String field = fields.field;
			int result=0;
			SimpleDateFormat sdfObj = new SimpleDateFormat("yyyy-MM-dd");
			if(isNumeric(a.valueForField(field))) 
			{
				double val1 = Double.parseDouble(a.valueForField(field));
				double val2 = Double.parseDouble(b.valueForField(field));
				
				
				if(val1 < val2)
					result= - 1;
				else if(val1 > val2)
					result= 1;
				else if(val1 == val2)
					result = 0;
			}
			else if(isValidDate(a.valueForField(field),"yyyy-MM-dd"))
			{
				try {
					Date d1=sdfObj.parse(a.valueForField(field));
					Date d2=sdfObj.parse(b.valueForField(field));
					result=d1.compareTo(d2);
				}
				catch(Exception e)
				{// this should not come here.
				}
			}
			else
				result=a.valueForField(field).compareTo(b.valueForField(field));
			
			if(result!=0 || !it.hasNext())
			{
				return orderType.equals("ASC")? result: -result;
			}

				
		}

		return 0;	
	}
	
	public boolean isNumeric(String s)
	{
		  try  
		  {  
		    double d = Double.parseDouble(s);  
		  }  
		  catch(NumberFormatException nfe)  
		  {  
		    return false;  
		  }  
		  return true;  
	}
	
	public boolean isValidDate(String dateToValidate, String dateFromat)
	{	 
		if(dateToValidate == null)
			return false;
 
		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
		sdf.setLenient(false);
 		try {
			Date date = sdf.parse(dateToValidate);
 		} 
		catch (Exception e){
 			return false;
		}
 
		return true;
	}
}