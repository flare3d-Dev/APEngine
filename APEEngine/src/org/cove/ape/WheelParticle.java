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
- review how the WheelParticle needs to have the o value passed during collision
- clear up the difference between speed and angularVelocity
- can the wheel rotate steadily using speed? angularVelocity causes (unwanted?) acceleration
*/
package org.cove.ape;

	
/**
 * A particle that simulates the behavior of a wheel 
 */ 
public class WheelParticle extends CircleParticle {

	private RimParticle rp;
	private Vector2D tan;	
	private Vector2D normSlip;
	private Vector2D orientation;
	
	private float _traction;
	

	/**
	 * @param x The initial x position.
	 * @param y The initial y position.
	 * @param radius The radius of this particle.
	 * @param fixed Determines if the particle is fixed or not. Fixed particles
	 * are not affected by forces or collisions and are good to use as surfaces.
	 * Non-fixed particles move freely in response to collision and forces.
	 * @param mass The mass of the particle
	 * @param elasticity The elasticity of the particle. Higher values mean more elasticity.
	 * @param friction The surface friction of the particle. 
	 * @param traction The surface traction of the particle.
	 * <p>
	 * Note that WheelParticles can be fixed but rotate freely.
	 * </p>
	 */
	public WheelParticle(
			float x, 
			float y, 
			float radius, 
			boolean fixed, 
			float mass, 
			float elasticity,
			float friction,
			float traction) {

		super(x,y,radius,fixed, mass, elasticity, friction);
		tan = new Vector2D(0,0);
		normSlip = new Vector2D(0,0);
		rp = new RimParticle(radius, 2); 	
		
		this.setTraction(traction);
		orientation = new Vector2D();
	}	
	
	public WheelParticle(
			float x, 
			float y, 
			float radius)
	{
		super(x,y,radius,false, 1, 0.3f, 0);
		tan = new Vector2D(0,0);
		normSlip = new Vector2D(0,0);
		rp = new RimParticle(radius, 2); 	
		
		this.setTraction(1);
		orientation = new Vector2D();		
	}

	
	/**
	 * The speed of the WheelParticle. You can alter this value to make the 
	 * WheelParticle spin.
	 */
	public float getSpeed() {
		return rp.getSpeed();
	}
	
	
	/**
	 * @private
	 */		
	public void setSpeed(float s) {
		rp.setSpeed(s);
	}

	
	/**
	 * The angular velocity of the WheelParticle. You can alter this value to make the 
	 * WheelParticle spin.
	 */
	public float getAngularVelocity() {
		return rp.getAngularVelocity();
	}
	
	
	/**
	 * @private
	 */		
	public void setAngularVelocity(float a) {
		rp.setAngularVelocity(a);
	}
	
	
	/**
	 * The amount of traction during a collision. This property controls how much traction is 
	 * applied when the WheelParticle is in contact with another particle. If the value is set
	 * to 0, there will be no traction and the WheelParticle will behave as if the 
	 * surface was totally slippery, like ice. Values should be between 0 and 1. 
	 * 
	 * <p>
	 * Note that the friction property behaves differently than traction. If the surface 
	 * friction is set high during a collision, the WheelParticle will move slowly as if
	 * the surface was covered in glue.
	 * </p>
	 */		
	public float getTraction() {
		return 1 - _traction;
	}


	/**
	 * @private
	 */				
	public void setTraction(float t) {
		_traction = 1 - t;
	}
	
	
	/**
	 * The default paint method for the particle. Note that you should only use
	 * the default painting methods for quick prototyping. For anything beyond that
	 * you should always write your own classes that either extend one of the
	 * APE particle and constraint classes, or is a composite of them. Then within that 
	 * class you can define your own custom painting method.
	 */
	public void paint() {
		getSprite().x = curr.x;
		getSprite().y = curr.y;
		getSprite().rotation = getAngle();	
		
		draw();
	}


	/**
	 * Sets up the visual representation of this particle. This method is automatically called when 
	 * an particle is added to the engine.
	 */
	public void init() {
		cleanup();
		if (displayObject != null) {
			initDisplay();
		} else {
			
			getSprite().graphics.clear();
			getSprite().graphics.lineStyle(lineThickness, lineColor, lineAlpha);
			
			float r = this.getRadius();
			
			// wheel circle
			getSprite().graphics.beginFill(fillColor, fillAlpha);
			getSprite().graphics.drawCircle(0, 0, r);
			getSprite().graphics.endFill();
			
			// spokes
			getSprite().graphics.moveTo(-r, 0);
			getSprite().graphics.lineTo( r, 0);
			getSprite().graphics.moveTo(0, -r);
			getSprite().graphics.lineTo(0, r);
		}
		paint();
	}
	
	public void draw()
	{
		getSprite().graphics.clear();
		getSprite().graphics.lineStyle(lineThickness, lineColor, lineAlpha);
		
		float r = this.getRadius();
		
		// wheel circle
		getSprite().graphics.beginFill(fillColor, fillAlpha);
		getSprite().graphics.drawCircle(0, 0, r);
		getSprite().graphics.endFill();
		
		// spokes
		getSprite().graphics.moveTo(-r, 0);
		getSprite().graphics.lineTo( r, 0);
		getSprite().graphics.moveTo(0, -r);
		getSprite().graphics.lineTo(0, r);	
	}


	/**
	 * The rotation of the wheel in radians.
	 */
	public float getRadian() {
		orientation.setTo(rp.curr.x, rp.curr.y);
		return (float) (Math.atan2(orientation.y, orientation.x) + Math.PI);
	} 


	/**
	 * The rotation of the wheel in degrees.
	 */
	public float getAngle() {
		return getRadian() * MathUtil.ONE_EIGHTY_OVER_PI;
	} 

	
	/**
	 *
	 */			
	public void update(float dt) {
		super.update(dt);
		rp.update(dt);
	}


	/**
	 * @private
	 */		
	void resolveCollision(
			Vector2D mtd, Vector2D vel, Vector2D n, float d, int o, AbstractParticle p) {
		
		// review the o (order) need here - its a hack fix
		super.resolveCollision(mtd, vel, n, d, o, p);
		resolve(n.mult(MathUtil.sign(d * o)));
	}
	

	/**
	 * simulates torque/wheel-ground interaction - n is the surface normal
	 * Origins of this code thanks to Raigan Burns, Metanet software
	 */
	private void resolve(Vector2D n) {

		// this is the tangent vector at the rim particle
		tan.setTo(-rp.curr.y, rp.curr.x);

		// normalize so we can scale by the rotational speed
		tan = tan.normalize();

		// velocity of the wheel's surface 
		Vector2D wheelSurfaceVelocity = tan.mult(rp.getSpeed());
		
		// the velocity of the wheel's surface relative to the ground
		Vector2D combinedVelocity = getVelocity().plusEquals(wheelSurfaceVelocity);
	
		// the wheel's comb velocity projected onto the contact normal
		float cp = combinedVelocity.cross(n);

		// set the wheel's spinspeed to track the ground
		tan.multEquals(cp);
		rp.prev.copy(rp.curr.minus(tan));

		// some of the wheel's torque is removed and converted into linear displacement
		float slipSpeed = (1 - _traction) * rp.getSpeed();
		normSlip.setTo(slipSpeed * n.y, slipSpeed * n.x);
		curr.plusEquals(normSlip);
		
		float _speed = rp.getSpeed() * _traction;		
		rp.setSpeed(_speed);		
	}
}



