package com.unideb.bosch.humanmachineinterface;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

public class HumanMachineInterface extends SystemComponent {
	
	private static final Logger LOGGER = LogManager.getLogger();

	public HumanMachineInterface() {
		super();
		LOGGER.debug("HMI created!");
	}
	
	@Override
	public void cyclic() {
		// TODO Auto-generated method stub
		LOGGER.debug("HMI cyclic()");
		
	}

	@Override
	public void receiveSignal(Signal s) {
		// TODO Auto-generated method stub
		LOGGER.debug("HMI receiveSignal()");	
	}

}
