import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.SocketException;


public class BroadcastListener implements Runnable {
	NetworkController nc;
	
	public BroadcastListener(NetworkController nc)
	{
		this.nc = nc;
	}
	
	
	@Override
	public void run() {
		try {
			InetAddress ia = InetAddress.getByName("224.0.0.22");
			byte[] buffer = new byte[65535];
			int port = 4572;

			DatagramPacket dp = new DatagramPacket(buffer, buffer.length);

			MulticastSocket ms = new MulticastSocket(port);
			ms.joinGroup(ia);
			while (nc.runListener) {
				ms.receive(dp);
				this.nc.processPacket(dp);

				
				Log.me(this, "Received Packet: " + new String(dp.getData(),0,dp.getLength()));
			}
			
		}
		
		catch (SocketException se) {
			Log.me(this,se.getMessage());
		}
		catch (IOException ie) {
			Log.me(this,ie.getMessage());
		}
		
	}
	
}
