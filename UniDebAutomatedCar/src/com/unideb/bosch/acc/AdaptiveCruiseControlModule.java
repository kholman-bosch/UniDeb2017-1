package com.unideb.bosch.acc;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.humanmachineinterface.HumanMachineInterface;

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
	
	private float lastGasPedalPositionBeforeRelease;
	
	private float cruiseControlGasPedalPosition;
	private float cruiseControlSpeed;

	@Override
	public void cyclic() {
		LOGGER.debug("Current ACC state: " + this.accState.value());
		LOGGER.debug("Currently set cruise control speed: " + this.cruiseControlSpeed);
		
		if (this.accState.equals(AdaptiveCruiseControlState.ACTIVE)) {
			if( this.currentSpeed <= this.cruiseControlSpeed ) {
				this.cruiseControlGasPedalPosition += 1.0;
			}
			if( this.currentSpeed > this.cruiseControlSpeed ) {
				this.cruiseControlGasPedalPosition -= 1.0;
			}
			
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GAS_PEDAL_POSITION, cruiseControlGasPedalPosition));
		}
	}

	@Override
	public void receiveSignal(Signal s) {

		switch (s.getID()) {
		case SignalDatabase.ACC_STATUS_CHANGED:
			LOGGER.debug("ACC_STATUS_CHANGED SIGNAL RECEIVED: " + s.getData());
			
			if (s.getData() == 1.0f) {
				LOGGER.debug("ACC ENABLE REQUEST");
				if (this.currentSpeed >= MIN_REQ_SPEED_TO_ENABLE && this.currentSpeed <= MAX_REQ_SPEED_TO_ENABLE) {
					LOGGER.debug("ACC IS NOW ENABLED");
					this.accState = AdaptiveCruiseControlState.ACTIVE;
					// TODO instead of capturing the gas pedal position and freeze it, set it manually for the speed
					this.cruiseControlGasPedalPosition = this.lastGasPedalPositionBeforeRelease; // cruise controling
					this.cruiseControlSpeed = this.currentSpeed;
				} else {
					LOGGER.debug("CANNOT ENABLE ACC DUE TO INAPPROPRIATE SPEED");
					this.accState = AdaptiveCruiseControlState.DISABLED; // disable acc
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 0.0f));
				}
			} else {
				LOGGER.debug("ACC DISABLE REQUEST");
				this.accState = AdaptiveCruiseControlState.DISABLED; // disable acc
//				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 0.0f));
			}
			break;
		case SignalDatabase.VEHICLE_SPEED:
			System.out.println("SPEED: " + s.getData());
			this.currentSpeed = s.getData();
			break;
		case SignalDatabase.GAS_PEDAL_POSITION:
			System.out.println("GAS PEDAL POSITION: " + s.getData());
			System.out.println("CRUISE CONTROL GAS PEDAL POSITION: " + this.cruiseControlGasPedalPosition);
			this.currentGasPedalPosition = s.getData();
			
			if( s.getData() != 0.0 ){
				this.lastGasPedalPositionBeforeRelease = s.getData();
			}
			
			// ACC is enabled, right now cruise controling, but gas pedal pressed
			if( this.accState == AdaptiveCruiseControlState.ACTIVE && s.getData() != this.cruiseControlGasPedalPosition && s.getData() != 0.0){
				this.accState = AdaptiveCruiseControlState.SUSPENDED;
			}
			
			break;
		case SignalDatabase.BRAKE_PEDAL_POSITION:
			System.out.println("BRAKE PEDAL POSITION: " + s.getData());
			this.currentBrakePedalPosition = s.getData();
			
			if( this.accState == AdaptiveCruiseControlState.ACTIVE && s.getData() != 0.0){
				this.accState = AdaptiveCruiseControlState.SUSPENDED;
			}
			break;
		}
	}

}
