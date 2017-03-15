package com.unideb.bosch.humanmachineinterface;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.automatedcar.VirtualWorld;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

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
		int acceleration = 0;
		
		@Override
		public void keyPressed(KeyEvent keyEvent) {
			// TODO Auto-generated method stub
			switch(keyEvent.getKeyChar()){
				case KeyEvent.VK_UP:
					if(acceleration == 10)
						VirtualFunctionBus.sendSignal(new Signal(4, 10));
					else
						VirtualFunctionBus.sendSignal(new Signal(4, acceleration++));
					break;
			}
			
			LOGGER.debug(keyEvent.getKeyCode() + " key were pressed!");
		}

		@Override
		public void keyReleased(KeyEvent keyEvent) {
			LOGGER.debug(keyEvent.getKeyCode() + " key were released!");
		}

		int lastIndicatorSignal = 0;
		boolean isHeadLightOn = false;
		
		@Override
		public void keyTyped(KeyEvent keyEvent) {
			char character = keyEvent.getKeyChar();
			if (character == 'p' || character == 'P') {
				VirtualFunctionBus.sendSignal(new Signal(7, 3));
			}
			else if (character == 'r' || character == 'R') {
				VirtualFunctionBus.sendSignal(new Signal(7, 2));
			}
			else if (character == 'n' || character == 'N') {
				VirtualFunctionBus.sendSignal(new Signal(7, 1));
			}
			else if (character == 'd' || character == 'D') {
				VirtualFunctionBus.sendSignal(new Signal(7, 0));
			}
			else if (character == 'q' || character == 'Q') {
				if (lastIndicatorSignal == 2) {
					VirtualFunctionBus.sendSignal(new Signal(3, 0));
					lastIndicatorSignal = 0;
				}
				else {
					VirtualFunctionBus.sendSignal(new Signal(3, 2));
					lastIndicatorSignal = 2;
				}
			}
			else if (character == 'w' || character == 'W') {
				if (lastIndicatorSignal == 3) {
					VirtualFunctionBus.sendSignal(new Signal(3, 0));
					lastIndicatorSignal = 0;
				}
				else {
					VirtualFunctionBus.sendSignal(new Signal(3, 3));
					lastIndicatorSignal = 3;
				}
			}
			else if (character == 'e' || character == 'E') {
				if (lastIndicatorSignal == 1) {
					VirtualFunctionBus.sendSignal(new Signal(3, 0));
					lastIndicatorSignal = 0;
				}
				else {
					VirtualFunctionBus.sendSignal(new Signal(3, 1));
					lastIndicatorSignal = 1;
				}
			}
			else if (character == 'l' || character == 'L') {
				if (isHeadLightOn) {
					VirtualFunctionBus.sendSignal(new Signal(10, 0));
					isHeadLightOn = false;
				}
				else {
					VirtualFunctionBus.sendSignal(new Signal(10, 1));
					isHeadLightOn = true;;
				}
			}
			
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
