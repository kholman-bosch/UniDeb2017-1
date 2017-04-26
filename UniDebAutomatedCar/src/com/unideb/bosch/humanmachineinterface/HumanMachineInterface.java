package com.unideb.bosch.humanmachineinterface;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.acc.AdaptiveCruiseControlState;
import com.unideb.bosch.automatedcar.VirtualWorld;
import com.unideb.bosch.automatedcar.VirtualWorldRenderer;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

public class HumanMachineInterface extends SystemComponent {

	private static final Logger LOGGER = LogManager.getLogger(HumanMachineInterface.class);

	private KeyListener keyListener;

	public HumanMachineInterface() {
		super();
		// LOGGER.debug("HMI created!");
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

	// By default TSR is enabled
	private boolean tsrEnabled = true;
	private boolean accEnabled = false;
	private float currentACCSetting = 0.0f; // the default is the cc speed

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

			// LOGGER.debug(keyEvent.getKeyCode() + " key were pressed!");
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
			case KeyEvent.VK_F1:
				VirtualWorldRenderer.showDebugWorldData = !VirtualWorldRenderer.showDebugWorldData;
				break;
			case KeyEvent.VK_F2:
				// Radar sensor debug data toggle
				// Turn off camera debug info
				VirtualWorldRenderer.showCameraDebugData = false;
				VirtualWorldRenderer.showRadarSensorDebugData = !VirtualWorldRenderer.showRadarSensorDebugData;
				break;
			case KeyEvent.VK_F3:
				// Camera debug data toggle
				// Turn off radar sensor debug info
				VirtualWorldRenderer.showRadarSensorDebugData = false;
				VirtualWorldRenderer.showCameraDebugData = !VirtualWorldRenderer.showCameraDebugData;
				break;
			}

			// LOGGER.debug(keyEvent.getKeyCode() + " key were released!");
		}

		int lastIndicatorSignal = 0;
		boolean isHeadLightOn = false;

		@Override
		public void keyTyped(KeyEvent keyEvent) {

			char character = keyEvent.getKeyChar();
			System.out.println("character typed: " + character);
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
			} else if (character == 't' || character == 'T') {
				tsrEnabled = !tsrEnabled;
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.TSR_MODULE_STATUS, tsrEnabled ? 1 : 0));
			} else if (character == 'a' || character == 'A') {
				LOGGER.debug("A key pressed!");
				LOGGER.debug("ACC status change " + (accEnabled ? "ENABLED" : "DISABLED") + " -> " + (!accEnabled ? "ENABLED" : "DISABLED") );
				accEnabled = !accEnabled;
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_STATUS_CHANGED, accEnabled ? 1 : 0));
			} else if (character == 's' || character == 'S') {
				if( currentACCSetting == 0.0f ) {
					currentACCSetting = 1.0f;
				} else {
					currentACCSetting = 0.0f;	
				}
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_SETTING_SWITCHED, currentACCSetting));
			} else if (character == '+') {
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_CHANGE_VALUE, 1.0f));
			} else if (character == '-') {
				VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.ACC_CHANGE_VALUE, 0.0f));
			}

			// LOGGER.debug(keyEvent.getKeyCode() + " key were typed!");
		}

	}

	@Override
	public void cyclic() {
		if (gasPressed) {
			if (gasPedalPosition < 100) {
				gasPedalPosition += 5;
			}
		} else {
			gasPedalPosition = 0;
		}

		if (breakPressed) {
			if (breakPedalPosition < 100) {
				breakPedalPosition += 5;
			}
		} else {
			breakPedalPosition = 0;
		}

		if (left && steeringWheelAngle > -720) {
			steeringWheelAngle -= 4;
		}
		if (right && steeringWheelAngle < 720) {
			steeringWheelAngle += 4;
		}

		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.GAS_PEDAL_POSITION, gasPedalPosition));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.BRAKE_PEDAL_POSITION, breakPedalPosition));
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.STEERING_WHEEL_ANGLE, steeringWheelAngle));
	}

	@Override
	public void receiveSignal(Signal s) {
		
		if( s.getID() == SignalDatabase.ACC_STATUS_CHANGED ){
			LOGGER.debug("ACC_STATUS_CHANGED SIGNAL RECEIVED: " + s.getData());
			
			int actValue = new Float(s.getData()).intValue();
			switch( actValue ){
			case 0: // DISABLED
				this.accEnabled = false;
				break;
			case 1: // ACTIVE
				this.accEnabled = true;
				break;
			case 2: // SUSPENDED
				break;
			case 3: // STOPANDGO
				break;
			}
			
			LOGGER.debug("ACC IS NOW " + (accEnabled ? "ENABLED" : "DISABLED")  );
		}
	}

}
