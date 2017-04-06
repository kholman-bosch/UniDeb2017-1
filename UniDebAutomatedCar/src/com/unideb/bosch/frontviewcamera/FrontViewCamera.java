package com.unideb.bosch.frontviewcamera;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.automatedcar.AutomatedCar;
import com.unideb.bosch.automatedcar.VirtualWorld;
import com.unideb.bosch.automatedcar.WorldObjectParser;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.framework.WorldObject;

public class FrontViewCamera extends SystemComponent {

	private static final Logger LOGGER = LogManager.getLogger();
	
	private static final int VIEW_DISTANCE = 2000;
	private static final int VIEW_ANGLE = 40;
	private static final int MAX_ANGLE_OF_INCIDENCE = 40;
	private AutomatedCar car;
	
	private long carPosX;
	private long carPosY;
	private long carAngle;
	
	private boolean isWorldObjectDetected(WorldObject worldObject) {
		long x = worldObject.getX() - carPosX;
		long y = worldObject.getY() - carPosY;
		float distance = (float)Math.sqrt(x * x + y * y);
		if (distance > VIEW_DISTANCE) {
			System.out.println("isWorldObjectDetected--> object is too far");
			return false;
		}
		
		double direction = Math.toDegrees(Math.atan2(x, y)) + 180;
		long viewAngle = Math.abs((long)direction - carAngle);
		long tempViewAngle = 360 - viewAngle;
		if (tempViewAngle < viewAngle) {
			viewAngle = tempViewAngle;
		}
		
		if (viewAngle > VIEW_ANGLE / 2) {
			System.out.println("isWorldObjectDetected--> object is out of angle");
			return false;
		}
		
		long riverseDirectionOfTable = worldObject.getOrientation() + 180;
		if (riverseDirectionOfTable > 360) {
			riverseDirectionOfTable -= 360;
		}
		
		long angleOfIncidence = Math.abs(riverseDirectionOfTable - carAngle);
		long tempAngleOfIncidence = 360 - angleOfIncidence;
		if (tempAngleOfIncidence < angleOfIncidence) {
			angleOfIncidence = tempAngleOfIncidence;
		}
		
		if (angleOfIncidence > MAX_ANGLE_OF_INCIDENCE) {
			System.out.println("isWorldObjectDetected--> object is out of angle");
			return false;
		}
			
		return true;
	}
	
	private float distanceFromCar(WorldObject worldObject){
		long x = worldObject.getX() - carPosX;
		long y = worldObject.getY() - carPosY;
		return (float)Math.sqrt(x * x + y * y);
	}
	
	private List<RoadSign> detectedRoadSigns(List<WorldObject> worldObjects){
		List<RoadSign> detectedRoadSigns = new ArrayList<RoadSign>();
		for (WorldObject worldObject : worldObjects) {
			long longEGO = Math.abs(worldObject.getX() - carPosX);
			long latEGO = Math.abs(worldObject.getY() - carPosY);
			switch (worldObject.getType()) {
				case "road_sign_speed_5":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_5));
					}
					break;
				case "road_sign_speed_10":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_10));
					}
					break;
				case "road_sign_speed_20":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_20));
					}
					break;
				case "road_sign_speed_30":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_30));
					}
					break;
				case "road_sign_speed_40":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_40));
					}
					break;
				case "road_sign_speed_50":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_50));
					}
					break;
				case "road_sign_speed_60":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_60));
					}
					break;
				case "road_sign_speed_70":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_70));
					}
					break;
				case "road_sign_speed_80":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_80));
					}
					break;
				case "road_sign_speed_90":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_90));
					}
					break;
				case "road_sign_speed_100":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_100));
					}
					break;
				case "road_sign_speed_110":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_110));
					}
					break;
				case "road_sign_speed_120":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_120));
					}
					break;
				case "road_sign_speed_130":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_130));
					}
					break;
				case "road_sign_direction_leftonly":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_DIRECTION_LEFTONLY));
					}
					break;
				case "road_sign_direction_rightonly":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_DIRECTION_RIGHTONLY));
					}
					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 16));
//					}
//					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 17));
//					}
//					break;
				case "road_sign_direction_roundabout":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_DIRECTION_ROUNDABOUT));
					}
					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 19));
//					}
//					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 20));
//					}
//					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 21));
//					}
//					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 22));
//					}
//					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 23));
//					}
//					break;
				case "road_sign_priority_stop":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_PRIORITY_STOP));
					}
					break;
				case "road_sign_priority_mainroad":
					if (isWorldObjectDetected(worldObject)) {
						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_PRIORITY_MAINROAD));
					}
					break;
//				case "":
//					if (isWorldObjectDetected(worldObject)) {
//						detectedRoadSigns.add(new RoadSign(distanceFromCar(worldObject), longEGO, latEGO, 26));
//					}
//					break;
				default:
					break;
			}
		}
		
		return detectedRoadSigns;
	}
	
	@Override
	public void cyclic() {
		List<WorldObject> worldObjects = WorldObjectParser.getInstance().getWorldObjects();
		List<RoadSign> detectedRoadSigns = detectedRoadSigns(worldObjects);
		Collections.sort(detectedRoadSigns);
		int size = detectedRoadSigns.size() > 5 ? 5 : detectedRoadSigns.size();
		for (int i = 0; i < size; i++) {
			VirtualFunctionBus.sendSignal(new Signal(12, detectedRoadSigns.get(i).getLongitudinalEGO()));
			VirtualFunctionBus.sendSignal(new Signal(14, detectedRoadSigns.get(i).getLateralEGO()));
			VirtualFunctionBus.sendSignal(new Signal(16, detectedRoadSigns.get(i).getTrafficSignMeaing()));
		}
	}
	
	@Override
	public void receiveSignal(Signal s) {
		switch (s.getID()) {
			case 0:
				carPosX = s.getData();
				break;
			case 1:
				carPosY = s.getData();
				break;
			case 2:
				carAngle = s.getData();
				break;
			default:
				break;
		}
	}

}