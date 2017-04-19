package com.unideb.bosch.automatedcar.vehicleparts;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

/**
 * This class implements the Powertrain System The responsible for this file is TeamA
 */
public class PowertrainSystem extends SystemComponent {

	private int data_gas_pedal_position = 0;
	private int data_brake_pedal_position = 0;
	//
	private int data_motor_rpm = 0;
	private int data_headlight = 0;
	private int data_index = 0;
	private int data_steering_wheel_angle = 0;
	private int data_gear_position = 3;
	//
	private float car_Speed = 0f;

	public PowertrainSystem() {
		super();
	}

	@Override
	public void cyclic() {
		this.calculatePowertrainPhysics();
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_HEADLIGHT, this.data_headlight));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_INDEX_INDICATORS, this.data_index));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_GEAR_POSITION, this.data_gear_position));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_STEERING_WHEEL_ANGLE, this.data_steering_wheel_angle));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.VEHICLE_SPEED, this.car_Speed));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.MOTOR_RPM, this.data_motor_rpm));
		// this.writeInTerminalInfos();
	}

	private void calculatePowertrainPhysics() {
		float maxForwardSpeed = 120f;
		float maxReverseSpeed = 35f;
		//negative powers
		float powerFromBreakPedal = this.data_brake_pedal_position / 100f;
		float maximumBrakingPower = -30f;
		float negativePowerFromBraking = maximumBrakingPower * powerFromBreakPedal;
		float baseNegativePowers_GRAV_FRICT = -4f;
		float allNegativePowers = baseNegativePowers_GRAV_FRICT + negativePowerFromBraking;
		//positive powers
		float powerFromGasPedal = this.data_gas_pedal_position / 100f;
		float maximumForwardPower = 5f;
		float allPositivePowers = 0f;
		//total powers
		if (powerFromGasPedal > 0f) {
			allPositivePowers = Math.abs(baseNegativePowers_GRAV_FRICT) + (maximumForwardPower * powerFromGasPedal);
		}
		float allPowers = (allPositivePowers + allNegativePowers);
		// gear logic
		switch (this.data_gear_position) {
		case 0: // drive
			this.car_Speed += allPowers;
			if (this.car_Speed < 0f) {
				this.car_Speed = 0f;
			}
			// max speed limit
			if (this.car_Speed > maxForwardSpeed) {
				this.car_Speed = maxForwardSpeed;
			}
			break;
		case 1: // neutral
			this.car_Speed += allNegativePowers;
			if (this.car_Speed < 0f) {
				this.car_Speed = 0f;
			}
			break;
		case 2: // reverse
			this.car_Speed -= allPowers;
			if (this.car_Speed > 0f) {
				this.car_Speed = 0f;
			}
			// max speed limit
			if (this.car_Speed < -maxReverseSpeed) {
				this.car_Speed = -maxReverseSpeed;
			}
			break;
		case 3: // park
			this.car_Speed += allNegativePowers;
			if (this.car_Speed < 0f) {
				this.car_Speed = 0f;
			}
			break;
		}
	}

	public int getCarSpeed() {
		return (int) this.car_Speed;
	}

	@Override
	public void receiveSignal(Signal s) {
		int actValue = (int) s.getData();
		switch (s.getID()) {
		case SignalDatabase.GAS_PEDAL_POSITION:
			// 0 100 1 % -
			this.data_gas_pedal_position = actValue;
			break;
		case SignalDatabase.BRAKE_PEDAL_POSITION:
			// 0 100 1 % -
			this.data_brake_pedal_position = actValue;
			break;
		case SignalDatabase.HEADLIGHT:
			// ON: 1
			// OFF: 0
			this.data_headlight = actValue;
			break;
		case SignalDatabase.INDICATOR:
			// NONE: 0
			// RIGHT: 1
			// LEFT: 2
			// EMERGENCY: 3
			this.data_index = actValue;
			break;
		case SignalDatabase.GEAR_POSITION:
			// D: 0
			// N: 1
			// R: 2
			// P: 3
			this.checkAndSetValidGearStatus(actValue);
			break;
		case SignalDatabase.STEERING_WHEEL_ANGLE:
			// -720 720 1
			this.data_steering_wheel_angle = SignalDatabase.limit(this.data_steering_wheel_angle, -actValue, -720, 720);
			break;
		}
	}

	public int getSteeringWheelAngle() {
		return this.data_steering_wheel_angle;
	}

	private void checkAndSetValidGearStatus(int value) {
		if (this.car_Speed == 0) {
			this.data_gear_position = value;
			return;
		}
		if (this.car_Speed > 0) {
			if ((this.data_gear_position == 0 || this.data_gear_position == 1)) {
				switch (value) {
				case 0:// D
				case 1:// N
					this.data_gear_position = value;
					return;
				}
			}
			if ((this.data_gear_position == 1 || this.data_gear_position == 2)) {
				switch (value) {
				case 1:// N
				case 2:// R
					this.data_gear_position = value;
					break;
				}
			}
		}
	}

	// Write in the Terminal the actual status about the Signals
	private void writeInTerminalInfos() {
		System.out.println("---POWERTRAINSYSTEM---");
		// System.out.println("GAS_PEDAL_POSITION data: " +
		// this.data_gas_pedal_position);
		// System.out.println("BRAKE_PEDAL_POSITION data: " +
		// this.data_brake_pedal_position);
		System.out.println("VEHICLE_SPEED data: " + this.car_Speed);
		System.out.println("MOTOR_RPM data: " + this.data_motor_rpm);
		System.out.println("HEADLIGHT data: " + this.data_headlight);
		System.out.println("INDEX_INDICATORS data: " + this.data_index);
		System.out.println("GEAR_POSITION data: " + this.data_gear_position);
		System.out.println("STEERING_WHEEL_ANGLE data: " + this.data_steering_wheel_angle);
		System.out.println("----------------------");
	}
}
