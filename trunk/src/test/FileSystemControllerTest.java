package test;

import static org.junit.Assert.*;

import java.util.UUID;

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
	
	final static String INVALID_PATH = "-0:\\dd\\";
	
	@BeforeClass
	public static void setUpClass() throws Exception
	{		
		// Set the priority of the log to DEBUG
		Log.init(Log.Priority.DEBUG);
		Log.me(null, "---------------------");
		Log.me(null, "UNIT TEST STARTS:");
		Log.me(null, "---------------------");
		Log.me(null, "Starting FileSystemControllerTest");
	}
		
	@Before
	public void setUp() throws Exception 
	{
		fsc = FileSystemController.getInstance(VALID_PATH);
		this.deleteAllFiles();
	}
	
	/**
	 * Test case: Save a new file on the local file system, and verify if data was correctly saved.
	 * 
	 * @throws Exception
	 */
	@Test 
	public void saveNewOnLocal() throws Exception
	{
		byte[] saved = "This data is needed to be saved".getBytes();
		byte[] buffer;
		fsc.saveNewFile("file.txt", 0, saved);
		assertTrue(fsc.hasChunk("file.txt", 0));
		buffer = fsc.getFileData("file.txt", 0, saved.length);
		assertArrayEquals(saved, buffer);
	}
	
	/**
	 * Test case: Save a file on the local file system using an invalid path, and verify the error.
	 * 
	 * @throws Exception
	 */
	@Test 
	public void saveOnInvalidPath() throws Exception
	{
		byte[] saved = "This data is needed to be saved".getBytes();
		fsc = FileSystemController.getInstance(INVALID_PATH);
		fsc.saveNewFile("file.txt", 0, saved);
		assertFalse(fsc.hasChunk("file.txt", 0));
		assertNull(fsc.getFileData("file.txt", 0, saved.length));
		
		assertFalse(fsc.removeFile(UUID.randomUUID()));
		
		fsc = FileSystemController.getInstance(VALID_PATH);
		fsc.saveNewFile("file.txt", 0, saved);
		assertTrue(fsc.hasChunk("file.txt", 0));
		
		fsc = FileSystemController.getInstance(INVALID_PATH);
		byte[] saved2 = "This data also needs to be saved".getBytes();
		assertFalse(fsc.saveUpdatedFile("file.txt", 0, saved2));
		
		assertTrue(fsc.hasChunk("file.txt", 0));
	}
	
	/**
	 * Test case: Save a new file on the local file system, and verify if data was correctly saved;
	 * try to save the same file and chunk as a new file, receive an error.
	 * 
	 * @throws Exception
	 */
	@Test 
	public void saveNewOnLocalError() throws Exception
	{
		byte[] saved = "This data is needed to be saved".getBytes();
		byte[] buffer;
		assertTrue(fsc.saveNewFile("file.txt", 0, saved));
		assertTrue(fsc.hasChunk("file.txt", 0));
		buffer = fsc.getFileData("file.txt", 0, saved.length);
		assertArrayEquals(saved, buffer);
		
		assertFalse(fsc.saveNewFile("file.txt", 0, saved));
		assertTrue(fsc.hasChunk("file.txt", 0));
		byte[] buffer2 = fsc.getFileData("file.txt", 0, saved.length);
		assertArrayEquals(saved, buffer2);
	}
	
	/**
	 * Test case: Save a new empty file on the local file system, and verify if data was correctly saved.
	 * 
	 * @throws Exception
	 */
	@Test
	public void saveEmptyFile() throws Exception
	{
		byte[] saved = {};
		fsc.saveNewFile("file.txt", 0, saved);
		assertTrue(fsc.hasChunk("file.txt", 0));
		byte [] buffer = fsc.getFileData("file.txt", 0, saved.length);
		assertArrayEquals(saved, buffer);
	}
	
	/**
	 * Test case: Save multiple chunks of a same file and verify its contents were saved, using different contents per chunk.
	 * 
	 * @throws Exception
	 */
	@Test
	public void saveMultipleChunks() throws Exception
	{
		byte[] saved = "This data is needed to be saved. FIRST CHUNK".getBytes();
		byte[] buffer;
		fsc.saveNewFile("file.txt", 0, saved);
		assertTrue(fsc.hasChunk("file.txt", 0));
		buffer = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer);
		byte[] saved2 = "This data is needed to be saved. SECOND CHUNK.".getBytes();
		byte[] buffer2;
		fsc.saveNewFile("file.txt", 1, saved2);
		assertTrue(fsc.hasChunk("file.txt", 1));
		buffer2 = fsc.getFileData("file.txt", 1, 0, saved2.length);
		assertArrayEquals(saved2, buffer2);
		
		byte[] buffer3 = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer3);
	}
	
	/**
	 * Test case: Save a file and overwrite it, saving multiple chunks each time and verifying it has its data saved.
	 * 
	 * @throws Exception
	 */
	@Test
	public void saveMultipleChunksOverwrite() throws Exception
	{
		byte[] saved = "This data is needed to be saved. FIRST CHUNK".getBytes();
		byte[] buffer;
		fsc.saveNewFile("file.txt", 0, saved);
		assertTrue(fsc.hasChunk("file.txt", 0));
		buffer = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer);
		byte[] saved2 = "This data is needed to be saved. SECOND CHUNK.".getBytes();
		byte[] buffer2;
		fsc.saveNewFile("file.txt", 1, saved2);
		assertTrue(fsc.hasChunk("file.txt", 1));
		buffer2 = fsc.getFileData("file.txt", 1, 0, saved2.length);
		assertArrayEquals(saved2, buffer2);
		
		byte[] buffer3 = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer3);
		
		byte[] saved4 = "This data is needed to be saved. THIRD CHUNK".getBytes();
		byte[] buffer4;
		assertTrue(fsc.hasChunk("file.txt", 0));
		fsc.saveUpdatedFile("file.txt", 0, saved4);
		assertTrue(fsc.hasChunk("file.txt", 0));
		buffer4 = fsc.getFileData("file.txt", 0, 0, saved4.length);
		assertArrayEquals(saved4, buffer4);
		byte[] saved5 = "This data is needed to be saved. FOURTH CHUNK.".getBytes();
		byte[] buffer5;
		assertTrue(fsc.hasChunk("file.txt", 1));
		fsc.saveUpdatedFile("file.txt", 1, saved5);
		assertTrue(fsc.hasChunk("file.txt", 1));
		buffer5 = fsc.getFileData("file.txt", 1, 0, saved5.length);
		assertArrayEquals(saved5, buffer5);
		
		byte[] buffer6 = fsc.getFileData("file.txt", 0, 0, saved4.length);
		assertArrayEquals(saved4, buffer6);
	}
	
	/**
	 * Test case: Save multiple chunks of a same file and verify its contents were saved; remove the first one, 
	 * verify the second one is there; remove the second one, verify both have been erased.
	 * 
	 * @throws Exception
	 */
	@Test
	public void saveMultipleChunksRemoveThem() throws Exception
	{
		byte[] saved = "This data is needed to be saved. FIRST CHUNK".getBytes();
		byte[] buffer;
		assertTrue(fsc.saveNewFile("file.txt", 0, saved));
		assertTrue(fsc.hasChunk("file.txt", 0));
		buffer = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer);
		byte[] saved2 = "This data is needed to be saved. SECOND CHUNK.".getBytes();
		byte[] buffer2;
		assertTrue(fsc.saveNewFile("file.txt", 1, saved2));
		assertTrue(fsc.hasChunk("file.txt", 1));
		buffer2 = fsc.getFileData("file.txt", 1, 0, saved2.length);
		assertArrayEquals(saved2, buffer2);
		
		byte[] buffer3 = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer3);
		
		assertTrue(fsc.removeChunk("file.txt", 0));
		assertFalse(fsc.hasChunk("file.txt", 0));
		assertNull(fsc.getFileData("file.txt", 0, 0, saved.length));
		assertTrue(fsc.hasChunk("file.txt", 1));
		byte[] buffer7 = fsc.getFileData("file.txt", 1, 0, saved2.length);
		assertArrayEquals(saved2, buffer7);
		
		assertFalse(fsc.removeChunk("file.txt", 0));
		assertTrue(fsc.removeChunk("file.txt", 1));
		assertFalse(fsc.hasChunk("file.txt", 0));
		assertFalse(fsc.hasChunk("file.txt", 1));
		assertNull(fsc.getFileData("file.txt", 0, 0, saved.length));
		assertNull(fsc.getFileData("file.txt", 1, 0, saved.length));
	}
	
	/**
	 * Test case: Save multiple chunks of a same file and verify its contents were saved; remove the first one, 
	 * verify the second one is there; remove the second one, verify both have been erased. Alternate Removal.
	 * 
	 * @throws Exception
	 */
	@Test
	public void saveMultipleChunksRemoveThem2() throws Exception
	{
		byte[] saved = "This data is needed to be saved. FIRST CHUNK".getBytes();
		byte[] buffer;
		assertTrue(fsc.saveNewFile("file.txt", 0, saved));
		assertTrue(fsc.hasChunk("file.txt", 0));
		buffer = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer);
		byte[] saved2 = "This data is needed to be saved. SECOND CHUNK.".getBytes();
		byte[] buffer2;
		assertTrue(fsc.saveNewFile("file.txt", 1, saved2));
		assertTrue(fsc.hasChunk("file.txt", 1));
		buffer2 = fsc.getFileData("file.txt", 1, 0, saved2.length);
		assertArrayEquals(saved2, buffer2);
		
		byte[] buffer3 = fsc.getFileData("file.txt", 0, 0, saved.length);
		assertArrayEquals(saved, buffer3);
		
		int[] zero_one = {0,1};
		assertArrayEquals(zero_one, fsc.getChunkSeqs("file.txt"));
		
		assertTrue(fsc.removeFile("file.txt"));
		assertFalse(fsc.hasChunk("file.txt", 0));
		assertNull(fsc.getFileData("file.txt", 0, 0, saved.length));
		assertTrue(fsc.hasChunk("file.txt", 1));
		byte[] buffer7 = fsc.getFileData("file.txt", 1, 0, saved2.length);
		assertArrayEquals(saved2, buffer7);
		
		int[] one = {1};
		assertArrayEquals(one, fsc.getChunkSeqs("file.txt"));
		
		assertTrue(fsc.removeFile("file.txt",true));
		assertFalse(fsc.hasChunk("file.txt", 0));
		assertFalse(fsc.hasChunk("file.txt", 1));
		assertNull(fsc.getFileData("file.txt", 0, 0, saved.length));
		assertNull(fsc.getFileData("file.txt", 1, 0, saved.length));
		
		int[] none = {};
		assertArrayEquals(none, fsc.getChunkSeqs("file.txt"));
		
		String[] empty = {};
		assertArrayEquals(empty, fsc.getFileNames());
		assertTrue(fsc.removeFile("file.txt",true));
		
		fsc.resetTable();
	}
	
	/**
	 * Test case: Save one chunk of a file and verify it is logically correctly saved.
	 * 
	 * @throws Exception
	 */
	@Test
	public void lookupForFile() throws Exception
	{
		byte[] saved = "This data is needed to be saved".getBytes();
		fsc.saveNewFile("file.txt", 0, saved);
		assertTrue(fsc.hasFile("file.txt"));
	}

	@After
	public void tearDown() throws Exception 
	{
		this.deleteAllFiles();
	}
	
	@AfterClass
	public static void tearDownClass() throws Exception
	{
	}
	
	// UTILITY 
	private void deleteAllFiles()
	{
		fsc = FileSystemController.getInstance(VALID_PATH);
		for (String filename : fsc.getFileNames())
		{
			fsc.removeFile(filename,true);
		}
		String[] expected = {};
		assertArrayEquals(expected, fsc.getFileNames());
	}
	
}
