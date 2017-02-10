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
	
	public WorldObject(int x, int y, int rotation, String type)
	{
		this.x = x;
		this.y = y;
		this.rotation =rotation;	
		this.type = type;
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public int getOrientation() { return rotation; }
	public String getType() { return type; }
}
