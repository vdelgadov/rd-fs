package network;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.UUID;
import java.util.zip.CRC32;
import java.util.zip.Checksum;

import common.Log;
import common.RDFSProperties;
import common.rdfs;
import fileSystem.FileSystemController;

import nodeDirectory.DirectoryController;
import nodeDirectory.Node;
import network.entities.*;


/**
 * @author vdelgado
 *
 */
public class NetworkController {

	FileSystemController FSC = FileSystemController.getInstance("./tmp/");
	DirectoryController DC = new DirectoryController();
	private ArrayList<DatagramPacket> UDPMessages = new ArrayList<DatagramPacket>();
	private ArrayList<String> receivedMessages = new ArrayList<String>();


	static NetworkController nc;


	//ThreadControll
	public boolean runListener = true;  //don't modify!!!!!!
	public boolean runImAliveThread = true;

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
	public Thread startListener()
	{
		Log.me(this, "Starting Broadcast Listener");
		nc.runListener = true;
		BroadcastListener bl = new BroadcastListener(NetworkController.getInstance());
		Thread t =  new Thread(bl);
		t.start();
		return t;
	}
	/**
	 * Stops the Broadcast Listener
	 */
	public void stopListener()
	{
		Log.me(this, "Stoping Broadcast Listener");
		nc.runListener = false;
	}

