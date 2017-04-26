package com.unideb.bosch;

/**
 * This class implements the Signals ID table
 */

public class SignalDatabase {

	// Signal ID table
	public static final int CAR_POSITION_X = 0;
	public static final int CAR_POSITION_Y = 1;
	public static final int CAR_ANGLE = 2;
	public static final int INDICATOR = 3;
	public static final int GAS_PEDAL_POSITION = 4;
	public static final int BRAKE_PEDAL_POSITION = 5;
	public static final int STEERING_WHEEL_ANGLE = 6;
	public static final int GEAR_POSITION = 7;
	public static final int VEHICLE_SPEED = 8;
	public static final int MOTOR_RPM = 9;
	public static final int HEADLIGHT = 10;
	public static final int RADAR_LONGITUDINAL_RELATIVE_VELOCITY = 11;
	public static final int RADAR_LONGITUDINAL_DISTANCE_FROM_EGO = 12;
	public static final int RADAR_LATERAL_RELATIVE_VELOCITY = 13;
	public static final int RADAR_LATERAL_DISTANCE_FROM_EGO = 14;
	public static final int OBJECT_SIZE = 15;
	public static final int TRAFFIC_SIGN_MEANING = 16;
	public static final int TRAFFIC_SIGN_LATERAL_DISTANCE_FROM_EGO = 17;
	public static final int TRAFFIC_SIGN_LONGITUDINAL_DISTANCE_FROM_EGO = 18;
	public static final int RADAR_SENSOR_POS_X = 19;
	public static final int RADAR_SENSOR_POS_Y = 20;
	public static final int MOST_RELEVANT_SPEED_LIMIT = 21;
	public static final int SHOW_SUPPLEMENTAL_SIGNS_ON_IC = 22;
	public static final int POWERTRAIN_STEERING_WHEEL_ANGLE = 23;
	public static final int POWERTRAIN_GEAR_POSITION = 24;
	public static final int POWERTRAIN_HEADLIGHT = 25;
	public static final int POWERTRAIN_INDEX_INDICATORS = 26;
	public static final int TSR_MODULE_STATUS = 27;
	public static final int DONT_SHOW_SUPPLEMENTAL_SIGNS_ON_IC = 28;
	// ACC signals goes here after merge into master branch

	public static final int ACC_CURRENT_CRUISE_CONTROL_SPEED = 32;
	public static final int ACC_CURRENT_SAFE_DISTANCE = 33;

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
