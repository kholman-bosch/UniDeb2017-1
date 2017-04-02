package com.unideb.bosch.radarsensor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import com.unideb.bosch.automatedcar.AutomatedCar;
import com.unideb.bosch.automatedcar.VirtualWorld;
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
	private int defaultMaximummDetectRange = 500;
	private int defaultMaximumDetectAngle = 85;
	//
	private ArrayList<RSensorDetectedObjectAttributes> detectedWorldObjects = new ArrayList<RSensorDetectedObjectAttributes>(64);
	private ArrayList<RSensorDetectedObjectAttributes> previousWorldObjects = new ArrayList<RSensorDetectedObjectAttributes>(64);
	private ArrayList<RSensorDetectedObjectAttributes> movingWorldObjects = new ArrayList<RSensorDetectedObjectAttributes>(64);
	private AutomatedCar car;
	//
	private float carForwardVector_X;
	private float carForwardVector_Y;

	public RSensor(int minDetectRange, int maxDetectRange, int minDetctAngle, int maxDetectAngle, AutomatedCar car_f) {
		this.minimumDetectRange = minDetectRange;
		this.maximumDetectRange = maxDetectRange;
		this.minimumDetectAngle = minDetctAngle;
		this.maximumDetectAngle = maxDetectAngle;
		this.validate_Sensor_Configuration();
		this.car = car_f;
	}

	public void draw_DebugData(Graphics2D g2) {
		// sadly the swing coordinate system is not a normal one (the 0,0 is in the upper left) so hacks are needed (a 180degree offset and sin/cos swaps)
		g2.setColor(Color.CYAN);
		g2.setStroke(new BasicStroke(2));
		float graphicsScale = VirtualWorld.getGraphicsScale();
		float radarSensorPos_X = (this.car.getRadarSensor_X() * graphicsScale);
		float radarSensorPos_Y = (this.car.getRadarSensor_Y() * graphicsScale);
		float maxDetectRangeScaled = (this.maximumDetectRange * graphicsScale);
		float sensorForward_X_start = radarSensorPos_X + this.carForwardVector_X;
		float sensorForward_Y_start = radarSensorPos_Y + this.carForwardVector_Y;
		float sensorForward_X_end = radarSensorPos_X + (this.carForwardVector_X * maxDetectRangeScaled);
		float sensorForward_Y_end = radarSensorPos_Y + (this.carForwardVector_Y * maxDetectRangeScaled);
		Line2D lin = new Line2D.Float(sensorForward_X_start, sensorForward_Y_start, sensorForward_X_end, sensorForward_Y_end);
		g2.draw(lin);
		// maxfovline1
		g2.setColor(Color.BLUE);
		float maxDetectAngleHalf = (float) Math.toRadians((180 - this.maximumDetectAngle) / 2f);
		float radarFOV_endX_1 = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(maxDetectAngleHalf) - (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(maxDetectAngleHalf));
		float radarFOV_endY_1 = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(maxDetectAngleHalf) + (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(maxDetectAngleHalf));
		Line2D fov_Line_1 = new Line2D.Float(sensorForward_X_start, sensorForward_Y_start, radarFOV_endX_1, radarFOV_endY_1);
		g2.draw(fov_Line_1);
		// maxfovline2
		float maxDetectAngleHalf_inTheOtherDirection = (float) Math.toRadians((180 - (180 - this.maximumDetectAngle) / 2f));
		float radarFOV_endX_2 = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(maxDetectAngleHalf_inTheOtherDirection) - (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(maxDetectAngleHalf_inTheOtherDirection));
		float radarFOV_endY_2 = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(maxDetectAngleHalf_inTheOtherDirection) + (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(maxDetectAngleHalf_inTheOtherDirection));
		Line2D fov_Line_2 = new Line2D.Float(sensorForward_X_start, sensorForward_Y_start, radarFOV_endX_2, radarFOV_endY_2);
		g2.draw(fov_Line_2);
		// tessalate the outer arc of the sensor the arc's lines start at radarFOV_endX_1 , radarFOV_endY_1
		float previousLineX = radarFOV_endX_1;
		float previousLineY = radarFOV_endY_1;
		float tessalationResolution = 12f;
		double tessalatedAngle = maxDetectAngleHalf;
		double step = (maxDetectAngleHalf_inTheOtherDirection - maxDetectAngleHalf) / tessalationResolution;
		for (int i = 0; i < tessalationResolution; i++) {
			tessalatedAngle += step;
			radarFOV_endX_1 = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(tessalatedAngle) - (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(tessalatedAngle));
			radarFOV_endY_1 = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(tessalatedAngle) + (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(tessalatedAngle));
			Line2D line = new Line2D.Float(previousLineX, previousLineY, radarFOV_endX_1, radarFOV_endY_1);
			g2.draw(line);
			previousLineX = radarFOV_endX_1;
			previousLineY = radarFOV_endY_1;
		}
		// non detect zone:
		maxDetectRangeScaled = (this.minimumDetectRange * graphicsScale);
		sensorForward_X_end = radarSensorPos_X + (this.carForwardVector_X * maxDetectRangeScaled);
		sensorForward_Y_end = radarSensorPos_Y + (this.carForwardVector_Y * maxDetectRangeScaled);
		// minfovline1
		g2.setColor(Color.BLACK);
		// initial point where the deadzone debug lines start
		float deadzone_radarFOV_X = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(maxDetectAngleHalf) - (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(maxDetectAngleHalf));
		float deadzone_radarFOV_Y = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(maxDetectAngleHalf) + (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(maxDetectAngleHalf));
		// tessalate the inner deadzone arc of the sensor
		previousLineX = deadzone_radarFOV_X;
		previousLineY = deadzone_radarFOV_Y;
		tessalationResolution = 6f;
		tessalatedAngle = maxDetectAngleHalf;
		step = (maxDetectAngleHalf_inTheOtherDirection - maxDetectAngleHalf) / tessalationResolution;
		for (int i = 0; i < tessalationResolution; i++) {
			tessalatedAngle += step;
			deadzone_radarFOV_X = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(tessalatedAngle) - (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(tessalatedAngle));
			deadzone_radarFOV_Y = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(tessalatedAngle) + (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(tessalatedAngle));
			Line2D line = new Line2D.Float(previousLineX, previousLineY, deadzone_radarFOV_X, deadzone_radarFOV_Y);
			g2.draw(line);
			previousLineX = deadzone_radarFOV_X;
			previousLineY = deadzone_radarFOV_Y;
		}
		// detected objects
		g2.setColor(Color.RED);
		for (int i = 0; i < this.detectedWorldObjects.size(); i++) {
			WorldObject actObj = this.detectedWorldObjects.get(i).parentWorldObject;
			float actobjSaledPos_X = actObj.getX() * graphicsScale;
			float actobjSaledPos_Y = actObj.getY() * graphicsScale;
			g2.draw(new Line2D.Float(sensorForward_X_start, sensorForward_Y_start, actobjSaledPos_X, actobjSaledPos_Y));
		}
	}

	public void update() {
		this.detectedWorldObjects.clear();
		this.movingWorldObjects.clear();
		for (int i = 0; i < WorldObjectParser.getInstance().getWorldObjects().size(); i++) {
			WorldObject actual_WorldObjet = WorldObjectParser.getInstance().getWorldObjects().get(i);
			if (isValid_WorldObject(actual_WorldObjet.getType())) {
				if (this.isWorldObject_Detected(actual_WorldObjet)) {
					RSensorDetectedObjectAttributes detectedObjWithAttributes = new RSensorDetectedObjectAttributes(actual_WorldObjet);
					this.detectedWorldObjects.add(detectedObjWithAttributes);
					this.previousWorldObjects.add(detectedObjWithAttributes);
				}
			}
		}
		for (int i = 0; i < this.detectedWorldObjects.size(); i++) {
			this.calculate_DetectedWorldObject_Attributes(this.detectedWorldObjects.get(i));
		}
		this.previousWorldObjects.clear();
	}

	private boolean isWorldObject_Detected(WorldObject object) {
		// sadly the swing coordinate system is not a normal one (the 0,0 is in the upper left) so hacks are needed (a 180degree offset and sin/cos swaps)
		// there are a number of better methods to calculate this but we are going with a simple triangle based method (vector math would be nicer)
		this.carForwardVector_X = (float) Math.sin(this.car.carHeading_Angle);
		this.carForwardVector_Y = (float) Math.cos(this.car.carHeading_Angle);
		// the first point of our FOV triangle is the position of the radar
		float radarSensorPos_X = (this.car.getRadarSensor_X());
		float radarSensorPos_Y = (this.car.getRadarSensor_Y());
		float dx = (radarSensorPos_X - object.getX()) * (radarSensorPos_X - object.getX());
		float dy = (radarSensorPos_Y - object.getY()) * (radarSensorPos_Y - object.getY());
		float distance = (float) Math.sqrt((double) (dx + dy));
		if (distance < this.maximumDetectRange && distance > this.minimumDetectRange) {
			float maxDetectRangeScaled = (this.maximumDetectRange * 2); // to make the triangle large enough, this hack works because before this test there is a distance test
			float sensorForward_X_end = radarSensorPos_X + (this.carForwardVector_X * maxDetectRangeScaled);
			float sensorForward_Y_end = radarSensorPos_Y + (this.carForwardVector_Y * maxDetectRangeScaled);
			float maxDetectAngleHalf = (float) Math.toRadians((180 - this.maximumDetectAngle) / 2f);
			// the second point of our FOV triangle is the endpoint of one of the fov's side
			float radarFOV_endX_1 = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(maxDetectAngleHalf) - (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(maxDetectAngleHalf));
			float radarFOV_endY_1 = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(maxDetectAngleHalf) + (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(maxDetectAngleHalf));
			float maxDetectAngleHalf_inTheOtherDirection = (float) Math.toRadians((180 - (180 - this.maximumDetectAngle) / 2f));
			// the third point of our FOV triangle is the endpoint of the fov's other side
			float radarFOV_endX_2 = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(maxDetectAngleHalf_inTheOtherDirection) - (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(maxDetectAngleHalf_inTheOtherDirection));
			float radarFOV_endY_2 = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(maxDetectAngleHalf_inTheOtherDirection) + (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(maxDetectAngleHalf_inTheOtherDirection));
			return pointInTriangle(object.getX(), object.getY(), radarSensorPos_X, radarSensorPos_Y, radarFOV_endX_1, radarFOV_endY_1, radarFOV_endX_2, radarFOV_endY_2);
		}
		return false;
	}

	private float sign(float p1X, float p1Y, float p2X, float p2Y, float p3X, float p3Y) {
		return (p1X - p3X) * (p2Y - p3Y) - (p2X - p3X) * (p1Y - p3Y);
	}

	private boolean pointInTriangle(float ptX, float ptY, float v1X, float v1Y, float v2X, float v2Y, float v3X, float v3Y) {
		boolean b1, b2, b3;
		b1 = sign(ptX, ptY, v1X, v1Y, v2X, v2Y) < 0.0f;
		b2 = sign(ptX, ptY, v2X, v2Y, v3X, v3Y) < 0.0f;
		b3 = sign(ptX, ptY, v3X, v3Y, v1X, v1Y) < 0.0f;
		return ((b1 == b2) && (b2 == b3));
	}

	private void calculate_DetectedWorldObject_Attributes(RSensorDetectedObjectAttributes objectWithAttributes) {
		// detect moving objects
		for (int i = 0; i < this.detectedWorldObjects.size(); i++) {
			for (int j = 0; j < this.previousWorldObjects.size(); j++) {
				WorldObject actObj = this.detectedWorldObjects.get(i).parentWorldObject;
				WorldObject prevObj = this.previousWorldObjects.get(j).parentWorldObject;
				if (actObj.equals(prevObj)) { // hashcode based compare, should implement ID in WorldObject
					if (actObj.getX() != prevObj.getX() || actObj.getY() != prevObj.getY()) {
						this.movingWorldObjects.add(this.detectedWorldObjects.get(i));
					}
				}
			}
		}
		// TODO: actually calculate remaining things
	}

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
