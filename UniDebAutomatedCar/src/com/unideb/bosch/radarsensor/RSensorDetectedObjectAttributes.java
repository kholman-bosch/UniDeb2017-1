package com.unideb.bosch.radarsensor;

import com.unideb.bosch.automatedcar.framework.WorldObject;

public class RSensorDetectedObjectAttributes {

	public WorldObject parentWorldObject;
	public float longitudinalRelative_Velcit;
	public float lateralRelative_Velcity;
	public float longitudinalDistance_From_EGO;
	public float lateralDistance_From_EGO;
	public int dangerValue = -1;

	public RSensorDetectedObjectAttributes(WorldObject object) {
		if (object == null) {
			System.err.println("NULL OBJECT ADDED TO ATTRIBUTE OBJECT!!! " + this.getClass().getName());
		}
		this.parentWorldObject = object;
	}
}
