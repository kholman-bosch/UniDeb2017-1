package com.unideb.bosch.acc;

import java.util.Arrays;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

public class AdaptiveCruiseControlModule extends SystemComponent {

	private static final Logger LOGGER = LogManager.getLogger(AdaptiveCruiseControlModule.class);

	private static final float MIN_REQ_SPEED_TO_ENABLE = 40.0f;
	private static final float MAX_REQ_SPEED_TO_ENABLE = 200.0f;
	
	private static final float MIN_VEHICLE_SPEED_FORWARD = 0.0f;
	private static final float MAX_VEHICLE_SPEED_FORWARD = 120.0f;
	

	// By default the ACC is disabled
	private AdaptiveCruiseControlState accState = AdaptiveCruiseControlState.DISABLED;

	// these values always reflect the signal values got from VFB
	private float currentSpeed;
	private float currentGasPedalPosition;
	private float currentBrakePedalPosition;

	private float cruiseControlGasPedalPosition;
	private float cruiseControlSpeed;
	private float safeDistance = 2.5f;

	private float currentACCSetting = 0.0f; // the default is the cc speed

	private List<Float> safeDistanceValues = Arrays.asList(1.0f, 1.5f, 2.0f, 2.5f);
	private int currentSafeDistanceIndex = 3;

