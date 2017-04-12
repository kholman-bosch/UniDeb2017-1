package com.unideb.bosch.instrumentclusterdisplay;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

/**
 * This class implements the InstrumentClusterLogic System
 */

public class InstrumentClusterLogic extends SystemComponent {

	private int data_vehicle_speed = 0;
	private int data_motor_rpm = 0;
	private int data_headlight = 0;
	private int data_turn_signal = 0;
	private int data_steering_wheel_angle = 0;
	private int data_gear_position = 3;
	//
	private int turn_signal_tick = 0;
	private boolean turn_signal_left = false, turn_signal_right = false;

	public InstrumentClusterLogic() {
		super();
	}

	@Override
	public void cyclic() {
		this.blinkingTurnSignals();
		// this.writeInTerminalInfos();
	}

	@Override
	public void receiveSignal(Signal s) {
		int actValue = (int) s.getData();
		switch (s.getID()) {
		case SignalDatabase.VEHICLE_SPEED:
			// 0 120 1 KM/H -
			this.data_vehicle_speed = Math.abs(actValue);
			break;
		case SignalDatabase.MOTOR_RPM:
			// 0 9000 1 RPM -
			this.data_motor_rpm = actValue;
			break;
		case SignalDatabase.POWERTRAIN_HEADLIGHT:
			// ON: 1
			// OFF: 0
			this.data_headlight = actValue;
			break;
		case SignalDatabase.POWERTRAIN_INDEX_INDICATORS:
			// NONE: 0
			// RIGHT: 1
			// LEFT: 2
			// EMERGENCY: 3
			this.data_turn_signal = actValue;
			break;
		case SignalDatabase.POWERTRAIN_GEAR_POSITION:
			// D: 0
			// N: 1
			// R: 2
			// P: 3
			this.data_gear_position = actValue;
			break;
		case SignalDatabase.POWERTRAIN_STEERING_WHEEL_ANGLE:
			// -720 720 1
			this.data_steering_wheel_angle = actValue;
			break;
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
		return this.data_headlight == 1;
	}

	public boolean getLeftTurnSignalStatus() {
		return this.turn_signal_left;
	}

	public boolean getRightTurnSignalStatus() {
		return this.turn_signal_right;
	}

	public boolean getEmergencySignalStatus() {
		return this.turn_signal_left && this.turn_signal_right;
	}

	// Blinking the Turn Signals on the Instrument Cluster and
	private void blinkingTurnSignals() {
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
		} else {
			this.turn_signal_left = false;
			this.turn_signal_right = false;
		}
	}

	// Write in the Terminal the actual status about the Signals
	private void writeInTerminalInfos() {
		System.out.println("---INSTRUMENT CLUSTER CYCLE---");
		System.out.println("Left/Right Turn Signals: " + this.turn_signal_left + " " + this.turn_signal_right + " data: " + this.data_turn_signal);
		System.out.println("----------------------");
	}
}
