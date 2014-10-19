package edu.buffalo.cse562.Miscellaneous;

import jdbm.SecondaryKeyExtractor;
import edu.buffalo.cse562.QueryHandler.Tuple;

public class CustomSecondaryKeyExtractor<ForeignKeyType,PrimaryKeyType,PrimaryValueType> implements SecondaryKeyExtractor
{
	private String fKey;
	public CustomSecondaryKeyExtractor(String key)
	{
		fKey=key;
	}
	public ForeignKeyType extractSecondaryKey(Object key,Object value)
	{
		// TODO Auto-generated method stub
		return (ForeignKeyType)((Tuple)value).valueForField(fKey);
	}
	
}
