package com.unideb.bosch.radarsensor;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.VirtualWorld;
import com.unideb.bosch.automatedcar.WorldObjectParser;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.framework.WorldObject;

public class RSensor extends SystemComponent { // radar sensor

	public int radarPos_X = 0;
	public int radarPos_Y = 0;
	private int car_Pos_X = 0;
	private int car_Pos_Y = 0;
	private float carHeadingAngle = 0f;
	//
	private int minimumDetectRange = 0;
	private int maximumDetectRange = 0;
	private int minimumDetectAngle = 0;
	private int maximumDetectAngle = 0;
	private int maximumDetectableObjs = 5;
	// default sensor configuration values
	private int defaultMinimumDetectRange = 20;
	private int defaultMinimumDetectAngle = 10;
	private int defaultMaximummDetectRange = 500;
	private int defaultMaximumDetectAngle = 85;
	//
	private ArrayList<RSensorDetectedObjectAttributes> detectedWorldObjects = new ArrayList<RSensorDetectedObjectAttributes>(64);
	private ArrayList<RSensorDetectedObjectAttributes> previousWorldObjects = new ArrayList<RSensorDetectedObjectAttributes>(64);
	//
	private float carForwardVector_X;
	private float carForwardVector_Y;
	//
	private final VirtualFunctionBus vfb;
	private boolean detected_Car_Or_Pedestrian = false;
	private int dangerPos_X = 0;
	private int dangerPos_Y = 0;

	public RSensor(int minDetectRange, int maxDetectRange, int minDetctAngle, int maxDetectAngle, int maxDetectableObjs_f, VirtualFunctionBus virtFuncBus) {
		super(virtFuncBus);
		this.vfb = virtFuncBus;
		this.minimumDetectRange = minDetectRange;
		this.maximumDetectRange = maxDetectRange;
		this.minimumDetectAngle = minDetctAngle;
		this.maximumDetectAngle = maxDetectAngle;
		this.maximumDetectableObjs = maxDetectableObjs_f;
		this.validate_Sensor_Configuration();
	}

	public void draw_DebugData(Graphics2D g2) {
		// sadly the swing coordinate system is not a normal one (the 0,0 is in the upper left) so hacks are needed (a 180degree offset and sin/cos swaps)
		g2.setColor(Color.CYAN);
		g2.setStroke(new BasicStroke(2));
		float graphicsScale = VirtualWorld.getGraphicsScale();
		float radarSensorPos_X = (this.radarPos_X * graphicsScale);
		float radarSensorPos_Y = (this.radarPos_Y * graphicsScale);
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
		this.detected_Car_Or_Pedestrian = false;
		for (int i = 0; i < WorldObjectParser.getWorldObjects().size(); i++) {
			WorldObject actual_WorldObjet = WorldObjectParser.getWorldObjects().get(i);
			if (isValid_WorldObject(actual_WorldObjet.getType())) {
				if (this.isWorldObject_Detected(actual_WorldObjet)) {
					RSensorDetectedObjectAttributes detectedObjWithAttributes = new RSensorDetectedObjectAttributes(actual_WorldObjet);
					this.detectedWorldObjects.add(detectedObjWithAttributes);
					if (isDetectedObj_Car_or_Pedestrian(actual_WorldObjet)) {
						this.detected_Car_Or_Pedestrian = true;
						this.dangerPos_X = actual_WorldObjet.getX();
						this.dangerPos_Y = actual_WorldObjet.getY();
					}
					this.previousWorldObjects.add(detectedObjWithAttributes);
				}
			}
		}
		for (int i = 0; i < this.detectedWorldObjects.size(); i++) {
			this.calculate_DetectedWorldObject_Attributes(this.detectedWorldObjects.get(i));
		}
		this.previousWorldObjects.clear();
		RSensorSignalSender.send_Radar_Sensor_Signals(this, this.vfb);
		if (this.detected_Car_Or_Pedestrian) {
			float radarSensorPos_X = this.radarPos_X;
			float radarSensorPos_Y = this.radarPos_Y;
			float dx = (radarSensorPos_X - this.dangerPos_X) * (radarSensorPos_X - this.dangerPos_X);
			float dy = (radarSensorPos_Y - this.dangerPos_Y) * (radarSensorPos_Y - this.dangerPos_Y);
			float distance = (float) Math.sqrt((double) (dx + dy));
			distance /= 7f;
			float brakeVal = 100f - distance;
			if (brakeVal > 50f) {
				brakeVal = 100f;
			}
			this.vfb.sendSignal(new Signal(SignalDatabase.RADAR_SENSOR_DANGER_DETECTED_EMERGENCY_BREAK, brakeVal));
		} else {
			this.vfb.sendSignal(new Signal(SignalDatabase.RADAR_SENSOR_DANGER_DETECTED_EMERGENCY_BREAK, 0));
		}
	}

