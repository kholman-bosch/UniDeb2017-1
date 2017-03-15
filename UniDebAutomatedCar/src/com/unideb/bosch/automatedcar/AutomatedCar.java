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
	private BufferedImage carImage;
	private Rectangle carImageRectange;
	public float carSpeed = 0f;
	private float steerAngle = 0f;
	public float carHeading_Angle = 0f;
	public final float wheelBase = 160f;

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

	public void drawCar(Graphics g) {
		Graphics2D gMatrix_Car = (Graphics2D) g.create();
		float carMidPoint_X = this.carPos_X;
		float carMidPoint_Y = this.carPos_Y;
		int carImageWidth_Half = this.carImage.getWidth();
		// because the car is scaled by 2 so the half of it is the original
		int carImageHeight_Half = this.carImage.getHeight();
		this.carImageRectange.setLocation((int) this.carPos_X - carImageWidth_Half, (int) this.carPos_Y - carImageHeight_Half);
		TexturePaint carPaint = new TexturePaint(this.carImage, this.carImageRectange);
		gMatrix_Car.setPaint(carPaint);
		gMatrix_Car.rotate(-this.carHeading_Angle, carMidPoint_X, carMidPoint_Y);
		gMatrix_Car.fillRect((int) this.carPos_X - carImageWidth_Half, (int) this.carPos_Y - carImageHeight_Half,
				carImageWidth_Half * 2, carImageHeight_Half * 2);
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
		if(this.carPos_X < 0){
			this.carPos_X = 5000;
		}
		if(this.carPos_X > 5000){
			this.carPos_X = 0;
		}
		if(this.carPos_Y < 0){
			this.carPos_Y = 4000;
		}
		if(this.carPos_Y > 4000){
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
