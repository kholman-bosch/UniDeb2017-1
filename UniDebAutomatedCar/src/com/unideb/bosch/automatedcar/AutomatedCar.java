package com.unideb.bosch.automatedcar;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.acc.AdaptiveCruiseControlModule;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.vehicleparts.PowertrainSystem;
import com.unideb.bosch.frontviewcamera.DetectedRoadSignCatcher;
import com.unideb.bosch.frontviewcamera.FrontViewCamera;
import com.unideb.bosch.humanmachineinterface.HumanMachineInterface;
import com.unideb.bosch.instrumentclusterdisplay.InstrumentClusterLogic;
import com.unideb.bosch.instrumentclusterdisplay.VirtualDisplay_Invoker;
import com.unideb.bosch.radarsensor.RSensor;
import com.unideb.bosch.traficsignrecognition.TSR_Logic;

public final class AutomatedCar {

	private float carPos_X = 200f;
	private float carPos_Y = 200f;
	private PowertrainSystem powertrainSystem;
	private BufferedImage carImage, scaledCarImage;
	private Rectangle carImageRectange;
	public float carSpeedInPixels = 0f;
	private float steerAngle = 0f;
	public float carHeading_Angle = 0f;
	public final float wheelBase = 160f;
	private float previousGraphicsScale = 1f;
	// values for interpolation
	private float previousCarPos_X = 200f;
	private float previousCarPos_Y = 200f;
	private float previousCarHeadingAngle = 0f;
	private boolean useNON_QuaternionAngleInterpolation = false;

	private FrontViewCamera frontViewCamera;
	private RSensor radarSensor;
	private TSR_Logic tsr;
	private AdaptiveCruiseControlModule accModule;

	public AutomatedCar() {
		try {
			this.carImage = ImageIO.read(new File("./world/bosch1.png"));
		} catch (IOException ex) {
			System.err.println(ex.getMessage() + " ImageIO.read! AutomatedCar");
		}
		this.carImageRectange = new Rectangle(0, 0, this.carImage.getWidth() * 2, this.carImage.getHeight() * 2);
		// Compose our car from brand new system components
		// The car has to know its PowertrainSystem, to get its coordinates
		this.powertrainSystem = new PowertrainSystem();
		// The rest of the components use the VirtualFunctionBus to communicate,
		// they do not communicate with the car itself
		new VirtualDisplay_Invoker(this, new InstrumentClusterLogic());
		new HumanMachineInterface(); // I don't know we need this. HMI branch has it so I just leave it here for now.
		this.frontViewCamera = new FrontViewCamera(this);
		new DetectedRoadSignCatcher();
		this.radarSensor = new RSensor(100, 500, 20, 85, 5);
		this.tsr = new TSR_Logic();
		this.accModule = new AdaptiveCruiseControlModule();
	}

	public void drawCar(Graphics g, float graphicsScale) {
		if (this.previousGraphicsScale != graphicsScale) {
			// rescale car image
			// the * 2 is needed to ensure decent pixel density for the car's scaledImage
			this.scaledCarImage = VirtualWorld.scale_WithAlpha(carImage, (int) (carImage.getWidth() * graphicsScale * 2f), (int) (carImage.getHeight() * graphicsScale * 2f));
			this.carImageRectange = new Rectangle(0, 0, this.scaledCarImage.getWidth(), this.scaledCarImage.getHeight());
			this.previousGraphicsScale = graphicsScale;
		}
		Graphics2D gMatrix_Car = (Graphics2D) g.create();
		float interpValue = VirtualWorld.getGraphicsInterpolationValue();
		float oneMinusInterp = 1.0f - interpValue;
		// handle interpolation of carPosition and headingAngle
		float carMidPointScaled_X = (this.carPos_X * graphicsScale * interpValue) + (this.previousCarPos_X * graphicsScale * oneMinusInterp);
		float carMidPointScaled_Y = (this.carPos_Y * graphicsScale * interpValue) + (this.previousCarPos_Y * graphicsScale * oneMinusInterp);
		float actualCarHeadingAngle;
		if (this.useNON_QuaternionAngleInterpolation) {
			if (((this.carHeading_Angle < 0f && this.previousCarHeadingAngle > 0f) || (this.carHeading_Angle > 0f && this.previousCarHeadingAngle < 0f))) {
				// shoud use quaternions so this small "hack" can be avoided. The interpolation between a positive and a negative number has to be thrown away.
				actualCarHeadingAngle = this.carHeading_Angle;
			} else {
				actualCarHeadingAngle = (this.carHeading_Angle * interpValue) + (this.previousCarHeadingAngle * oneMinusInterp);
			}
		} else {
			actualCarHeadingAngle = this.carHeading_Angle;
		}
		// because the car is scaled by 2 so the half of it is the original
		int scaledCarImageWidth_Half = this.scaledCarImage.getWidth();
		int scaledCarImageHeight_Half = this.scaledCarImage.getHeight();
		// the /2 is needed because we artificially double the scaledCar's image for better pixel density
		int scaledCarPos_X = (int) (carMidPointScaled_X - this.scaledCarImage.getWidth() / 2f);
		int scaledCarPos_Y = (int) (carMidPointScaled_Y - this.scaledCarImage.getHeight() / 2f);
		this.carImageRectange.setLocation(scaledCarPos_X, scaledCarPos_Y);
		TexturePaint carPaint = new TexturePaint(this.scaledCarImage, this.carImageRectange);
		gMatrix_Car.setPaint(carPaint);
		gMatrix_Car.rotate(-actualCarHeadingAngle, carMidPointScaled_X, carMidPointScaled_Y);
		gMatrix_Car.fillRect((int) scaledCarPos_X, (int) scaledCarPos_Y, scaledCarImageWidth_Half, scaledCarImageHeight_Half);
	}

