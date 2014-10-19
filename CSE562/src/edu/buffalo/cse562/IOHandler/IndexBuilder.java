package edu.buffalo.cse562.IOHandler;

import java.io.IOException;
import java.util.ArrayList;

import jdbm.PrimaryTreeMap;
import jdbm.RecordManager;
import jdbm.RecordManagerFactory;
import jdbm.SecondaryKeyExtractor;
import jdbm.SecondaryTreeMap;
import edu.buffalo.cse562.Operators.ScannerNode;
import edu.buffalo.cse562.QueryHandler.SQLTable;
import edu.buffalo.cse562.QueryHandler.Tuple;
import edu.buffalo.cse562.QueryHandler.TupleSerializer;

public class IndexBuilder<PrimaryKeyType> {
	
	String indexDir;
	String dataDir;
	static RecordManager lRecordManager;
	static int IndexDumpLimit = 40000; 
	
	public IndexBuilder(String ddir,String idir) throws IOException
	{
		indexDir=idir;
		dataDir=ddir;
		lRecordManager = RecordManagerFactory.createRecordManager(indexDir+"/Index");
	}
	
	public void buildIndexOnMultiplePrimaryKey(SQLTable parentTable)
	{
		try {
			//RecordManager lRecordManager = RecordManagerFactory.createRecordManager(indexDir+"/"+parentTable.getTableAlias().toLowerCase());
			
			Tuple tpl;
			String primaryMapName=parentTable.getTableAlias().toLowerCase()+"_primary";
//			System.out.println("Building for::"+primaryMapName);
//			long startTime = System.currentTimeMillis();
			PrimaryTreeMap<PrimaryKeyType,Tuple> primaryMap=(PrimaryTreeMap<PrimaryKeyType, Tuple>) lRecordManager.treeMap(primaryMapName,
					//new SpecialStringComparator(), 	
					new TupleSerializer(parentTable)
						);
				
			ArrayList<SecondaryTreeMap<String,PrimaryKeyType, Tuple>>nonclusteredIndexList=new ArrayList<>();
			for(String fKey:parentTable.getIndexField())
			{
				String fKeyMapName=parentTable.getTableAlias().toLowerCase()+"_"+fKey;
				nonclusteredIndexList.add(primaryMap.secondaryTreeMap(fKeyMapName,new CustomSecondaryKeyExtractor(fKey)));
						
			}
				

			String basePath=dataDir+"/"+parentTable.getTableName()+".dat";
			ScannerNode scn = new ScannerNode(null,parentTable,basePath,false);
			int count = 0;
			while(scn.hasNextTuple())
			{
				//	Optimization possible, Tuple to Integer
				
				tpl = scn.readNextTuple();
//				IntegerSet primaryKey=new IntegerSet();
				int value1=Integer.parseInt(tpl.valueForField(parentTable.getPrimaryField().get(0)));
				int value2=Integer.parseInt(tpl.valueForField(parentTable.getPrimaryField().get(1)));
//				for(String pkey : parentTable.getPrimaryField())
//				{	
//					
//					
//					primaryKey.list.add(Integer.parseInt(tpl.valueForField(pkey)));
//				}
				String key = value1+"#"+value2;
				primaryMap.put((PrimaryKeyType) key,tpl);
				count++;
				if(count % IndexDumpLimit == 0) //depends on how many tuples you want to commit.
				{
					lRecordManager.commit();
					lRecordManager.defrag();
				}
				//System.out.println(count);
			}
			lRecordManager.commit();
			lRecordManager.defrag();
//			long endTime = System.currentTimeMillis();
//			System.out.println("Operation Took: "+(endTime-startTime)/1000+"s");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	
	public void buildIndexOnSinglePrimaryKey(SQLTable parentTable)
	{
		try {
			//RecordManager lRecordManager = RecordManagerFactory.createRecordManager(indexDir+"/"+parentTable.getTableAlias().toLowerCase());
			//lRecordManager = RecordManagerFactory.createRecordManager(indexDir+"/SHAIL");
			Tuple tpl;
			String primaryMapName=parentTable.getTableAlias().toLowerCase()+"_primary";
//			System.out.println("Building for::"+primaryMapName);
//			long startTime = System.currentTimeMillis();
			PrimaryTreeMap<PrimaryKeyType,Tuple> primaryMap=(PrimaryTreeMap<PrimaryKeyType, Tuple>) lRecordManager.treeMap(primaryMapName, new TupleSerializer(parentTable));
				
				
			ArrayList<SecondaryTreeMap<String,PrimaryKeyType,Tuple>>nonclusteredIndexList=new ArrayList<>();
			for(String fKey:parentTable.getIndexField())
			{
				String fKeyMapName=parentTable.getTableAlias().toLowerCase()+"_"+fKey;
				nonclusteredIndexList.add(primaryMap.secondaryTreeMap(fKeyMapName,new CustomSecondaryKeyExtractor<String,PrimaryKeyType,Tuple>(fKey)));
			}
				

			String basePath=dataDir+"/"+parentTable.getTableName()+".dat";
			ScannerNode scn = new ScannerNode(null,parentTable,basePath,false);
			int count = 0;
			while(scn.hasNextTuple())
			{
				//	Optimization possible, Tuple to Integer
				
				tpl = scn.readNextTuple();
				String primaryField = parentTable.getPrimaryField().get(0);
				String primaryVal = tpl.valueForField(primaryField);
				//Integer primaryKey=Integer.parseInt(primaryVal);
				primaryMap.put((PrimaryKeyType) primaryVal,tpl);
				count++;
				if(count % IndexDumpLimit == 0) //depends on how many tuples you want to commit.
				{
					lRecordManager.commit();
					lRecordManager.defrag();
				}
				//System.out.println(count);
			}
		
			lRecordManager.commit();
			lRecordManager.defrag();
//			long endTime = System.currentTimeMillis();
//			System.out.println("Operation Took: "+(endTime-startTime)/1000+"s");
		}
		catch (IOException e)
		{
			e.printStackTrace();
		}
	}
	
	public class CustomSecondaryKeyExtractor<ForeignKeyType,PrimaryKeyType,PrimaryValueType> implements SecondaryKeyExtractor
	{
		private String fKey;
		public CustomSecondaryKeyExtractor(String key)
		{
			fKey=key;
		}
		public ForeignKeyType extractSecondaryKey(Object key,Object value)
		{
			return (ForeignKeyType)((Tuple)value).valueForField(fKey);
		}
	}
}
