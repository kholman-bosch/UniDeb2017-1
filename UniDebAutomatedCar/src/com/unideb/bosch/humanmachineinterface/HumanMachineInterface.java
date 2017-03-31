package com.unideb.bosch.humanmachineinterface;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.automatedcar.VirtualWorld;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.instrumentclusterdisplay.SignalDatabase;

public class HumanMachineInterface extends SystemComponent {

	private static final Logger LOGGER = LogManager.getLogger();

	private KeyListener keyListener;

	public HumanMachineInterface() {
		super();
		LOGGER.debug("HMI created!");
		keyListener = this.new HMIKeyHandler();
		VirtualWorld.addKeyListenerToFrame(keyListener);
	}

	int gasPedalPosition = 0;
	int breakPedalPosition = 0;
	int steeringWheelAngle = 0;
	boolean gasPressed = false;
	boolean breakPressed = false;
	boolean left = false;
	boolean right = false;

	private class HMIKeyHandler implements KeyListener {

		@Override
		public void keyPressed(KeyEvent keyEvent) {
			switch (keyEvent.getKeyCode()) {
			case KeyEvent.VK_UP:
				gasPressed = true;
				break;
			case KeyEvent.VK_DOWN:
				breakPressed = true;
				break;
			case KeyEvent.VK_LEFT:
				left = true;
				break;
			case KeyEvent.VK_RIGHT:
				right = true;
				break;
			}

			LOGGER.debug(keyEvent.getKeyCode() + " key were pressed!");
		}

		@Override
		public void keyReleased(KeyEvent keyEvent) {
			switch (keyEvent.getKeyCode()) {
			case KeyEvent.VK_UP:
				gasPressed = false;
				break;
			case KeyEvent.VK_DOWN:
				breakPressed = false;
				break;
			case KeyEvent.VK_LEFT:
				left = false;
				break;
			case KeyEvent.VK_RIGHT:
				right = false;
				break;
			}

			LOGGER.debug(keyEvent.getKeyCode() + " key were released!");
		}

		int lastIndicatorSignal = 0;
		boolean isHeadLightOn = false;

		@Override
		public void keyTyped(KeyEvent keyEvent) {

			char character = keyEvent.getKeyChar();
			if (character == 'p' || character == 'P') {
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GEAR_POSITION, 3));
			} else if (character == 'r' || character == 'R') {
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GEAR_POSITION, 2));
			} else if (character == 'n' || character == 'N') {
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GEAR_POSITION, 1));
			} else if (character == 'd' || character == 'D') {
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GEAR_POSITION, 0));
			} else if (character == 'q' || character == 'Q') {
				if (lastIndicatorSignal == 2) {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.INDICATOR, 0));
					lastIndicatorSignal = 0;
				} else {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.INDICATOR, 2));
					lastIndicatorSignal = 2;
				}
			} else if (character == 'w' || character == 'W') {
				if (lastIndicatorSignal == 3) {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.INDICATOR, 0));
					lastIndicatorSignal = 0;
				} else {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.INDICATOR, 3));
					lastIndicatorSignal = 3;
				}
			} else if (character == 'e' || character == 'E') {
				if (lastIndicatorSignal == 1) {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.INDICATOR, 0));
					lastIndicatorSignal = 0;
				} else {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.INDICATOR, 1));
					lastIndicatorSignal = 1;
				}
			} else if (character == 'l' || character == 'L') {
				if (isHeadLightOn) {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.HEADLIGHT, 0));
					isHeadLightOn = false;
				} else {
					VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.HEADLIGHT, 1));
					isHeadLightOn = true;
					;
				}
			}

			LOGGER.debug(keyEvent.getKeyCode() + " key were typed!");
		}

	}

	@Override
	public void cyclic() {
		if (gasPressed) {
			if (gasPedalPosition < 100) {
				gasPedalPosition++;
			}
		} else {
			if (gasPedalPosition > 0) {
				gasPedalPosition--;
			}
		}

		if (breakPressed) {
			if (breakPedalPosition < 100) {
				breakPedalPosition++;
			}
		} else {
			if (breakPedalPosition > 0) {
				breakPedalPosition--;
			}
		}

		if (left && steeringWheelAngle > -720) {
			steeringWheelAngle -= 4;
		}
		if (right && steeringWheelAngle < 720) {
			steeringWheelAngle += 3;
		}

		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GAS_PEDAL_POSITION, gasPedalPosition));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.BRAKE_PEDAL_POSITION, breakPedalPosition));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.STEERING_WHEEL_ANGLE, steeringWheelAngle));

		// TODO Auto-generated method stub
		LOGGER.debug("HMI cyclic()");

	}

	@Override
	public void receiveSignal(Signal s) {
		// TODO Auto-generated method stub
		LOGGER.debug("HMI receiveSignal()");
	}

}
