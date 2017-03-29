package com.unideb.bosch.radarsensor;

import java.util.ArrayList;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.instrumentclusterdisplay.SignalDatabase;

public class RSensorSignalSender {

	private RSensorSignalSender() {
	}

	public static void send_Radar_Sensor_Signals(RSensor radar) {
		ArrayList<RSensorDetectedObjectAttributes> objectList = radar.get_Detected_WorldObjects();
		RSensorDetectedObjectAttributes actRadarObject;
		for (int i = 0; i < objectList.size(); i++) {
			actRadarObject = objectList.get(i);
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LONGITUDINAL_RELATIVE_VELOCITY,
					(int) actRadarObject.longitudinalRelative_Velcit));
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LONGITUDINAL_DISTANCE_FROM_EGO,
					(int) actRadarObject.longitudinalDistance_From_EGO));
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LATERAL_RELATIVE_VELOCITY,
					(int) actRadarObject.longitudinalRelative_Velcit));
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LATERAL_DISTANCE_FROM_EGO,
					(int) actRadarObject.lateralDistance_From_EGO));
		}
	}
}
