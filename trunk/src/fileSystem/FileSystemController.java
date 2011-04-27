package fileSystem;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.io.RandomAccessFile;
import java.util.UUID;

import common.Log;


/**
 * A controller for all local file system activities and tasks. 
 * 
 * It is the interface for the connections between the other controllers and the File System.
 * 
 *  Singleton.
 * 
 * @author Pavooou
 *
 */
public class FileSystemController {
	
	private FileTable ft;
	private static String path;
	
	private static FileSystemController fsc;
	
	private static final String FILE_TABLE_FILENAME = ".filetable";
	
	private FileSystemController(String path)
	{
		FileSystemController.path = path;
		if ((ft = FileTableLookup()) == null)
		{
			ft = new FileTable();
		}
	}
	
	/**
	 * Retrieves an instance of this controller.
	 * 
	 * @param path Path to be used to save all the data into, must be a writeable path.
	 * @return An instance to be used.
	 */
	public static FileSystemController getInstance(String path)
	{
		if (fsc == null)
		{
			fsc = new FileSystemController(path);
		}
		else if (path != null && path != "") { FileSystemController.path = path; }
		return fsc;
	}
	
	/**
	 *  Restarts the table, without physically removing the files from the system.
	 */
	public synchronized void resetTable()
	{
		ft = new FileTable();
	}
	
	/**
	 * Saves a new chunk into the file system, assigns a random-local UUID and physically saves the data.
	 * 
	 * @param filename name of the file to be saved
	 * @param chunkSeq sequence of the chunk to be saved
	 * @param data an array with bytes of the file
	 * @return true if saved correctly, false otherwise
	 * @throws Exception if data sent is null, or if chunkSeq is less than 0
	 */
	public synchronized boolean saveNewFile(String filename, UUID globalId, int chunkSeq, byte[] data) throws Exception
	{
		Log.me(this, "Saving new file to Local File System: "+ filename);
		if (ft.lookupByName(filename,chunkSeq) != null) 
		{
			Log.me(this, "An existing file was found with this name and chunk, delete it first", Log.Priority.WARNING);
			return false;
		}
		if (data == null) { throw new Exception("Data is null"); }
		if (chunkSeq < 0) { throw new Exception("chunkSeq is Zero."); };
		if (data.length == 0) { Log.me(this, "Data to be saved is going to be of zero length", Log.Priority.WARNING); }
		UUID fileId = ft.addExistingFile(filename, globalId, chunkSeq);
		FileOutputStream receivingFS = null;
		try {
			receivingFS = new FileOutputStream(path + fileId.toString());
			receivingFS.write(data);
		} catch (Exception e) {
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
			ft.removeExistingFile(fileId);
			return false;
		} 
		finally {
			if (receivingFS != null)
				try {
					receivingFS.close();
				} catch (final IOException e) {
					Log.me(this, "Couldn't close the file saver.");
				}
		}
		FileTablePersist();
		return true;
	}
	
	/**
	 * Overwrites a chunk into the file system, assigns a random-local UUID and physically saves the data.
	 * 
	 * @param filename name of the file to be saved
	 * @param chunkSeq sequence of the chunk to be saved
	 * @param data an array with bytes of the file
	 * @return true if saved correctly, false otherwise
	 */
	public synchronized boolean saveUpdatedFile(String filename, UUID globalId, int chunkSeq, byte[] data)
	{
		Log.me(this, "Saving updated file to Local File System: "+ filename);
		UUID existingFileId = ft.lookupByName(filename);
		UUID fileId = ft.addExistingFile(filename, globalId, chunkSeq);
		FileOutputStream receivingFS = null;
		try {
			receivingFS = new FileOutputStream(path + fileId.toString());
			receivingFS.write(data);
		} catch (Exception e) {
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
			ft.removeExistingFile(fileId);
			return false;
		} 
		finally {
			if (receivingFS != null)
				try {
					receivingFS.close();
				} catch (final IOException e) {
					Log.me(this, "Couldn't close the file saver.");
				}
		}
		// FileTablePersistence will occur on the following function, no need to duplicate
		return removeFile(existingFileId);
	}
	
	/**
	 * Looks for a file in the local file table.
	 * 
	 * @param filename name to look for
	 * @return true if found on the table, false otherwise.
	 */
	public synchronized boolean hasFile(String filename)
	{
		Log.me(this, "Looking for file: "+ filename);
		return ft.lookupByName(filename) != null;
	}
	
