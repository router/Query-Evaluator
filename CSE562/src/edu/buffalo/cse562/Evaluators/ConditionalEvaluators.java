package edu.buffalo.cse562.Evaluators;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import edu.buffalo.cse562.AbstractExpressionVisitor;
import edu.buffalo.cse562.QueryHandler.Tuple;

public class ConditionalEvaluators extends AbstractExpressionVisitor {
	
	private boolean result;
	private Tuple inputTuple;
	
	public ConditionalEvaluators(Tuple in)
	{
		inputTuple=in;
	}
	
	public boolean getResult()
	{
		return result;
	}
	
	
    @Override
    public void visit(AndExpression ae) {
       // throw new UnsupportedOperationException("Not supported yet.");
    	
    	Expression leftexp=ae.getLeftExpression();
    	Expression rightexp=ae.getRightExpression();
    	boolean leftValue=false,rightValue=false;
    	
    	if(leftexp instanceof AndExpression || leftexp instanceof OrExpression)
    	{
    		ae.getLeftExpression().accept(this);
    		leftValue=result;
    	}
    	else
    	{
    		RelationalEvaluator rel=new RelationalEvaluator(inputTuple);
    		ae.getLeftExpression().accept(rel);
    		leftValue=rel.getResult();
    	}
       
        
    	if(rightexp instanceof AndExpression || rightexp instanceof OrExpression)
    	{
    		ae.getRightExpression().accept(this);
    		rightValue=result;
    	}
       	else
    	{
    		RelationalEvaluator rel=new RelationalEvaluator(inputTuple);
    		ae.getRightExpression().accept(rel);
    		rightValue=rel.getResult();
    	}
    	
        //System.out.println(leftValue+""+rightValue);
        result=leftValue && rightValue;
        
    }

    @Override
    public void visit(OrExpression oe) {
       // throw new UnsupportedOperationException("Not supported yet."); 
//    	oe.getLeftExpression().accept(this);
//        boolean leftValue = result;
//        oe.getRightExpression().accept(this);
//        boolean rightValue = result;
        
        
        Expression leftexp=oe.getLeftExpression();
    	Expression rightexp=oe.getRightExpression();
    	boolean leftValue=false,rightValue=false;
    	
    	if(leftexp instanceof AndExpression || leftexp instanceof OrExpression)
    	{
    		oe.getLeftExpression().accept(this);
    		leftValue=result;
    	}
    	else
    	{
    		RelationalEvaluator rel=new RelationalEvaluator(inputTuple);
    		oe.getLeftExpression().accept(rel);
    		leftValue=rel.getResult();
    	}
       
        
    	if(rightexp instanceof AndExpression || rightexp instanceof OrExpression)
    	{
    		oe.getRightExpression().accept(this);
    		rightValue=result;
    	}
       	else
    	{
    		RelationalEvaluator rel=new RelationalEvaluator(inputTuple);
    		oe.getRightExpression().accept(rel);
    		rightValue=rel.getResult();
    	}
        
        
        //System.out.println(leftValue+""+rightValue);
        result=leftValue || rightValue;
    }
    public static boolean isConditionalOperator(Expression ex)
    {
    	return (ex instanceof AndExpression || ex instanceof OrExpression);
    }
}
