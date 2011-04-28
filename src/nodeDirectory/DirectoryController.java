package nodeDirectory;

public class DirectoryController {
	NodeDirectory nd = new NodeDirectory();
	
	private static DirectoryController instance = null;
	
	private DirectoryController()
	{
		
	}
	
	public static DirectoryController getInstance()
	{
		if (instance == null)
			instance = new DirectoryController();
		return instance;
	}
	
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
