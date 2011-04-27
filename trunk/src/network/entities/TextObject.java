package network.entities;
import java.io.Serializable;

public class TextObject implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2952766343874792906L;

	public TextObject(String s)
	{
		this.text = s;
	}
	private String text = "";

	public void setText(String text) {
		this.text = text;
	}

	public String getText() {
		return text;
	}
	
}
