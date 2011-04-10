package network;

import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.Socket;
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
	
	/**
	 * Starts the Broadcast Listener
	 */
	public void startListener()
	{
		Log.me(this, "Starting Broadcast Listener");
		nc.runListener = true;
		BroadcastListener bl = new BroadcastListener(NetworkController.getInstance());
		new Thread(bl).start();
	}
	/**
	 * Stops the Broadcast Listener
	 */
	public void stopListener()
	{
		Log.me(this, "Stoping Broadcast Listener");
		nc.runListener = false;
	}

	
	/**
	 * This method will process all packets that are received via a broadcast.
	 * @param dp
	 */
	public synchronized void processPacket(DatagramPacket dp)
	{
		try{
			String message = new String(dp.getData(),0,dp.getLength());
			String[] split = message.split("@", 2);
			Log.me(this, "Proscessing packet: " + message);
			//Im alive packet format: imAlive@UUID
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
					InetAddress IPAddress = dp.getAddress();
					Socket socket = new Socket(IPAddress, RDFSProperties.getP2PPort());
					socket.setSoTimeout(1000);
					
				    OutputStream os = socket.getOutputStream();
				    
				    byte[] sendData = new byte[1024];
					//Send Response format: rSave@UUID
					String response = "rSaveMe@" + uuid;
					sendData = response.getBytes();
				    os.write(sendData);
				    
				    //TODO  how to create an array of size big int
				    byte[] receiveData = new byte[bytes.intValue()];
				    InputStream is = socket.getInputStream();
				    int numBytesReaded = is.read(receiveData, 0, bytes.intValue());
				    //If the requester Drops the petition it means that another node will save the file
				    if(receiveData.toString().equals("Drop"))
				    {
				    	return;
				    }
				    else if(numBytesReaded != bytes.intValue())
				    {
				    	//TODO vic: handle error
				    }
				    
				    //TODO pablo: save file and return crc
				    
				    
				    
				    
				    socket.close();
				    
					
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
	
	private byte[] toByteArray(int in_int) {
	    byte a[] = new byte[4];
	    for (int i=0; i < 4; i++) {

	      int  b_int = (in_int >> (i*8) ) & 255;
	      byte b = (byte) ( b_int );
	 
	      a[i] = b;
	     }
	    return a;
	  }
	private int toInt(byte[] byte_array_4) {
	    int ret = 0;  
	    for (int i=0; i<4; i++) {
	      int b = (int) byte_array_4[i];
	      if (i<3 && b<0) {
	        b=256+b;
	      }
	      ret += b << (i*8);
	    }
	    return ret;
	  }

}