	@Override
	public void cyclic() {
		LOGGER.debug("Current ACC state: " + this.accState.value());
		LOGGER.debug("Currently set cruise control speed: " + this.cruiseControlSpeed);
		LOGGER.debug("Current setting value: " + (this.currentACCSetting == 0.0 ? "CRUISE CONTROL SPEED" : "SAFE DISTANCE"));
		LOGGER.debug("Currently set safe distance: " + this.safeDistance);
		LOGGER.debug("Vehicle speed: " + this.currentSpeed);
		LOGGER.debug("Vehicle gas pedal position: " + this.currentGasPedalPosition);
		LOGGER.debug("Vehicle brake pedal position: " + this.currentBrakePedalPosition);
		
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_CURRENT_SAFE_DISTANCE, this.safeDistance));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_CURRENT_CRUISE_CONTROL_SPEED, this.cruiseControlSpeed));

		if (this.accState.equals(AdaptiveCruiseControlState.ACTIVE)) {
			if (Math.abs(this.currentSpeed - this.cruiseControlSpeed) < 1.0) {
				fineTuneCruiseControlGasPedalPosition(0.1f);
			} else {
				fineTuneCruiseControlGasPedalPosition(1.0f);
			}

			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GAS_PEDAL_POSITION, cruiseControlGasPedalPosition));
			if (this.currentSpeed == 0.0f) {
				// suspend acc
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 2.0f));
			}
		}
	}

	@Override
	public void receiveSignal(Signal s) {
		switch (s.getID()) {
		case SignalDatabase.ACC_STATUS_CHANGED:
			onReceiveAccStatusChangeSignal(s);
			break;
		case SignalDatabase.VEHICLE_SPEED:
			onReceiveVehicleSpeedSignal(s);
			break;
		case SignalDatabase.GAS_PEDAL_POSITION:
			onReceiveGasPedalPositionSignal(s);
			break;
		case SignalDatabase.BRAKE_PEDAL_POSITION:
			onReceiveBrakePedalPositionSignal(s);
			break;
		case SignalDatabase.ACC_SETTING_SWITCHED:
			onReceiveAccSettingSwitchedSignal(s);
			break;
		case SignalDatabase.ACC_CHANGE_VALUE:
			onReceiveAccChangeValueSignal(s);
			break;
		}
	}

	private void fineTuneCruiseControlGasPedalPosition(float step) {
		if (this.currentSpeed <= this.cruiseControlSpeed) {
			this.cruiseControlGasPedalPosition += step;
		}
		if (this.currentSpeed > this.cruiseControlSpeed) {
			this.cruiseControlGasPedalPosition -= step;
		}
	}

	private void onReceiveAccStatusChangeSignal(Signal s) {
		LOGGER.debug("ACC_STATUS_CHANGED SIGNAL RECEIVED: " + s.getData());

		switch (new Float(s.getData()).intValue()) {
		case 0:
			LOGGER.debug("ACC DISABLE REQUEST");
			disableACC();
			break;
		case 1:
			LOGGER.debug("ACC ENABLE REQUEST");
			if (this.currentSpeed >= MIN_REQ_SPEED_TO_ENABLE && this.currentSpeed <= MAX_REQ_SPEED_TO_ENABLE) {
				enableACC();
			} else {
				LOGGER.debug("CANNOT ENABLE ACC DUE TO INAPPROPRIATE SPEED");
				disableACC();
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 0.0f));
			}
			break;
		case 2: // SUSPENDED
			suspendACC();
			break;
		case 3: // STOPANDGO
			break;
		}
	}

	private void onReceiveVehicleSpeedSignal(Signal s) {
		this.currentSpeed = s.getData();
	}

	private void onReceiveGasPedalPositionSignal(Signal s) {
		this.currentGasPedalPosition = s.getData();

		// ACC is enabled, right now cruise controling, but gas pedal pressed
		if (this.accState == AdaptiveCruiseControlState.ACTIVE && s.getData() != this.cruiseControlGasPedalPosition
				&& s.getData() != 0.0) {
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 2.0f));
		}
	}

	private void onReceiveBrakePedalPositionSignal(Signal s) {
		this.currentBrakePedalPosition = s.getData();

		if (this.accState == AdaptiveCruiseControlState.ACTIVE && s.getData() != 0.0) {
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 2.0f));
		}
	}

	private void onReceiveAccSettingSwitchedSignal(Signal s) {
		this.currentACCSetting = s.getData();
	}

	private void onReceiveAccChangeValueSignal(Signal s) {
		if (this.accState == AdaptiveCruiseControlState.ACTIVE) {
			if (this.currentACCSetting == 0.0f) {
				// cruise control speed setting
				changeAccCruiseControlSpeed(s.getData());
			} else if (this.currentACCSetting == 1.0f) {
				// safe distance setting
				changeAccSafeDistance(s.getData());
			}
		}
	}

	private void changeAccCruiseControlSpeed(float signalValue) {

		if (signalValue == 0.0f) {
			// decrement
			if (this.cruiseControlSpeed > MIN_VEHICLE_SPEED_FORWARD) {
				this.cruiseControlSpeed -= 1.0f;
			}
		} else if (signalValue == 1.0f) {
			// increment
			if (this.cruiseControlSpeed < MAX_VEHICLE_SPEED_FORWARD) {
				this.cruiseControlSpeed += 1.0f;
			}
		}
		
		if( this.cruiseControlSpeed < MIN_VEHICLE_SPEED_FORWARD ){
			this.cruiseControlSpeed = MIN_VEHICLE_SPEED_FORWARD;
		}
		
		if( this.cruiseControlSpeed > MAX_VEHICLE_SPEED_FORWARD ){
			this.cruiseControlSpeed = MAX_VEHICLE_SPEED_FORWARD;
		}
	}

	private void changeAccSafeDistance(float signalValue) {
		if (signalValue == 0.0f) {
			// decrement
			if (this.currentSafeDistanceIndex > 0) {
				this.currentSafeDistanceIndex -= 1;
			}
		} else if (signalValue == 1.0f) {
			// increment
			if (this.currentSafeDistanceIndex < this.safeDistanceValues.size() - 1) {
				this.currentSafeDistanceIndex += 1;
			}
		}
		this.safeDistance = this.safeDistanceValues.get(this.currentSafeDistanceIndex);
	}

	private void enableACC() {
		LOGGER.debug("ACC IS NOW ENABLED");
		this.accState = AdaptiveCruiseControlState.ACTIVE;
		// start cruise controlling
		this.cruiseControlSpeed = this.currentSpeed;
		this.cruiseControlGasPedalPosition = this.cruiseControlSpeed / 120.0f * 100;
	}

	private void disableACC() {
		LOGGER.debug("ACC IS NOW DISABLED");
		this.accState = AdaptiveCruiseControlState.DISABLED;
	}

	private void suspendACC() {
		LOGGER.debug("ACC IS NOW SUSPENDED");
		this.accState = AdaptiveCruiseControlState.SUSPENDED;
	}

}
