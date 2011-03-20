import java.net.DatagramPacket;
import java.util.LinkedList;

public class NetworkController {

	FileSystemController FSC = new FileSystemController();
	DirectoryController DC = new DirectoryController();
	private LinkedList<DatagramPacket> BroadcastPacketsQueue = new LinkedList<DatagramPacket>();

	static NetworkController nc;
	
	private NetworkController() {
		// TODO Auto-generated constructor stub
	}
	
	public static NetworkController getInstance()
	{
		if (nc == null)
		{
			nc = new NetworkController();
		}
		return nc;
	}
	
	public void startListener()
	{
		BroadcastListener bl = new BroadcastListener(getInstance());
		new Thread(bl).start();
	}
	public synchronized void queueDatagramPacket(DatagramPacket dp)
	{
		BroadcastPacketsQueue.addLast(dp);
	}
	public synchronized DatagramPacket dequeueDatagramPacket()
	{
		return BroadcastPacketsQueue.removeFirst();
	}
	
	public void processPacket()
	{
		DatagramPacket dp = this.dequeueDatagramPacket();
		String message = new String(dp.getData(),0,dp.getLength());
		String[] split = message.split("@", 3);
		if (split[0].compareTo("CD") == 0)
		{
			
		}
	}

}
