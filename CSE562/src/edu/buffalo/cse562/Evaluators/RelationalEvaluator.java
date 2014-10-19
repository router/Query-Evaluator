package edu.buffalo.cse562.Evaluators;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.ExpressionVisitor;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.StringValue;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import net.sf.jsqlparser.expression.operators.relational.Between;
import net.sf.jsqlparser.expression.operators.relational.EqualsTo;
import net.sf.jsqlparser.expression.operators.relational.GreaterThan;
import net.sf.jsqlparser.expression.operators.relational.GreaterThanEquals;
import net.sf.jsqlparser.expression.operators.relational.MinorThan;
import net.sf.jsqlparser.expression.operators.relational.MinorThanEquals;
import net.sf.jsqlparser.expression.operators.relational.NotEqualsTo;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.AbstractExpressionVisitor;
import edu.buffalo.cse562.QueryHandler.Tuple;
import net.sf.jsqlparser.expression.Function;

public class RelationalEvaluator extends AbstractExpressionVisitor {
	
	private boolean result;
	private Tuple inputTuple;
	
	public RelationalEvaluator(Tuple in)
	{
		inputTuple=in;
	}
	public boolean getResult()
	{
		return result;
	}
	@Override
    public void visit(Between btwn) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }

    @Override
    public void visit(EqualsTo et) {
        //throw new UnsupportedOperationException("Not supported yet.");
    	Expression leftex=et.getLeftExpression();
    	Expression rightex=et.getRightExpression();
    	
    	int r=evaluateExpression(leftex, rightex);
        result=(r==0);
    	
//    	if(leftex instanceof Column)
//    	{
//    		long leftvalue= Long.valueOf(inputTuple.valueForField(((Column) leftex).getColumnName())).longValue();
//    		//gt.getRightExpression().accept(this);
//    		long rightValue=0;
//    		if(rightex instanceof Column)
//    			rightValue=Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
//    		else
//    			rightValue=Long.valueOf(rightex.toString()).longValue();
//    		result = leftvalue == rightValue;
//    	}
    }

    @Override
    public void visit(GreaterThan gt) {
    	//throw new UnsupportedOperationException("Not supported yet.");
    	Expression leftex=gt.getLeftExpression();
    	Expression rightex=gt.getRightExpression();
    	
    	int r=evaluateExpression(leftex, rightex);
        result=(r>0);
    	
//    	if(leftex instanceof Column) // assuming that the left exp will always be a column
//    	{
//    		// For greter than the value on the RHS has to be an integer/long
//    		long leftvalue= Long.valueOf(inputTuple.valueForField(((Column) leftex).getColumnName())).longValue();
//    		//gt.getRightExpression().accept(this);
//    		
//    		long rightValue=0;
//    		if(rightex instanceof Column)
//    			rightValue=Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
//    		else
//    			rightValue=Long.valueOf(rightex.toString()).longValue();
//            //System.out.println(leftValue+""+rightValue);
//            result=leftvalue > rightValue;
//    	}
//        
        

    }

    @Override
    public void visit(GreaterThanEquals gte) {
        //throw new UnsupportedOperationException("Not supported yet.");
       	Expression leftex=gte.getLeftExpression();
    	Expression rightex=gte.getRightExpression();
    	int r=evaluateExpression(leftex, rightex);
        result=(r>=0);
		
    	
//    	if(leftex instanceof Column) // assuming that the left exp will always be a column
//    	{
//    		// For greter than the value on the RHS has to be an integer/long
//    		double leftvalue=Double.valueOf(inputTuple.valueForField(((Column) leftex).getColumnName()));//Long.valueOf().longValue();
//    		//gt.getRightExpression().accept(this);
//    		double rightValue=0.0f;
//    		if(rightex instanceof Column)
//    			rightValue=Double.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName()));//Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
//    		else
//    			rightValue=Double.valueOf(rightex.toString());//Long.valueOf(rightex.toString()).longValue();
//            //System.out.println(leftValue+""+rightValue);
//    		int r=Double.compare(leftvalue, rightValue);
//            result=(r>=0);
//    	}
//    	else // assuming that the only two possible types for the lhs are column or double. note for possible longvalues
//    	{
//    		double leftValue=Double.valueOf(leftex.toString());
//    		double rightValue=0.0f;
//    		if(rightex instanceof Column)
//    			rightValue=Double.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName()));//Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
//    		else
//    			rightValue=Double.valueOf(rightex.toString());
//    	}
    	
    }
	
    @Override
    public void visit(MinorThan mt) {
        //throw new UnsupportedOperationException("Not supported yet."); 
       	Expression leftex=mt.getLeftExpression();
    	Expression rightex=mt.getRightExpression();
    	int r=evaluateExpression(leftex, rightex);
        result=(r<0);
    	
//    	if(leftex instanceof Column) // assuming that the left exp will always be a column
//    	{
//    		// For greter than the value on the RHS has to be an integer/long
//    		long leftvalue= Long.valueOf(inputTuple.valueForField(((Column) leftex).getColumnName())).longValue();
//    		//gt.getRightExpression().accept(this);
//    		long rightValue=0;
//    		if(rightex instanceof Column)
//    			rightValue=Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
//    		else
//    			rightValue=Long.valueOf(rightex.toString()).longValue();
//            //System.out.println(leftValue+""+rightValue);
//            result=leftvalue < rightValue;
//    	}
        
    }

    @Override
    public void visit(MinorThanEquals mte) {
       // throw new UnsupportedOperationException("Not supported yet."); 
       	Expression leftex=mte.getLeftExpression();
    	Expression rightex=mte.getRightExpression();
    	int r=evaluateExpression(leftex, rightex);
        result=(r<=0);
    	
//    	if(leftex instanceof Column) // assuming that the left exp will always be a column
//    	{
//    		// For greter than the value on the RHS has to be an integer/long
//    		long leftvalue= Long.valueOf(inputTuple.valueForField(((Column) leftex).getColumnName())).longValue();
//    		//gt.getRightExpression().accept(this);
//    		long rightValue=0;
//    		if(rightex instanceof Column)
//    			rightValue=Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
//    		else
//    			rightValue=Long.valueOf(rightex.toString()).longValue();
//            //System.out.println(leftValue+""+rightValue);
//            result=leftvalue <= rightValue;
//    	}
        
    }

    @Override
    public void visit(NotEqualsTo net) {
      //  throw new UnsupportedOperationException("Not supported yet."); 
       	Expression leftex=net.getLeftExpression();
    	Expression rightex=net.getRightExpression();
    	int r=evaluateExpression(leftex, rightex);
        result=(r!=0);
    	
    	
//    	if(leftex instanceof Column) // assuming that the left exp will always be a column
//    	{
//    		// For greter than the value on the RHS has to be an integer/long
//    		long leftvalue= Long.valueOf(inputTuple.valueForField(((Column) leftex).getColumnName())).longValue();
//    		//gt.getRightExpression().accept(this);
//    		long rightValue=0;
//    		if(rightex instanceof Column)
//    			rightValue=Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
//    		else
//    			rightValue=Long.valueOf(rightex.toString()).longValue();
//            //System.out.println(leftValue+""+rightValue);
//            result=leftvalue != rightValue;
//    	}
        
        
    }

    public void visit(Parenthesis prnths) 
    {
    	Expression exp=prnths.getExpression();
    	if(ArithmeticEvaluator.isArithMeticOperator(exp))
    	{
    		ArithmeticEvaluator aeva=new ArithmeticEvaluator(inputTuple);
    		exp.accept(aeva); 
    	}
    	else if(ConditionalEvaluators.isConditionalOperator(exp))
    	{
    		ConditionalEvaluators aeva=new ConditionalEvaluators(inputTuple);
    		exp.accept(aeva); 
    	}
    	else if(RelationalEvaluator.isRelationalOperator(exp))
    	{
    		RelationalEvaluator aeva=new RelationalEvaluator(inputTuple);
    		exp.accept(aeva); 
    	}
    	
    }
    @Override
    public void visit(Column column) {
        throw new UnsupportedOperationException("Not supported yet."); 
    }
    
    private boolean isArithemetic(Expression e)
    {
    	return (e instanceof Addition || e instanceof Subtraction || e instanceof Multiplication || e instanceof Division);
    }
    
    
    public static boolean isRelationalOperator(Expression e)
    {
    	return (e instanceof Between || e instanceof EqualsTo || e instanceof GreaterThan || e instanceof GreaterThanEquals || e instanceof MinorThan || e instanceof MinorThanEquals || e instanceof NotEqualsTo);
    }
    
    private int evaluateExpression(Expression leftex,Expression rightex)
    {
    	int comparisonType=-1; //0-->Strings 1---> Double 2----> dates
    	String rValue=null;
    	
    	if(rightex instanceof Function)
    		comparisonType=2;
    	else if(rightex instanceof StringValue)
    	{
    		comparisonType=0;
    		rValue=((StringValue)rightex).getValue();
    		
    	}
    	else if(isArithemetic(rightex) )
    		comparisonType=1;
    	else if(rightex instanceof Column)
    	{
    		String rightexp=rightex.toString();
    		String type=inputTuple.getOwner().getTypeFormFieldName(rightexp);
    		rValue = inputTuple.valueForField(rightexp);
//    		if(isNumeric(rValue))
//    			comparisonType=1;
//    		else if(isValidDate(rValue,"yyyy-MM-dd"))
//    			comparisonType=2;
//    		else
//    			comparisonType=0;
    		
    		if(type.equals("int")|| type.equals("dec"))
    			comparisonType=1;
    		else
    			comparisonType=0;
    			
    	}
    	
    	
    		
    	//if(comparisonType==2)
//    	{
//    		String dateright=null;
//    		if(rightex instanceof Function)
//    		{
//    			String pa = ((Function) rightex).getName();
//    		
//		    	if((pa.equals("date")||pa.equals("DATE")))
//		    	{
//		    		
//		    		dateright= dr.substring(2, (dr.length()-2));
//		    	}
//    		}
//	    	else
//	    		dateright=rValue;
//    	
//    		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
//    		
//    		String dateleft = inputTuple.valueForField(leftex.toString());
//    		try {
//				Date dateleft1 = sdf.parse(dateleft);
//				Date dateright1 = sdf.parse(dateright);
//				return dateleft1.compareTo(dateright1);
//				
//			} catch (ParseException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//    	}
    		
    	
    	if(comparisonType==0)
    	{
    		String value=inputTuple.valueForField(((Column) leftex).toString());
    		return value.compareTo(rValue);
    	}
    	
    	
    	double leftValue=0.0f,rightValue=0.0f;
    	if(leftex instanceof Column)
    		leftValue=Double.valueOf(inputTuple.valueForField(((Column) leftex).toString()));
    	else if(isArithemetic(leftex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		leftex.accept(ae);
    		leftValue=ae.getResult();
    	}
    	else
    		leftValue=Double.valueOf(leftex.toString());
    	
		if(rightex instanceof Column)
			rightValue=Double.valueOf(inputTuple.valueForField(((Column) rightex).toString()));//Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
    	else if(isArithemetic(rightex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		rightex.accept(ae);
    		rightValue=ae.getResult();
    	}
		else
			rightValue=Double.valueOf(rightex.toString());

		return Double.compare(leftValue, rightValue);
    }
//	public boolean isNumeric(String s)
//	{
//		  try  
//		  {  
//		    double d = Double.parseDouble(s);  
//		  }  
//		  catch(NumberFormatException nfe)  
//		  {  
//		    return false;  
//		  }  
//		  return true;  
//	}
//	public boolean isValidDate(String dateToValidate, String dateFromat)
//	{	 
//		if(dateToValidate == null)
//			return false;
// 
//		SimpleDateFormat sdf = new SimpleDateFormat(dateFromat);
//		sdf.setLenient(false);
// 		try {
//			Date date = sdf.parse(dateToValidate);
// 		} 
//		catch (Exception e){
// 			return false;
//		}
// 
//		return true;
//	}
}