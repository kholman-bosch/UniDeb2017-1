package com.unideb.bosch.automatedcar.framework;

public class WorldObject {
	private int x;
	private int y;
	private ObjectOrientation orientation;
	private String type;
	private String directoryPath;
	
	
	public WorldObject(int x, int y, ObjectOrientation o, String name)
	{
		this.x = x;
		this.y = y;
		this.orientation = o;	
		try {
			String[] parts = name.split("\\.");
			this.type = "";
			this.directoryPath = parts[0];
		} catch (Exception e) {
			System.out.println("Invalid name for WorldObject type: " + name + " " + e.toString());
			this.type="";
			this.directoryPath="";
		}
	}
	
	public int getX() { return x; }
	public int getY() { return y; }
	public ObjectOrientation getOrientation() { return orientation; }
	public String getImageFile() { return directoryPath + type + ".png"; }
}