	private boolean isDetectedObj_Car_or_Pedestrian(WorldObject actual_WorldObjet) {
		switch (actual_WorldObjet.getType()) {
		case "car":
		case "man":
			return true;
		default:
			return false;
		}
	}

	private boolean isWorldObject_Detected(WorldObject object) {
		// sadly the swing coordinate system is not a normal one (the 0,0 is in the upper left) so hacks are needed (a 180degree offset and sin/cos swaps)
		// there are a number of better methods to calculate this but we are going with a simple triangle based method (vector math would be nicer)
		this.carForwardVector_X = (float) Math.sin(this.carHeadingAngle);
		this.carForwardVector_Y = (float) Math.cos(this.carHeadingAngle);
		// the first point of our FOV triangle is the position of the radar
		float radarSensorPos_X = this.radarPos_X;
		float radarSensorPos_Y = this.radarPos_Y;
		float dx = (radarSensorPos_X - object.getX()) * (radarSensorPos_X - object.getX());
		float dy = (radarSensorPos_Y - object.getY()) * (radarSensorPos_Y - object.getY());
		float distance = (float) Math.sqrt((double) (dx + dy));
		distance -= object.getRadius();
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
			float radarFOV_endX_2 = (float) (radarSensorPos_X + (sensorForward_X_end - radarSensorPos_X) * Math.sin(maxDetectAngleHalf_inTheOtherDirection)
					- (sensorForward_Y_end - radarSensorPos_Y) * Math.cos(maxDetectAngleHalf_inTheOtherDirection));
			float radarFOV_endY_2 = (float) (radarSensorPos_Y + (sensorForward_X_end - radarSensorPos_X) * Math.cos(maxDetectAngleHalf_inTheOtherDirection)
					+ (sensorForward_Y_end - radarSensorPos_Y) * Math.sin(maxDetectAngleHalf_inTheOtherDirection));
			return pointInTriangle(object.getX(), object.getY(), radarSensorPos_X, radarSensorPos_Y, radarFOV_endX_1, radarFOV_endY_1, radarFOV_endX_2, radarFOV_endY_2);
		}
		return false;
	}

	private void calculate_DetectedWorldObject_Attributes(RSensorDetectedObjectAttributes objectWithAttributes) {
		// detect moving objects
		for (int i = 0; i < this.detectedWorldObjects.size(); i++) {
			for (int j = 0; j < this.previousWorldObjects.size(); j++) {
				WorldObject actObj = this.detectedWorldObjects.get(i).parentWorldObject;
				WorldObject prevObj = this.previousWorldObjects.get(j).parentWorldObject;
				if (actObj.equals(prevObj)) { // hashcode based compare, should implement ID in WorldObject
					if (actObj.getX() != prevObj.getX() || actObj.getY() != prevObj.getY()) {
						objectWithAttributes.longitudinalRelative_Velcity = actObj.getX() - prevObj.getX();
						objectWithAttributes.lateralRelative_Velcity = actObj.getY() - prevObj.getY();
						objectWithAttributes.longitudinalDistance_From_EGO = Math.abs(this.car_Pos_X - actObj.getX());
						objectWithAttributes.lateralDistance_From_EGO = Math.abs(this.car_Pos_Y - actObj.getY());
					}
				}
			}
		}
		// TODO: actually calculate remaining things
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

	public ArrayList<RSensorDetectedObjectAttributes> get_Detected_WorldObjects() {
		return this.detectedWorldObjects;
	}

	public int getMaxDetectableObjsNum() {
		return this.maximumDetectableObjs;
	}

	private boolean isValid_WorldObject(String object_Type) {
		if (object_Type == null) {
			System.err.println("NO OBJECT TYPE!!! " + this.getClass().getName());
		}
		switch (object_Type) {
		case "tree":
			return true;
		case "car":
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

	@Override
	public void cyclic() {
		this.update();
	}

	@Override
	public void receiveSignal(Signal s) {
		switch (s.getID()) {
		case SignalDatabase.CAR_POSITION_X:
			this.car_Pos_X = (int) s.getData();
			break;
		case SignalDatabase.CAR_POSITION_Y:
			this.car_Pos_Y = (int) s.getData();
			break;
		case SignalDatabase.RADAR_SENSOR_POS_X:
			this.radarPos_X = (int) s.getData();
			break;
		case SignalDatabase.RADAR_SENSOR_POS_Y:
			this.radarPos_Y = (int) s.getData();
			break;
		case SignalDatabase.CAR_ANGLE:
			this.carHeadingAngle = (float) Math.toRadians(s.getData() - 180);
			break;
		}
	}
}
