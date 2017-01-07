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
	- should all getters for composites, particles, constraints arrays return
	  a copy of the array? do we want to give the user direct access to it?
	- addConstraintList, addParticleList
	- if get particles and get constraints returned members of the Groups composites
	  (as they probably should, the checkCollision... methods would probably be much
	  cleaner.
*/ 
package org.cove.ape;

import java.util.ArrayList;
import java.util.List;

/**
 * The Group class can contain Particles, Constraints, and Composites. Groups
 * can be assigned to be checked for collision with other Groups or internally. 
 */ 
public class Group extends AbstractCollection {
	
	private List<Composite> _composites;
	private List<Group> _collisionList;
	private boolean _collideInternal;
	
	
	/**
	 * The Group class is the main organizational class for APE. Once groups are created and populated 
	 * with particles, constraints, and composites, they are added to the APEngine. Groups may contain
	 * particles, constraints, and composites. Composites may only contain particles and constraints.
	 */
	public Group(boolean collideInternal) {
		_composites = new ArrayList<Composite>();
		_collisionList = new ArrayList<Group>();
		this.setCollideInternal(collideInternal);
	}
	
	public Group()
	{		
		_composites = new ArrayList<Composite>();
		_collisionList = new ArrayList<Group>();
		this.setCollideInternal(false);
	}
	
	
	/**
	 * Initializes every member of this Group by in turn calling 
	 * each members <code>init()</code> method.
	 */
	public void init() {
		super.init();
		for (int i = 0; i < _composites.size(); i++) {
			_composites.get(i).init();			
		} 
	}
	

	/**
	 * Returns an Array containing all the Composites added to this Group
	 */
	public List<Composite> getComposites() {
		return _composites;
	}
	
	
	/**
	 * Adds a Composite to the Group.
	 * 
	 * @param c The Composite to be added.
	 */
	public void addComposite(Composite c) {
		_composites.add(c);
		c.setIsParented(true);
		if (getIsParented()) 
			c.init();
	}


	/**
	 * Removes a Composite from the Group.
	 * 
	 * @param c The Composite to be removed.
	 */
	public void removeComposite(Composite c) {
		
		if ( _composites.remove(c) )
		{
			c.setIsParented(false);
			c.cleanup();
		}		
	}
	

	/**
	 * Paints all members of this Group. This method is called automatically
	 * by the APEngine class.
	 */
	public void paint() {

		super.paint();
	
		int len = _composites.size();
		for (int i = 0; i < len; i++) {
			Composite c = _composites.get(i);
			c.paint();
		}						
	}


	/**
	 * Adds an Group instance to be checked for collision against
	 * this one.
	 */
	public void addCollidable(Group g) {
		 _collisionList.add(g);
	}


	/**
	 * Removes a Group from the collidable list of this Group.
	 */
	public void removeCollidable(Group g) {
		
		if ( _collisionList.remove(g) )
		{
			; 
		}		
	}


	/**
	 * Adds an array of AbstractCollection instances to be checked for collision 
	 * against this one.
	 */
	public void addCollidableList(List<Group> list) {
		 for (int i = 0; i < list.size(); i++) {
		 	Group g = list.get(i);
		 	_collisionList.add(g);
		 }
	}
	
	
	/**
	 * Returns the array of every Group assigned to collide with 
	 * this Group instance.
	 */
	public List<Group> getCollisionList() {
		return _collisionList;
	}	


	/**
	 * Returns an array of every particle, constraint, and composite added to the Group.
	 */
	public ArrayList getAll() {
		ArrayList list = new ArrayList();
		list.addAll(getParticles());
		list.addAll(getConstraints());
		list.addAll(getComposites());
		return list;
	}	

					
	/**
	 * Determines if the members of this Group are checked for
	 * collision with one another.
	 */
	public boolean getCollideInternal() {
		return _collideInternal;
	}
	
	
	/**
	 * @private
	 */
	public void setCollideInternal(boolean b) {
		_collideInternal = b;
	}
	
	
	/**
	 * Calls the <code>cleanup()</code> method of every member of this Group.
	 * The cleanup() method is called automatically when an Group is removed
	 * from the APEngine.
	 */
	public void cleanup() {
		super.cleanup();
		for (int i = 0; i < _composites.size(); i++) {
			_composites.get(i).cleanup();	
		}
	}
	
			
	/**
	 * @private
	 */
	void integrate(float dt2) {
		
		super.integrate(dt2);
	
		int len = _composites.size();
		for (int i = 0; i < len; i++) {
			Composite cmp = _composites.get(i);
			cmp.integrate(dt2);
		}						
	}
	
	
	/**
	 * @private
	 */
	void satisfyConstraints() {
		
		super.satisfyConstraints();
	
		int len = _composites.size();
		for (int i = 0; i < len; i++) {
			Composite cmp = _composites.get(i);
			cmp.satisfyConstraints();
		}				
	}
	
	
	/**
	 * @private
	 */
	void checkCollisions() {
		
		if (getCollideInternal()) 
			checkCollisionGroupInternal();
		
		int len = _collisionList.size();
		for (int i = 0; i < len; i++) {
			Group g = _collisionList.get(i);
			checkCollisionVsGroup(g);
		}
	}
	
	
	private void checkCollisionGroupInternal() {
		
		// check collisions not in composites
		checkInternalCollisions();
		
		// for every composite in this Group..
		int clen = _composites.size();
		for (int j = 0; j < clen; j++) {
			
			Composite ca = _composites.get(j);
			
			// .. vs non composite particles and constraints in this group
			ca.checkCollisionsVsCollection(this);
			
			// ...vs every other composite in this Group
			for (int i = j + 1; i < clen; i++) {
				Composite cb = _composites.get(i);
				ca.checkCollisionsVsCollection(cb);
			}
		}
	}
	
	
	private void checkCollisionVsGroup(Group g) {
		
		// check particles and constraints not in composites of either group
		checkCollisionsVsCollection(g);
		
		int clen = _composites.size();
		int gclen = g.getComposites().size();
		
		// for every composite in this group..
		for (int i = 0; i < clen; i++) {
		
			// check vs the particles and constraints of g
			Composite c = _composites.get(i);
			c.checkCollisionsVsCollection(g);
			
			// check vs composites of g
			for (int j = 0; j < gclen; j++) {
				Composite gc = g.getComposites().get(j);
				c.checkCollisionsVsCollection(gc);
			}
		}
		
		// check particles and constraints of this group vs the composites of g
		for (int j = 0; j < gclen; j++) {
			Composite gc = g.getComposites().get(j);	
			checkCollisionsVsCollection(gc);
		}
	}
}
