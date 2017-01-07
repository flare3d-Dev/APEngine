package org.cove.ape.util;

import flash.display.Graphics;
import flash.display.Sprite;

public class GCreator {
	static IGraphicsCreator _Creator = null;
	
	public static Graphics Create(Sprite sprite)
	{
		if ( null == _Creator )
			throw new ArgumentError("Please call method 'SetGraphics' first!!");
		
		return _Creator.create(sprite);
	}
	
	public static void SetCreator(IGraphicsCreator creator)
	{
		_Creator = creator;
	}
}
