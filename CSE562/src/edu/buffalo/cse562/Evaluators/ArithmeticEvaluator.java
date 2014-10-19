/**
 * 
 */
package edu.buffalo.cse562.Evaluators;

import net.sf.jsqlparser.expression.DoubleValue;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.Parenthesis;
import net.sf.jsqlparser.expression.operators.arithmetic.Addition;
import net.sf.jsqlparser.expression.operators.arithmetic.Division;
import net.sf.jsqlparser.expression.operators.arithmetic.Multiplication;
import net.sf.jsqlparser.expression.operators.arithmetic.Subtraction;
import net.sf.jsqlparser.schema.Column;
import edu.buffalo.cse562.AbstractExpressionVisitor;
import edu.buffalo.cse562.QueryHandler.Tuple;

/** 
 * @author Sumit Agarwal
 *
 */
public class ArithmeticEvaluator extends AbstractExpressionVisitor {
private double result;
private Tuple inputTuple;
    
    public double  getResult()
    {
    	return result; 
    }
    public ArithmeticEvaluator(Tuple in)
    {
    	inputTuple = in;
    }

    //@Override
//    public void visit(double dv)
//    { 
//    	result=dv.getValue();
//    }
    

    @Override
    public void visit(Addition adtn) 
    {
    	Expression leftex=adtn.getLeftExpression();
    	Expression rightex=adtn.getRightExpression();
    	double leftValue = 0;
    	double rightValue = 0;
    	
    	if(leftex instanceof Column)
    		leftValue = Double.parseDouble(inputTuple.valueForField(((Column) leftex).toString()));//Double.valueOf();
    	else if(isArithemetic(leftex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		leftex.accept(ae);
    		leftValue=ae.getResult();
    	}
    	else
    		leftValue= Double.parseDouble(leftex.toString());
    	
		if(rightex instanceof Column)
			rightValue= Double.parseDouble(inputTuple.valueForField(((Column) rightex).toString()));//.valueOf();//Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
    	else if(isArithemetic(rightex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		rightex.accept(ae);
    		rightValue=ae.getResult();
    	}
		else
			rightValue=Double.parseDouble(rightex.toString());//Double.valueOf(rightex.toString());
		
		result = leftValue + rightValue;//(leftValue+rightValue);
        
    }

    @Override
    public void visit(Subtraction s) 
    {
    	Expression leftex=s.getLeftExpression();
    	Expression rightex=s.getRightExpression();
    	double leftValue = 0;
		double rightValue = 0;
    	
    	if(leftex instanceof Column)
    		leftValue= Double.parseDouble(inputTuple.valueForField(((Column) leftex).toString()));
    	else if(isArithemetic(leftex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		leftex.accept(ae);
    		leftValue=ae.getResult();
    	}
    	else
    		leftValue= Double.parseDouble(leftex.toString());
    	
		if(rightex instanceof Column)
			rightValue=Double.parseDouble(inputTuple.valueForField(((Column) rightex).toString()));//Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
    	else if(isArithemetic(rightex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		rightex.accept(ae);
    		rightValue=ae.getResult();
    	}
		else
			rightValue=  Double.parseDouble(rightex.toString());
		
		result = leftValue - rightValue;//leftValue-rightValue);
		
    }
    
    @Override
    public void visit(Multiplication mul) 
    {
    	Expression leftex=mul.getLeftExpression();
    	Expression rightex=mul.getRightExpression();
    	double leftValue = 0;
		double rightValue = 0;
    	
    	if(leftex instanceof Column)
    		leftValue=Double.parseDouble(inputTuple.valueForField(((Column) leftex).toString()));//.valueOf();
    	else if(isArithemetic(leftex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		leftex.accept(ae);
    		leftValue=ae.getResult();
    	}
    	else
    		leftValue=Double.parseDouble(leftex.toString());//.valueOf();
    	
		if(rightex instanceof Column)
			rightValue=Double.parseDouble(inputTuple.valueForField(((Column) rightex).toString()));//.valueOf();//Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
    	else if(isArithemetic(rightex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		rightex.accept(ae);
    		rightValue=ae.getResult();
    	}
		else
			rightValue= Double.parseDouble(rightex.toString());//.valueOf();
    	
		result= leftValue * rightValue;//(leftValue*rightValue);
    }
    
    @Override
    public void visit(Division div) 
    {
    	Expression leftex=div.getLeftExpression();
    	Expression rightex=div.getRightExpression();
    	double leftValue = 0;
		double rightValue = 0;
    	
    	if(leftex instanceof Column)
    		leftValue=Double.parseDouble(inputTuple.valueForField(((Column) leftex).toString()));//.valueOf();
    	else if(isArithemetic(leftex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		leftex.accept(ae);
    		leftValue=ae.getResult();
    	}
    	else
    		leftValue=Double.parseDouble(leftex.toString());//.valueOf();
    	
		if(rightex instanceof Column)
			rightValue=Double.parseDouble(inputTuple.valueForField(((Column) rightex).toString()));//.valueOf();//Long.valueOf(inputTuple.valueForField(((Column) rightex).getColumnName())).longValue();
    	else if(isArithemetic(rightex))
    	{
    		ArithmeticEvaluator ae= new ArithmeticEvaluator(inputTuple);
    		rightex.accept(ae);
    		rightValue=ae.getResult();
    	}
		else
			rightValue= Double.parseDouble(rightex.toString());//.valueOf();
		
		result = leftValue / rightValue;
    }
    
    @Override
    public void visit(Parenthesis prnths) 
    {
    	Expression exp=prnths.getExpression();
    	//prnths.getExpression().accept(this);
		ArithmeticEvaluator aeva=new ArithmeticEvaluator(inputTuple);
		exp.accept(aeva);
		result=aeva.getResult();
		
    	
    }
    
    public void visit(Column column) {
    	result=Double.parseDouble(inputTuple.valueForField(column.toString()));;//.valueOf();
       // throw new UnsupportedOperationException("Not supported yet."); 
    }
    private boolean isArithemetic(Expression e)
    {
    	return (e instanceof Addition || e instanceof Subtraction || e instanceof Multiplication || e instanceof Division || e instanceof Parenthesis);
    }
    
    public static boolean isArithMeticOperator(Expression e)
    {
    	return (e instanceof Addition || e instanceof Subtraction || e instanceof Multiplication || e instanceof Division);
    }
}
