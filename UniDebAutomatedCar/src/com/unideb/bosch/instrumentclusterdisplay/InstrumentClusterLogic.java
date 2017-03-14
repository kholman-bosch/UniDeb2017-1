package com.unideb.bosch.instrumentclusterdisplay;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

/**
 * This class implements the InstrumentClusterLogic System
 */

public class InstrumentClusterLogic extends SystemComponent {

	// Signal ID table for InstrumentClusterLogic System
	private static final int INDICATOR = 3;
	private static final int STEERING_WHEEL_ANGLE = 6;
	private static final int GEAR_POSITION = 7;
	private static final int VEHICLE_SPEED = 8;
	private static final int MOTOR_RPM = 9;
	private static final int HEADLIGHT = 10;
	//
	// Turn Signal values
	private int turn_signal_tick = 0;
	private int data_turn_signal = 0;
	private boolean turn_signal_left = false, turn_signal_right = false;
	private int data_steering_wheel_angle = 0;
	private int data_gear_position = 3;
	private int data_vehicle_speed = 0;
	private int data_motor_rpm = 0;
	private int data_headlight = 0;
	private boolean headlight_on_off = false;

	public InstrumentClusterLogic() {
		super();
	}

	@Override
	public void cyclic() {
		this.logicTurnSignals();
		this.logicHeadlight();
		this.writeInTerminalInfos();
	}

	@Override
	public void receiveSignal(Signal s) {
		int actValue = (int) s.getData();
		switch (s.getID()) {
		case INDICATOR:
			// NONE: 0
			// RIGHT: 1
			// LEFT: 2
			// EMERGENCY: 3
			this.data_turn_signal = this.limit(this.data_turn_signal, actValue, 0, 3);
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
		case VEHICLE_SPEED:
			// 0 120 1 KM/H -
			this.data_vehicle_speed = this.limit(this.data_vehicle_speed, actValue, 0, 120);
			break;
		case MOTOR_RPM:
			// 0 9000 1 RPM -
			this.data_motor_rpm = this.limit(this.data_motor_rpm, actValue, 0, 9000);
			break;
		case HEADLIGHT:
			// ON: 1
			// OFF: 0
			this.data_headlight = this.limit(this.data_headlight, actValue, 0, 1);
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

	// Set the actual status and
	private void logicHeadlight() {
		switch (this.data_headlight) {
		case 0:
			this.headlight_on_off = false;
			break;
		case 1:
			this.headlight_on_off = true;
			break;
		}
	}

	// Blinking the Turn Signals on the Instrument Cluster and
	private void logicTurnSignals() {
		if (this.data_turn_signal != 0) {
			this.turn_signal_tick++;
			if (this.turn_signal_tick <= 5) {
				switch (this.data_turn_signal) {
				case 1:
					this.turn_signal_left = false;
					this.turn_signal_right = true;
					break;
				case 2:
					this.turn_signal_left = true;
					this.turn_signal_right = false;
					break;
				case 3:
					this.turn_signal_left = true;
					this.turn_signal_right = true;
					break;
				}
			} else {
				this.turn_signal_left = false;
				this.turn_signal_right = false;
				if (this.turn_signal_tick > 10) {
					this.turn_signal_tick = 0;
				}
			}
		}
	}

	// get Values for the Instrument Cluster
	public int getVehicleSpeed() {
		return this.data_vehicle_speed;
	}

	public int getMotorRPM() {
		return this.data_motor_rpm;
	}

	public int getSteeringWheelAngle() {
		return this.data_steering_wheel_angle;
	}

	public boolean getGearPos_D_Status() {
		return this.data_gear_position == 0;
	}

	public boolean getGearPos_N_Status() {
		return this.data_gear_position == 1;
	}

	public boolean getGearPos_R_Status() {
		return this.data_gear_position == 2;
	}

	public boolean getGearPos_P_Status() {
		return this.data_gear_position == 3;
	}

	public boolean getHeadlightStatus() {
		return this.headlight_on_off;
	}

	public boolean getLeftTurnSignalStatus() {
		return this.turn_signal_left;
	}

	public boolean getRightTurnSignalStatus() {
		return this.turn_signal_right;
	}
	
	// Write in the Terminal the actual status about the Signals
	private void writeInTerminalInfos() {
		System.out.println("---INSTRUMENT CLUSTER CYCLE---");
		switch (this.data_gear_position) {
		case 0:
			System.out.println("Gear Position: D data: " + this.data_gear_position);
			break;
		case 1:
			System.out.println("Gear Position: N data: " + this.data_gear_position);
			break;
		case 2:
			System.out.println("Gear Position: R data: " + this.data_gear_position);
			break;
		case 3:
			System.out.println("Gear Position: P data: " + this.data_gear_position);
			break;
		}
		switch (this.data_headlight) {
		case 0:
			System.out.println("Headlight: OFF data: " + this.data_headlight);
			break;
		case 1:
			System.out.println("Headlight: ON data: " + this.data_headlight);
			break;
		}
		System.out.println("Left/Right Turn Signals: " + this.turn_signal_left + " " + this.turn_signal_right
				+ " data: " + this.data_turn_signal);
		System.out.println("Steering Wheel Angle data: " + this.data_steering_wheel_angle);
		System.out.println("----------------------");
	}
}
