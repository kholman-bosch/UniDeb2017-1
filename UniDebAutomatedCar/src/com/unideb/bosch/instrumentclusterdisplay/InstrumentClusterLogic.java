package com.unideb.bosch.instrumentclusterdisplay;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.vehicleparts.PowertrainSystem;

/**
 * This class implements the InstrumentClusterLogic System
 */

public class InstrumentClusterLogic extends SystemComponent {

	// Turn Signal values
	private int data_turn_signal = 0;
	private int data_vehicle_speed = 0;
	private int data_motor_rpm = 0;
	//
	private int turn_signal_tick = 0;
	private boolean turn_signal_left = false, turn_signal_right = false;
	private PowertrainSystem pts;

	public InstrumentClusterLogic(PowertrainSystem ptsf) {
		super();
		this.pts = ptsf;
	}

	@Override
	public void cyclic() {
		this.blinkingTurnSignals();
		this.writeInTerminalInfos();
	}

	@Override
	public void receiveSignal(Signal s) {
		int actValue = (int) s.getData();
		switch (s.getID()) {
		case SignalDatabase.INDICATOR:
			// NONE: 0
			// RIGHT: 1
			// LEFT: 2
			// EMERGENCY: 3
			this.data_turn_signal = actValue;
			break;
		case SignalDatabase.VEHICLE_SPEED:
			// 0 120 1 KM/H -
			this.data_vehicle_speed = actValue;
			break;
		case SignalDatabase.MOTOR_RPM:
			// 0 9000 1 RPM -
			this.data_motor_rpm = actValue;
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
		return this.pts.getSteeringWheelAngle();
	}

	public boolean getGearPos_D_Status() {
		return this.pts.getGearPos_D_Status();
	}

	public boolean getGearPos_N_Status() {
		return this.pts.getGearPos_N_Status();
	}

	public boolean getGearPos_R_Status() {
		return this.pts.getGearPos_R_Status();
	}

	public boolean getGearPos_P_Status() {
		return this.pts.getGearPos_P_Status();
	}

	public boolean getHeadlightStatus() {
		return this.pts.getHeadlightStatus();
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
		System.out.println("Left/Right Turn Signals: " + this.turn_signal_left + " " + this.turn_signal_right
				+ " data: " + this.data_turn_signal);
		System.out.println("----------------------");
	}
}
