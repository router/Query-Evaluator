package edu.buffalo.cse562;

import edu.buffalo.cse562.IOHandler.Parser;
import edu.buffalo.cse562.Operators.ASTNode;

public class Main {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//CHK0
		//System.out.println("We, the members of our team, agree that we will not submit any code that we have not written ourselves, share our code with anyone outside of our group, or use code that we have not written ourselves as a reference.");
		ASTNode.startTime = System.currentTimeMillis();
		Parser parser=new Parser();
		parser.parseCmdLineArguments(args);
//		long endTime   = System.currentTimeMillis();
//		long totalTime = endTime - ASTNode.startTime;
//		System.out.println(totalTime/100);	 
	}
}
