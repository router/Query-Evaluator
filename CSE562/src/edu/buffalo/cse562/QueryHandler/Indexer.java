package edu.buffalo.cse562.QueryHandler;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;

//import com.sun.7ml.internal.ws.policy.privateutil.PolicyUtils.Collections;

//Inspired from the implementation of B+ tree in wikipedia (Subhranil)
public class Indexer<Key extends Comparable<? super Key>, Value>	
{
    /** Pointer to the root node. It may be a leaf or an inner node, but it is never null. */
    private Node root;
    /** the maximum number of keys in the leaf node, M must be > 0 */
    //private final int M;
    /** the maximum number of keys in inner node, the number of pointer is N+1, N must be > 2 */
    private final int MaxKeyCount;
 
    /** Create a new empty tree. */
    public Indexer(int n) {
    	MaxKeyCount=n;
    	root=new LeafNode();
    }
 

    public void insert(Key key, Value value) 
    {
		System.out.println("insert key=" + key);
		SplitNode result = root.insert(key, value);
        if(result!=null)
        {
		    // The old root was splitted in two parts.
		    // We have to create a new root pointing to them
            NonLeafNode newRoot = new NonLeafNode();
            newRoot.keycount=1;
            newRoot.keys[0]=result.key;
            newRoot.children[0]= result.leftChild;
            newRoot.children[1]= result.rightChild;
            root=newRoot;
        }
    }
 

    
//    public void rangeLookup(Key minValue,Key maxValue,Value[] offsets)
//    {
//    	offsets[0]=search(minValue);
//    	offsets[1]=search(maxValue);
//    }
//    
//    public Value search(Key key)
//    {
//        Node node = root;
//        while(node instanceof Indexer.NonLeafNode)
//        { // need to traverse down to the leaf
//        	NonLeafNode inner=(NonLeafNode) node;
//            int index=inner.findLocation(key);
//            node=inner.children[index];
//        }
// 
//
//        LeafNode leaf = (LeafNode) node;
//        int index=leaf.findLocation(key);
//        if(index<leaf.keycount)//&&leaf.keys[index].equals(key))
//        {
//        	return leaf.values[index];
//        } 
//        else
//        {
//        	return null;
//        }
//    }
// 
//    public void dump() {
//	root.dump();
//    }
    
    abstract class Node 
    {
		protected int keycount; //number of keys
		protected Key[] keys;
	 
		abstract public int findLocation(Key key);
		abstract public SplitNode insert(Key key, Value value);
		abstract public void dump();
    }
 
    public class LeafNode extends Node
    {

    	private ArrayList<ArrayList<Value>> values;
    	private LeafNode successor;
    	private LeafNode predecessor;
    	
    	public LeafNode()
    	{
    		keys= (Key[]) new Comparable[MaxKeyCount];
    		values=new ArrayList<ArrayList<Value>>(MaxKeyCount);
    		predecessor=null;
    		successor=null;
    		//values= (ArrayList<Value>[]) new Object[MaxKeyCount]; // offset values for the different keys
//    		for(int i=0;i<MaxKeyCount;i++)
//    		{
//    			values.add(new ArrayList<Value>());
//    		}
    		
    	}
		
    	// Returns the proper bucket for adding the key
		public int findLocation(Key key)
		{

		    //Implementeed as linear search . optimize(subhro)
		    for (int i= 0;i<keycount;i++) 
		    {
		    	if (keys[i].compareTo(key)>=0)
		    	{
				    return i;
				}
		    }
		    return keycount;
		}
 
	public SplitNode insert(Key key, Value value) 
	{
	    
	    int index = findLocation(key);
	    if(index<MaxKeyCount && keys[index]!=null && keys[index].compareTo(key)==0)
	    {
	    	values.get(index).add(value);
	    	
	    	return null;
	    }
	    // new key in the leaf node ..to be added
	    if(keycount==MaxKeyCount) //leaf node is full, split
	    {
	    	int mid=MaxKeyCount/2;
			int siblingSize=keycount-mid;
			
			LeafNode sibling = new LeafNode(); //the sibling always comes to the right of the original node
			sibling.successor=this.successor;
			sibling.predecessor=this;
			this.successor=sibling;
			sibling.keycount = siblingSize;
			
			System.arraycopy(keys,mid,sibling.keys,0,siblingSize);
			//System.arraycopy(values,mid,sibling.values,0,siblingSize);
			
			sibling.values.addAll(values.subList(mid, MaxKeyCount));
			//sibling.values=new ArrayList<ArrayList<Value>>(values.subList(mid, MaxKeyCount-1));
			values=new ArrayList<ArrayList<Value>>(values.subList(0, mid-1));
			
			keycount=mid-1;
			if (index<mid)  //value goes into the left sibling
			    insertNonfull(key,value,index);
			else  // value in right sibling
			    sibling.insertNonfull(key, value, index-mid);

			// Notify the parent about the split
			SplitNode result = new SplitNode(sibling.keys[0],this,sibling);
			return result;
		    }
	    else
	    {
			// The node was not full
			this.insertNonfull(key,value,index);
			return null;
	    }
	}
 
