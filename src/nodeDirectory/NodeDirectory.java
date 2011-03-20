package nodeDirectory;

import java.util.Date;
import java.util.LinkedList;

import common.Log;

public class NodeDirectory {
	private LinkedList<Node> nodes = new LinkedList<Node>();
	

	public synchronized boolean nodeExists(Node n)
	{
		for (Node tmpNode : nodes) 
		{
			if (tmpNode.uuid.toString().equals(n.uuid.toString()))
			{
				return true;
			}
		}
		return false;
	}

	public synchronized boolean addNode(Node node)
	{
		Log.me(this, "Adding node to list: "+ node.uuid.toString());
		if(!nodeExists(node))
		{
			nodes.add(node);
		}
		return false;
	}

	public synchronized void removeNode(Node node)
	{
		Log.me(this, "Removing node from list: "+ node.uuid.toString());
		for(int i = 0; i<nodes.size(); i++)
		{
			if(nodes.get(i).uuid.toString().equals(node.uuid.toString()))
			{
				nodes.remove(i);
				break;
			}
		}
		Log.me(this, "Node not found to be removed: "+ node.uuid.toString());
	}

	public synchronized void nodeUpdating(Node node)
	{
		Node x =  null;
		for (Node tmpNode : nodes) 
		{
			if (tmpNode.uuid.toString().equals(node.uuid.toString()))
			{
				x = tmpNode;
				break;
			}
		}
		if(x != null)
		{
			x.lastUpdate = new Date();
			Log.me(this, "Receive update from node (updated on NodeDirectory): " + node.uuid.toString());
		}
		else
		{
			nodes.add(new Node(node.uuid));
			Log.me(this, "Receive update from node (didn't exist NodeDirectory): " + node.uuid.toString());
		}
	}
	
	//check this code!
	public synchronized void update()
	{
		boolean clear = true;
		while (clear)
		{
			for(int i = 0; i<nodes.size(); i++)
			{
				Date lastUpdatePlusOffset = new Date(nodes.get(i).lastUpdate.getTime()+60000);

				if( lastUpdatePlusOffset.before(new Date() ))
				{
					nodes.remove(i);
					Log.me(this, "Removing node due to inactivity: " + nodes.get(i).uuid.toString());
					break;
				}
			}
			clear = false;
		}

	}

}
