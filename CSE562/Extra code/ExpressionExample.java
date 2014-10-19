package edu.buffalo.cse562;

import java.io.StringReader;
import net.sf.jsqlparser.expression.*;
import net.sf.jsqlparser.parser.*;

public class ExpressionExample 
{
    public static void main(String[] args) throws ParseException
    {
        String str = "21+((23-67)-(2+3))";
        Expression arithmeticExpr = parseArithmeticExpression(str);
     //   SimpleCalculator calculator = new SimpleCalculator();
     //   arithmeticExpr.accept(calculator);
     //   System.out.println(str+" = "+calculator.getResult());
    }
    
    /**
     * 
     * Parse something like "(3+2)-A"
     * 
     * @param exprStr
     * @return
     * @throws ParseException 
     */
    public static Expression parseArithmeticExpression(String exprStr) throws ParseException
    {
        CCJSqlParser parser = new CCJSqlParser(new StringReader(exprStr));
       // System.out.println(parser.SimpleExpression());
        return parser.SimpleExpression();
    }
    
    
    /**
     * 
     * Parse something like "A<B*10"
     * 
     * @param exprStr
     * @return
     * @throws ParseException 
     */
    public static Expression parseGeneralExpression(String exprStr) throws ParseException
    {
        CCJSqlParser parser = new CCJSqlParser(new StringReader(exprStr));
        return parser.Expression();
    }
}