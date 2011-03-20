import java.net.InetAddress;

public class rdfs {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		
		Log.init(Log.Priority.DEBUG);
		Log.me(null, "RDFS starting...");
		
		NetworkController nc = NetworkController.getInstance();
		nc.startListener();
		
	}

}
