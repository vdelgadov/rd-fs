package test;

import static org.junit.Assert.*;

import java.io.File;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import common.Log;

import fileSystem.FileSystemController;

public class FileSystemControllerTest {

	FileSystemController fsc;
	
	
	final static String VALID_PATH = "C:\\Users\\Pavooou\\workspace\\mx.itesm.gda.rdfs\\tmp\\";
	final static String VALID_FILENAME = "file.txt";
	
	@BeforeClass
	public static void setUpClass() throws Exception
	{		
		// Set the priority of the log to DEBUG
		Log.init(Log.Priority.DEBUG);
		FileSystemControllerTest.deleteAllOnFolder(VALID_PATH);
		Log.me(null, "Starting FileSystemControllerTest");
	}
		
	@Before
	public void setUp() throws Exception 
	{
		fsc = FileSystemController.getInstance(VALID_PATH);
	}
	
	@Test 
	public void saveNewOnLocal()
	{
		byte[] saved = "This data is needed to be saved".getBytes();
		byte[] buffer;
		fsc.saveNewFile("file.txt", 0, saved);
		buffer = fsc.getFileData("file.txt", 0, saved.length);
		assertArrayEquals(saved, buffer);
	}
	
	public void lookupForFile()
	{
		
	}

	@After
	public void tearDown() throws Exception 
	{
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception
	{
		FileSystemControllerTest.deleteAllOnFolder(VALID_PATH);
	}
	
	// UTILITY 
	public static void deleteAllOnFolder(String path)
	{
		// Remove all files from the valid path.
		File directory = new File(VALID_PATH);

		File[] files = directory.listFiles();
		for (File file : files)
		{
		   // Delete each file
		   if (!file.delete())
		   {
		       // Failed to delete file
		       System.err.println("Failed to delete "+file);
		   }
		}
	}
	
}
