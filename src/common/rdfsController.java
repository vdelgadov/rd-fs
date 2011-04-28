package common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.UUID;

import network.NetworkController;

public class rdfsController implements ActionListener {

	private rdfsGui gui;
	private NetworkController nc;
	private UUID uuid;
	
	public static final String EXIT_ITEM_ACTION_COMMAND = "Exit";
	public static final String JOIN_SYSTEM_ITEM_ACTION_COMMAND = "Join";
	public static final String CONNECT_USER_ITEM_ACTION_COMMAND = "Connect";
	public static final String DISCONNECT_ITEM_ACTION_COMMAND = "Disconnect";
	
	public static final String UUID_FILENAME = "uuid"; 
	
	public rdfsController(rdfsGui gui)
	{
		Log.init(Log.Priority.DEBUG);
		
		this.gui = gui;
	}
	
	public void setGuiDefaults()
	{
		gui.deactivateAll();
		gui.disconnect();
	}
	
	@Override
	public void actionPerformed(ActionEvent arg0) {
		if (arg0.getActionCommand().equals(EXIT_ITEM_ACTION_COMMAND))
		{
			gui.dispose();			
			return;
		}
		if (arg0.getActionCommand().equals(DISCONNECT_ITEM_ACTION_COMMAND))
		{
			gui.changeStatus("Not connected");
			this.disconnect();
			return;
		}
		if (arg0.getActionCommand().equals(JOIN_SYSTEM_ITEM_ACTION_COMMAND))
		{
			gui.changeStatus("Connected to file system, server mode.");
			gui.activateAll();
			gui.connect();
			
			initServerMode();
			
			return;
		}
		if (arg0.getActionCommand().equals(CONNECT_USER_ITEM_ACTION_COMMAND))
		{
			gui.changeStatus("Connected to file system, user mode.");
			gui.activateAll();
			gui.connect();
			return;
		}
	}
	
	private void initServerMode()
	{
		Log.me(null, "Starting server mode");
		
		uuid = getUUID();
		if (uuid == null)
		{
			Log.me(null, "Error obtaining or generating UUID", Log.Priority.ERROR);
			gui.changeStatus("Error with UUID on this computer");
		}
		
		nc = NetworkController.getInstance();
	}
	
	private void disconnect()
	{
		gui.disconnect();
		gui.deactivateAll();
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
