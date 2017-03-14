package com.unideb.bosch.humanmachineinterface;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.automatedcar.VirtualWorld;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

public class HumanMachineInterface extends SystemComponent {
	
	private static final Logger LOGGER = LogManager.getLogger();
	
	private KeyListener keyListener;

	public HumanMachineInterface() {
		super();
		LOGGER.debug("HMI created!");
		keyListener = this.new HMIKeyHandler();
		VirtualWorld.addKeyListenerToFrame(keyListener);
	}
	
	private class HMIKeyHandler implements KeyListener {

		@Override
		public void keyPressed(KeyEvent keyEvent) {
			// TODO Auto-generated method stub
			LOGGER.debug(keyEvent.getKeyCode() + " key were pressed!");
		}

		@Override
		public void keyReleased(KeyEvent keyEvent) {
			LOGGER.debug(keyEvent.getKeyCode() + " key were released!");
		}

		@Override
		public void keyTyped(KeyEvent keyEvent) {
			LOGGER.debug(keyEvent.getKeyCode() + " key were typed!");
		}
		
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
