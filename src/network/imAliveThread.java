package network;

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
		while(nc.runImAliveThread)
		{

			String str = "imAlive@" + rdfs.uuid;
			byte[] buffer = str.getBytes();

			while (nc.runListener) {
				try {
					Thread.sleep(5000);
				} catch (InterruptedException e) {
					Log.me(this, "Error while trying to sleep in ImAliveThread - " + e.toString());
				}

				this.nc.sendUDPMessage(buffer);
			}

		}
		Log.me(this, "Exiting imAliveThread");

	}
}
