package network;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;

import common.Log;
import common.rdfs;

public class imAliveThread implements Runnable {
	NetworkController nc;

	public imAliveThread(NetworkController nc)
	{
		this.nc = nc;
	}

	@Override
	public void run() {
		while(true)
		{
			InetAddress ia;
			try {
				ia = InetAddress.getByName("224.0.0.205");

				String str = "imAlive" + rdfs.uuid;
				byte[] buffer = str.getBytes();
				int port = 4572;

				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

				while (nc.runListener) {
					try {
						Thread.sleep(5000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					//TODO: pablo: send udp packet
					Log.me(this, "Sending Packet: " + new String(dp.getData(),0,dp.getLength()));
				}
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}

	}
}

