package com.unideb.bosch.automatedcar;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;

import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.vehicleparts.Driver;
import com.unideb.bosch.automatedcar.vehicleparts.PowertrainSystem;
import com.unideb.bosch.instrumentclusterdisplay.InstrumentClusterLogic;
import com.unideb.bosch.instrumentclusterdisplay.VirtualDisplay_Invoker;

public final class AutomatedCar {

	private int x = 100;
	private int y = 100;
	private double angle = 0;
	private PowertrainSystem powertrainSystem;
	private BufferedImage carImage;
	private Rectangle carImageRectange;
	private float carHeading = 0f;
	private float carSpeed = 2f;
	private float steerAngle = 33f;
	private float wheelBase = 160f; // the distance between the front and back

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
	}

	public void drawCar(Graphics g) {
		Graphics2D gMatrix_Car = (Graphics2D) g.create();
		int carMidPoint_X = this.x + this.carImage.getWidth();
		// only width and height since the car is scaled by a factor of 2
		int carMidPoint_Y = this.y + this.carImage.getHeight();
		this.carImageRectange.setLocation(this.x, this.y);
		TexturePaint carPaint = new TexturePaint(this.carImage, this.carImageRectange);
		gMatrix_Car.setPaint(carPaint);
		gMatrix_Car.rotate(this.carHeading, carMidPoint_X, carMidPoint_Y);
		gMatrix_Car.fillRect(this.x, this.y, this.carImage.getWidth() * 2, this.carImage.getHeight() * 2);
	}

	public void drive() {
		// Call components
		VirtualFunctionBus.cyclic();
		// Update the position and orientation of the car
		//this.x = powertrainSystem.getPositionX();
		//this.y = powertrainSystem.getPositionY();
		//this.angle = powertrainSystem.getAngle();
		//
		float frontWheel_X = (this.x + this.carImage.getWidth()) + ((wheelBase / 2f) * (float) (Math.cos(carHeading)));
		float frontWheel_Y = (this.y + this.carImage.getHeight()) + ((wheelBase / 2f) * (float) (Math.sin(carHeading)));
		float backWheel_X = (this.x + this.carImage.getWidth()) - ((wheelBase / 2f) * (float) (Math.cos(carHeading)));
		float backWheel_Y = (this.y + this.carImage.getHeight()) - ((wheelBase / 2f) * (float) (Math.sin(carHeading)));
		backWheel_X += (carSpeed * Math.cos(carHeading));
		backWheel_Y += (carSpeed * Math.sin(carHeading));
		frontWheel_X += (carSpeed * Math.cos(carHeading + steerAngle));
		frontWheel_Y += (carSpeed * Math.sin(carHeading + steerAngle));
		float newLocation_X = (frontWheel_X + backWheel_X) / 2f;
		float newLocation_Y = (frontWheel_Y + backWheel_Y) / 2f;
		this.powertrainSystem.positionX = (int) newLocation_X;
		this.powertrainSystem.positionY = (int) newLocation_Y;
		this.carHeading = (float) Math.atan2(frontWheel_Y - backWheel_Y, frontWheel_X - backWheel_X);
	}

	public int getX() {
		return x;
	}

	public int getY() {
		return y;
	}

	public double getAngle() {
		return angle;
	}
}
