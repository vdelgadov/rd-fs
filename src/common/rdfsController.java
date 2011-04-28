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
import java.io.RandomAccessFile;
import java.util.UUID;

import javax.swing.DefaultListModel;
import javax.swing.JFileChooser;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import network.NetworkController;
import network.entities.BytesObject;
import nodeDirectory.DirectoryController;

public class rdfsController implements ActionListener, ListSelectionListener {

	private rdfsGui gui;
	private NetworkController nc;
	public static UUID uuid;
	
	private String fileSelected = null;
	
	private DirectoryController dc;
	
	public static final String EXIT_ITEM_ACTION_COMMAND = "Exit";
	public static final String JOIN_SYSTEM_ITEM_ACTION_COMMAND = "Join";
	public static final String CONNECT_USER_ITEM_ACTION_COMMAND = "Connect";
	public static final String DELETE_BUTTON_ACTION_COMMAND = "Delete";
	public static final String UPLOAD_BUTTON_ACTION_COMMAND = "Upload";
	public static final String DOWNLOAD_BUTTON_ACTION_COMMAND = "Download";
	public static final String DISCONNECT_ITEM_ACTION_COMMAND = "Disconnect";
	
	public static final String UUID_FILENAME = "uuid"; 
	
	public rdfsController(rdfsGui gui)
	{
		Log.init(Log.Priority.DEBUG);
		dc = DirectoryController.getInstance();
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
			gui.toggleButtons(false);
			gui.toggleUploadButton(true);
			gui.connect();
			
			initServerMode();
			
			return;
		}
		if (arg0.getActionCommand().equals(CONNECT_USER_ITEM_ACTION_COMMAND))
		{
			gui.changeStatus("Connected to file system, user mode.");
			gui.activateAll();
			gui.toggleButtons(false);
			gui.toggleUploadButton(true);
			gui.connect();
			return;
		}
		if (arg0.getActionCommand().equals(UPLOAD_BUTTON_ACTION_COMMAND))
		{
			int returnVal = gui.showOpenDialog();

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = gui.getSelectedFile();
	            //This is where a real application would open the file.
	            Log.me(this,"Uploading: " + file.getName() + ".");
	            
	            byte[] data = getLocalFileData(file);
	            BytesObject obj = new BytesObject(data);
	            UUID fileUuid = UUID.randomUUID();
	            
	            gui.changeStatus("Uploading file to system.");
	            boolean saved = nc.saveFile(data.length, fileUuid, file.getName(), 0, 1, obj);
	            if (saved)
	            {
	            	((DefaultListModel)gui.getFileList().getModel()).addElement(file.getName());
	            	
	            	gui.changeStatus("Uploaded completed succesfully");
	            }
	            else
	            {
	            	gui.changeStatus("Upload failed");
	            }
	        } else {
	            Log.me(this,"Open command cancelled by user.");
	        }
			
			return;
		}
		if (arg0.getActionCommand().equals(DOWNLOAD_BUTTON_ACTION_COMMAND))
		{
			int returnVal = gui.showSaveDialog();

	        if (returnVal == JFileChooser.APPROVE_OPTION) {
	            File file = gui.getSelectedFile();
	            
	            UUID fileUuid = dc.getNodeDirectory().getUuidFromFileName(fileSelected);
	            
	            nc.getFile(fileUuid, fileSelected, 0);
	            
	            Log.me(this,"Downloading: " + file.getName() + ".");
	        } else {
	            Log.me(this,"Upload command cancelled by user.");
	        }
			gui.changeStatus("Downloading file to local fs.");
			return;
		}
		if (arg0.getActionCommand().equals(DELETE_BUTTON_ACTION_COMMAND))
		{
			UUID fileUuid = dc.getNodeDirectory().getUuidFromFileName(fileSelected);
			int r = nc.deleteFile(fileUuid, fileSelected, 0);
			if (r == 1)
			{
				gui.changeStatus("Deleted file.");
			}
			else
			{
				gui.changeStatus("Couldn't delete file.");
			}
			
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
			disconnect();
			return;
		}
		
		nc = NetworkController.getInstance();
		
		Thread bl = nc.startListener();
		
		Thread ia = nc.startImAliveThread();
	}
	
	private void disconnect()
	{
		nc.stopImAliveThread();
		nc.stopListener();
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
	
	private byte[] getLocalFileData(File file)
	{
		int fileSize  = new Long(file.length()).intValue();
		byte[] buffer = new byte[fileSize];
		RandomAccessFile fis = null;
		try {
			fis = new RandomAccessFile(file,"r");
			
			if (fis.read(buffer, 0, fileSize) != -1)
			{
				fis.close();
				return  buffer;
			}
			else
			{
				fis.close();
				return null;
			}
		}
		catch (Exception e) 
		{
			Log.me(this, "Failed: "+ e.getMessage(), Log.Priority.ERROR);
		}
		finally 
		{
			if  (fis != null)
				try{
					fis.close();
				}
				catch(final Exception e)
				{
					Log.me(this, "Couldn't close the file accesor");
				}
		}
		return null;
	}

	@Override
	public void valueChanged(ListSelectionEvent e) {
		ListSelectionModel lsm = (ListSelectionModel)e.getSource();
		
		if (lsm.isSelectionEmpty())
		{
			gui.toggleButtons(false);
			gui.toggleUploadButton(true);
			return;
		}
		gui.toggleButtons(true);
		
		int selected = e.getFirstIndex();	
		fileSelected = (String)gui.getFileList().getModel().getElementAt(selected);
	}
	
	public void stopAll()
	{
		disconnect();
	}
}
