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

	- scale the post collision velocity by both the position *and* mass of each particle. 
	  currently only the position and average inverse mass is used. as with the velocity,
	  it might be a problem since the contact point is not available when the mass is 
	  needed.
	  
	- review all p1 p2 getters (eg get mass). can it be stored instead of computed everytime?
	
	- consider if the API should let the user set the SCP's properties directly. elasticity, 
	  friction, mass, etc are all inherited from the attached particles
	  
	- consider a more accurate velocity getter. should use a parameterized value
	  to scale the velocity relative to the contact point. one problem is the velocity is
	  needed before the contact point is established.
	
	- setCorners is a duplicate from the updateCornerPositions method in the RectangleParticle class,
	  it needs to be placed back in that class but use the displacement as suggested by Jim B. Its here
	  because of the way RectangleParticle calculates the corners -- once on, they are calculated
	  constantly. that should be fixed too.
	  
	- getContactPointParam should probably belong to the rectangleparticle and circleparticle classes. 
	  also the functions respective to each, for better OOD
	
	- clean up resolveCollision with submethods
	
*/

package org.cove.ape;

	
import flash.display.Sprite;
import flash.display.DisplayObject;

class SpringConstraintParticle extends RectangleParticle {
	
	private AbstractParticle p1;
	private AbstractParticle p2;
	
	private Vector2D avgVelocity;
	private Vector2D lambda;
	private SpringConstraint parent;
	private boolean scaleToLength;
	
	private Vector2D rca;
	private Vector2D rcb;
	private float s;
	
	private float _rectScale;
	private float _rectHeight;
	private float _fixedEndLimit;
			
	public SpringConstraintParticle(
			AbstractParticle p1, 
			AbstractParticle p2, 
			SpringConstraint p, 
			float rectHeight, 
			float rectScale,
			boolean scaleToLength) {
		
		super(0,0,0,0,0,false, 1, 0.3f, 0);
		
		this.p1 = p1;
		this.p2 = p2;
		
		lambda = new Vector2D(0,0);
		avgVelocity = new Vector2D(0,0);
		
		parent = p;
		this.setRectScale(rectScale);
		this.setRectHeight(rectHeight);
		this.scaleToLength = scaleToLength;
		
		setFixedEndLimit(0);
		rca = new Vector2D();
		rcb = new Vector2D();
	}
	
	
		
	void setRectScale(float s) {
		_rectScale = s;
	}
	
	
	/**
	 * @private
	 */		
	float getRectScale() {
		return _rectScale;
	}
	
	
	void setRectHeight(float r) {
		_rectHeight = r;
	}
	
	
	/**
	 * @private
	 */	
	float getRectHeight() {
		return _rectHeight;
	}

	
	/**
	 * For cases when the SpringConstraint is both collidable and only one of the
	 * two end particles are fixed, this value will dispose of collisions near the
	 * fixed particle, to correct for situations where the collision could never be
	 * resolved.
	 */	
	void setFixedEndLimit(float f) {
		_fixedEndLimit = f;
	}
	
	
	/**
	 * @private
	 */	
	float getFixedEndLimit() {
		return _fixedEndLimit;
	}


	/**
	 * returns the average mass of the two connected particles
	 */
	public float getMass() {
		return (p1.getMass() + p2.getMass()) / 2; 
	}
	
	
	/**
	 * returns the average elasticity of the two connected particles
	 */
	public float getElasticity() {
		return (p1.getElasticity() + p2.getElasticity()) / 2; 
	}
	
	
	/**
	 * returns the average friction of the two connected particles
	 */
	public float getFriction() {
		return (p1.getFriction() + p2.getFriction()) / 2; 
	}
	
	
	/**
	 * returns the average velocity of the two connected particles
	 */
	public Vector2D getVelocity() {
		Vector2D p1v =  p1.getVelocity();
		Vector2D p2v =  p2.getVelocity();
		
		avgVelocity.setTo(((p1v.x + p2v.x) / 2), ((p1v.y + p2v.y) / 2));
		return avgVelocity;
	}	
	
	Sprite inner;
	
