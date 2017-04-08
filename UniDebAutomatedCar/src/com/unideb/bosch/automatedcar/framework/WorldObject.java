package com.unideb.bosch.automatedcar.framework;

/**
 * This class represents objects in the virtual world
 * An object has its coordinates, orientation and type
 *
 * Students must not modify this class!
 */

public class WorldObject {
	private int x;
	private int y;
	private int rotation;
	private String type;
	
	private float m11;
	private float m12;
	private float m21;
	private float m22;
	
	public WorldObject(int x, int y, int rotation, String type)
	{
		this.x = x;
		this.y = y;
		this.rotation =rotation;	
		this.type = type;
	}
	
	public WorldObject(int x, int y, float m11, float m12, float m21, float m22, String type)
	{
		this.x = x;
		this.y = y;
		this.m11 = m11;
		this.m12 = m12;
		this.m21 = m21;
		this.m22 = m22;
		this.type = type;
	}
	
	
	
	public int getRotation() {
		return rotation;
	}

	public float getM11() {
		return m11;
	}

	public float getM12() {
		return m12;
	}

	public float getM21() {
		return m21;
	}

	public float getM22() {
		return m22;
	}

	public int getX() { return x; }
	public int getY() { return y; }
	public int getOrientation() { return rotation; }
	public String getType() { return type; }
}
