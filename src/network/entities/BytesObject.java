package network.entities;
import java.io.Serializable;

public class BytesObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2952766343874792906L;

	public BytesObject(byte[] b)
	{
		this.setBytes(b);
	}
	public void setBytes(byte[] bytes) {
		this.bytes = bytes;
	}
	public byte[] getBytes() {
		return bytes;
	}
	private byte[] bytes;

	
}
