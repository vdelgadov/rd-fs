/**
 * 
 */
package test;

import static org.junit.Assert.*;

import java.io.File;
import java.util.UUID;

import org.junit.Test;


/**
 * @author Pavooou
 *
 */
public class CommonTest 
{
	
	@Test
	public void getExistingUUID()
	{
		UUID uuid = null;
		uuid = common.rdfs.getUUID();
		assertNotNull(uuid);
	}
	
	@Test
	public void generateNewUUID()
	{
		UUID uuid = null;
		uuid = common.rdfs.getUUID();
		assertNotNull(uuid);
		
		File f = new File(common.rdfs.UUID_FILENAME);
		
		assertTrue(f.delete());
		
		UUID newUuid = null;
		newUuid = common.rdfs.getUUID();
		assertNotNull(newUuid);
		assertFalse(newUuid.equals(uuid));
	}
	
	@Test
	public void readProperties() throws Exception
	{
		assertTrue(common.RDFSProperties.getBroadcastPort() > -1);
		assertTrue(common.RDFSProperties.getP2PPort() > -1);
	}
	
}
