package network;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
/*import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.FutureTask;
import java.util.concurrent.Callable;
 */
import common.Log;
import common.RDFSProperties;

public class BroadcastListener implements Runnable {
	NetworkController nc;
	//private final ExecutorService pool;

	public BroadcastListener(NetworkController nc)
	{
		this.nc = nc;
	}


	@Override
	public void run() {
		try {
			InetAddress ia = InetAddress.getByName("224.0.0.205");


			final MulticastSocket ms = new MulticastSocket(RDFSProperties.getBroadcastPort());
			ms.joinGroup(ia);
			ExecutorService executor = Executors.newFixedThreadPool(20);
			while (nc.runListener) {
				byte[] buffer = new byte[65535];
				DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
				ms.receive(dp);
				ms.setTimeToLive(1);
				executor.execute( new Handler( dp ) );



				/*		try {
								byte[] buffer = new byte[65535];
								DatagramPacket dp = new DatagramPacket(buffer, buffer.length);
								ms.setSoTimeout(5000);
								ms.receive(dp);
								if (!dp.getAddress().equals(InetAddress.getLocalHost()))
								{
									Log.me(this, "Received Packet: " + new String(dp.getData(),0,dp.getLength()));
									nc.processPacket(dp);
								}
								//nc.addUDPMessages(dp);
							}
							catch(InterruptedIOException e)
							{
							}
							catch(Exception e)
							{
								ms.close();
								Log.me(this,"Error while listening for broadcasts" + e.getMessage());
							}
				 */

			}

		}
		catch (IOException ie) {
			Log.me(this,ie.getMessage());
		}

	}
	private class Handler implements Runnable {
		private final DatagramPacket dp;   
		public Handler(DatagramPacket dp) { this.dp = dp; }
		public void run() {
			try
			{
				if (!dp.getAddress().equals(InetAddress.getLocalHost()))
				{
					Log.me(this, "Received Packet: " + new String(dp.getData(),0,dp.getLength()));
					nc.processPacket(dp);
				}

			}
			catch(Exception e)
			{
				Log.me(this,"Error while getting Datagram Packet" + e.getMessage());
			}
		}
	}
}
