package com.unideb.bosch.automatedcar.vehicleparts;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.VirtualWorld;
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
	private float car_Speed_Pixels = 0f;
	private float neutral_rpm = 0;
	private int inner_gear_state_in_D = 1;
	private int keep_alive_rpm = 1100, maximum_rpm = 9000;

	public PowertrainSystem() {
		super();
	}

	@Override
	public void cyclic() {
		this.calculatePowertrainPhysics();
		this.calcMotorRPM();
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_HEADLIGHT, this.data_headlight));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_INDEX_INDICATORS, this.data_index));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_GEAR_POSITION, this.data_gear_position));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.POWERTRAIN_STEERING_WHEEL_ANGLE, this.data_steering_wheel_angle));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.VEHICLE_SPEED, this.pixels_To_mps_To_kmh(this.car_Speed_Pixels)));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.MOTOR_RPM, this.data_motor_rpm));
		// this.writeInTerminalInfos();
	}

	private void calculatePowertrainPhysics() {
		float maxForwardSpeed_InPixels = this.kmh_To_pixelsPerTick(120f); // when we clamp speeds we need to clamp pixel values so we need to convert kmh to pixels
		float maxReverseSpeed_InPixels = this.kmh_To_pixelsPerTick(35f);
		// negative powers
		float powerFromBreakPedal = this.data_brake_pedal_position / 100f;
		float maximumBrakingPower = -30f;
		float negativePowerFromBraking = maximumBrakingPower * powerFromBreakPedal;
		float baseNegativePowers_GRAV_FRICT = -4f;
		float allNegativePowers = baseNegativePowers_GRAV_FRICT + negativePowerFromBraking;
		// positive powers
		float powerFromGasPedal = this.data_gas_pedal_position / 100f;
		float maximumForwardPower = 5f;
		float allPositivePowers = 0f;
		// total powers
		if (powerFromGasPedal > 0f) {
			allPositivePowers = Math.abs(baseNegativePowers_GRAV_FRICT) + (maximumForwardPower * powerFromGasPedal);
		}
		float allPowers = (allPositivePowers + allNegativePowers);
		// gear logic
		switch (this.data_gear_position) {
		case 0: // drive
			this.car_Speed_Pixels += allPowers;
			if (this.car_Speed_Pixels < 0f) {
				this.car_Speed_Pixels = 0f;
			}
			// max speed limit
			maxForwardSpeed_InPixels *= powerFromGasPedal; //simple maxSpeedClampBasedOnGasPedal
			if (this.car_Speed_Pixels > maxForwardSpeed_InPixels) {
				this.car_Speed_Pixels = maxForwardSpeed_InPixels;
			}
			break;
		case 1: // neutral
			this.car_Speed_Pixels += allNegativePowers;
			if (this.car_Speed_Pixels < 0f) {
				this.car_Speed_Pixels = 0f;
			}
			break;
		case 2: // reverse
			this.car_Speed_Pixels -= allPowers;
			if (this.car_Speed_Pixels > 0f) {
				this.car_Speed_Pixels = 0f;
			}
			// max speed limit
			if (this.car_Speed_Pixels < -maxReverseSpeed_InPixels) {
				this.car_Speed_Pixels = -maxReverseSpeed_InPixels;
			}
			break;
		case 3: // park
			this.car_Speed_Pixels += allNegativePowers;
			if (this.car_Speed_Pixels < 0f) {
				this.car_Speed_Pixels = 0f;
			}
			break;
		}
	}

	public float pixels_To_mps_To_kmh(float pixelVelocity) {
		float ticksPerSecond = VirtualWorld.getTicksPerSecond();
		float MPS_to_KMH_ratio = 3.6f;
		float onePixelInMeters = 0.02f;
		return pixelVelocity * ticksPerSecond * onePixelInMeters * MPS_to_KMH_ratio;
	}

	public float kmh_To_pixelsPerTick(float kmh) {
		float ticksPerSecond = VirtualWorld.getTicksPerSecond();
		float MPS_to_KMH_ratio = 3.6f;
		float onePixelInMeters = 0.02f;
		return kmh / ticksPerSecond / onePixelInMeters / MPS_to_KMH_ratio;
	}

	public int getCarSpeed_InPixels() {
		return (int) this.car_Speed_Pixels;
	}

	public void calcMotorRPM() {
		int minRPM, maxRPM, startSpeed, endSpeed;
		float car_Speed = this.pixels_To_mps_To_kmh(this.car_Speed_Pixels);

		switch (this.data_gear_position) {
		case 0: // drive
			if (this.inner_gear_state_in_D <= 0) {
				this.inner_gear_state_in_D = 1;
			}
			this.neutral_rpm = 0;
			break;
		case 1: // neutral
			this.inner_gear_state_in_D = -1;
			if (this.data_gas_pedal_position != 0) {
				this.neutral_rpm = this.data_gas_pedal_position;
			} else {
				this.neutral_rpm -= 5;
				if (this.neutral_rpm < 0) {
					this.neutral_rpm = 0;
				}
			}
			break;
		case 2: // reverse
			this.inner_gear_state_in_D = 0;
			this.neutral_rpm = 0;
			break;
		case 3: // park
			this.neutral_rpm = 0;
			break;
		}

		switch (this.inner_gear_state_in_D) {
		case -1:
		default:
			startSpeed = 0;
			endSpeed = 120;
			minRPM = this.keep_alive_rpm;
			maxRPM = 9000;
			car_Speed = this.neutral_rpm * 1.2f;
			break;
		case 0:// reverse 0-50
			startSpeed = 0;
			endSpeed = 35;
			minRPM = this.keep_alive_rpm;
			maxRPM = 3200;
			car_Speed = -car_Speed;
			break;
		case 1:// 0-30
			startSpeed = 0;
			endSpeed = 30;
			minRPM = this.keep_alive_rpm;
			maxRPM = 3700;
			if (car_Speed >= endSpeed) {
				this.inner_gear_state_in_D = 2;
			}
			break;
		case 2:// 30-70
			startSpeed = 30;
			endSpeed = 70;
			minRPM = 2200;
			maxRPM = 3700;
			if (car_Speed < startSpeed) {
				this.inner_gear_state_in_D = 1;
			}
			if (car_Speed >= endSpeed) {
				this.inner_gear_state_in_D = 3;
			}
			break;
		case 3:// 70-90
			startSpeed = 70;
			endSpeed = 90;
			minRPM = 2200;
			maxRPM = 3700;
			if (car_Speed < startSpeed) {
				this.inner_gear_state_in_D = 2;
			}
			if (car_Speed >= endSpeed) {
				this.inner_gear_state_in_D = 4;
			}
			break;
		case 4:// 90-120
			startSpeed = 90;
			endSpeed = 120;
			minRPM = 2700;
			maxRPM = 9000;
			if (car_Speed < startSpeed) {
				this.inner_gear_state_in_D = 3;
			}
			break;
		}
		this.data_motor_rpm = (int) (minRPM + (car_Speed - startSpeed) * ((maxRPM - minRPM) / (endSpeed - startSpeed)));
		if (this.data_motor_rpm < this.keep_alive_rpm) {
			this.data_motor_rpm = this.keep_alive_rpm;
		}
		if (this.data_motor_rpm > this.maximum_rpm) {
			this.data_motor_rpm = this.maximum_rpm;
		}
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
		if (this.car_Speed_Pixels == 0) {
			this.data_gear_position = value;
			return;
		}
		if (this.car_Speed_Pixels > 0) {
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
		System.out.println("VEHICLE_SPEED data: " + this.car_Speed_Pixels);
		System.out.println("MOTOR_RPM data: " + this.data_motor_rpm);
		System.out.println("HEADLIGHT data: " + this.data_headlight);
		System.out.println("INDEX_INDICATORS data: " + this.data_index);
		System.out.println("GEAR_POSITION data: " + this.data_gear_position);
		System.out.println("STEERING_WHEEL_ANGLE data: " + this.data_steering_wheel_angle);
		System.out.println("----------------------");
	}
}
