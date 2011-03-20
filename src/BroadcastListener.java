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
			while (true) {
				ms.receive(dp);
				this.nc.queueDatagramPacket(dp);
				//TODO: agregar logger
				/*String s = new String(dp.getData(),0,dp.getLength());
				s +=  dp.getAddress().toString();
				System.out.println(s);
				*/
			}
			
		}
		
		catch (SocketException se) {
			System.err.println(se);
		}
		catch (IOException ie) {
			System.err.println(ie);
		}
		
	}
	
}
