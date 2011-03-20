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
	
}
