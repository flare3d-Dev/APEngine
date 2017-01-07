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
	- get sprite() is duplicated in AbstractItem. Should be in some parent class.
	- checkCollisionsVsCollection and checkInternalCollisions methods use SpringConstraint. 
      it should be AbstractConstraint but the isConnectedTo method is in SpringConstraint.
    - same deal with the paint() method here -- needs to test connected particles state 
      using SpringConstraint methods but should really be AbstractConstraint. need to clear up
      what an AbstractConstraint really means.
    - would an explicit cast be more efficient in the paint() method here?
*/

package org.cove.ape;

	
import flash.display.Sprite;

import java.util.ArrayList;
import java.util.List;

import org.cove.ape.util.ArgumentError;

import flash.utils;


/**
 * The abstract base class for all grouping classes. 
 * 
 * <p>
 * You should not instantiate this class directly -- instead use one of the subclasses.
 * </p>
 */	
public class AbstractCollection {
	

	private Sprite _sprite;
	private List<AbstractParticle> _particles;
	private List<AbstractConstraint> _constraints;
	private boolean _isParented;
	
	
	public AbstractCollection() {	
		if (utils.getQualifiedClassName(this) == "org.cove.ape.AbstractCollection") {
			throw new ArgumentError("AbstractCollection can't be instantiated directly");
		}
		_isParented = false;
		_particles = new ArrayList<AbstractParticle>();
		_constraints = new ArrayList<AbstractConstraint>();
	}
	
	
	/**
	 * The Array of all AbstractParticle instances added to the AbstractCollection
	 */
	public List<AbstractParticle> getParticles() {
		return _particles;
	}
	
	
	/**
	 * The Array of all AbstractConstraint instances added to the AbstractCollection
	 */	
	public List<AbstractConstraint> getConstraints() {
		return _constraints;	
	}

	
	/**
	 * Adds an AbstractParticle to the AbstractCollection.
	 * 
	 * @param p The particle to be added.
	 */
	public void addParticle(AbstractParticle p) {		
		_particles.add(p);
		if (_isParented) p.init();
	}
	
	
	/**
	 * Removes an AbstractParticle from the AbstractCollection.
	 * 
	 * @param p The particle to be removed.
	 */
	public void removeParticle(AbstractParticle p) {
		
		boolean _hasSuch = _particles.remove(p);
		if ( _hasSuch )
			p.cleanup();		
	}
	
	
	/**
	 * Adds a constraint to the Collection.
	 * 
	 * @param c The constraint to be added.
	 */
	public void addConstraint(AbstractConstraint c) {		
		_constraints.add(c);		
		if (_isParented) c.init();
	}


