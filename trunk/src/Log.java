import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


public class Log {
	
	public static final String LOG_FILENAME = "rdfs.log"; 
	
	public enum Priority
	{
		ERROR (0), WARNING (1), DEBUG (2);
		
		private final int priority;
		
		private Priority(int p) {
			this.priority = p;
		}
		
		public boolean isHigherPriorityThan(Priority p)
		{
			return priority < p.priority;
		}
	}
	
	private Priority p; 
	private File f;
	private FileWriter fstream;
	
	public Log(Priority p)
	{
		this.p = p;
		this.f = new File(Log.LOG_FILENAME);
		
		try
		{
			this.fstream = new FileWriter(this.f, true);
		}
		catch(IOException ioe)
		{
			System.err.println("Log file cannot be opened: "+ioe.getMessage());
		}
	}
	
	public void me(Object o, String message)
	{
		me(o, message, Priority.DEBUG);
	}
	
	public void me(Object o, String message, Priority p)
	{
		if (!p.isHigherPriorityThan(this.p))
		{
			// Nothing should be saved, the priority isn't high enough.
			return;
		}
		try
		{
			BufferedWriter out = new BufferedWriter(fstream);
			out.write(o.toString()+":");
		    out.write(message+"\r");
		    //Close the output stream
		    out.close();
	    }
		catch (Exception e)
		{//Catch exception if any
			System.err.println("Error: " + e.getMessage());
	    }
	}
	
}
