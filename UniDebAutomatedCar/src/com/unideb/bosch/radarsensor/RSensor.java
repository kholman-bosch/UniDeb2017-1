package com.unideb.bosch.radarsensor;

import java.util.ArrayList;

import com.unideb.bosch.automatedcar.WorldObjectParser;
import com.unideb.bosch.automatedcar.framework.WorldObject;

public class RSensor { // radar sensor

	private int minimumDetectRange = 0;
	private int maximumDetectRange = 0;
	private int minimumDetectAngle = 0;
	private int maximumDetectAngle = 0;
	// default sensor configuration values
	private int defaultMinimumDetectRange = 20;
	private int defaultMinimumDetectAngle = 10;
	private int defaultMaximummDetectRange = 250;
	private int defaultMaximumDetectAngle = 85;
	//
	private int maximum_DetectableDangerousObjects = 5;
	private ArrayList<RSensorDetectedObjectAttributes> detectedWorldObjects = new ArrayList<RSensorDetectedObjectAttributes>(64);

	public RSensor(int minDetectRange, int maxDetectRange, int minDetctAngle, int maxDetectAngle) {
		this.minimumDetectRange = minDetectRange;
		this.maximumDetectRange = maxDetectRange;
		this.minimumDetectAngle = minDetctAngle;
		this.maximumDetectAngle = maxDetectAngle;
		this.validate_Sensor_Configuration();
	}

	public void update() {
		this.detectedWorldObjects.clear();
		for (int i = 0; i < WorldObjectParser.getInstance().getWorldObjects().size(); i++) {
			WorldObject actual_WorldObjet = WorldObjectParser.getInstance().getWorldObjects().get(i);
			if (isValid_WorldObject(actual_WorldObjet.getType())) {
				if (this.isWorldObject_Detected(actual_WorldObjet)) {
					this.detectedWorldObjects.add(new RSensorDetectedObjectAttributes(actual_WorldObjet));
				}
			}
		}
		for (int i = 0; i < this.detectedWorldObjects.size(); i++) {
			this.calculate_DetectedWorldObject_Attributes(this.detectedWorldObjects.get(i));
		}
		this.filterOut_LessDangerousObjects();
	}

	private boolean isWorldObject_Detected(WorldObject object) {
		// TODO: sensor math
		return false;
	}

	private void calculate_DetectedWorldObject_Attributes(RSensorDetectedObjectAttributes objectWithAttributes) {
		// TODO: sensor math and set attributes
	}

	private void filterOut_LessDangerousObjects() {
		for (int i = 0; i < this.detectedWorldObjects.size(); i++) {
			// TODO: remove objects from list based on danger value
		}
	}

	// gives back detected WorldObjects up to 5 objects
	public ArrayList<RSensorDetectedObjectAttributes> get_Detected_WorldObjects() {
		return this.detectedWorldObjects;
	}

	private boolean isValid_WorldObject(String object_Type) {
		if (object_Type == null) {
			System.err.println("NO OBJECT TYPE!!! " + this.getClass().getName());
		}
		switch (object_Type) {
		case "tree":
			return true;
		case "man":
			return true;
		case "cyclist":
			return true;
		case "pedestrian":
			return true;
		}
		return false;
	}

	private void validate_Sensor_Configuration() {
		if (this.minimumDetectRange < this.defaultMinimumDetectRange) {
			this.minimumDetectRange = this.defaultMinimumDetectRange;
		}
		if (this.minimumDetectAngle < this.defaultMinimumDetectAngle) {
			this.minimumDetectAngle = this.defaultMinimumDetectAngle;
		}
		if (this.maximumDetectRange > this.defaultMaximummDetectRange) {
			this.maximumDetectRange = this.defaultMaximummDetectRange;
		}
		if (this.maximumDetectAngle > this.defaultMaximumDetectAngle) {
			this.maximumDetectAngle = this.defaultMaximumDetectAngle;
		}
	}
}
