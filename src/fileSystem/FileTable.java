package fileSystem;
import java.io.ByteArrayInputStream;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.UUID;

import common.Log;


/**
 * A FileTable, to save the relation between the logical files, chunks 
 * and physical files in each node. 
 * 
 * @author Pavooou
 */
public class FileTable implements Serializable {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 7738165900361831927L;
	
	private ArrayList<Entry> entries;
	
	/**
	 *  Constructor for the FileTable, which initializes the list with the 
	 *  entries.
	 */
	public FileTable()
	{
		entries = new ArrayList<Entry>();
	}
	
	/**
	 * Adds a new file to the entries, and assigns a random UUID for it. 
	 * 
	 * @param filename of the file to add
	 * @return Random UUID assigned to the added file
	 */
	public UUID addNewFile(String filename, UUID globalId)
	{
		Log.me(this, "Adding new file to table: "+ filename);
		UUID id = UUID.randomUUID();
		Entry entry = new Entry();
		entry.setLocalId(id);
		entry.setGlobalId(globalId);
		entry.setFilename(filename);
		entries.add(entry);
		return id;
	}
	
	/**
	 * Adds a new file and a chunk sequence to the entries table, and assigns a 
	 * random UUID for it.
	 * 
	 * @param filename of the file to add
	 * @param chunkSequence that is going to be saved locally
	 * @return Random UUID assigned to the added file
	 */
	public UUID addExistingFile(String filename, UUID globalId, int chunkSequence)
	{
		Log.me(this, "Adding existing file to table: "+ filename);
		UUID id = UUID.randomUUID();
		Entry entry = new Entry();
		entry.setLocalId(id);
		entry.setGlobalId(globalId);
		entry.setFilename(filename);
		entry.setChunkSequence(chunkSequence);
		entries.add(entry);
		return id;
	}
	
	/**
	 * Looks for a file in the table, regardless of the chunk sequence.
	 * 
	 * @param filename of the file to be looked for
	 * @return local UUID assigned to that file, null if not found
	 */
	public UUID lookupByName(String filename)
	{
		Log.me(this, "Looking up for file: "+ filename);
		for (Entry entry : entries) 
		{
			if (entry.getFilename().equals(filename))
			{
				return entry.getLocalId();
			}
		}
		Log.me(this, "File could not be found: "+ filename,Log.Priority.WARNING);
		return null;
	}
	
	/**
	 * Looks for a file in the table, regardless of the chunk sequence.
	 * 
	 * @param filename of the file to be looked for
	 * @return local UUID assigned to that file, null if not found
	 */
	public UUID lookupGlobalIdByName(String filename)
	{
		Log.me(this, "Looking up for file: "+ filename);
		for (Entry entry : entries) 
		{
			if (entry.getFilename().equals(filename))
			{
				return entry.getGlobalId();
			}
		}
		Log.me(this, "File could not be found: "+ filename,Log.Priority.WARNING);
		return null;
	}
	
	/**
	 * Looks for a file and chunk sequence in the entries table.
	 * 
	 * @param filename of the file to be looked for
	 * @param chunkSeq chunk sequence to be looked for
	 * @return local UUID assigned to that file, null if not found
	 */
	public UUID lookupByName(String filename,int chunkSeq)
	{
		Log.me(this, "Looking up for file: "+ filename+", with chunk number: "+ chunkSeq);
		for (Entry entry : entries) 
		{
			if (entry.getFilename().equals(filename) && entry.getChunkSequence() == chunkSeq)
			{
				return entry.getLocalId();
			}
		}
		Log.me(this, "File could not be found: "+ filename,Log.Priority.WARNING);
		return null;
	}
	
	/**
	 * Looks for a file given a known global UUID.
	 * 
	 * @param globalUUID of the file to be looked for
	 * @return local UUID assigned to that file, null if not found
	 */
	public String lookupByGlobalUUID(UUID globalUUID)
	{
		Log.me(this, "Looking up for file with global UUID: "+ globalUUID.toString());
		for (Entry entry : entries) 
		{
			if (entry.getGlobalId().equals(globalUUID))
			{
				return entry.getFilename();
			}
		}
		Log.me(this, "File could not be found: "+ globalUUID.toString(),Log.Priority.WARNING);
		return null;
	}
	