	public void init() {
		if (displayObject != null) {
			initDisplay();
		} else {
			inner = new Sprite();
			parent.getSprite().addChild(inner);
			inner.name = "inner";
						
			float w = parent.getCurrLength() * getRectScale();
			float h = getRectHeight();
			
			inner.graphics.clear();
			inner.graphics.lineStyle(parent.lineThickness, parent.lineColor, parent.lineAlpha);
			inner.graphics.beginFill(parent.fillColor, parent.fillAlpha);
			inner.graphics.drawRect(-w/2, -h/2, w, h);
			inner.graphics.endFill();
		}
		paint();
	}
	
	public void draw()
	{
		float w = parent.getCurrLength() * getRectScale();
		float h = getRectHeight();
		
		inner.graphics.clear();
		inner.graphics.lineStyle(parent.lineThickness, parent.lineColor, parent.lineAlpha);
		inner.graphics.beginFill(parent.fillColor, parent.fillAlpha);
		inner.graphics.drawRect(-w/2, -h/2, w, h);
		inner.graphics.endFill();
	}
		
	public void paint() {
		
		Vector2D c = parent.getCenter();
		Sprite s = parent.getSprite();
		
		if (scaleToLength) {
			s.getChildByName("inner").width = parent.getCurrLength() * getRectScale();
		} else if (displayObject != null) {
			s.getChildByName("inner").width = parent.getRestLength() * getRectScale();
		}
		s.x = c.x; 
		s.y = c.y;
		s.rotation = parent.getAngle();
		
		draw();
	}
	
	
	/**
	 * @private
	 */
	void initDisplay() {
		displayObject.x = displayObjectOffset.x;
		displayObject.y = displayObjectOffset.y;
		displayObject.rotation = displayObjectRotation;
		
		Sprite inner = new Sprite();
		inner.name = "inner";
		
		inner.addChild(displayObject);
		parent.getSprite().addChild(inner);
	}	
	
			
   /**
	 * @private
	 * returns the average inverse mass.
	 */		
	float getInvMass() {
		if (p1.getFixed() && p2.getFixed()) return 0;
		return 1 / ((p1.getMass() + p2.getMass()) / 2);  
	}
	
	
	/**
	 * called only on collision
	 */
	void updatePosition() {
		Vector2D c = parent.getCenter();
		curr.setTo(c.x, c.y);
		
		setWidth( (scaleToLength) ? parent.getCurrLength() * getRectScale() : parent.getRestLength() * getRectScale() );
		setHeight( getRectHeight() );
		setRadian( parent.getRadian());
	}
	
		
	void resolveCollision(
			Vector2D mtd, Vector2D vel, Vector2D n, float d, int o, AbstractParticle p) {
			
		float t = getContactPointParam(p);
		float c1 = (1 - t);
		float c2 = t;
		
		// if one is fixed then move the other particle the entire way out of collision.
		// also, dispose of collisions at the sides of the scp. The higher the fixedEndLimit
		// value, the more of the scp not be effected by collision. 
		if (p1.getFixed()) {
			if (c2 <= getFixedEndLimit()) return;
			lambda.setTo(mtd.x / c2, mtd.y / c2);
			p2.curr.plusEquals(lambda);
			p2.setVelocity( vel );

		} else if (p2.getFixed()) {
			if (c1 <= getFixedEndLimit()) return;
			lambda.setTo(mtd.x / c1, mtd.y / c1);
			p1.curr.plusEquals(lambda);
			p1.setVelocity(vel);		

		// else both non fixed - move proportionally out of collision
		} else { 
			float denom = (c1 * c1 + c2 * c2);
			if ( MathUtil.equal(denom, 0) ) 
				return;
			lambda.setTo(mtd.x / denom, mtd.y / denom);
		
			p1.curr.plusEquals(lambda.mult(c1));
			p2.curr.plusEquals(lambda.mult(c2));
		
			// if collision is in the middle of SCP set the velocity of both end particles
			if ( MathUtil.equal( t, 0.5f) ) {
				p1.setVelocity(vel);
				p2.setVelocity(vel);
			
			// otherwise change the velocity of the particle closest to contact
			} else {
				AbstractParticle corrParticle = (t < 0.5f) ? p1 : p2;
				corrParticle.setVelocity(vel);
			}
		}
	}
	
	
	/**
	 * given point c, returns a parameterized location on this SCP. Note
	 * this is just treating the SCP as if it were a line segment (ab).
	 */
	private float closestParamPoint(Vector2D c) {
		Vector2D ab = p2.curr.minus(p1.curr);
		float t = (ab.dot(c.minus(p1.curr))) / (ab.dot(ab));
		return MathUtil.clamp(t, 0, 1);
	}


