package flash.display;

public class DisplayObject {
	public boolean visible = true;
	
	public float x, y, z;
	
	public float rotation;
	
	public float width;
	public float height;
	
	public String name;
	
	public boolean cacheAsBitmaps;
	
	public DisplayObject parent;
	
	public float getX()
	{
		float result = x;		
		DisplayObject p = parent;
		while ( p != null )
		{
			result += p.x;			
			p = p.parent;
		}		
		return result;
	}
	
	public float getY()
	{
		float result = y;		
		DisplayObject p = parent;
		while ( p != null )
		{
			result += p.y;			
			p = p.parent;
		}		
		return result;
	}
	
	public float getZ()
	{
		float result = z;		
		DisplayObject p = parent;
		while ( p != null )
		{
			result += p.z;			
			p = p.parent;
		}		
		return result;
	}
	
	public float getRotation()
	{
		float result = rotation;		
		DisplayObject p = parent;
		while ( p != null )
		{
			result += p.rotation;			
			p = p.parent;
		}		
		return result;
	}
}