	/**
	 * Removes a constraint from the Collection.
	 * 
	 * @param c The constraint to be removed.
	 */
	public void removeConstraint(AbstractConstraint c) {
		
		boolean _hasSuch = _constraints.remove(c);
		if ( _hasSuch )
			c.cleanup();		
	}
	
	
	/**
	 * Initializes every member of this AbstractCollection by in turn calling 
	 * each members <code>init()</code> method.
	 */
	public void init() {
		
		for (int i = 0; i < _particles.size(); i++) {
			_particles.get(i).init();	
		}
		
		for (int i = 0; i < _constraints.size(); i++) {
			_constraints.get(i).init();
		}
	}
	
			
	/**
	 * paints every member of this AbstractCollection by calling each members
	 * <code>paint()</code> method.
	 */
	public void paint() {
		
		AbstractParticle p;
		int len = _particles.size();
		for (int i = 0; i < len; i++) {
			p = _particles.get(i);
			// TODO
			//if ((! p.getFixed()) || p.getAlwaysRepaint()) 
			//	p.paint();
			p.paint();
		}
		
		SpringConstraint c;
		len = _constraints.size();
		for (int i = 0; i < len; i++) {
			c = (SpringConstraint)_constraints.get(i);
			// TODO
			//if ((! c.getFixed()) || c.getAlwaysRepaint()) 
			//	c.paint();
			c.paint();
		}
	}
	
	
	/**
	 * Calls the <code>cleanup()</code> method of every member of this AbstractCollection.
	 * The cleanup() method is called automatically when an AbstractCollection is removed
	 * from its parent.
	 */
	public void cleanup() {
		
		for (int i = 0; i < _particles.size(); i++) {
			_particles.get(i).cleanup();	
		}
		for (int i = 0; i < _constraints.size(); i++) {
			_constraints.get(i).cleanup();
		}
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
	

	/**
	 * Returns an array of every particle and constraint added to the AbstractCollection.
	 */
	public ArrayList getAll() {		
		ArrayList list = new ArrayList();		
		list.addAll(_particles);
		list.addAll(_constraints);		
		return list;
	}	
	
	
	/**
	 * @private
	 */
	boolean getIsParented() {
		return _isParented;
	}	


	/**
	 * @private
	 */		
	void setIsParented(boolean b) {
		_isParented = b;
	}	
	
							
	/**
	 * @private
	 */
	void integrate(float dt2) {
		int len = _particles.size();
		for (int i = 0; i < len; i++) {
			AbstractParticle p = _particles.get(i);;
			p.update(dt2);	
		}
	}		
	
		
	/**
	 * @private
	 */
	void satisfyConstraints() {
		int len = _constraints.size();
		for (int i = 0; i < len; i++) {
			AbstractConstraint c = _constraints.get(i);
			c.resolve();	
		}
	}			
	

	/**
	 * @private
	 */	
	void checkInternalCollisions() {
	 
		// every particle in this AbstractCollection
		int plen = _particles.size();
		for (int j = 0; j < plen; j++) {
			
			AbstractParticle pa = _particles.get(j);
			if (! pa.getCollidable()) continue;
			
			// ...vs every other particle in this AbstractCollection
			for (int i = j + 1; i < plen; i++) {
				AbstractParticle pb = _particles.get(i);
				if (pb.getCollidable()) CollisionDetector.test(pa, pb);
			}
			
			// ...vs every other constraint in this AbstractCollection
			int clen = _constraints.size();
			for (int n = 0; n < clen; n++) {
				SpringConstraint c = (SpringConstraint)_constraints.get(n);
				if (c.getCollidable() && ! c.isConnectedTo(pa)) {
					c.getScp().updatePosition();
					CollisionDetector.test(pa, c.getScp());
				}
			}
		}
	}


	/**
	 * @private
	 */	
	void checkCollisionsVsCollection(AbstractCollection ac) {
		
		// every particle in this collection...
		int plen = _particles.size();
		for (int j = 0; j < plen; j++) {
			
			AbstractParticle pga = _particles.get(j);
			if (! pga.getCollidable()) continue;
			
			// ...vs every particle in the other collection
			int acplen = ac.getParticles().size();
			for (int x = 0; x < acplen; x++) {
				AbstractParticle pgb = ac.getParticles().get(x);
				if (pgb.getCollidable()) CollisionDetector.test(pga, pgb);
			}
			// ...vs every constraint in the other collection
			int acclen = ac.getConstraints().size();
			for (int x = 0; x < acclen; x++) {
				SpringConstraint cgb = (SpringConstraint)ac.getConstraints().get(x);
				if (cgb.getCollidable() && ! cgb.isConnectedTo(pga)) {
					cgb.getScp().updatePosition();
					CollisionDetector.test(pga, cgb.getScp());
				}
			}
		}
		
		// every constraint in this collection...
		int clen = _constraints.size();
		for (int j = 0; j < clen; j++) {
			SpringConstraint cga = (SpringConstraint)_constraints.get(j);
			if (! cga.getCollidable()) continue;
			
			// ...vs every particle in the other collection
			int acplen = ac.getParticles().size();
			for (int n = 0; n < acplen; n++) {
				AbstractParticle pgb = ac.getParticles().get(n);
				if (pgb.getCollidable() && ! cga.isConnectedTo(pgb)) {
					cga.getScp().updatePosition();
					CollisionDetector.test(pgb, cga.getScp());
				}
			}
		}
	}			
}



