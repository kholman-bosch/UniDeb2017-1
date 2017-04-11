package com.unideb.bosch.automatedcar.vehicleparts;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.instrumentclusterdisplay.SignalDatabase;

/**
 * This class implements the Powertrain System The responsible for this file is TeamA
 */
public class PowertrainSystem extends SystemComponent {

	private int data_gas_pedal_position = 3;
	private int data_brake_pedal_position = 0;
	private int data_steering_wheel_angle = 0;
	private int data_gear_position = 3;
	private int data_headlight = 0;
	private int data_vehicle_speed = 0;
	private int data_motor_rpm = 0;

	public PowertrainSystem() {
		super();
	}

	@Override
	public void cyclic() {
		this.calcSpeedandMotorRPM();
		// this.writeInTerminalInfos();
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
		case SignalDatabase.STEERING_WHEEL_ANGLE:
			// -720 720 1 � -
			this.data_steering_wheel_angle = SignalDatabase.limit(this.data_steering_wheel_angle, -actValue, -720, 720);
			break;
		case SignalDatabase.GEAR_POSITION:
			// D: 0
			// N: 1
			// R: 2
			// P: 3
			this.checkAndSetValidGearStatus(actValue);
			break;
		case SignalDatabase.HEADLIGHT:
			// ON: 1
			// OFF: 0
			this.data_headlight = actValue;
			break;
		}
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

	public int getSteeringWheelAngle() {
		return this.data_steering_wheel_angle;
	}

	public int getCarSpeed() {
		float brakeSlowdown = 1f - this.data_brake_pedal_position / 100f;
		if (brakeSlowdown == 0) {
			return this.data_vehicle_speed;
		}
		return (int) (this.data_vehicle_speed * brakeSlowdown);
	}

	private void checkAndSetValidGearStatus(int value) {
		if (this.data_vehicle_speed == 0) {
			this.data_gear_position = value;
			return;
		}
		if (this.data_vehicle_speed > 0) {
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

	private void calcSpeedandMotorRPM() {
		switch (this.data_gear_position) {
		case 0: // drive
			this.data_vehicle_speed = SignalDatabase.limit(this.data_vehicle_speed, (int) (this.data_gas_pedal_position * 1.2f), 0, 120);
			break;
		case 1: // neutral
			this.data_vehicle_speed--;
			if (this.data_vehicle_speed < 0) {
				this.data_vehicle_speed = 0;
			}
			break;
		case 2: // reverse
			this.data_vehicle_speed = -SignalDatabase.limit(this.data_vehicle_speed, (int) (this.data_gas_pedal_position * 1.2f), 0, 120);
			break;
		case 3: // park
			break;
		}

		float rpm = this.data_gas_pedal_position / 100f * 9000;
		this.data_motor_rpm = SignalDatabase.limit(this.data_motor_rpm, (int) rpm, 0, 9000);
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.VEHICLE_SPEED, this.data_vehicle_speed));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.MOTOR_RPM, this.data_motor_rpm));
	}

	// Write in the Terminal the actual status about the Signals
	private void writeInTerminalInfos() {
		System.out.println("---POWERTRAINSYSTEM---");
		System.out.println("GAS_PEDAL_POSITION data: " + this.data_gas_pedal_position);
		System.out.println("BRAKE_PEDAL_POSITION data: " + this.data_brake_pedal_position);
		System.out.println("STEERING_WHEEL_ANGLE data: " + this.data_steering_wheel_angle);
		System.out.println("GEAR_POSITION data: " + this.data_gear_position);
		System.out.println("VEHICLE_SPEED data: " + this.data_vehicle_speed);
		System.out.println("----------------------");
	}
}
