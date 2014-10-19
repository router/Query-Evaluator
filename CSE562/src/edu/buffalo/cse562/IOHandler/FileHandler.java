/*
 * Author: Kaushal Hakani 
 */
package edu.buffalo.cse562.IOHandler;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.IOException;

public class FileHandler {
	
	File file = null;
	FileReader fr = null;
	BufferedReader br = null;
	FileWriter fw = null;
	BufferedWriter bw = null;
	
	
	
	int offset;
	

	public boolean createDirectory(String directoryName) {
		return (new File(directoryName)).mkdir();
	}
	
	public void createFile(String name) {
		try {
			file=new File(name);
			file.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void openReadFile(String fileName)
	{
		try {
			fr = new FileReader(new File(fileName));
			br = new BufferedReader(fr);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}
	
	public String readFile()
	{
		String line = "";
		try {
			line = br.readLine();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return line;
	}
	
	public void resetFile()
	{
		try {
			fr.reset();
			br = new BufferedReader(fr);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void writeFile(String fileName, String write)
	{
		try {
			file = new File(fileName);
			if(!file.exists())
				createFile(fileName);
			
			fw = new FileWriter(file,true);
			bw = new BufferedWriter(fw);
			bw.write(write);
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public boolean endOfFile()
	{
		try {
			if(fr.ready() || br.ready())
				return false;
			else
			{
				//closeFile();
				return true;
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		return true;
	}
	
	public void closeFile()
	{
		try {
			fr.close();
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean deleteFile(String fileName)
	{
		File f = new File(fileName);
		return f.delete();
	}
}