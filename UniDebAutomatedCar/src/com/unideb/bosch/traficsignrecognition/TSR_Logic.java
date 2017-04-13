package com.unideb.bosch.traficsignrecognition;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.frontviewcamera.RoadSign;

public class TSR_Logic extends SystemComponent {

	private boolean on = true;
	private boolean discardSignSignals = false;
	private int actualSpeedLimit = 0;
	private boolean stopSignDetected = false;
	private boolean noSpeedLimitSignDetected = false;
	private boolean sixtyInCitySignDetected = false;
	private boolean yieldSignDetected = false;

	@Override
	public void cyclic() {
		if (this.discardSignSignals) {
			this.yieldSignDetected = false;
			this.sixtyInCitySignDetected = false;
			this.noSpeedLimitSignDetected = false;
			this.stopSignDetected = false;
			this.actualSpeedLimit = 0;
		}
		// TO DO: sign drop logic
		this.sendSignals();
	}

	public void sendSignals() {
		if (this.stopSignDetected) {
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.SHOW_SUPPLEMENTAL_SIGNS_ON_IC, 1));
		}
		if (this.noSpeedLimitSignDetected) {
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.SHOW_SUPPLEMENTAL_SIGNS_ON_IC, 0));
		}
		if (this.sixtyInCitySignDetected) {
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.SHOW_SUPPLEMENTAL_SIGNS_ON_IC, 2));
		}
		if (this.yieldSignDetected) {
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.SHOW_SUPPLEMENTAL_SIGNS_ON_IC, 3));
		}
		VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.MOST_RELEVANT_SPEED_LIMIT, this.actualSpeedLimit));
	}

	@Override
	public void receiveSignal(Signal s) {
		switch (s.getID()) {
		case SignalDatabase.TRAFFIC_SIGN_MEANING:
			switch ((int) s.getData()) {
			case RoadSign.ROAD_SIGN_PRIORITY_STOP:
				this.stopSignDetected = true;
				break;
			case RoadSign.ROAD_SIGN_PRIORITY_MAINROAD:
				this.sixtyInCitySignDetected = true;
				break;
			////
			case RoadSign.ROAD_SIGN_SPEED_5:
				if (this.actualSpeedLimit < 5) {
					this.actualSpeedLimit = 5;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_10:
				if (this.actualSpeedLimit < 10) {
					this.actualSpeedLimit = 10;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_20:
				if (this.actualSpeedLimit < 20) {
					this.actualSpeedLimit = 20;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_30:
				if (this.actualSpeedLimit < 30) {
					this.actualSpeedLimit = 30;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_40:
				if (this.actualSpeedLimit < 40) {
					this.actualSpeedLimit = 40;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_50:
				if (this.actualSpeedLimit < 50) {
					this.actualSpeedLimit = 50;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_60:
				if (this.actualSpeedLimit < 60) {
					this.actualSpeedLimit = 60;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_70:
				if (this.actualSpeedLimit < 70) {
					this.actualSpeedLimit = 70;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_80:
				if (this.actualSpeedLimit < 80) {
					this.actualSpeedLimit = 80;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_90:
				if (this.actualSpeedLimit < 90) {
					this.actualSpeedLimit = 90;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_100:
				if (this.actualSpeedLimit < 100) {
					this.actualSpeedLimit = 100;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_110:
				if (this.actualSpeedLimit < 110) {
					this.actualSpeedLimit = 110;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_120:
				if (this.actualSpeedLimit < 120) {
					this.actualSpeedLimit = 120;
				}
				break;
			case RoadSign.ROAD_SIGN_SPEED_130:
				if (this.actualSpeedLimit < 130) {
					this.actualSpeedLimit = 130;
				}
				break;
			}
			break;
		case SignalDatabase.GEAR_POSITION:
			switch ((int) s.getData()) {
			case 0: // D: 0
			case 1: // N: 1
				this.discardSignSignals = false;
				break;
			case 2: // R: 2
			case 3: // P: 3
				this.discardSignSignals = true;
				break;
			}
			break;
		case SignalDatabase.TSR_MODULE_STATUS:
			if (s.getData() == 0) {
				this.on = false;
			} else {
				this.on = true;
			}
			break;
		}
	}
}
