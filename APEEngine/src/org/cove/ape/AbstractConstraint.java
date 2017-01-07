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

package org.cove.ape;

	
import flash.display.Sprite;
import flash.display.DisplayObject;

import org.cove.ape.util.ArgumentError;

import flash.utils;

/**
 * The abstract base class for all constraints. 
 * 
 * <p>
 * You should not instantiate this class directly -- instead use one of the subclasses.
 * </p>
 */
public class AbstractConstraint extends AbstractItem {
	
	private float _stiffness;
	

	/** 
	 * @private
	 */
	public AbstractConstraint (float stiffness) {	
		if (utils.getQualifiedClassName(this) == "org.cove.ape.AbstractConstraint") {
			throw new ArgumentError("AbstractConstraint can't be instantiated directly");
		}
		this.setStiffness(stiffness);
		setStyle();
	}
		
	
	/**
	 * The stiffness of the constraint. Higher values result in result in 
	 * stiffer constraints. Values should be > 0 and <= 1. Depending on the situation, 
	 * setting constraints to very high values may result in instability.
	 */ 
	public float getStiffness() {
		return _stiffness;
	}
	
	
	/**
	 * @private
	 */			
	public void setStiffness(float s) {
		_stiffness = s;
	}
	
	
	/**
	 * @private
	 */					
	void resolve() {}
}
