package com.unideb.bosch.instrumentclusterdisplay;

/**
 * This class implements the Signals ID table
 */

public class SignalDatabase {

	// Signal ID table
	public static final int INDICATOR = 3;
	public static final int GAS_PEDAL_POSITION = 4;
	public static final int BRAKE_PEDAL_POSITION = 5;
	public static final int STEERING_WHEEL_ANGLE = 6;
	public static final int GEAR_POSITION = 7;
	public static final int VEHICLE_SPEED = 8;
	public static final int MOTOR_RPM = 9;
	public static final int HEADLIGHT = 10;
	
	// Check signal boundaries and limit according to the CommMatrix
	public static int limit(int localData, int actData, int limit1, int limit2) {
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
}
