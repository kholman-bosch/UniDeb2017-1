package com.unideb.bosch.frontviewcamera;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
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

	// The view distance is a constant for now, later it should be configurable
	private static final int VIEW_DISTANCE = 600;
	private static final int VIEW_ANGLE = 50;
	private static final int MAX_TOLERANCE_OF_ROADSIGN_ANGLE = 40;

	private AutomatedCar car;

	private ArrayList<RoadSign> detectedRoadSigns;
	private ArrayList<WorldObject> detectedWorldObjects;
	
	private long carPosX;
	private long carPosY;
	private long carAngle;

	private float carForwardVector_X;
	private float carForwardVector_Y;

	public FrontViewCamera(AutomatedCar car) {
		this.car = car;
	}

	private double angleBetween2Lines(Line2D line1, Line2D line2) {
		double angle1 = Math.atan2(line1.getY1() - line1.getY2(), line1.getX1() - line1.getX2());
		double angle2 = Math.atan2(line2.getY1() - line2.getY2(), line2.getX1() - line2.getX2());
		return angle1 - angle2;
	}

	private boolean isRoadSignDetected(WorldObject worldObject) {

		float graphicsScale = VirtualWorld.getGraphicsScale();

		// the road sign's distance from the camera
		double distance = distanceFromCamera(worldObject);

		if (distance > ((double) VIEW_DISTANCE * graphicsScale)) {
			System.out.println(worldObject.getType() + " distance: " + distance + " is TOO FAR");
			return false;
		}

		// TODO should be received from bus!!
		double carHeadingAngle = Math.toDegrees(this.car.carHeading_Angle);
		// int roadSignAngle = worldObject.getOrientation();

		// TODO should be calculated from carX and carY which received from bus
		float cameraPos_X = (this.car.getCamera_X() * graphicsScale);
		float cameraPos_Y = (this.car.getCamera_Y() * graphicsScale);

		float maxDetectRangeScaled = (this.VIEW_DISTANCE * graphicsScale);

		float cameraForward_X_start = cameraPos_X + this.carForwardVector_X;
		float cameraForward_Y_start = cameraPos_Y + this.carForwardVector_Y;
		float cameraForward_X_end = cameraPos_X + (this.carForwardVector_X * maxDetectRangeScaled);
		float cameraForward_Y_end = cameraPos_Y + (this.carForwardVector_Y * maxDetectRangeScaled);

		Line2D camera_maxDist = new Line2D.Float(cameraForward_X_start, cameraForward_Y_start, cameraForward_X_end,
				cameraForward_Y_end);

		// world object position
		float woldObjectPos_X = worldObject.getX() * graphicsScale;
		float woldObjectPos_Y = worldObject.getY() * graphicsScale;

		Line2D camera_worldObj = new Line2D.Float(cameraForward_X_start, cameraForward_Y_start, woldObjectPos_X,
				woldObjectPos_Y);

		double angleBetween2Lines = angleBetween2Lines(camera_maxDist, camera_worldObj);
		System.out.println(worldObject.getType() + " seeing angle: " + Math.toDegrees(angleBetween2Lines));

		if ((int)Math.abs(Math.toDegrees(angleBetween2Lines)) > (MAX_TOLERANCE_OF_ROADSIGN_ANGLE / 2)) {
			System.out.println(worldObject.getType() + " IS OUT OF ANGLE");
			return false;
		}

		System.out.println(worldObject.getType() + " IS DETECTED######################");
		detectedWorldObjects.add(worldObject);

		return true;
	}

	public void connectCameraWithRoadSigns(Graphics2D g2) {

		float graphicsScale = VirtualWorld.getGraphicsScale();
		g2.setStroke(new BasicStroke(2));

		// TODO should be calculated from carX and carY which received from bus
		float cameraPos_X = (this.car.getCamera_X() * graphicsScale);
		float cameraPos_Y = (this.car.getCamera_Y() * graphicsScale);

//		List<WorldObject> worldObjects = WorldObjectParser.getInstance().getWorldObjects();

		for (WorldObject worldObject : detectedWorldObjects) {

			if (worldObject.getType().startsWith("road_sign")) {
				float cameraForward_X_start = cameraPos_X + this.carForwardVector_X;
				float cameraForward_Y_start = cameraPos_Y + this.carForwardVector_Y;

				float worldObject_X = worldObject.getX() * graphicsScale;
				float worldObject_Y = worldObject.getY() * graphicsScale;

				double distanceFromCamera = distanceFromCamera(worldObject);

				if (distanceFromCamera > ((double) VIEW_DISTANCE * VirtualWorld.getGraphicsScale())) {
					g2.setColor(Color.RED);
				} else {
					g2.setColor(Color.MAGENTA);
				}

				Line2D lin = new Line2D.Float(cameraForward_X_start, cameraForward_Y_start, worldObject_X,
						worldObject_Y);
				g2.draw(lin);
			}
		}
	}

	private double distanceFromCamera(WorldObject worldObject) {
		// calculate the distance between the object and the camera

		// camera position
		float graphicsScale = VirtualWorld.getGraphicsScale();
		// TODO should be calculated from carX and carY which received from bus
		float cameraPos_X = (this.car.getCamera_X() * graphicsScale);
		float cameraPos_Y = (this.car.getCamera_Y() * graphicsScale);

		// world object position
		float woldObjectPos_X = worldObject.getX() * graphicsScale;
		float woldObjectPos_Y = worldObject.getY() * graphicsScale;

		double distance = Point2D.distance(cameraPos_X, cameraPos_Y, woldObjectPos_X, woldObjectPos_Y);
		return distance;
	}

	private void detectRoadSigns(List<WorldObject> worldObjects) {
		detectedRoadSigns = new ArrayList<RoadSign>();
		detectedWorldObjects = new ArrayList<>();

		for (WorldObject worldObject : worldObjects) {
			float scaledWorldObject_X = worldObject.getX() * VirtualWorld.getGraphicsScale();
			float scaledWorldObject_Y = worldObject.getY() * VirtualWorld.getGraphicsScale();

			float scaledCarPos_X = carPosX * VirtualWorld.getGraphicsScale();
			float scaledCarPos_Y = carPosY * VirtualWorld.getGraphicsScale();

			float longEGO = Math.abs(scaledWorldObject_X - scaledCarPos_X);
			float latEGO = Math.abs(scaledWorldObject_Y - scaledCarPos_Y);

			switch (worldObject.getType()) {
			case "road_sign_speed_5":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(
							new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO, RoadSign.ROAD_SIGN_SPEED_5));
				}
				break;
			case "road_sign_speed_10":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_10));
				}
				break;
			case "road_sign_speed_20":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_20));
				}
				break;
			case "road_sign_speed_30":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_30));
				}
				break;
			case "road_sign_speed_40":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_40));
				}
				break;
			case "road_sign_speed_50":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_50));
				}
				break;
			case "road_sign_speed_60":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_60));
				}
				break;
			case "road_sign_speed_70":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_70));
				}
				break;
			case "road_sign_speed_80":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_80));
				}
				break;
			case "road_sign_speed_90":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_90));
				}
				break;
			case "road_sign_speed_100":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_100));
				}
				break;
			case "road_sign_speed_110":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_110));
				}
				break;
			case "road_sign_speed_120":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_120));
				}
				break;
			case "road_sign_speed_130":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_SPEED_130));
				}
				break;
			case "road_sign_direction_leftonly":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_DIRECTION_LEFTONLY));
				}
				break;
			case "road_sign_direction_rightonly":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_DIRECTION_RIGHTONLY));
				}
				break;
			case "road_sign_direction_roundabout":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_DIRECTION_ROUNDABOUT));
				}
				break;
			case "road_sign_priority_stop":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_PRIORITY_STOP));
				}
				break;
			case "road_sign_priority_mainroad":
				if (isRoadSignDetected(worldObject)) {
					detectedRoadSigns.add(new RoadSign(distanceFromCamera(worldObject), longEGO, latEGO,
							RoadSign.ROAD_SIGN_PRIORITY_MAINROAD));
				}
				break;
			default:
				break;
			}
		}

	}

	@Override
	public void cyclic() {
		// TODO should be received from bus
		this.carForwardVector_X = (float) Math.sin(this.car.carHeading_Angle);
		this.carForwardVector_Y = (float) Math.cos(this.car.carHeading_Angle);

		List<WorldObject> worldObjects = WorldObjectParser.getInstance().getWorldObjects();

		detectRoadSigns(worldObjects);
		Collections.sort(detectedRoadSigns);
		int size = detectedRoadSigns.size() > 5 ? 5 : detectedRoadSigns.size();
		for (int i = 0; i < size; i++) {
			VirtualFunctionBus.sendSignal(new Signal(12, (long)detectedRoadSigns.get(i).getLongitudinalEGO()));
			VirtualFunctionBus.sendSignal(new Signal(14, (long)detectedRoadSigns.get(i).getLateralEGO()));
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

	private void drawCarCenter(Graphics2D g2) {
		float graphicsScale = VirtualWorld.getGraphicsScale();

		g2.setColor(Color.GREEN);
		g2.setStroke(new BasicStroke(2));

		// TODO should be received from bus
		float car_X = this.car.getX() * graphicsScale;
		float car_Y = this.car.getY() * graphicsScale;

		Rectangle2D.Double rect = new Rectangle2D.Double(car_X - 1.0, car_Y - 1.0, 2, 2);
		g2.draw(rect);
	}

	private void drawStraightLineFromCameraToViewDistance(Graphics2D g2) {
		float graphicsScale = VirtualWorld.getGraphicsScale();
		g2.setColor(Color.RED);
		g2.setStroke(new BasicStroke(2));

		// TODO should be calculated from carX and carY which received from bus
		float cameraPos_X = (this.car.getCamera_X() * graphicsScale);
		float cameraPos_Y = (this.car.getCamera_Y() * graphicsScale);

		float maxDetectRangeScaled = (this.VIEW_DISTANCE * graphicsScale);

		float cameraForward_X_start = cameraPos_X + this.carForwardVector_X;
		float cameraForward_Y_start = cameraPos_Y + this.carForwardVector_Y;
		float cameraForward_X_end = cameraPos_X + (this.carForwardVector_X * maxDetectRangeScaled);
		float cameraForward_Y_end = cameraPos_Y + (this.carForwardVector_Y * maxDetectRangeScaled);

		Line2D lin = new Line2D.Float(cameraForward_X_start, cameraForward_Y_start, cameraForward_X_end,
				cameraForward_Y_end);
		g2.draw(lin);
	}

	public void draw_DebugData(Graphics2D g2) {
		drawCarCenter(g2);
		drawStraightLineFromCameraToViewDistance(g2);

		float graphicsScale = VirtualWorld.getGraphicsScale();
		// TODO should be calculated from carX and carY which received from bus
		float cameraPos_X = (this.car.getCamera_X() * graphicsScale);
		float cameraPos_Y = (this.car.getCamera_Y() * graphicsScale);
		float cameraForward_X_start = cameraPos_X + this.carForwardVector_X;
		float cameraForward_Y_start = cameraPos_Y + this.carForwardVector_Y;
		float maxDetectRangeScaled = (this.VIEW_DISTANCE * graphicsScale);
		float cameraForward_X_end = cameraPos_X + (this.carForwardVector_X * maxDetectRangeScaled);
		float cameraForward_Y_end = cameraPos_Y + (this.carForwardVector_Y * maxDetectRangeScaled);
		// maxfovline1
		g2.setColor(Color.YELLOW);
		float maxDetectAngleHalf = (float) Math.toRadians((180 - this.VIEW_ANGLE) / 2f);
		float cameraFOV_endX_1 = (float) (cameraPos_X
				+ (cameraForward_X_end - cameraPos_X) * Math.sin(maxDetectAngleHalf)
				- (cameraForward_Y_end - cameraPos_Y) * Math.cos(maxDetectAngleHalf));
		float cameraFOV_endY_1 = (float) (cameraPos_Y
				+ (cameraForward_X_end - cameraPos_X) * Math.cos(maxDetectAngleHalf)
				+ (cameraForward_Y_end - cameraPos_Y) * Math.sin(maxDetectAngleHalf));
		Line2D fov_Line_1 = new Line2D.Float(cameraForward_X_start, cameraForward_Y_start, cameraFOV_endX_1,
				cameraFOV_endY_1);
		g2.draw(fov_Line_1);

		// maxfovline2
		float maxDetectAngleHalf_inTheOtherDirection = (float) Math.toRadians((180 - (180 - this.VIEW_ANGLE) / 2f));
		float radarFOV_endX_2 = (float) (cameraPos_X
				+ (cameraForward_X_end - cameraPos_X) * Math.sin(maxDetectAngleHalf_inTheOtherDirection)
				- (cameraForward_Y_end - cameraPos_Y) * Math.cos(maxDetectAngleHalf_inTheOtherDirection));
		float radarFOV_endY_2 = (float) (cameraPos_Y
				+ (cameraForward_X_end - cameraPos_X) * Math.cos(maxDetectAngleHalf_inTheOtherDirection)
				+ (cameraForward_Y_end - cameraPos_Y) * Math.sin(maxDetectAngleHalf_inTheOtherDirection));
		Line2D fov_Line_2 = new Line2D.Float(cameraForward_X_start, cameraForward_Y_start, radarFOV_endX_2,
				radarFOV_endY_2);
		g2.draw(fov_Line_2);

		float previousLineX = cameraFOV_endX_1;
		float previousLineY = cameraFOV_endY_1;
		float tessalationResolution = 12f;
		double tessalatedAngle = maxDetectAngleHalf;
		double step = (maxDetectAngleHalf_inTheOtherDirection - maxDetectAngleHalf) / tessalationResolution;
		for (int i = 0; i < tessalationResolution; i++) {
			tessalatedAngle += step;
			cameraFOV_endX_1 = (float) (cameraPos_X + (cameraForward_X_end - cameraPos_X) * Math.sin(tessalatedAngle)
					- (cameraForward_Y_end - cameraPos_Y) * Math.cos(tessalatedAngle));
			cameraFOV_endY_1 = (float) (cameraPos_Y + (cameraForward_X_end - cameraPos_X) * Math.cos(tessalatedAngle)
					+ (cameraForward_Y_end - cameraPos_Y) * Math.sin(tessalatedAngle));
			Line2D line = new Line2D.Float(previousLineX, previousLineY, cameraFOV_endX_1, cameraFOV_endY_1);
			g2.draw(line);
			previousLineX = cameraFOV_endX_1;
			previousLineY = cameraFOV_endY_1;
		}
	}
}