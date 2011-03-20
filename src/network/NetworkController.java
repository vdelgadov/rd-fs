package network;

import java.net.DatagramPacket;

import common.Log;
import fileSystem.FileSystemController;

import nodeDirectory.DirectoryController;

public class NetworkController {

	FileSystemController FSC = FileSystemController.getInstance("");
	DirectoryController DC = new DirectoryController();
	
	static NetworkController nc;
	
	
	//ThreadControll
	public boolean runListener = true;  //dont modify!!!!!!
	
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
		BroadcastListener bl = new BroadcastListener(getInstance());
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
		String[] split = message.split("@", 3);
		Log.me(this, "Proscessing packet: " + message);
	}

}
