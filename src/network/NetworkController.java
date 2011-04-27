package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.InvalidClassException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import common.Log;
import common.RDFSProperties;
import fileSystem.FileSystemController;

import nodeDirectory.DirectoryController;
import nodeDirectory.Node;
import network.entities.*;


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

				int  bytes = Integer.parseInt(saveSplit[0]);
				UUID uuid = UUID.fromString(saveSplit[1]);

				if((int)this.FSC.getFreeSize() >= bytes)
				{
					InetAddress IPAddress = dp.getAddress();
					
					//Send Response format: rSave@UUID  (i can save the file)
					TextObject to = new TextObject("rSaveMe@" + uuid);
					this.sendObject(to);
					
					Object received = this.receiveObject(IPAddress);
					
					if(received instanceof TextObject)
					{
						TextObject text = (TextObject)received;
						if(text.getText().equals("Drop"))
						{
							Log.me(this, "Received drop petition");
						}
						
					}
					else if(received instanceof byteObject)
					{
						//TODO pablo: save file and return crc
					}

				}
			}
			//Petition to send file
			else if(split[0].equals("get"))
			{
				
			}
			//receiving file to be saved
			//TODO vic: this methods shouldnt be here?? only broadcast?
			/*if(split[0].equals("send"))
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
			}*/


		}
		catch(Exception e)
		{
			Log.me(this, "Failed to Process Packet - " + e.toString());
		}
	}
	private Object receiveObject(InetAddress IPAddress)
	{
		Socket socket;
		try
		{
			socket = new Socket(IPAddress, RDFSProperties.getP2PPort());
			InputStream iStream = socket.getInputStream();
			ObjectInputStream oiStream = new ObjectInputStream(iStream);
			return oiStream.readObject();
		}
		catch (UnknownHostException e)
		{
			Log.me(this, "Failed to receive Object from host: " + IPAddress  + " - " + e.toString());
			return null;
		}
		catch (IOException e)
		{
			Log.me(this, "Failed to receive Object  because of IO from host: " + IPAddress  + " - " + e.toString());
			return null;
		}
		catch(ClassNotFoundException e)
		{
			Log.me(this, "Failed to receive Object  because of ClassNotFound from host: " + IPAddress  + " - " + e.toString());
			return null;
		}
	}
	private boolean sendObject(Object obj)
	{
		ServerSocket serverSocket = null;
		try
        {
			serverSocket = new java.net.ServerSocket(RDFSProperties.getP2PPort());
	        assert serverSocket.isBound();
	        if (serverSocket.isBound())
	        {
	        	Log.me(this, "Sendig Object");
	        }
			 Socket sock = serverSocket.accept();
	         OutputStream oStream = sock.getOutputStream();
	         ObjectOutputStream ooStream = new ObjectOutputStream(oStream);
	         ooStream.writeObject(obj);  // send serilized payload
	         ooStream.close();
	         return true;
        }
        catch (SecurityException e)
        {
        	Log.me(this,"Unable to get host address due to security. - " + e.toString());
        	return false;
        }
        catch (IOException e)
        {
        	Log.me(this,"Unable to read data from an open socket. - " + e.toString());
        	return false;
        }
        finally
        {
            try
            {
            	serverSocket.close();
            }
            catch (IOException e)
            {
            	Log.me(this,"Unable to close an open socket. - " + e.toString());
                
            }
        }
	}

}
