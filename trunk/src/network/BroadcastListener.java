package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import common.Log;
import common.RDFSProperties;

public class BroadcastListener implements Runnable {
	NetworkController nc;

	public BroadcastListener(NetworkController nc)
	{
		this.nc = nc;
	}


	@Override
	public void run() {
		try {
			InetAddress ia = InetAddress.getByName("224.0.0.205");
			byte[] buffer = new byte[65535];

			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			MulticastSocket ms = new MulticastSocket(RDFSProperties.getBroadcastPort());
			ms.joinGroup(ia);
			while (nc.runListener) {
				try {
					ms.setSoTimeout(5000);
					ms.receive(dp);
					Log.me(this, "Received Packet: " + new String(dp.getData(),0,dp.getLength()));
					//TODO is it necessary to copy the object? add to a Thread?
					this.nc.processPacket(dp);
				}
				catch(Exception e)
				{
					//Log.me(this,e.getMessage());
				}

			}

		}
		catch (IOException ie) {
			Log.me(this,ie.getMessage());
		}

	}

}
