package com.unideb.bosch.automatedcar.vehicleparts;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

/**
 * This class implements the Powertrain System The responsible for this file is
 * TeamA
 */
public class PowertrainSystem extends SystemComponent {

	// Signal ID table for Powertrain System
	public static final int GAS_PEDAL_POSITION = 4;
	public static final int BRAKE_PEDAL_POSITION = 5;
	public static final int STEERING_WHEEL_ANGLE = 6;
	public static final int GEAR_POSITION = 7;
	//
	private int data_gas_pedal_position = 0;
	private int data_brake_pedal_position = 0;
	private int data_steering_wheel_angle = 0;
	private int data_gear_position = 3;
	// Only these are available trough getters
	public int positionX = 200;
	public int positionY = 200;
	private double angle = 0;

	public PowertrainSystem() {
		super();
	}

	@Override
	public void cyclic() {
		this.writeInTerminalInfos();
	}

	@Override
	public void receiveSignal(Signal s) {
		int actValue = (int) s.getData();
		switch (s.getID()) {
		case GAS_PEDAL_POSITION:
			// 0 100 1 % -
			this.data_gas_pedal_position += this.limit(this.data_gas_pedal_position, actValue, 0, 100);
			break;
		case BRAKE_PEDAL_POSITION:
			// 0 100 1 % -
			this.data_brake_pedal_position += this.limit(this.data_brake_pedal_position, actValue, 0, 100);
			break;
		case STEERING_WHEEL_ANGLE:
			// -720 720 1 ° -
			this.data_steering_wheel_angle += this.limit(this.data_steering_wheel_angle, actValue, -720, 720);
			break;
		case GEAR_POSITION:
			// D: 0
			// N: 1
			// R: 2
			// P: 3
			this.data_gear_position = this.limit(this.data_gear_position, actValue, 0, 3);
			break;
		}
	}

	// Check signal boundaries and limit according to the CommMatrix
	private int limit(int localData, int actData, int limit1, int limit2) {
		if (localData != actData) {
			localData = actData;
			if (localData < limit1) {
				localData = limit1;
			} else if (localData > limit2) {
				localData = limit2;
			}
		}
		return localData;
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
	
	public int getSteeringWheelAngle() {
		return this.data_steering_wheel_angle;
	}

	// Write in the Terminal the actual status about the Signals
	private void writeInTerminalInfos() {
		System.out.println("---POWERTRAINSYSTEM---");
		System.out.println("GAS_PEDAL_POSITION data: " + this.data_gas_pedal_position);
		System.out.println("BRAKE_PEDAL_POSITION data: " + this.data_brake_pedal_position);
		System.out.println("STEERING_WHEEL_ANGLE data: " + this.data_steering_wheel_angle);
		switch (this.data_gear_position) {
		case 0:
			System.out.println("GEAR_POSITION: D data: " + this.data_gear_position);
			break;
		case 1:
			System.out.println("GEAR_POSITION: N data: " + this.data_gear_position);
			break;
		case 2:
			System.out.println("GEAR_POSITION: R data: " + this.data_gear_position);
			break;
		case 3:
			System.out.println("GEAR_POSITION: P data: " + this.data_gear_position);
			break;
		}
		System.out.println("----------------------");
	}
}
