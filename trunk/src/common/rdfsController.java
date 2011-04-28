package common;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class rdfsController implements ActionListener {

	private rdfsGui gui;
	
	public static final String EXIT_ITEM_ACTION_COMMAND = "Exit";
	public static final String JOIN_SYSTEM_ITEM_ACTION_COMMAND = "Join";
	public static final String CONNECT_USER_ITEM_ACTION_COMMAND = "Connect";
	public static final String DISCONNECT_ITEM_ACTION_COMMAND = "Disconnect";
	
	
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
			gui.disconnect();
			gui.deactivateAll();
			return;
		}
		if (arg0.getActionCommand().equals(JOIN_SYSTEM_ITEM_ACTION_COMMAND))
		{
			gui.changeStatus("Connected to file system, server mode.");
			gui.activateAll();
			gui.connect();
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
	
}
