package nodeDirectory;
import java.util.Date;
import java.util.UUID;

import fileSystem.FileTable;


public class Node {
	UUID uuid;
	Date lastUpdate;
	FileTable ft;
	private boolean active;
	
	
	public Node(UUID uuid)
	{
		this.uuid = uuid;
		this.lastUpdate = new Date();
		this.setActive(true);
		this.ft = null;
	}
	
	@Override
	public boolean equals(Object compareObj)
	{
		if (this == compareObj) // Are they exactly the same instance?
	           return true;
	 
		if (compareObj == null) // Is the object being compared null?
		    return false;
	 
		if (!(compareObj instanceof Node)) // Is the object being compared also a Person?
		    return false;
		
		if(this.uuid.toString().compareTo(((Node)compareObj).uuid.toString()) == 0)
		{
			return true;
		}
		else
		{
			return false;
		}
		
	}
	
	public UUID getUUID()
	{
		return uuid;
	}
	
	public FileTable getFileTable()
	{
		return this.ft;
	}
	
	public void setFileTable(FileTable ft)
	{
		this.ft = ft;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public boolean isActive() {
		return active;
	}
}
