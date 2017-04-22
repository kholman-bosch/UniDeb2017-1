package com.unideb.bosch.acc;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

public class AdaptiveCruiseControlModule extends SystemComponent {
	
	private boolean canEnable = false;
	private final float MIN_REQ_SPEED = 40.0f;
	private final float MAX_REQ_SPEED = 200.0f;
	private float currentSpeed;

	@Override
	public void cyclic() {
		// TODO Auto-generated method stub

	}

	@Override
	public void receiveSignal(Signal s) {
		if( s.getID() == SignalDatabase.ACC_STATUS_CHANGED && s.getData() == 1.0f ){
			if( this.currentSpeed >= MIN_REQ_SPEED && this.currentSpeed <= MAX_REQ_SPEED ){
				this.canEnable = true;
			} else {
				this.canEnable = false;
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, 0.0f));
			}
		}
		
		if( s.getID() == SignalDatabase.VEHICLE_SPEED ){
			System.out.println("SPEED: " + s.getData());
			this.currentSpeed = s.getData();
			
		}
	}

}
