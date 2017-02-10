package com.unideb.bosch.automatedcar.vehicleparts;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

/**
 * This class implements the Powertrain System
 * The responsible for this file is TeamA
 */
public class PowertrainSystem extends SystemComponent{
	
	// Signal ID table for Powertrain System
	private static final int GAS_ID = 0;
	private static final int WHEEL_ANGLE_ID = 1;

	// Input signals
	private long gas = 0;

	// Output signals
	// Only these are available trough getters
	private int positionX = 200;
	private int positionY = 200;
	private double angle = 0;
	
	public PowertrainSystem() {
		super();
	}
	
	@Override
	public void cyclic() {
		if(gas != 0)
		{
		    positionY++;
		}
	}

	@Override
	public void receiveSignal(Signal s) {
		switch(s.getID()) {
		
		// Handle gas pedal position
		case GAS_ID:
			// Save the signal value
			gas = s.getData();
			// Check signal boundaries and limit according to the CommMatrix
			if(gas < 0) {
				gas = 0;
			} else if (gas > 100) {
				gas = 100;
			}
			System.out.println("Received gas position: " + gas + "%");
		break;
			
			
		// Handle wheel angle
		case WHEEL_ANGLE_ID:
			// Save the signal value
			angle = s.getData();
			// Apply scaling according to the CommMatrix
			angle /= 10;			
			// Check signal boundaries and limit according to the CommMatrix
			if(gas < -60) {
				angle = -60;
			} else if (gas > 60) {
				gas = 60;
			}
			
		default:
		// Ignore other signals
				
		}
	}
	
	public int getPositionX() {
		return positionX;
	}
	
	public int getPositionY() {
		return positionY;
	}
	
	public double getAngle() {
		return angle;
	}
 
}
