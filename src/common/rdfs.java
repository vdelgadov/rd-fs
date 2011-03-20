package common;

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



public class rdfs {

	/**
	 * @param args
	 */
	static UUID uuid = null;
	public static void main(String[] args) {
		
		Log.init(Log.Priority.DEBUG);
		Log.me(null, "RDFS starting...");
		
		
		Log.me(null, "Obtaining UUID");
		getUUID();
		
		NetworkController nc = NetworkController.getInstance();
		nc.startListener();
		
		
		//this sleep is for debugging purposes - eliminate on final versions
		/*
		try {
			Thread.sleep(5000);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		nc.stopListener();
		Log.me(null, "RDFS terminating...");
		*/
		
	}
	public static void getUUID()
	{
		File f = new File("uuid");
		if (f.exists())
		{
			FileInputStream fstream;
			try {
				fstream = new FileInputStream("uuid");
			    DataInputStream in = new DataInputStream(fstream);
			    BufferedReader br = new BufferedReader(new InputStreamReader(in));
				uuid = UUID.fromString(br.readLine());
				Log.me(null, "Using existent UUID: " + uuid.toString());
			}
			catch (IOException e) {
				Log.me(null,e.getMessage());
				return;
			}
		}
		else
		{
			uuid = UUID.randomUUID();
			try
			{
				FileWriter fstream;
				BufferedWriter out;
				
				fstream = new FileWriter(f);
				out = new BufferedWriter(fstream);
				out.write(uuid.toString());
			    out.close();
			    fstream.close();
		    }
			catch (Exception e)
			{//Catch exception if any
				System.err.println("Error: " + e.getMessage());
		    }
			
			Log.me(null, "Generated UUID: " + uuid.toString());
		}
	}

}
