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
- provide passible vectors for results. too much object creation happening here
- review the division by zero checks/corrections. why are they needed?
*/

package org.cove.ape;

	
public class Vector2D {
	
	public float x;
	public float y;

	public Vector2D()
	{
		this.x = 0.0f;
		this.y = 0.0f;
	}

	public Vector2D(float px, float py) 
	{
		x = px;
		y = py;
	}
	
	
	public void setTo(float px, float py) {
		x = px;
		y = py;
	}
	
	
	public void copy(Vector2D v) {
		x = v.x;
		y = v.y;
	}


	public float dot(Vector2D v) {
		return x * v.x + y * v.y;
	}
	
	
	public float cross(Vector2D v) {
		return x * v.y - y * v.x;
	}
	

	public Vector2D plus(Vector2D v) {
		return new Vector2D(x + v.x, y + v.y); 
	}

	
	public Vector2D plusEquals(Vector2D v) {
		x += v.x;
		y += v.y;
		return this;
	}
	
	
	public Vector2D minus(Vector2D v) {
		return new Vector2D(x - v.x, y - v.y);    
	}


	public Vector2D minusEquals(Vector2D v) {
		x -= v.x;
		y -= v.y;
		return this;
	}


	public Vector2D mult(float s) {
		return new Vector2D(x * s, y * s);
	}


	public Vector2D multEquals(float s) {
		x *= s;
		y *= s;
		return this;
	}


	public Vector2D times(Vector2D v) {
		return new Vector2D(x * v.x, y * v.y);
	}
	
	
	public Vector2D divEquals(float s) {
		if ( MathUtil.equal(s, 0) ) 
			s = 0.0001f;
		
		x /= s;
		y /= s;
		return this;
	}
	
	
	public float magnitude() {
		return (float)Math.sqrt(x * x + y * y);
	}

	
	public float distance(Vector2D v) {
		Vector2D delta = this.minus(v);
		return delta.magnitude();
	}


	public Vector2D normalize() {
		 float m = magnitude();
		 if ( MathUtil.equal(m, 0) ) 
			 m = 0.0001f;
		 
		 return mult(1 / m);
	}
	
			
	public String toString() {
		return (x + " : " + y);
	}
}
