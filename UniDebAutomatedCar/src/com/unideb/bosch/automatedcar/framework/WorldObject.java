package com.unideb.bosch.automatedcar.framework;

/**
 * This class represents objects in the virtual world An object has its coordinates, orientation and type
 *
 * Students must not modify this class!
 */

public class WorldObject {
	
	// 100 pixels in the World represents 2 meters in reality
	public static final float PixelsToMetersFactor = 0.02f; 
	
	private int x;
	private int y;
	private float rotation;
	private String type;
	public WorldObject(int x, int y, float rotation, String type) {
		this.x = x;
		this.y = y;
		this.rotation = rotation;
		this.type = type;
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public float getOrientation() {
		return rotation;
	}

	public String getType() {
		return type;
	}
}
