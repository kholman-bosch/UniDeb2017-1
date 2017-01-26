package com.unideb.bosch.automatedcar;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.vehicleparts.Driver;
import com.unideb.bosch.automatedcar.vehicleparts.PowertrainSystem;

public final class AutomatedCar {

	private int x = 100;
	private int y = 100;
	private int angle = 0;
	
	public AutomatedCar() {
		// Compose our car from brand new system components
		new PowertrainSystem(this);
		// Place a driver into our car
		new Driver();
	}

	public void setPosition(int positionX, int positionY, int ang) {
		x = positionX;
		y = positionY;
		angle = ang;
	}
	
	public void drive() {	
		VirtualFunctionBus.cyclic();
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public int getAngle() {
		return angle;
	}
}
