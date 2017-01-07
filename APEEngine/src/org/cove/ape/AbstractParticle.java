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
	- Need removeForces method(s)
	- Center and Position are the same, needs review.
	- Should have alwaysRepaint functionality for Constraints, and bump up to AbstractItem
	- See if there's anywhere where Vectors can be downgraded to simple Point classes
*/

package org.cove.ape;

	
import flash.display.Sprite;
import flash.display.DisplayObject;

import org.cove.ape.util.ArgumentError;

import flash.utils;

 	
/**
 * The abstract base class for all particles.
 * 
 * <p>
 * You should not instantiate this class directly -- instead use one of the subclasses.
 * </p>
 */
public class AbstractParticle extends AbstractItem {
	
	/** @private */
	Vector2D curr;
	/** @private */
	Vector2D prev;
	/** @private */
	Vector2D samp;
	/** @private */
	Interval interval;
	
	private Vector2D forces;
	private Vector2D temp;
	private Collision collision;
			
	private float _kfr;
	private float _mass;
	private float _invMass;
	private float _friction;
	
	private boolean _fixed;
	private boolean _collidable;
	
	private Vector2D _center;
	private int _multisample;
		
	
	/** 
	 * @private
	 */
	public AbstractParticle (
			float x, 
			float y, 
			boolean isFixed, 
			float mass, 
			float elasticity,
			float friction) {
		
		if (utils.getQualifiedClassName(this) == "org.cove.ape.AbstractParticle") {
			throw new ArgumentError("AbstractParticle can't be instantiated directly");
		}
		
		interval = new Interval(0,0);
		
		curr = new Vector2D(x, y);
		prev = new Vector2D(x, y);
		samp = new Vector2D();
		temp = new Vector2D();
		this.setFixed(isFixed);
		
		forces = new Vector2D();
		collision = new Collision(new Vector2D(), new Vector2D());
		this.setCollidable(true);
		
		this.setMass(mass);
		this.setElasticity(elasticity);
		this.setFriction(friction);
		
		setStyle();
		
		_center = new Vector2D();
		_multisample = 0;
	}

	
	/**
	 * The mass of the particle. Valid values are greater than zero. By default, all particles
	 * have a mass of 1. The mass property has no relation to the size of the particle.
	 * 
	 * @throws ArgumentError ArgumentError if the mass is set less than zero. 
	 */
	public float getMass() {
		return _mass; 
	}
	
	
	/**
	 * @private
	 */
	public void setMass(float m) {
		if (m <= 0) throw new ArgumentError("mass may not be set <= 0"); 
		_mass = m;
		_invMass = 1 / _mass;
	}	

	
	/**
	 * The elasticity of the particle. Standard values are between 0 and 1. 
	 * The higher the value, the greater the elasticity.
	 * 
	 * <p>
	 * During collisions the elasticity values are combined. If one particle's
	 * elasticity is set to 0.4 and the other is set to 0.4 then the collision will
	 * be have a total elasticity of 0.8. The result will be the same if one particle
	 * has an elasticity of 0 and the other 0.8.
	 * </p>
	 * 
	 * <p>
	 * Setting the elasticity to greater than 1 (of a single particle, or in a combined
	 * collision) will cause particles to bounce with energy greater than naturally 
	 * possible.
	 * </p>
	 */ 
	public float getElasticity() {
		return _kfr; 
	}
	
	
	/**
	 * @private
	 */
	public void setElasticity(float k) {
		_kfr = k;
	}
	

	/**
	 * Determines the number of intermediate position steps checked for collision each
	 * cycle. Setting this number higher on fast moving particles can prevent 'tunneling'
	 * -- when a particle moves so fast it misses collision with certain surfaces.
	 */ 
	public int getMultisample() {
		return _multisample; 
	}
	
	
	/**
	 * @private
	 */
	public void setMultisample(int m) {
		_multisample = m;
	}
	
	
	/**
	 * Returns A Vector of the current location of the particle
	 */	
	public Vector2D getCenter() {
		_center.setTo(getPx(), getPy());
		return _center;
	}
	
			
	/**
	 * The surface friction of the particle. Values must be in the range of 0 to 1.
	 * 
	 * <p>
	 * 0 is no friction (slippery), 1 is full friction (sticky).
	 * </p>
	 * 
	 * <p>
	 * During collisions, the friction values are summed, but are clamped between 1 and 0.
	 * For example, If two particles have 0.7 as their surface friction, then the resulting
	 * friction between the two particles will be 1 (full friction).
	 * </p>
	 * 
	 * <p>
	 * In the current release, only dynamic friction is calculated. Static friction
	 * is planned for a later release.
	 * </p>
	 *
	 * <p>
	 * There is a bug in the current release where colliding non-fixed particles with friction
	 * greater than 0 will behave erratically. A workaround is to only set the friction of
	 * fixed particles.
	 * </p>
	 * @throws ArgumentError ArgumentError if the friction is set less than zero or greater than 1
	 */	
	public float getFriction() {
		return _friction; 
	}

	
	/**
	 * @private
	 */
	public void setFriction(float f) {
		if (f < 0 || f > 1) throw new ArgumentError("Legal friction must be >= 0 and <=1");
		_friction = f;
	}
	
	
	/**
	 * The fixed state of the particle. If the particle is fixed, it does not move
	 * in response to forces or collisions. Fixed particles are good for surfaces.
	 */
		public boolean getFixed() {
			return _fixed;
		}

 
		/**
	 * @private
	 */
	public void setFixed(boolean f) {
		_fixed = f;
	}
	
	
	/**
	 * The position of the particle. Getting the position of the particle is useful
	 * for drawing it or testing it for some custom purpose. 
	 * 
	 * <p>
	 * When you get the <code>position</code> of a particle you are given a copy of the current
	 * location. Because of this you cannot change the position of a particle by
	 * altering the <code>x</code> and <code>y</code> components of the Vector you have retrieved from the position property.
	 * You have to do something instead like: <code> position = new Vector(100,100)</code>, or
	 * you can use the <code>px</code> and <code>py</code> properties instead.
	 * </p>
	 * 
	 * <p>
	 * You can alter the position of a particle three ways: change its position, set
	 * its velocity, or apply a force to it. Setting the position of a non-fixed particle
	 * is not the same as setting its fixed property to true. A particle held in place by 
	 * its position will behave as if it's attached there by a 0 length spring constraint. 
	 * </p>
	 */
	public Vector2D getPosition() {
		return new Vector2D(curr.x,curr.y);
	}
	
	
	/**
	 * @private
	 */
	public void setPosition(Vector2D p) {
		curr.copy(p);
		prev.copy(p);
	}


