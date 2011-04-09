package network;

import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import common.Log;
import common.RDFSProperties;
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
		try{
			String message = new String(dp.getData(),0,dp.getLength());
			String[] split = message.split("@", 2);
			Log.me(this, "Proscessing packet: " + message);
			if(split[0].equals("imAlive"))
			{
				Node n = new Node(UUID.fromString(split[1]));
				this.DC.getNodeDirectory().addNode(n);
			}
			//this is the request to save a file
			//format: pSave@sizeBytes@UUID
			else if(split[0].equals("pSave"))
			{
				String[] saveSplit = split[1].split("@", 2);
				
				BigInteger  bytes = new BigInteger(saveSplit[0]);
				UUID uuid = UUID.fromString(saveSplit[1]);
								
				if(this.FSC.getFreeSize().compareTo(bytes) >= 0)
				{
					//TODO vic: change to TCP socket
					DatagramSocket clientSocket;
					clientSocket = new DatagramSocket();
					clientSocket.setSoTimeout(1000);

					InetAddress IPAddress = dp.getAddress();
					byte[] sendData = new byte[1024];
					//Send Response
					//format: rSave@UUID
					String response = "rSaveMe@" + uuid;
					sendData = response.getBytes();
					DatagramPacket sendPacket = new DatagramPacket(sendData,
							sendData.length, IPAddress,
							RDFSProperties.getP2PPort());
					clientSocket.send(sendPacket);
				}
			}
			//receiving file to be saved
			//TODO vic: this methods shouldnt be here?? only broadcast?
			if(split[0].equals("send"))
			{

				String[] sendSplit = split[1].split("@", 5);
				String name = sendSplit[0];
				int chunk = Integer.parseInt(sendSplit[1]);
				long crc = Long.parseLong(sendSplit[2]);
				int numBytes = Integer.parseInt(sendSplit[3]);
				byte[] bytes = sendSplit[4].getBytes();

				Checksum checksum = new CRC32();
				checksum.update(bytes,0,bytes.length);
				long lngChecksum = checksum.getValue();
				if(crc == lngChecksum)
				{
					//TODO pablo: save file (FileSystemController)
				}
				else
				{
					//TODO vic: send response (failed)

				}
			}
			//Petition to send file
			if(split[0].equals("get"))
			{

			}


		}
		catch(Exception e)
		{
			Log.me(this, "Failed to Process Packet - " + e.toString());
		}
	}

}
