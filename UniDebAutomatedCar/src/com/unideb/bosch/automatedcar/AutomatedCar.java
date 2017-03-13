package com.unideb.bosch.automatedcar;

import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.vehicleparts.Driver;
import com.unideb.bosch.automatedcar.vehicleparts.PowertrainSystem;
import com.unideb.bosch.instrumentclusterdisplay.VirtualDisplay_Invoker;

public final class AutomatedCar {

	private int x = 100;
	private int y = 100;
	private double angle = 0;
	private PowertrainSystem powertrainSystem;
	
	public AutomatedCar() {
		// Compose our car from brand new system components
		// The car has to know its PowertrainSystem, to get its coordinates
		powertrainSystem = new PowertrainSystem();
		// The rest of the components use the VirtualFunctionBus to communicate,
		// they do not communicate with the car itself
		
		// Place a driver into our car
		new Driver();
		new VirtualDisplay_Invoker(this);
	}

	public void drive() {	
		// Call components
		VirtualFunctionBus.cyclic();
		// Update the position and orientation of the car
		x = powertrainSystem.getPositionX();
		y = powertrainSystem.getPositionY();
		angle = powertrainSystem.getAngle();	
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
