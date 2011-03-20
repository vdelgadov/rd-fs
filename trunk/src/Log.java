import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;


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
			return priority <= p.priority;
		}
	}
	
	private Priority p; 
	private File f;
	private FileWriter fstream;
	private BufferedWriter out;
	
	private static Log l;
	
	private Log(Priority p)
	{
		this.p = p;
		this.f = new File(Log.LOG_FILENAME);
	}
	
	private static Log getInstance(Priority p)
	{
		if (l == null)
		{
			l = new Log(p);
		}
		return l;
	}
	
	public static void init(Priority p)
	{
		Log.getInstance(p);
	}
	
	public static void me(Object o, String message)
	{
		me(o, message, Priority.DEBUG);
	}
	
	public static void me(Object o, String message, Priority p)
	{
		if (!p.isHigherPriorityThan(l.p))
		{
			// Nothing should be saved, the priority isn't high enough.
			return;
		}
		try
		{
			l.fstream = new FileWriter(l.f, true);
			l.out = new BufferedWriter(l.fstream);
			SimpleDateFormat df = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			l.out.write(df.format(new Date())+ "  ");
			if (o != null)
			{
				l.out.write(o.toString()+":");
			}
		    l.out.write(message+"\n\r");
		    //Close the output stream
		    l.out.close();
		    l.fstream.close();
	    }
		catch (Exception e)
		{//Catch exception if any
			System.err.println("Error: " + e.getMessage());
	    }
	}
	
}
