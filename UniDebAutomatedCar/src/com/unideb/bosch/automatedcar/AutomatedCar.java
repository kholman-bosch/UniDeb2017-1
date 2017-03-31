package com.unideb.bosch.automatedcar;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.vehicleparts.Driver;
import com.unideb.bosch.automatedcar.vehicleparts.PowertrainSystem;
import com.unideb.bosch.humanmachineinterface.HumanMachineInterface;
import com.unideb.bosch.instrumentclusterdisplay.InstrumentClusterLogic;
import com.unideb.bosch.instrumentclusterdisplay.VirtualDisplay_Invoker;

public final class AutomatedCar {

	private float carPos_X = 2500f;
	private float carPos_Y = 2000f;
	private PowertrainSystem powertrainSystem;
	private BufferedImage carImage, scaledCarImage;
	private Rectangle carImageRectange;
	public float carSpeed = 0f;
	private float steerAngle = 0f;
	public float carHeading_Angle = 0f;
	public final float wheelBase = 160f;
	private float previousGraphicsScale = 1f;
	// values for interpolation
	private float previousCarPos_X = 2500f;
	private float previousCarPos_Y = 2000f;

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
		// Place a driver into our car
		new Driver();
		new VirtualDisplay_Invoker(this, new InstrumentClusterLogic(this.powertrainSystem));
		new HumanMachineInterface(); // I don't know we need this. HMI branch has it so I just leave it here for now.
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
		// handle interpolation of carPosition
		float carMidPointScaled_X = (this.carPos_X * graphicsScale * interpValue) + (this.previousCarPos_X * graphicsScale * oneMinusInterp);
		float carMidPointScaled_Y = (this.carPos_Y * graphicsScale * interpValue) + (this.previousCarPos_Y * graphicsScale * oneMinusInterp);
		// because the car is scaled by 2 so the half of it is the original
		int scaledCarImageWidth_Half = this.scaledCarImage.getWidth();
		int scaledCarImageHeight_Half = this.scaledCarImage.getHeight();
		// the /2 is needed because we artificially double the scaledCar's image for better pixel density
		int scaledCarPos_X = (int) (carMidPointScaled_X - this.scaledCarImage.getWidth() / 2f);
		int scaledCarPos_Y = (int) (carMidPointScaled_Y - this.scaledCarImage.getHeight() / 2f);
		this.carImageRectange.setLocation(scaledCarPos_X, scaledCarPos_Y);
		TexturePaint carPaint = new TexturePaint(this.scaledCarImage, this.carImageRectange);
		gMatrix_Car.setPaint(carPaint);
		gMatrix_Car.rotate(-this.carHeading_Angle, carMidPointScaled_X, carMidPointScaled_Y);
		gMatrix_Car.fillRect((int) scaledCarPos_X, (int) scaledCarPos_Y, scaledCarImageWidth_Half, scaledCarImageHeight_Half);
	}

	public void drive() {
		// Call components
		VirtualFunctionBus.cyclic();
		// Update the position and orientation of the car
		this.steerAngle = this.powertrainSystem.getSteeringWheelAngle();
		this.carSpeed = this.powertrainSystem.getCarSpeed();
		this.carPhysics();
		this.teleportCarIntoBounds();
	}

	private void carPhysics() {
		// need to save previous position for interpolation to work
		this.previousCarPos_X = this.carPos_X;
		this.previousCarPos_Y = this.carPos_Y;
		float frontWheel_X = this.carPos_X + (this.wheelBase / 2f) * (float) Math.sin(this.carHeading_Angle);
		float frontWheel_Y = this.carPos_Y + (this.wheelBase / 2f) * (float) Math.cos(this.carHeading_Angle);
		float backWheel_X = this.carPos_X - (this.wheelBase / 2f) * (float) Math.sin(this.carHeading_Angle);
		float backWheel_Y = this.carPos_Y - (this.wheelBase / 2f) * (float) Math.cos(this.carHeading_Angle);
		frontWheel_X += this.carSpeed * (float) Math.sin(this.carHeading_Angle + Math.toRadians(this.steerAngle));
		frontWheel_Y += this.carSpeed * (float) Math.cos(this.carHeading_Angle + Math.toRadians(this.steerAngle));
		backWheel_X += this.carSpeed * (float) Math.sin(this.carHeading_Angle);
		backWheel_Y += this.carSpeed * (float) Math.cos(this.carHeading_Angle);
		this.carPos_X = (frontWheel_X + backWheel_X) / 2f;
		this.carPos_Y = (frontWheel_Y + backWheel_Y) / 2f;
		this.carHeading_Angle = (float) Math.atan2(frontWheel_X - backWheel_X, frontWheel_Y - backWheel_Y);
	}

	private void teleportCarIntoBounds() {
		if (this.carPos_X < 0) {
			this.carPos_X = 5000;
		}
		if (this.carPos_X > 5000) {
			this.carPos_X = 0;
		}
		if (this.carPos_Y < 0) {
			this.carPos_Y = 4000;
		}
		if (this.carPos_Y > 4000) {
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
