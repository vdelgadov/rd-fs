package nodeDirectory;

import java.util.Date;
import java.util.LinkedList;
import java.util.UUID;

import common.Log;
import fileSystem.FileTable;

public class NodeDirectory {
	private LinkedList<Node> nodes = new LinkedList<Node>();
	private int timeToDeleteNode = 60000;
	

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

	public synchronized void addNode(Node node)
	{
		Log.me(this, "Adding node to list: "+ node.uuid.toString());
		if(!nodeExists(node))
		{
			nodes.add(node);
		}
		else
		{
			this.nodeAlive(node);
		}
	}

	public synchronized void removeNode(Node node)
	{
		Log.me(this, "Removing node from list: "+ node.uuid.toString());
		for(int i = 0; i<nodes.size(); i++)
		{
			if(nodes.get(i).equals(node))
			{
				nodes.remove(i);
				break;
			}
		}
		Log.me(this, "Node not found to be removed: "+ node.uuid.toString());
	}
	
	public synchronized UUID getUuidFromFileName(String filename)
	{
		UUID fileUuid = null;
		for (Node n : nodes)
		{
			fileUuid = n.getFileTable().lookupGlobalIdByName(filename);
			if (fileUuid != null)
			{
				return fileUuid;
			}
		}
		return fileUuid;
	}

	public synchronized void nodeAlive(Node node)
	{
		Node x =  null;
		for (Node tmpNode : nodes) 
		{
			if (tmpNode.equals(node))
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
	
	// Should be called every I am Alive message is sent.
	public synchronized void update()
	{
		for(Node n : nodes)
		{
			Date lastUpdatePlusOffset = new Date(n.lastUpdate.getTime()+ this.timeToDeleteNode);

			if( lastUpdatePlusOffset.before(new Date() ))
			{
				n.setActive(false);
				Log.me(this, "Removing node due to inactivity: " + n.uuid.toString());
			}
		}
	}

}
