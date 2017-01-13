package com.unideb.bosch.automatedcar.vehicleparts;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

public class Driver extends SystemComponent {

	@Override
	public void cyclic() {
		// Set the gas to 50%
		VirtualFunctionBus.sendSignal(new Signal(0, 50));
	}

	@Override
	public void receiveSignal(Signal s) {
		// TODO Auto-generated method stub		
	}

}
