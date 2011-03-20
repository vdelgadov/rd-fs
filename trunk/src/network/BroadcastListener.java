package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;

import common.Log;

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
			int port = 4572;

			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			MulticastSocket ms = new MulticastSocket(port);
			ms.joinGroup(ia);
			while (nc.runListener) {
				try {
					ms.setSoTimeout(5000);
					ms.receive(dp);
					Log.me(this, "Received Packet: " + new String(dp.getData(),0,dp.getLength()));
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
