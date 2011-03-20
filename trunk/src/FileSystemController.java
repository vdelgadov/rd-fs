import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.RandomAccessFile;
import java.util.UUID;


public class FileSystemController {
	
	private FileTable ft;
	private String path;
	
	private static FileSystemController fsc;
	
	private FileSystemController(String path)
	{
		this.path = path;
	}
	
	public static FileSystemController getInstance(String path)
	{
		if (fsc == null)
		{
			fsc = new FileSystemController(path);
		}
		return fsc;
	}
	
	public boolean saveNewFile(String filename, int chunkSeq, byte[] data)
	{
		Log.me(this, "Saving new file to Local File System: "+ filename);
		UUID fileId = ft.addExistingFile(filename, chunkSeq);
		try {
			FileOutputStream receivingFS = new FileOutputStream(path + fileId.toString());
			receivingFS.write(data);
			receivingFS.close();
		} catch (Exception e) {
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
			return false;
		} 
		return true;
	}
	
	public boolean saveUpdatedFile(String filename, int chunkSeq, byte[] data)
	{
		Log.me(this, "Saving updated file to Local File System: "+ filename);
		UUID existingFileId = ft.lookupByName(filename);
		UUID fileId = ft.addExistingFile(filename, chunkSeq);
		try {
			FileOutputStream receivingFS = new FileOutputStream(path + fileId.toString());
			receivingFS.write(data);
			receivingFS.close();
		} catch (Exception e) {
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
			return false;
		} 
		return removeFile(existingFileId);
	}
	
	public boolean hasFile(String filename)
	{
		Log.me(this, "Looking for file: "+ filename);
		return ft.lookupByName(filename) != null;
	}
	
	public boolean hasChunk(String filename, int chunkSeq)
	{
		Log.me(this, "Looking for file: "+ filename);
		return ft.lookupByName(filename,chunkSeq) != null;
	}
	
	public byte[] getFileData(String filename, int offset, int maxData)
	{
		Log.me(this, "Getting data from file: "+ filename);
		UUID existingFileId = ft.lookupByName(filename);
		File file = new File(path+existingFileId);
		byte[] buffer = new byte[maxData];
		try {
			RandomAccessFile fis = new RandomAccessFile(file,"r");
			
			if (fis.read(buffer, offset, maxData) != -1)
			{
				return  buffer;
			}
			else
			{
				return null;
			}
		}
		catch (Exception e) 
		{
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
			return null;
		}
	}
	
	public byte[] getFileData(String filename, int chunkSeq, int offset, int maxData)
	{
		Log.me(this, "Getting data from file: "+ filename);
		UUID existingFileId = ft.lookupByName(filename,chunkSeq);
		File file = new File(path+existingFileId);
		byte[] buffer = new byte[maxData];
		try {
			RandomAccessFile fis = new RandomAccessFile(file,"r");
			
			if (fis.read(buffer, offset, maxData) != -1)
			{
				return  buffer;
			}
			else
			{
				return null;
			}
		}
		catch (Exception e) 
		{
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
			return null;
		}
	}
	
	public boolean removeChunk(String filename, int chunkSeq)
	{
		Log.me(this, "Removing file from Local File System: "+ filename);
		UUID fileId = ft.lookupByName(filename, chunkSeq);
		return removeFile(fileId);
	}
	
	public boolean removeFile(String filename)
	{
		Log.me(this, "Removing file from Local File System: "+ filename);
		UUID fileId = ft.lookupByName(filename);
		return removeFile(fileId);
	}
	
	public boolean removeFile(UUID fileId)
	{
		Log.me(this, "Removing file from Local File System with UUID: "+ fileId.toString());
		try {
		      // Construct a File object for the file to be deleted.
		      File target = new File(path + fileId.toString());

		      if (!target.exists()) 
		      {
			      Log.me(this,"File " + path + fileId.toString() + " not present to begin with!", Log.Priority.WARNING);
			      return false;
		      }

		      // Quick, now, delete it immediately:
		      if (target.delete())
		      {
		    	  Log.me(this,"File " + path + fileId.toString() + " deleted.", Log.Priority.DEBUG);
		    	  return true;
		      }
		      else 
		      {
		    	  Log.me(this,"File " + path + fileId.toString() + " was not deleted.", Log.Priority.WARNING);
		    	  return false;
		      }
		 }
		 catch (SecurityException e) 
		 {
			 Log.me(this,"File " + path + fileId.toString() + " could not be deleted.", Log.Priority.ERROR);
			 return false;
		 }
	}
}