	/**
	 * Retrieves an array with the sequence numbers for all the local chunks.
	 * 
	 * @param filename name to look for
	 * @return An array containing the chunk sequence of this file
	 */
	public synchronized int[] getChunkSeqs(String filename)
	{
		Log.me(this, "Looking for all chunks for file: " + filename);
		return ft.getChunkSeqs(filename);
	}
	
	/**
	 * Looks for an specific chunk of a file in the local file table. 
	 * 
	 * @param filename name to look for
	 * @param chunkSeq chunk to look for
	 * @return true if the chunk was found, false otherwise
	 */
	public synchronized boolean hasChunk(String filename, int chunkSeq)
	{
		Log.me(this, "Looking for file: "+ filename);
		return ft.lookupByName(filename,chunkSeq) != null;
	}
	
	/**
	 * Reads the content of a file, from the first chunk found (if multiple).
	 * 
	 * @param filename name of the file to read the content
	 * @param offset starting point of the reading
	 * @param maxData maximum amount of data read 
	 * @return An array of bytes, that contains the file data, which has maxData length. Null if file not found.
	 */
	public synchronized byte[] getFileData(String filename, int offset, int maxData)
	{
		Log.me(this, "Getting data from file: "+ filename);
		UUID existingFileId = ft.lookupByName(filename);
		File file = new File(path+existingFileId);
		byte[] buffer = new byte[maxData];
		RandomAccessFile fis = null;
		try {
			fis = new RandomAccessFile(file,"r");
			
			if (fis.read(buffer, offset, maxData) != -1)
			{
				fis.close();
				return  buffer;
			}
			else
			{
				fis.close();
				return null;
			}
		}
		catch (Exception e) 
		{
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
		}
		finally
		{
			if  (fis != null)
				try{
					fis.close();
				}
				catch(final Exception e)
				{
					Log.me(this, "Couldn't close the file accesor");
				}
		}
		return null;
	}
	
	/**
	 * Reads the content of a file and the specific chunk sequence defined.
	 * 
	 * @param filename name of the file to read the content
	 * @param chunkSeq sequence number of the chunk to look for
	 * @param offset starting point of the reading
	 * @param maxData maximum amount of data read 
	 * @return An array of bytes, that contains the file data, which has maxData length. Null if not found.
	 */
	public synchronized byte[] getFileData(String filename, int chunkSeq, int offset, int maxData)
	{
		Log.me(this, "Getting data from file: "+ filename);
		UUID existingFileId = ft.lookupByName(filename,chunkSeq);
		File file = new File(path+existingFileId);
		byte[] buffer = new byte[maxData];
		RandomAccessFile fis = null;
		try {
			fis = new RandomAccessFile(file,"r");
			
			if (fis.read(buffer, offset, maxData) != -1)
			{
				fis.close();
				return  buffer;
			}
			else
			{
				fis.close();
				return null;
			}
		}
		catch (Exception e) 
		{
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
		}
		finally 
		{
			if  (fis != null)
				try{
					fis.close();
				}
				catch(final Exception e)
				{
					Log.me(this, "Couldn't close the file accesor");
				}
		}
		return null;
	}
	
	/**
	 * Removes physically and logically the chunk of a file from the file system.
	 * 
	 * @param filename name of the file to remove
	 * @param chunkSeq chunk sequence to remove
	 * @return true if removed correctly, false otherwise
	 */
	public synchronized boolean removeChunk(String filename, int chunkSeq)
	{
		Log.me(this, "Removing file from Local File System: "+ filename);
		UUID fileId = ft.lookupByName(filename, chunkSeq);
		return removeFile(fileId);
	}
	
	/**
	 * Removes physically and logically the first chunk found of a file from the file system.
	 * 
	 * @param filename name of the file to remove
	 * @return true if removed correctly, false otherwise
	 */
	public synchronized boolean removeFile(String filename)
	{
		return removeFile(filename, false);
	}
	
