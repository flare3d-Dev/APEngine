/*
Copyright (c) 2006, 2007 Alec Cove

Permission is hereby granted, free of charge, to any person obtaining a copy of this 
software and associated documentation files (the "Software"), to deal in the Software 
without restriction, including without limitation the rights to use, copy, modify, 
merge, publish, distribute, sublicense, and/or sell copies of the Software, and to 
permit persons to whom the Software is furnished to do so, subject to the following 
conditions:

The above copyright notice and this permission notice shall be included in all copies 
or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, 
INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A 
PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT 
HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF 
CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE 
OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
*/

/*
	TODO:
*/

package org.cove.ape;

	
import flash.display.Sprite;
import flash.display.DisplayObject;

/** 
 * The base class for all constraints and particles
 */
public class AbstractItem {
	
	private Sprite _sprite;
	private boolean _visible;
	private boolean _alwaysRepaint;
	

	/** @private */
	float lineThickness;
	/** @private */
	long lineColor;
	/** @private */
	float lineAlpha;
	/** @private */
	long fillColor;
	/** @private */
	float fillAlpha;
	/** @private */
	DisplayObject displayObject;
	/** @private */
	Vector2D displayObjectOffset;
	/** @private */
	float displayObjectRotation;
	
	
	public AbstractItem() {
		_visible = true;	
		_alwaysRepaint = false;
	}
	
	
	/**
	 * This method is automatically called when an item's parent group is added to the engine,
	 * an item's Composite is added to a Group, or the item is added to a Composite or Group.
	 */
	public void init() {}
	
			
	/**
	 * The default painting method for this item. This method is called automatically
	 * by the <code>APEngine.paint()</code> method. 
	 */			
	public void paint() {}	
	
	
	/**
	 * This method is called automatically when an item's parent group is removed
	 * from the APEngine.
	 */
	public void cleanup() {
		getSprite().graphics.clear();
		while ( getSprite().numChildren > 0 )
			getSprite().removeChildAt(0);		
	}
	
	
	/**
	 * For performance, fixed Particles and SpringConstraints don't have their <code>paint()</code>
	 * method called in order to avoid unnecessary redrawing. A SpringConstraint is considered
	 * fixed if its two connecting Particles are fixed. Setting this property to <code>true</code>
	 * forces <code>paint()</code> to be called if this Particle or SpringConstraint <code>fixed</code>
	 * property is true. If you are rotating a fixed Particle or SpringConstraint then you would set 
	 * it's repaintFixed property to true. This property has no effect if a Particle or 
	 * SpringConstraint is not fixed.
	 */
	public final boolean getAlwaysRepaint() {
		return _alwaysRepaint;
	}
	
	
	/**
	 * @private
	 */
	public final void setAlwaysRepaint(boolean b) {
		_alwaysRepaint = b;
	}	
	
			
	/**
	 * The visibility of the item. 
	 */	
	public boolean getVisible() {
		return _visible;
	}
	
	
	/**
	 * @private
	 */			
	public void setVisible(boolean v) {
		_visible = v;
		getSprite().visible = v;
	}


	/**
	 * Sets the line and fill of this Item.
	 */ 		
	
	public void setStyle()
	{
		this.setStyle(0, 0x000000L, 1.0f, 0xffffff, 1.0f);
	}
	
	public void setStyle(
			float lineThickness, long lineColor, float lineAlpha,
			long fillColor, float fillAlpha)
	{
		
		setLine(lineThickness, lineColor, lineAlpha);		
		setFill(fillColor, fillAlpha);		
	}		
	
	
	/**
	 * Sets the style of the line for this Item. 
	 */ 
	public void setLine() {
		this.setLine(0, 0x000000L, 1.0f);
	}
	
	public void setLine(float thickness, long color, float alpha) {
		lineThickness = thickness;
		lineColor = color;
		lineAlpha = alpha;
	}
		
		
	/**
	 * Sets the style of the fill for this Item. 
	 */ 
	public void setFill()
	{
		this.setFill(0xffffffL, 1.0f);
	}
	
	public void setFill(long color, float alpha) {
		fillColor = color;
		fillAlpha = alpha;
	}
	
	
	/**
	 * Provides a Sprite to use as a container for drawing or adding children. When the
	 * sprite is requested for the first time it is automatically added to the global
	 * container in the APEngine class.
	 */	
	public Sprite getSprite() {
		
		if (_sprite != null) return _sprite;
		
		if (APEngine.getContainer() == null) {
			throw new Error("The container property of the APEngine class has not been set");
		}
		
		_sprite = new Sprite();
		APEngine.getContainer().addChild(_sprite);
		return _sprite;
	}	
}

