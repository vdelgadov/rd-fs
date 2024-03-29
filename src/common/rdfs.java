package common;

import static org.junit.Assert.assertTrue;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import network.NetworkController;
import network.entities.BytesObject;
import network.entities.TextObject;



public class rdfs {

	public static final String UUID_FILENAME = "uuid"; 
	
	/**
	 * @param args
	 */
	public static UUID uuid = rdfsController.getUUID();
	public static void main(String[] args) {
		
		Log.init(Log.Priority.DEBUG);
		Log.me(null, "RDFS starting...");
		
		
		Log.me(null, "Obtaining UUID");
		uuid = getUUID();
		if (uuid == null)
		{
			Log.me(null, "Error obtaining or generating UUID", Log.Priority.ERROR);
			System.exit(1);
		}
		
		NetworkController nc = NetworkController.getInstance();
		
		
		Thread bl = nc.startListener();
		
		//tester2
		BytesObject obj = new BytesObject("ojojo".getBytes());
		UUID uuid = UUID.randomUUID();
		boolean b = nc.saveFile(obj.getBytes().length, uuid, "test.txt", 0, 2, obj);
		System.out.println(b);
		
		BytesObject gObj = (BytesObject)nc.getFile(uuid, "test.txt", 0);
		System.out.println(new String(gObj.getBytes()));
		
		if (b)
		{
			int r = nc.deleteFile(uuid, "test.txt", 2);
			if (r == 1)
			{
				System.out.println("Success");
			}
			else
			{
				System.out.println("Failure");
			}
		}
		
		//Thread iat = nc.startImAliveThread();
		try {
			bl.join();
		} catch (InterruptedException e) {
			Log.me(null, "Error on joining BroadcastListener Thread", Log.Priority.ERROR);
		}
		
		/*try {
			iat.join();
		} catch (InterruptedException e) {
			Log.me(null, "Error on joining imAliveThread", Log.Priority.ERROR);
		}
		*/
		
		
		//tester1

		

		
	}
	
	/**
	 * Obtains the UUID for this node, either from a file or generates a new one.
	 */
	public static UUID getUUID()
	{
		File f = new File(UUID_FILENAME);
		UUID uuid = null;
		if (f.exists())
		{
			uuid = getUUID(f);
			
			if (uuid != null)
				Log.me(null, "Obtained UUID: " + uuid.toString());
		}
		if (uuid == null)
		{
			uuid = generateUUID(f);
			
			if (uuid != null)
				Log.me(null, "Generated UUID: " + uuid.toString());
		}
		return uuid;
	}
	/**
	 * Generates a new random UUID to assign it to this node, and saves it to a file.
	 * 
	 * @param f file where the generated UUID is going to be saved.
	 */
	private static UUID generateUUID(File f) {
		UUID uuid = UUID.randomUUID();
		FileWriter fstream = null;
		BufferedWriter out = null;
		try
		{
			fstream = new FileWriter(f);
			out = new BufferedWriter(fstream);
			out.write(uuid.toString());
			out.flush();
		}
		catch (Exception e)
		{//Catch exception if any
			Log.me(null, "Problem saving the generated UUID in the file", Log.Priority.ERROR);
		}
		finally
		{
			if (fstream != null)
				try 
				{
					fstream.close();
				}
				catch(Exception e)
				{
					Log.me(null, "Problem closing UUID file writer", Log.Priority.WARNING);
				}
		}
		return uuid;
	}
	/**
	 * Obtains the UUID from an existing file.
	 * 
	 * @param f file which only contains the UUID.
	 */
	private static UUID getUUID(File f) {
		FileInputStream fstream = null;
		UUID uuid = null;
		try {
			fstream = new FileInputStream(f);
		    DataInputStream in = new DataInputStream(fstream);
		    BufferedReader br = new BufferedReader(new InputStreamReader(in));
			uuid = UUID.fromString(br.readLine());
			Log.me(null, "Using existent UUID: " + uuid.toString());
		}
		catch (IOException e) {
			Log.me(null,"Error getting existing UUID: "+e.getMessage(),Log.Priority.ERROR);
		}
		finally
		{
			if (fstream != null)
				try 
				{
					fstream.close();
				}
				catch(Exception e)
				{
					Log.me(null, "Problem closing UUID file", Log.Priority.WARNING);
				}
		}
		return uuid;
	}

}
