package common;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Log class that is used on the RDFS project.
 * 
 * @author Pavooou
 *
 */
public class Log {
	
	public static final String LOG_FILENAME = "rdfs.log"; 
	
	/**
	 * Priority enum, to be used on the Log class.
	 * 
	 * @author Pavooou
	 *
	 */
	public enum Priority
	{
		ERROR (0), WARNING (1), DEBUG (2);
		
		private final int priority;
		
		private Priority(int p) {
			this.priority = p;
		}
		
		/**
		 * Verifies if this instance has higher priority than the parameter.
		 * 
		 * @param p value that compares it to.
		 * @return true if higher priority, false otherwise
		 */
		public boolean isHigherPriorityThan(Priority p)
		{
			return priority <= p.priority;
		}
	}
	
	private Priority p; 
	private File f;
	private FileWriter fstream;
	private BufferedWriter out;
	
	private static boolean isInitialized = false;
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
	
	/**
	 * Initializes the Log.
	 * 
	 * @param p Lowest priority that should be saved onto the log-file.
	 */
	public static void init(Priority p)
	{
		Log.getInstance(p);
		Log.isInitialized = true;
	}
	
	/**
	 * Logs a message on the log file. Verifies it has enough priority to save it and formats it for saving; uses debug priority.
	 * 
	 * @param o Instance of an object which is calling the log service.
	 * @param message Message to be logged on the file
	 */	
	public static void me(Object o, String message)
	{
		me(o, message, Priority.DEBUG);
	}
	
	/**
	 * Logs a message on the log file. Verifies it has enough priority to save it and formats it for saving.
	 * 
	 * @param o Instance of an object which is calling the log service.
	 * @param message Message to be logged on the file
	 * @param p Priority of the message
	 */
	public synchronized static void me(Object o, String message, Priority p)
	{
		if (!Log.isInitialized) { System.err.println("Log was not initialized properly."); return; };
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
		    l.out.write(message+System.getProperty("line.separator"));
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
