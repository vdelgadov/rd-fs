package nodeDirectory;
import java.util.Date;
import java.util.UUID;


public class Node {
	UUID uuid;
	Date lastUpdate;
	
	
	public Node(UUID uuid)
	{
		this.uuid = uuid;
		this.lastUpdate = new Date();
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
}