	/**
	 * Removes physically and logically a file from the file system.
	 * 
	 * @param filename name of the file to remove
	 * @param allChunks true if all chunks should be removed, false otherwise
	 * @return true if removed correctly, false otherwise
	 */
	public synchronized boolean removeFile(String filename, boolean allChunks)
	{
		Log.me(this, "Removing file from Local File System: "+ filename+" Remove all chunks: "+allChunks);
		if (!allChunks)
		{
			UUID fileId = ft.lookupByName(filename);
			return removeFile(fileId);
		}
		boolean removedOk = true;
		for (int chunkSeq : ft.getChunkSeqs(filename))
		{
			UUID fileId = ft.lookupByName(filename,chunkSeq);
			removedOk = removedOk && removeFile(fileId);
		}
		return removedOk;
	}
	
	/**
	 * Removes physically and logically the file defined with a Unique Id from the file system.
	 * 
	 * @param fileId Local unique Id of the chunk to remove
	 * @return true if removed correctly, false otherwise
	 */
	public synchronized boolean removeFile(UUID fileId)
	{
		if (fileId == null) 
		{
			Log.me(this, "Trying to remove a null file", Log.Priority.ERROR);
			return false;
		}
		Log.me(this, "Removing file from Local File System with UUID: "+ fileId.toString());
		boolean removalStatus = false;
		try {
		      // Construct a File object for the file to be deleted.
		      File target = new File(path + fileId.toString());

		      if (!target.exists()) 
		      {
			      Log.me(this,"File " + path + fileId.toString() + " not present to begin with!", Log.Priority.WARNING);
		      }
		      // Quick, now, delete it immediately:
		      else if (target.delete())
		      {
		    	  Log.me(this,"File " + path + fileId.toString() + " deleted.", Log.Priority.DEBUG);
		    	  removalStatus = true;
		      }
		      else 
		      {
		    	  Log.me(this,"File " + path + fileId.toString() + " was not deleted.", Log.Priority.WARNING);
		      }
		 }
		 catch (SecurityException e) 
		 {
			 Log.me(this,"File " + path + fileId.toString() + " could not be deleted.", Log.Priority.ERROR);
		 }
		 if (removalStatus)
			 ft.removeExistingFile(fileId);
		 FileTablePersist();
		 return removalStatus;
	}
	
	/**
	 * Retrieves all the file names from the file table
	 * 
	 * @return An array of strings with all the filenames.
	 */
	public synchronized String[] getFileNames()
	{
		Log.me(this,"Getting All FileNames");
		return ft.getFileNames();
	}
	
	/**
	 * Retrieves the physical free space on the path.
	 * 
	 * @return The total space available (in bytes)
	 */
	public long getFreeSize()
	{
		File f = new File(FileSystemController.path);
		return f.getFreeSpace();
	}
	
	private FileTable FileTableLookup()
	{
		Log.me(this, "Looking up for a existing file table");
		FileTable ft = null;
		
		File file = new File(path+FILE_TABLE_FILENAME);
		if (!file.canRead())
		{
			if (file.exists())
			{
				Log.me(this, "Couldn't read the file table", Log.Priority.WARNING);
			}
			Log.me(this, "The filetable file does not exist.");
			return null;
		}
		ObjectInputStream in = null;
		try
		{
			in = new ObjectInputStream(new FileInputStream(file));
			ft = (FileTable) in.readObject();
		}
		catch(Exception e)
		{
			Log.me(this, "Could not deserialize filetable "+e.getMessage(), 
					Log.Priority.WARNING);
			ft = null;
		}
		finally
		{
			if (in != null)
				try
				{
					in.close();
				}
				catch(final Exception e)
				{
					Log.me(this, "Couldn't close the file table accesor");
				}
		}
		return ft;
	}
	
	private void FileTablePersist()
	{
		Log.me(this, "Persisting the filetable.");
		File file = new File(path+FILE_TABLE_FILENAME);
		if (file.exists() && !file.canWrite())
		{
			Log.me(this, "Cannot persist the filetable file due to permissions.");
			return;
		}
		ObjectOutput out = null;
		try
		{
			if (!file.exists() && !file.createNewFile())
			{
				Log.me(this, "Cannot persist the filetable file due to permissions on creating the file.");
				return;
			}
			out = new ObjectOutputStream(new FileOutputStream(file));
		    out.writeObject(ft);
		}
		catch(Exception e)
		{
			Log.me(this, "Could not persist filetable "+e.getMessage(), 
					Log.Priority.WARNING);
		}
		finally
		{
			if (out != null)
				try
				{
					out.close();
				}
				catch(final Exception e)
				{
					Log.me(this, "Couldn't close the file table accesor");
				}
		}
	}
}
