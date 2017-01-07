package flash.display;

import org.cove.ape.util.GCreator;

public class Sprite extends DisplayObjectContainer {
	public Graphics graphics; 
	
	public Sprite()
	{
		graphics = GCreator.Create(this);
	}
}