	private void insertNonfull(Key key, Value value, int index) 
	{
		System.arraycopy(keys,index, keys,index+1,keycount-index);
		keys[index]= key;
		values.add(index, new ArrayList<Value>());
		values.get(index).add(value);
		keycount++;
	}
	public void dump()
	{
	    System.out.println("LeafNode h==0");
	    for (int i=0;i<keycount;i++)
	    	System.out.println(keys[i]);
	    
	}

}
 
    class NonLeafNode extends Node {
    	
	public final Node[] children;
 
	
	public NonLeafNode()
	{
		children=new Indexer.Node[MaxKeyCount+1];
		keys=(Key[]) new Comparable[MaxKeyCount]; 
	}

	// Returns the proper bucket for adding the key
	public int findLocation(Key key)
	{
	    //Implementeed as linear search . optimize(subhro)

	    for (int i= 0;i<keycount;i++) 
	    {
	    	if (keys[i].compareTo(key)>0)
	    	{
			    return i;
			}
	    }
	    return keycount;
	}
 
	public SplitNode insert(Key key, Value value)
	{

		if(keycount==MaxKeyCount) // splitting (may not be necessary) but just for safety
		{ // Split
			int mid=(keycount+1)/2;
			int siblingSize=keycount-mid;
			
			NonLeafNode sibling = new NonLeafNode();
			sibling.keycount=siblingSize;
			
			
			System.arraycopy(keys,mid,sibling.keys,0,siblingSize);
			System.arraycopy(children,mid,sibling.children,0,siblingSize+1);
			
	 
			keycount=mid-1;//this is important, so the middle one elevate to next depth(height), inner node's key don't repeat itself
	 
			// Set up the return variable
			SplitNode result = new SplitNode(this.keys[mid-1],this, sibling);
	 
			// Now insert in the appropriate sibling
			if (key.compareTo(result.key)<0) {
			    insertNonfull(key,value);
			} else {
			    sibling.insertNonfull(key, value);
			}
			return result;
	 
	    }
		else
		{	// No split
			insertNonfull(key, value);
			return null;
	    }
	}
 
	private void insertNonfull(Key key, Value value) {
	    // Simple linear search
	    int index=findLocation(key);
	    SplitNode result=children[index].insert(key,value);
	     
	   	if(result!=null) // the child node was split , handle pointers accordingly
	   	{
			if(index==keycount)
			{
			    // Insertion at the rightmost key
			    keys[index]=result.key;
			    children[index]=result.leftChild;
			    children[index+1]=result.rightChild;
			    keycount++;
			}
			else 
			{
			    // Insertion not at the rightmost key
			    //shift i>idx to the right
			    System.arraycopy(keys, index, keys,index+1,keycount-index);
			    System.arraycopy(children,index,children,index+1,keycount-index+1);
	 
			    children[index]=result.leftChild;
			    children[index+1]=result.rightChild;
			    keys[index]=result.key;
			    keycount++;
			}
	    } // else the current node is not affected
	}
 
	/**
	 * This one only dump integer key
	 */
	public void dump() {
	    System.out.println("NonLeafNode h==?");
	    for (int i=0;i<keycount; i++){
		children[i].dump();
		System.out.print('>');
		System.out.println(keys[i]);
	    }
	    children[keycount].dump();
	}
    }
 
    class SplitNode
    {
		public final Key key;
		public final Node leftChild;
		public final Node rightChild;
	 
		public SplitNode(Key k, Node l, Node r)
		{
		    key= k; 
		    leftChild = l;
		    rightChild = r;
		}
    }
    
	public int binarySearch(Key[] keyList,Key K)
	{
		
		int low=0,high=keyList.length-1;
		int index=0;
		while(low<high)
		{
			index=(low+high)/2;
			int comp=K.compareTo(keyList[index]);
			if(comp==0)
				return index;
			else if(comp<0)
			{
				high=index-1;
			}
			else
				low=index+1;
			
		}
		return index;
		
	}
    
    
    public static void main(String[] args)
    {
		String filePath ="/home/subhranil/Desktop/test1";
		File file = new File(filePath);
		FileInputStream fis = null;
		BufferedInputStream bis = null;
		byte[] buffer=new byte[1024];
		String[] lines={};
		String temp=new String();
		try 
		{
			fis = new FileInputStream(file);
			bis = new BufferedInputStream(fis);
		 
			int bytesRead = 0;
			while ((bytesRead = bis.read(buffer)) != -1) 
			{
				String chunk = new String(buffer, 0, bytesRead);
			    temp = temp.concat(chunk);
			}
			lines = temp.split("\n");
		}
		catch (IOException e){
			e.printStackTrace();
		}
		finally {
			try {
			    fis.close();
			    bis.close();
			} 
			catch (IOException ex) {
			    ex.printStackTrace();
			}
		}
		
		Indexer<Integer,Integer> indexer=new Indexer<Integer,Integer>(5);
		
		int off=0;
		for(String str:lines)
		{
			
			Integer i=Integer.parseInt(str);
			indexer.insert(i, off);
			off+=str.length();
		}
		
		Integer[] finalValue=new Integer[2];
		//indexer.rangeLookup(Integer.valueOf(50),Integer.valueOf(61),finalValue);
		System.out.println(finalValue[0]+"_______"+finalValue[1]);
		
    }
    
    
}

