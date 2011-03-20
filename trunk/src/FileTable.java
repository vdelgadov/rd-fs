import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;


public class FileTable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7738165900361831927L;
	
	ArrayList<Entry> entries;
	
	public FileTable()
	{
		entries = new ArrayList<Entry>();
	}
	
	public UUID addNewFile(String filename)
	{
		Log.me(this, "Adding new file to table: "+ filename);
		UUID id = UUID.randomUUID();
		Entry entry = new Entry();
		entry.setId(id);
		entry.setFilename(filename);
		entries.add(entry);
		return id;
	}
	
	public UUID addExistingFile(String filename, int chunkSequence)
	{
		Log.me(this, "Adding existing file to table: "+ filename);
		UUID id = UUID.randomUUID();
		Entry entry = new Entry();
		entry.setId(id);
		entry.setFilename(filename);
		entry.setChunkSequence(chunkSequence);
		entries.add(entry);
		return id;
	}
	
	public UUID lookupByName(String filename)
	{
		Log.me(this, "Looking up for file: "+ filename);
		for (Entry entry : entries) 
		{
			if (entry.getFilename().equals(filename))
			{
				return entry.getId();
			}
		}
		Log.me(this, "File could not be found: "+ filename,Log.Priority.WARNING);
		return null;
	}
	
	public UUID lookupByName(String filename,int chunkSeq)
	{
		Log.me(this, "Looking up for file: "+ filename+", with chunk number: "+ chunkSeq);
		for (Entry entry : entries) 
		{
			if (entry.getFilename().equals(filename) && entry.getChunkSequence() == chunkSeq)
			{
				return entry.getId();
			}
		}
		Log.me(this, "File could not be found: "+ filename,Log.Priority.WARNING);
		return null;
	}
	
	public boolean removeExistingFile(UUID id)
	{
		Log.me(this, "Removing file with UUID: "+ id.toString());
		Entry toRemove = null;
		for (Entry entry : entries) 
		{
			if (id.equals(entry.getId()))
			{
				toRemove = entry;
				break;
			}
		}
		if (toRemove == null)
		{
			// This file was not found, hence could not be removed.
			Log.me(this, "File UUID: "+id.toString()+" could not be found.",Log.Priority.WARNING);
			return false;
		}
		entries.remove(toRemove);
		return true;
	}
	
	private class Entry
	{
		private UUID id;
		private String filename;
		private int chunkSequence;
		
		public UUID getId()
		{
			return id;
		}
		
		public void setId(UUID id)
		{
			this.id = id; 
		}
		
		public String getFilename()
		{
			return filename;
		}
		
		public void setFilename(String filename)
		{
			this.filename = filename;
		}
		
		public int getChunkSequence()
		{
			return chunkSequence;
		}
		
		public void setChunkSequence(int chunkSequence)
		{
			this.chunkSequence = chunkSequence;
		}
	}
	
}
