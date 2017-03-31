package com.unideb.bosch.radarsensor;

import java.util.ArrayList;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.instrumentclusterdisplay.SignalDatabase;

public class RSensorSignalSender {

	private static int maximum_DetectableDangerousObjects = 5;
	private static ArrayList<RSensorDetectedObjectAttributes> objectList;

	private RSensorSignalSender(int max_DetectableDangerousObjects) {
		maximum_DetectableDangerousObjects = max_DetectableDangerousObjects;
		objectList = new ArrayList<RSensorDetectedObjectAttributes>();
	}
	
	//this method creates a list of most dangerous objects
	private static void filterOut_LessDangerousObjects(RSensor radar) {
		objectList.clear();
		ArrayList<RSensorDetectedObjectAttributes> localList = radar.get_Detected_WorldObjects();
		for (int i = 0; i < localList.size(); i++) {
			// TODO: remove objects from list based on danger value
			// objectList
			//dangerValue
		}
	}

	public static void send_Radar_Sensor_Signals(RSensor radar) {
		filterOut_LessDangerousObjects(radar);
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
