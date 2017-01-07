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
	- review getProjection() for precomputing. radius can definitely be precomputed/stored
*/

package org.cove.ape;


	
import java.util.ArrayList;

import flash.display.Graphics;

/**
 * A rectangular shaped particle. 
 */ 
public class RectangleParticle extends AbstractParticle {

	private float[] _extents;
	private Vector2D[] _axes;
	private float _radian;
	
	
	/**
	 * @param x The initial x position.
	 * @param y The initial y position.
	 * @param width The width of this particle.
	 * @param height The height of this particle.
	 * @param rotation The rotation of this particle in radians.
	 * @param fixed Determines if the particle is fixed or not. Fixed particles
	 * are not affected by forces or collisions and are good to use as surfaces.
	 * Non-fixed particles move freely in response to collision and forces.
	 * @param mass The mass of the particle
	 * @param elasticity The elasticity of the particle. Higher values mean more elasticity.
	 * @param friction The surface friction of the particle. 
	 * <p>
	 * Note that RectangleParticles can be fixed but still have their rotation property 
	 * changed.
	 * </p>
	 */
	public RectangleParticle (
			float x, 
			float y, 
			float width, 
			float height, 
			float rotation, 
			boolean fixed,
			float mass, 
			float elasticity,
			float friction) 
	{
		
		super(x, y, fixed, mass, elasticity, friction);
		
		_extents = new float[]{width/2, height/2};
		_axes = new Vector2D[]{new Vector2D(0,0), new Vector2D(0,0)};
		setRadian(rotation);
	}
	
	
	public RectangleParticle (
			float x, 
			float y, 
			float width, 
			float height)
	{
		super(x, y, false, 1.0f, 0.3f, 0.0f);
		
		_extents = new float[]{width/2, height/2};
		_axes = new Vector2D[]{new Vector2D(0,0), new Vector2D(0,0)};
		setRadian(0.0f);	
	}
	
	/**
	 * The rotation of the RectangleParticle in radians. For drawing methods you may 
	 * want to use the <code>angle</code> property which gives the rotation in
	 * degrees from 0 to 360.
	 * 
	 * <p>
	 * Note that while the RectangleParticle can be rotated, it does not have angular
	 * velocity. In otherwords, during collisions, the rotation is not altered, 
	 * and the energy of the rotation is not applied to other colliding particles.
	 * </p>
	 */
	public float getRadian() {
		return _radian;
	}
	
	
	/**
	 * @private
	 */		
	public void setRadian(float t) {
		_radian = t;
		setAxes(t);
	}
		
	
	/**
	 * The rotation of the RectangleParticle in degrees. 
	 */
	public float getAngle() {
		return getRadian() * MathUtil.ONE_EIGHTY_OVER_PI;
	}


	/**
	 * @private
	 */		
	public void setAngle(float a) {
		setRadian( a * MathUtil.PI_OVER_ONE_EIGHTY);
	}
		
	
	/**
	 * Sets up the visual representation of this RectangleParticle. This method is called 
	 * automatically when an instance of this RectangleParticle's parent Group is added to 
	 * the APEngine, when  this RectangleParticle's Composite is added to a Group, or the 
	 * RectangleParticle is added to a Composite or Group.
	 */				
	public void init() {
		cleanup();
		if (displayObject != null) {
			initDisplay();
		} else {
		
			float w = _extents[0] * 2;
			float h = _extents[1] * 2;
			
			getSprite().graphics.clear();
			getSprite().graphics.lineStyle(lineThickness, lineColor, lineAlpha);
			getSprite().graphics.beginFill(fillColor, fillAlpha);
			getSprite().graphics.drawRect(-w/2, -h/2, w, h);
			getSprite().graphics.endFill();
		}
		paint();
	}
	
	
	public void draw()
	{
		float w = _extents[0] * 2;
		float h = _extents[1] * 2;
		
		getSprite().graphics.clear();
		getSprite().graphics.lineStyle(lineThickness, lineColor, lineAlpha);
		getSprite().graphics.beginFill(fillColor, fillAlpha);
		getSprite().graphics.drawRect(-w/2, -h/2, w, h);
		getSprite().graphics.endFill();	
	}
	
	/**
	 * The default painting method for this particle. This method is called automatically
	 * by the <code>APEngine.paint()</code> method. If you want to define your own custom painting
	 * method, then create a subclass of this class and override <code>paint()</code>.
	 */	
	public void paint() {
		getSprite().x = curr.x;
		getSprite().y = curr.y;
		getSprite().rotation = getAngle();
		
		draw();
	}
	
	
	public void setWidth(float w) {
		_extents[0] = w/2;
	}

	
	public float getWidth() {
		return _extents[0] * 2;
	}


	public void setHeight(float h) {
		_extents[1] = h / 2;
	}


	public float getHeight() {
		return _extents[1] * 2;
	}
			
	
	/**
	 * @private
	 */	
	final Vector2D[] getAxes() {
		return _axes;
	}
	

	/**
	 * @private
	 */	
	final float[] getExtents() {
		return _extents;
	}
	
	
	/**
	 * @private
	 */	
	Interval getProjection(Vector2D axis) {
		
		float radius =
				_extents[0] * Math.abs(axis.dot(_axes[0]))+
				_extents[1] * Math.abs(axis.dot(_axes[1]));
		
		float c = samp.dot(axis);
		
		interval.min = c - radius;
		interval.max = c + radius;
		return interval;
	}


	/**
	 * 
	 */					
	private void setAxes(float t) {
		float s = (float) Math.sin(t);
		float c = (float) Math.cos(t);
		
		_axes[0].x = c;
		_axes[0].y = s;
		_axes[1].x = -s;
		_axes[1].y = c;
	}
}
