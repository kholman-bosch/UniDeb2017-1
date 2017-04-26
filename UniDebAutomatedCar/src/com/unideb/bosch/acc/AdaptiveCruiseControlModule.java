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
		
		if (this.accState.equals(AdaptiveCruiseControlState.ACTIVE)) {
			if( Math.abs(this.currentSpeed - this.cruiseControlSpeed) < 1.0 ) {
				fineTuneCruiseControlGasPedalPosition(0.1f);
			} else {
				fineTuneCruiseControlGasPedalPosition(1.0f);
			}
			
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GAS_PEDAL_POSITION, cruiseControlGasPedalPosition));
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
	
	private void fineTuneCruiseControlGasPedalPosition( float step ) {
		if( this.currentSpeed <= this.cruiseControlSpeed ) {
			this.cruiseControlGasPedalPosition += step;
		}
		if( this.currentSpeed > this.cruiseControlSpeed ) {
			this.cruiseControlGasPedalPosition -= step;
		}
	}
	
	private void onReceiveAccStatusChangeSignal(Signal s) {
		LOGGER.debug("ACC_STATUS_CHANGED SIGNAL RECEIVED: " + s.getData());
		
		if (s.getData() == 1.0f) {
			LOGGER.debug("ACC ENABLE REQUEST");
			if (this.currentSpeed >= MIN_REQ_SPEED_TO_ENABLE && this.currentSpeed <= MAX_REQ_SPEED_TO_ENABLE) {
				enableACC();
			} else {
				LOGGER.debug("CANNOT ENABLE ACC DUE TO INAPPROPRIATE SPEED");
				disableACC();
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 0.0f));
			}
		} else {
			LOGGER.debug("ACC DISABLE REQUEST");
			disableACC();
		}
	}
	
	private void onReceiveVehicleSpeedSignal(Signal s) {
		System.out.println("SPEED: " + s.getData());
		this.currentSpeed = s.getData();
	}
	
	private void onReceiveGasPedalPositionSignal(Signal s) {
		System.out.println("GAS PEDAL POSITION: " + s.getData());
		System.out.println("CRUISE CONTROL GAS PEDAL POSITION: " + this.cruiseControlGasPedalPosition);
		this.currentGasPedalPosition = s.getData();
		
		// ACC is enabled, right now cruise controling, but gas pedal pressed
		if( this.accState == AdaptiveCruiseControlState.ACTIVE && s.getData() != this.cruiseControlGasPedalPosition && s.getData() != 0.0){
			this.accState = AdaptiveCruiseControlState.SUSPENDED;
		}
	}
	
	private void onReceiveBrakePedalPositionSignal(Signal s) {
		System.out.println("BRAKE PEDAL POSITION: " + s.getData());
		this.currentBrakePedalPosition = s.getData();
		
		if( this.accState == AdaptiveCruiseControlState.ACTIVE && s.getData() != 0.0){
			this.accState = AdaptiveCruiseControlState.SUSPENDED;
		}
	}
	
	private void onReceiveAccSettingSwitchedSignal(Signal s) {
		this.currentACCSetting = s.getData();
	}
	
	private void onReceiveAccChangeValueSignal(Signal s) {
		if( this.currentACCSetting == 0.0f ) {
			// cruise control speed setting
			changeAccCruiseControlSpeed(s.getData());
		} else if ( this.currentACCSetting == 1.0f ){
			// safe distance setting
			changeAccSafeDistance(s.getData());
		}
	}
	
	private void changeAccCruiseControlSpeed(float signalValue) {
		if( signalValue == 0.0f ) {
			// decrement
			if( this.cruiseControlSpeed > 0.0f ){
				this.cruiseControlSpeed -= 1.0f;
			}
		} else if ( signalValue == 1.0f ) {
			// increment
			// TODO should not increment over 120km/h
			if( this.cruiseControlSpeed < 200.0f ){
				this.cruiseControlSpeed += 1.0f;
			}
		}
	}
	
	private void changeAccSafeDistance(float signalValue) {
		if( signalValue == 0.0f ) {
			// decrement
			if( this.currentSafeDistanceIndex > 0 ){
				this.currentSafeDistanceIndex -= 1;
			}
		} else if ( signalValue == 1.0f ) {
			// increment
			if( this.currentSafeDistanceIndex < this.safeDistanceValues.size()-1 ){
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

}
