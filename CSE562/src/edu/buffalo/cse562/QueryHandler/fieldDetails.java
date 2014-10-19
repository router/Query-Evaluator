package edu.buffalo.cse562.QueryHandler;

public class fieldDetails implements Comparable<fieldDetails>
{
	private String name;
	private String type;
	
	public fieldDetails(String name, String type) {
		this.setName(name);
		
		if(type.trim().toUpperCase().startsWith("INT"))
			this.setType("int");
		
		else if(type.trim().toUpperCase().startsWith("DECIMAL"))
			this.setType("dec");
		
		else if(type.trim().toUpperCase().startsWith("DEC"))
			this.setType("dec");
		
		else if(type.trim().toUpperCase().startsWith("CHAR"))
			this.setType("char");
		
		else if(type.trim().toUpperCase().startsWith("VARCHAR"))
			this.setType("str");
		
		else if(type.trim().toUpperCase().startsWith("STR"))
			this.setType("str");
		
		else if(type.trim().toUpperCase().startsWith("DATE"))
			this.setType("date");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}
	
	public int compareTo(fieldDetails f)
	{
		return (this.getName().equals(f.getName())&& this.getType().equals(f.getType())) ? 0 : 1 ;
	}
}