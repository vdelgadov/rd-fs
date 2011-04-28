/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.io.File;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.UUID;

import network.NetworkController;
import network.imAliveThread;
import network.entities.TextObject;

import org.junit.Test;

import common.Log;
import common.Log.Priority;

import fileSystem.FileSystemController;



public class NetworkControllerTest {
	
	
	@Test
	public void one()
	{
		/*NetworkController nc;
		try {
			Log.init(Priority.DEBUG);
			nc = NetworkController.getInstance();
			InetAddress ip = InetAddress.getLocalHost();
			TextObject sendObject = new TextObject("hi");
			
			Object obj = nc.receiveObject(InetAddress.getLocalHost());
			nc.sendObject(ip, sendObject);
			assertTrue(obj instanceof TextObject);
			assertTrue(((TextObject)obj).getText().equals("hi"));
		} catch (UnknownHostException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		*/
		
	}
}
