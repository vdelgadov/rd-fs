
public class DirectoryController {
	NodeDirectory nd = new NodeDirectory();
	
	public synchronized NodeDirectory getNodeDirectory()
	{
		return this.nd;
	}
	
	//this method should be called periodically to detect nodes down
	public synchronized void update()
	{
		nd.update();
	}
}
