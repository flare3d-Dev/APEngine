package flash.display;

import java.util.ArrayList;
import java.util.List;

public class DisplayObjectContainer extends DisplayObject {

	private List<DisplayObject> children = new ArrayList<DisplayObject>();
	
	public int numChildren = 0;
	
	public DisplayObject addChild(DisplayObject child)
	{
		if ( !children.contains(child) ) {
			children.add(child);
			child.parent = this;
		}
		
		numChildren = children.size();
		
		return child;
	}
	
	public void removeChildAt(int index)
	{
		DisplayObject ch = children.remove(index);
		if ( ch != null )
		{
			ch.parent = null;
			numChildren = children.size();
		}
	}
	
	public DisplayObject getChildByName(String name)
	{
		for (DisplayObject _do : children )
		{
			if ( _do.equals(name) )
				return _do;
		}
		
		return null;
	}
}