	/**
	 * The x position of this particle
	 */
	public float getPx() {
		return curr.x;
	}

	
	/**
	 * @private
	 */
	public void setPx(float x){
		curr.x = x;
		prev.x = x;	
	}


	/**
	 * The y position of this particle
	 */
	public float getPy() {
		return curr.y;
	}


	/**
	 * @private
	 */
	public void setPy(float y) {
		curr.y = y;
		prev.y = y;	
	}


	/**
	 * The velocity of the particle. If you need to change the motion of a particle, 
	 * you should either use this property, or one of the addForce methods. Generally,
	 * the addForce methods are best for slowly altering the motion. The velocity property
	 * is good for instantaneously setting the velocity, e.g., for projectiles.
	 * 
	 */
	public Vector2D getVelocity() {
		return curr.minus(prev);
	}
	
	
	/**
	 * @private
	 */	
	public void setVelocity(Vector2D v) {
		prev = curr.minus(v);	
	}
	
	
	/**
	 * Determines if the particle can collide with other particles or constraints.
	 * The default state is true.
	 */
	public boolean getCollidable() {
		return _collidable;
	}

			
	/**
	 * @private
	 */		
	public void setCollidable(boolean b) {
		_collidable = b;
	}
	
	
	/**
	 * Assigns a DisplayObject to be used when painting this particle.
	 */ 
	
	public void setDisplay(DisplayObject d)
	{
		this.setDisplay(d, 0, 0, 0);
	}
	
	public void setDisplay(DisplayObject d, float offsetX, float offsetY, float rotation) {
		displayObject = d;
		displayObjectRotation = rotation;
		displayObjectOffset = new Vector2D(offsetX, offsetY);
	}
	
	
	/**
	 * Adds a force to the particle. The mass of the particle is taken into 
	 * account when using this method, so it is useful for adding forces 
	 * that simulate effects like wind. Particles with larger masses will
	 * not be affected as greatly as those with smaller masses. Note that the
	 * size (not to be confused with mass) of the particle has no effect 
	 * on its physical behavior with respect to forces.
	 * 
	 * @param f A Vector represeting the force added.
	 */ 
	public void addForce(Vector2D f) {
		forces.plusEquals(f.mult(getInvMass()));
	}
	
	
	/**
	 * Adds a 'massless' force to the particle. The mass of the particle is 
	 * not taken into account when using this method, so it is useful for
	 * adding forces that simulate effects like gravity. Particles with 
	 * larger masses will be affected the same as those with smaller masses.
	 *
	 * @param f A Vector represeting the force added.
	 */ 	
	public void addMasslessForce(Vector2D f) {
		forces.plusEquals(f);
	}
	
		
	/**
	 * The <code>update()</code> method is called automatically during the
	 * APEngine.step() cycle. This method integrates the particle.
	 */
	public void update(float dt2) {
		
		if (getFixed()) 
			return;
		
		// global forces
		addForce(APEngine.force);
		addMasslessForce(APEngine.masslessForce);

		// integrate
		temp.copy(curr);
		
		Vector2D nv = getVelocity().plus(forces.multEquals(dt2));
		curr.plusEquals(nv.multEquals(APEngine.getDamping()));
		prev.copy(temp);

		// clear the forces
		forces.setTo(0,0);
	}
	
	
	/**
	 * @private
	 */
	void initDisplay() {
		displayObject.x = displayObjectOffset.x;
		displayObject.y = displayObjectOffset.y;
		displayObject.rotation = displayObjectRotation;
		getSprite().addChild(displayObject);
	}	
	
		
	/**
	 * @private
	 */		
	Collision getComponents(Vector2D collisionNormal) {
		Vector2D vel = getVelocity();
		float vdotn = collisionNormal.dot(vel);
		collision.vn = collisionNormal.mult(vdotn);
		collision.vt = vel.minus(collision.vn);	
		return collision;
	}


	/**
	 * @private
	 */	
	void resolveCollision(
			Vector2D mtd, Vector2D vel, Vector2D n, float d, int o, AbstractParticle p) 
	{
				
		curr.plusEquals(mtd);
		setVelocity( vel );
	}
	
	
	/**
	 * @private
	 */		
	float getInvMass() {
		return (getFixed()) ? 0 : _invMass; 
	}
}	