	public void drive() {
		// Call components
		VirtualFunctionBus.cyclic();
		// Update the position and orientation of the car
		this.steerAngle = this.powertrainSystem.getSteeringWheelAngle();
		this.carSpeedInPixels = this.powertrainSystem.getCarSpeed_InPixels();
		this.carPhysics();
		this.teleportCarIntoBounds();
		// TODO somehow need to send float value instead of long
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.CAR_POSITION_X, this.carPos_X));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.CAR_POSITION_Y, this.carPos_Y));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.CAR_ANGLE, (float) Math.toDegrees(this.carHeading_Angle) + 180));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.RADAR_SENSOR_POS_X, (this.carPos_X + (this.wheelBase / 2f) * (float) Math.sin(this.carHeading_Angle))));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.RADAR_SENSOR_POS_Y, (this.carPos_Y + (this.wheelBase / 2f) * (float) Math.cos(this.carHeading_Angle))));
	}

	private void carPhysics() {
		// need to save previous position for interpolation to work
		this.previousCarPos_X = this.carPos_X;
		this.previousCarPos_Y = this.carPos_Y;
		this.previousCarHeadingAngle = this.carHeading_Angle;
		float frontWheel_X = this.carPos_X + (this.wheelBase / 2f) * (float) Math.sin(this.carHeading_Angle);
		float frontWheel_Y = this.carPos_Y + (this.wheelBase / 2f) * (float) Math.cos(this.carHeading_Angle);
		float backWheel_X = this.carPos_X - (this.wheelBase / 2f) * (float) Math.sin(this.carHeading_Angle);
		float backWheel_Y = this.carPos_Y - (this.wheelBase / 2f) * (float) Math.cos(this.carHeading_Angle);
		frontWheel_X += this.carSpeedInPixels * (float) Math.sin(this.carHeading_Angle + Math.toRadians(this.steerAngle));
		frontWheel_Y += this.carSpeedInPixels * (float) Math.cos(this.carHeading_Angle + Math.toRadians(this.steerAngle));
		backWheel_X += this.carSpeedInPixels * (float) Math.sin(this.carHeading_Angle);
		backWheel_Y += this.carSpeedInPixels * (float) Math.cos(this.carHeading_Angle);
		this.carPos_X = (frontWheel_X + backWheel_X) / 2f;
		this.carPos_Y = (frontWheel_Y + backWheel_Y) / 2f;
		this.carHeading_Angle = (float) Math.atan2(frontWheel_X - backWheel_X, frontWheel_Y - backWheel_Y);
	}

	public RSensor getRadarSensor() {
		return this.radarSensor;
	}

	public FrontViewCamera getFrontViewCamera() {
		return this.frontViewCamera;
	}

	public int getRadarSensor_X() {
		return (int) (this.carPos_X + (this.wheelBase / 2f) * (float) Math.sin(this.carHeading_Angle));
	}

	public int getRadarSensor_Y() {
		return (int) (this.carPos_Y + (this.wheelBase / 2f) * (float) Math.cos(this.carHeading_Angle));
	}

	// CAMERA
	public int getCamera_X() {
		return (int) (this.carPos_X + (this.wheelBase / 4f) * (float) Math.sin(this.carHeading_Angle));
	}

	public int getCamera_Y() {
		return (int) (this.carPos_Y + (this.wheelBase / 4f) * (float) Math.cos(this.carHeading_Angle));
	}

	private void teleportCarIntoBounds() {
		if (this.carPos_X < 0) {
			this.carPos_X = VirtualWorld.getWorldWidth();
		}
		if (this.carPos_X > VirtualWorld.getWorldWidth()) {
			this.carPos_X = 0;
		}
		if (this.carPos_Y < 0) {
			this.carPos_Y = VirtualWorld.getWorldHeight();
		}
		if (this.carPos_Y > VirtualWorld.getWorldHeight()) {
			this.carPos_Y = 0;
		}
	}

	public float getX() {
		return this.carPos_X;
	}

	public float getY() {
		return this.carPos_Y;
	}

	public float getAngle() {
		return this.steerAngle;
	}
}