	/**
	 * returns a contact location on this SCP expressed as a parametric value in [0,1]
	 */
	private float getContactPointParam(AbstractParticle p) {
		
		float t = 0;
		
		if (p instanceof CircleParticle)  {
			t = closestParamPoint(p.curr);
		} else if (p instanceof RectangleParticle) {
				
			// go through the sides of the colliding rectangle as line segments
			int shortestIndex = 0;
			float[] paramList = new float[4];
			float shortestDistance = Float.MAX_VALUE;
			
			for (int i = 0; i < 4; i++) {
				setCorners((RectangleParticle)p, i);
				
				// check for closest points on SCP to side of rectangle
				float d = closestPtSegmentSegment();
				if (d < shortestDistance) {
					shortestDistance = d;
					shortestIndex = i;
					paramList[i] = s;
				}
			}
			t = paramList[shortestIndex];
		}
		return t;
	}
	
	
	/**
	 * 
	 */
	private void setCorners(RectangleParticle r, int i) {
	
		float rx = r.curr.x;
		float ry = r.curr.y;
		
		Vector2D[] axes = r.getAxes();
		float[] extents = r.getExtents();
		
		float ae0_x = axes[0].x * extents[0];
		float ae0_y = axes[0].y * extents[0];
		float ae1_x = axes[1].x * extents[1];
		float ae1_y = axes[1].y * extents[1];
		
		float emx = ae0_x - ae1_x;
		float emy = ae0_y - ae1_y;
		float epx = ae0_x + ae1_x;
		float epy = ae0_y + ae1_y;
		
		
		if (i == 0) {
			// 0 and 1
			rca.x = rx - epx;
			rca.y = ry - epy;
			rcb.x = rx + emx;
			rcb.y = ry + emy;
		
		} else if (i == 1) {
			// 1 and 2
			rca.x = rx + emx;
			rca.y = ry + emy;
			rcb.x = rx + epx;
			rcb.y = ry + epy;
			
		} else if (i == 2) {
			// 2 and 3
			rca.x = rx + epx;
			rca.y = ry + epy;
			rcb.x = rx - emx;
			rcb.y = ry - emy;
			
		} else if (i == 3) {
			// 3 and 0
			rca.x = rx - emx;
			rca.y = ry - emy;
			rcb.x = rx - epx;
			rcb.y = ry - epy;
		}
	}
	
	
	/**
	 * pp1-pq1 will be the SCP line segment on which we need parameterized s. 
	 */
	private float closestPtSegmentSegment() {
		
		Vector2D pp1 = p1.curr;
		Vector2D pq1 = p2.curr;
		Vector2D pp2 = rca;
		Vector2D pq2 = rcb;
		
		Vector2D d1 = pq1.minus(pp1);
		Vector2D d2 = pq2.minus(pp2);
		Vector2D r = pp1.minus(pp2);
	
		float t;
		float a = d1.dot(d1);
		float e = d2.dot(d2);
		float f = d2.dot(r);
		
		float c = d1.dot(r);
		float b = d1.dot(d2);
		float denom = a * e - b * b;
		
		if ( MathUtil.notEqual(denom, 0.0f) ) {
			s = MathUtil.clamp((b * f - c * e) / denom, 0, 1);
		} else {
			s = 0.5f; // give the midpoint for parallel lines
		}
		t = (b * s + f) / e;
		 
		if (t < 0) {
			t = 0;
		 	s = MathUtil.clamp(-c / a, 0, 1);
		} else if (t > 0) {
		 	t = 1;
		 	s = MathUtil.clamp((b - c) / a, 0, 1);
		}
		 
		Vector2D c1 = pp1.plus(d1.mult(s));
		Vector2D c2 = pp2.plus(d2.mult(t));
		Vector2D c1mc2 = c1.minus(c2);
		return c1mc2.dot(c1mc2);
	}
}
