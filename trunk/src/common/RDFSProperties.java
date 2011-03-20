package common;
import java.io.IOException;

import com.sun.org.apache.xerces.internal.parsers.DOMParser;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

public class RDFSProperties {
	
	private static final String SERVER_PORT_NODENAME = "ServerPort";
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
	
	public static int getServerPort() throws SAXException, IOException, NumberFormatException
	{
		// Only one node called Port
		org.w3c.dom.Node node = RDFSProperties.getNodeList(SERVER_PORT_NODENAME).item(0);
		return Integer.parseInt(node.getTextContent());
	}
	
}
