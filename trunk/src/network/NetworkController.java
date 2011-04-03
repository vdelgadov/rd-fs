package network;

import java.net.DatagramPacket;
import java.util.ArrayList;
import java.util.UUID;

import common.Log;
import fileSystem.FileSystemController;

import nodeDirectory.DirectoryController;
import nodeDirectory.Node;

public class NetworkController {

	FileSystemController FSC = FileSystemController.getInstance("");
	DirectoryController DC = new DirectoryController();
	
	static NetworkController nc;
	
	
	//ThreadControll
	public boolean runListener = true;  //don't modify!!!!!!
	
	private NetworkController() {
	}
	
	public static NetworkController getInstance()
	{
		if (nc == null)
		{
			nc = new NetworkController();
		}
		return nc;
	}
	
	public void startListener()
	{
		Log.me(this, "Starting Broadcast Listener");
		nc.runListener = true;
		BroadcastListener bl = new BroadcastListener(NetworkController.getInstance());
		new Thread(bl).start();
	}
	public void stopListener()
	{
		Log.me(this, "Stoping Broadcast Listener");
		nc.runListener = false;
	}

	
	public synchronized void processPacket(DatagramPacket dp)
	{
		String message = new String(dp.getData(),0,dp.getLength());
		String[] split = message.split("@", 2);
		Log.me(this, "Proscessing packet: " + message);
		if(split[0].equals("imAlive"))
		{
			Node n = new Node(UUID.fromString(split[1]));
			this.DC.getNodeDirectory().addNode(n);
		}
	}

}
