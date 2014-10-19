package edu.buffalo.cse562.Operators;

import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.expression.operators.conditional.AndExpression;
import net.sf.jsqlparser.expression.operators.conditional.OrExpression;
import edu.buffalo.cse562.Evaluators.ConditionalEvaluators;
import edu.buffalo.cse562.Evaluators.RelationalEvaluator;
import edu.buffalo.cse562.QueryHandler.*;

public class SelectNode extends ASTNode {
	
	void setInput(Relation input) {}
	public Relation evaluateOnPredicate(Relation input, Expression filter) 
	{
		if(filter == null)
			return input;
		else
		{
			Relation output=new Relation();
			for(Tuple t: input.getTuples())
			{
				if(filterTuple(t, filter))
					output.addTuple(t);
			}
			return output;
		}
	}
	
	public boolean filterTuple(Tuple t, Expression exp) // true if passes and false otherwise
	{
		if(exp instanceof AndExpression || exp instanceof OrExpression)
		{
			ConditionalEvaluators c = new ConditionalEvaluators(t);
			exp.accept(c);
			return c.getResult();
		}
		else
		{
			RelationalEvaluator r=new RelationalEvaluator(t);
			exp.accept(r);
			return r.getResult();
		}
	}

}
