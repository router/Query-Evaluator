package edu.buffalo.cse562;

import java.io.StringReader;
import java.util.Iterator;

import net.sf.jsqlparser.JSQLParserException;
import net.sf.jsqlparser.expression.Expression;
import net.sf.jsqlparser.parser.*;
import net.sf.jsqlparser.statement.Statement;
import net.sf.jsqlparser.statement.StatementVisitor;
import net.sf.jsqlparser.statement.create.table.CreateTable;
import net.sf.jsqlparser.statement.select.Select;

public class temp
{
	public static void main(String[] args) throws ParseException, JSQLParserException
    {
		//String sql = "CREATE TABLE example (id INT, data VARCHAR(100));";
		String sql = "SELECT COLUMN_1, COLUMN_2 FROM MY_TABLE_1 JOIN MY_TABLE_2 JOIN MY_TABLE_3 WHERE COLUMN_1 = 'CONDITION_1'";
		//String sql = "SELECT fname, lname, addr FROM prospect UNION SELECT first_name, last_name, address FROM customer UNION SELECT company, '', street FROM vendor;";
		Statement statement = parseSQLExpression(sql);
		Evaluator temp = new Evaluator(statement);
		temp.Statement();
    }
	
	public static Statement parseSQLExpression(String exprStr) throws ParseException, JSQLParserException
	{
		CCJSqlParserManager pm = new CCJSqlParserManager();
		return pm.parse(new StringReader(exprStr));
	}
}