	public Thread startImAliveThread()
	{
		Log.me(this, "Starting ImAliveThread");
		nc.runImAliveThread = true;
		imAliveThread iat = new imAliveThread(NetworkController.getInstance());
		Thread t =  new Thread(iat);
		t.start();
		return t;

	}
	public void stopImAliveThread()
	{
		Log.me(this, "Stopping ImAliveThread");
		nc.runImAliveThread = false;
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
			if(this.receivedMessages.contains(message))
			{
				Log.me(this, "Duplicated message");
				return;
			}
			else
			{
				this.receivedMessages.add(message);
			}

			//Im alive packet format: imAlive@UUID
			if(split[0].equals("imAlive"))
			{
				UUID uuid = UUID.fromString(split[1]);
				//if(!uuid.equals(rdfs.uuid))
				//{
				Node n = new Node(uuid);
				this.DC.getNodeDirectory().addNode(n);
				//}
			}
			//this is the request to save a file
			//format: pSave@sizeBytes@UUID@fileName@chunk
			//TODO vic: change flow
			else if(split[0].equals("pSave"))
			{

				String[] saveSplit = split[1].split("@", 4);

				int  bytes = Integer.parseInt(saveSplit[0]);
				UUID uuid = UUID.fromString(saveSplit[1]);
				String fileName = saveSplit[2];
				int chunk = Integer.parseInt(saveSplit[3]);

				if(this.FSC.getFreeSize() >= (long)bytes)
				{
					InetAddress IPAddress = dp.getAddress();

					//Send Response format: rSave@UUID  (i can save the file)
					TextObject to = new TextObject("rSaveMe@" + uuid);
					sendObjectDirectly(IPAddress, to);

					ArrayList<Object[]> list = this.receiveObjectsByPetition(1);
					if(list == null || list.size() != 1)
					{
						//TODO abort
						return;
					}

					Object[] obj = list.get(0);
					if(obj[1] instanceof BytesObject)
					{
						BytesObject data = (BytesObject)obj[1];
						if(FSC.saveNewFile(fileName, uuid, chunk, data.getBytes()))
						{
							TextObject ack = new TextObject("ACK@" + uuid);
							this.sendObjectDirectly(IPAddress, ack);
							Log.me(this, "File saved ");

						}
						else
						{
							TextObject fail = new TextObject("FAIL");
							this.sendObjectDirectly(IPAddress, fail);
							Log.me(this, "Error while saving the file");
						}
					}


				}
			}
			//petition to delete a file
			else if(split[0].equals("pDelete"))
			{
				String[] deleteSplit = split[1].split("@", 2);

				UUID uuid = UUID.fromString(deleteSplit[0]);
				String fileName = deleteSplit[1];


				InetAddress IPAddress = dp.getAddress();

				//Send Response format: rDelete@UUID  (i can save the file)
				if(FSC.removeFile(fileName, true))
				{
					Thread.sleep(200);
					TextObject to = new TextObject("rSaveMe@" + uuid + "@" + fileName);
					sendObjectDirectly(IPAddress, to);
				}

			}
			//Petition to send file
			else if(split[0].equals("pGet"))
			{
				String[] saveSplit = split[1].split("@", 3);

				UUID uuid = UUID.fromString(saveSplit[0]);
				String fileName = saveSplit[1];
				int chunk = Integer.parseInt(saveSplit[2]);


				InetAddress IPAddress = dp.getAddress();

				//Send Response format: rSave@UUID  (i can save the file)
				TextObject to = new TextObject("rGetMe@" + uuid);
				sendObjectDirectly(IPAddress, to);
				
				byte[] bytes = FSC.getFileData(fileName, 0, 65535);
				if(bytes == null)
				{
					this.receivedMessages.remove(message);
					return;
				}
				BytesObject file = new BytesObject(FSC.getFileData(fileName, 0, 65535));
				if(sendObjectDirectly(IPAddress, file))
				{					
					Log.me(this, "File send succesfully");
				}
				
				


			}

		}
		catch(Exception e)
		{
			Log.me(this, "Failed to Process Packet - " + e.toString());
		}
	}
	public synchronized boolean saveFile(int sizeBytes, UUID uuid, String fileName, int chunk, int numNodes, Object file)
	{
		//format: pSave@sizeBytes@UUID@fileName@chunk
		String str = "pSave@"+ sizeBytes + "@" + uuid + "@" + fileName + "@" + chunk;
		byte[] buffer = str.getBytes();
		this.sendUDPMessage(buffer);

		//Receive Response format: rSave@UUID  (i can save the file)
		//TextObject to = new TextObject("rSaveMe@" + uuid);
		//this.sendObject(to);

		//TODO the parameter in the next function should be the number of nodes required to save the file
		ArrayList<Object[]> list = this.receiveObjectsByPetition(numNodes);
		if(list == null || list.size() < numNodes)
		{
			return false;
		}
		for(int i = 0; i< numNodes; i++ )
		{
			Object[] obj = list.get(i);
			if(obj[1] instanceof TextObject)
			{
				TextObject text = (TextObject)obj[1];
				if(text.getText().equals(("rSaveMe@" + uuid.toString())))
				{
					Log.me(this, "rSave for uuid: " + uuid.toString());
				}
			}
		}
		//send file to hosts
		for(int i = 0; i< numNodes; i++ )
		{
			Object[] obj = list.get(i);
			//send the object
			if (!sendObjectDirectly((InetAddress)obj[0], file))
			{
				//TODO abort
			}
			ArrayList<Object[]> ack= this.receiveObjectsByPetition(1);
			if(  (ack.get(0)[1] instanceof TextObject) && ( ((TextObject) ack.get(0)[1]).getText().startsWith("ACK") )  )
			{
				String[] split = ((TextObject)ack.get(0)[1]).getText().split("@", 2);
				UUID node = UUID.fromString( split[1] );
				//TODO pablo: add the file entry to directory (was saved by that node)
				return true;
			}
			else
			{
				//TODO abort
			}

		}
		Log.me(this, "file saved uuid: " + uuid.toString());



		return false;
	}
	//TODO how to eliminate a file by its uuid? (not local)

	/**
	 * This function will send a deleteFile in al nodes, returning the number of nodes that deleted the file
	 * @param sizeBytes
	 * @param uuid
	 * @param fileName
	 * @return
	 */
	public synchronized int deleteFile( UUID uuid, String fileName, int numNodes)
	{
		//format: pDelete@UUID@fileName@chunk
		String str = "pDelete@" + uuid + "@" + fileName;
		FSC.removeFile(fileName, true);
		byte[] buffer = str.getBytes();
		this.sendUDPMessage(buffer);

		//TODO the parameter in the next function should be the number of nodes required to delete the file
		ArrayList<Object[]> list = this.receiveObjectsByPetition(numNodes);
		if(list == null)
		{
			return 0;
		}
		int numResponses = 0;
		for(int i = 0; i< numNodes; i++ )
		{
			Object[] obj = list.get(i);
			if(obj[1] instanceof TextObject)
			{
				TextObject text = (TextObject)obj[1];
				if(text.getText().equals("rSaveMe@" + uuid + "@" + fileName))
				{
					numResponses++;
					Log.me(this, "rDelete for uuid: " + uuid.toString());
				}
			}
		}
		Log.me(this, "rDelete by "+ numNodes+" nodes uuid: " + uuid.toString());
		return numResponses;

	}
	public synchronized Object getFile(UUID uuid, String fileName, int chunk)
	{
		//format: pSave@sizeBytes@UUID@fileName@chunk
		String str = "pGet@" + uuid + "@" + fileName + "@" + chunk;
		byte[] buffer = str.getBytes();
		this.sendUDPMessage(buffer);

		//Receive Response format: rSave@UUID  (i can save the file)
		//TextObject to = new TextObject("rSaveMe@" + uuid);
		//this.sendObject(to);

		//TODO the parameter in the next function should be the number of nodes required to save the file
		ArrayList<Object[]> list = this.receiveObjectsByPetition(1);
		if(list == null || list.size() < 1)
		{
			return null;
		}
		for(int i = 0; i< 1; i++ )
		{
			Object[] obj = list.get(i);
			if(obj[1] instanceof TextObject)
			{
				TextObject text = (TextObject)obj[1];
				if(text.getText().equals(("rGetMe@" + uuid.toString())))
				{
					Log.me(this, "rGet for uuid: " + uuid.toString());
				}
			}
		}
		//get file

		ArrayList<Object[]> ack= this.receiveObjectsByPetition(1);
		if(  (ack.get(0)[1] instanceof BytesObject)  )
		{
			return ack.get(0)[1];
		}

		return null;
	}
	/**
	 * This function will receive a certain amount of objects over TCP in the P2P port
	 * @param numObjects
	 * @return
	 */
	public ArrayList<Object[]> receiveObjectsByPetition(int numObjects)
	{
		ArrayList<Object[]> list = new ArrayList<Object[]>();
		ServerSocket serversocket = null;
		Socket socket = null;
		try
		{
			serversocket = new ServerSocket(RDFSProperties.getP2PPort());
			for(int i = 0; i < numObjects;i++)
			{
				socket = serversocket.accept();
				socket.setSoTimeout(1000);
				InputStream iStream = socket.getInputStream();
				ObjectInputStream oiStream = new ObjectInputStream(iStream);
				Object[] tmp = new Object[2];
				tmp[0] = socket.getInetAddress();
				tmp[1] = oiStream.readObject();
				list.add( tmp );
				socket.close();
			}
			return list;
		}
		catch (UnknownHostException e)
		{
			Log.me(this, "Failed to receive Object from host: - " + e.toString());
			return null;
		}
		catch (IOException e)
		{
			Log.me(this, "Failed to receive Object  because of IO from host: - " + e.toString());
			return null;
		}
		catch(ClassNotFoundException e)
		{
			Log.me(this, "Failed to receive Object  because of ClassNotFound from host: - " + e.toString());
			return null;
		}
		finally
		{
			try {
				if(serversocket != null)
				{
					serversocket.close();
				}
				if(socket != null)
				{
					socket.close();
				}
			} catch (IOException e) {
			}
		}
	}
	public Object receiveObjectDirectly(InetAddress IPAddress)
	{
		Socket socket;
		try
		{
			socket = new Socket(IPAddress, RDFSProperties.getP2PPort());
			socket.setSoTimeout(1000);
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
	public boolean sendObjectByPetition(Object obj)
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
			else
			{
				Log.me(this, "Couldnt bound socket on sendObjectByPetition");
				return false;
			}
			Socket sock = serverSocket.accept();
			sock.setSoTimeout(1000);
			OutputStream oStream = sock.getOutputStream();
			ObjectOutputStream ooStream = new ObjectOutputStream(oStream);
			ooStream.writeObject(obj);
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
	public boolean sendObjectDirectly(InetAddress IPAddress, Object obj)
	{
		Socket socket = null;
		try
		{
			socket = new Socket(IPAddress, RDFSProperties.getP2PPort());
			socket.setSoTimeout(1000);
			OutputStream oStream = socket.getOutputStream();
			ObjectOutputStream ooStream = new ObjectOutputStream(oStream);
			ooStream.writeObject(obj);
			ooStream.close();
			return true;
		}
		catch (UnknownHostException e)
		{
			Log.me(this, "Failed to receive Object from host: " + IPAddress  + " - " + e.toString());
			return false;
		}
		catch (IOException e)
		{
			Log.me(this, "Failed to receive Object  because of IO from host: " + IPAddress  + " - " + e.toString());
			return false;
		}
		finally
		{
			try {
				socket.close();
			} catch (IOException e) {

			}
		}
	}
	public boolean sendUDPMessage(byte[] buffer)
	{
		InetAddress ia;
		try {
			ia = InetAddress.getByName("224.0.0.205");

			//TODO: vic: change port to xml (breadcastSendPort)
			DatagramSocket dSocket = new DatagramSocket(4575);
			DatagramPacket packet = new DatagramPacket(buffer,buffer.length, ia, RDFSProperties.getBroadcastPort());
			dSocket.send(packet);
			dSocket.close();

			Log.me(this, "Sending Packet: " + new String(packet.getData(),0,packet.getLength()));
			return true;

		} catch (Exception e) {
			Log.me(this, "Error while sending UDP Message: " + new String(buffer) + " - " + e.toString());
			return false;
		}

	}

	public void xaddUDPMessages(DatagramPacket dp) {
		UDPMessages.add(dp);
	}

	public DatagramPacket xgetUDPMessage() {
		DatagramPacket dp = UDPMessages.get(0);
		UDPMessages.remove(0);
		return dp;

	}

}