	/**
	 * Retrieves all the chunk sequences stored on the entries table.
	 * 
	 * @param filename of the file to get the chunk sequences.
	 * @return An array containing the chunk sequences stored on the table. Empty if none found.
	 */
	public int[] getChunkSeqs(String filename)
	{
		Log.me(this, "Looking up for file: "+ filename);
		ArrayList<Integer> chunkSeqs = new ArrayList<Integer>();
		int[] chunkSeqArray;
		for (Entry entry : entries)
		{
			if (entry.getFilename().equals(filename))
			{
				chunkSeqs.add(entry.getChunkSequence());
			}
		}
		chunkSeqArray = new int[chunkSeqs.size()];
		for(int i = 0;i < chunkSeqArray.length;i++)
			chunkSeqArray[i] = chunkSeqs.get(i);
		return chunkSeqArray;
	}
	
	/**
	 * Retrieves all the filenames stored on the entries table.
	 * 
	 * @return An array containing the file names stored on the entries table. Empty if not found.
	 */
	public String[] getFileNames()
	{
		Log.me(this, "Getting registered file names");
		ArrayList<String> fileNames = new ArrayList<String>();
		String[] fileNamesArray;
		for (Entry entry : entries)
		{
			if (!fileNames.contains(entry.getFilename()))
			{
				fileNames.add(entry.getFilename());
			}
		}
		fileNamesArray = new String[fileNames.size()];
		for(int i = 0;i < fileNamesArray.length;i++)
			fileNamesArray[i] = fileNames.get(i);
		return fileNamesArray;
	}
	
	/**
	 * Removes a chunk entry completely from the table.
	 * 
	 * @param id chunk local UUID to remove
	 * @return true if the file was found and removed correctly, false otherwise.
	 */
	public boolean removeExistingFile(UUID id)
	{
		Log.me(this, "Removing file with UUID: "+ id.toString());
		Entry toRemove = null;
		for (Entry entry : entries) 
		{
			if (id.equals(entry.getLocalId()))
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
	
	
	
	/**
	 * Each one of the entries in the file table, defined with an UUID, a filename and a chunk sequence.
	 * 
	 * @author Pavooou
	 */
	private class Entry implements Serializable
	{
		/**
		 * 
		 */
		private static final long serialVersionUID = -3193901222438596755L;
		
		private UUID localId;
		private UUID globalId;
		private String filename;
		private int chunkSequence;
		
		/**
		 * @return Unique Local Id of the entry
		 */
		public UUID getLocalId()
		{
			return localId;
		}
		
		/**
		 * Sets the Unique Local Id of this entry.
		 * @param id value to be set
		 */
		public void setLocalId(UUID id)
		{
			this.localId = id; 
		}
		
		/**
		 * @return Unique Global (across the network) Id of the entry
		 */
		public UUID getGlobalId()
		{
			return globalId;
		}
		
		/**
		 * Sets the Unique Global (across the network) Id of this entry.
		 * @param id value to be set
		 */
		public void setGlobalId(UUID id)
		{
			this.globalId = id; 
		}
		
		/**
		 * @return File name of the entry
		 */
		public String getFilename()
		{
			return filename;
		}
		
		/**
		 * Sets the file name of this entry
		 * @param filename value to be set
		 */
		public void setFilename(String filename)
		{
			this.filename = filename;
		}
		
		/**
		 * @return Sequence of the Chunk in this entry
		 */
		public int getChunkSequence()
		{
			return chunkSequence;
		}
		
		/**
		 * Sets the chunk sequence for this entry
		 * @param chunkSequence value to be set
		 */
		public void setChunkSequence(int chunkSequence)
		{
			this.chunkSequence = chunkSequence;
		}
	}



	public static FileTable fromString(String string) {
		byte [] data = string.getBytes();
		FileTable o = null;
		try
		{
	        ObjectInputStream ois = new ObjectInputStream( 
	                                        new ByteArrayInputStream(  data ) );
	        o  = (FileTable)ois.readObject();
	        ois.close();
		}
		catch(Exception e)
		{
			Log.me(null, "Filetable Deserialization error");
		}
        return o;
	}
	
}
