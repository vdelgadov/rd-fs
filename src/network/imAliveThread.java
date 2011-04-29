package network;

import nodeDirectory.DirectoryController;
import common.Log;
import common.rdfsController;

import common.rdfs;
import fileSystem.FileSystemController;

public class imAliveThread implements Runnable {
	NetworkController nc;
	DirectoryController dc;
	FileSystemController fsc;

	public imAliveThread()
	{
		this.nc = NetworkController.getInstance();
		dc = DirectoryController.getInstance();
		fsc = FileSystemController.getInstance("./tmp/");
	}

	@Override
	public void run() {
		while(nc.runImAliveThread)
		{

			String str = "imAlive@" + rdfsController.uuid + "@"+fsc.getSerializedFileTable();
			byte[] buffer = str.getBytes();

			while (nc.runImAliveThread) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Log.me(this, "Error while trying to sleep in ImAliveThread - " + e.toString());
				}
				this.dc.update();
				this.nc.sendUDPMessage(buffer);
			}

		}
		Log.me(this, "Exiting imAliveThread");

	}
}
