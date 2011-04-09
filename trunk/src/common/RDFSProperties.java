package common;
import java.io.IOException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class RDFSProperties {

	private static final String BROADCAST_PORT_NODENAME = "BroadcastPort";
	private static final String P2P_PORT_NODENAME = "P2PPort";	
	private static final String PROPERTIES_FILENAME = "Properties.xml";

	protected static Document getDocument() throws SAXException, IOException
	{
		DOMParser parser = new DOMParser();
		parser.parse(RDFSProperties.PROPERTIES_FILENAME);
		return parser.getDocument();
	}

	protected static NodeList getNodeList(String nodeName) throws SAXException, IOException
	{
		Document doc = RDFSProperties.getDocument();
		return doc.getElementsByTagName(nodeName);
	}

	public static int getBroadcastPort()
	{
		// Only one node called BroadCastPort
		try {
			org.w3c.dom.Node node = RDFSProperties.getNodeList(BROADCAST_PORT_NODENAME).item(0);
			return Integer.parseInt(node.getTextContent());
		} catch (Exception e) {
			Log.me(null, "Failed to obtain BroadCast Port (RDFSProperties)");
			return -1;
		}
	}
	public static int getP2PPort() 
	{
		// Only one node called P2PPort
		try {
			org.w3c.dom.Node node = RDFSProperties.getNodeList(P2P_PORT_NODENAME).item(0);
			return Integer.parseInt(node.getTextContent());
		} catch (Exception e) {
			Log.me(null, "Failed to obtain P2P Port (RDFSProperties)");
			return -1;
		}
	}

}
