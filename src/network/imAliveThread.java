package network;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


import common.Log;
import common.RDFSProperties;
import common.rdfs;

public class imAliveThread implements Runnable {
	NetworkController nc;

	public imAliveThread(NetworkController nc)
	{
		this.nc = nc;
	}

	@Override
	public void run() {
		while(nc.runImAliveThread)
		{
			InetAddress ia;
			try {
				ia = InetAddress.getByName("224.0.0.205");

				String str = "imAlive@" + rdfs.uuid;
				byte[] buffer = str.getBytes();

				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

				while (nc.runListener) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						Log.me(this, "Error while trying to sleep in ImAliveThread - " + e.toString());
					}
					//TODO: vic: change port to xml (breadcastSendPort)
					
					DatagramSocket dSocket = new DatagramSocket(4575);
					DatagramPacket reply = new DatagramPacket(buffer,
							buffer.length, ia, RDFSProperties.getBroadcastPort());
					dSocket.send(reply);
					dSocket.close();
					
					
					
					Log.me(this, "Sending Packet: " + new String(dp.getData(),0,dp.getLength()));
				}
			} catch (Exception e) {
				Log.me(this, "Error while sending imAlive packet: " + e.toString());
			}

		}

	}
}
