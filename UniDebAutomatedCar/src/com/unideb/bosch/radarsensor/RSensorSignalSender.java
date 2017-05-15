package com.unideb.bosch.radarsensor;

import java.util.ArrayList;

import com.unideb.bosch.SignalDatabase;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.framework.WorldObject;

public class RSensorSignalSender {

	private static ArrayList<RSensorDetectedObjectAttributes> objectList = new ArrayList<RSensorDetectedObjectAttributes>();

	private RSensorSignalSender() {
	}

	// this method creates a list of most dangerous objects
	private static void filterOut_LessDangerousObjects(RSensor radar) {
		objectList.clear();
		ArrayList<RSensorDetectedObjectAttributes> detectedObjects_bySensor = radar.get_Detected_WorldObjects();
		int sensorX = radar.radarPos_X;
		int sensorY = radar.radarPos_Y;

		for (int i = 0; i < detectedObjects_bySensor.size(); i++) {
			WorldObject actObj = detectedObjects_bySensor.get(i).parentWorldObject;
			RSensorDetectedObjectAttributes actObjWithAtts = detectedObjects_bySensor.get(i);
			int actObj_X_PosWithPreditcion = actObj.getX() + (int) actObjWithAtts.longitudinalRelative_Velcity;
			int actObj_Y_PosWithPreditcion = actObj.getY() + (int) actObjWithAtts.lateralRelative_Velcity;
			float dx = (sensorX - actObj_X_PosWithPreditcion) * (sensorX - actObj_X_PosWithPreditcion);
			float dy = (sensorY - actObj_Y_PosWithPreditcion) * (sensorY - actObj_Y_PosWithPreditcion);
			float distance = (float) Math.sqrt((double) (dx + dy));
			actObjWithAtts.dangerValue = (int) distance;
		}

		boolean added = false;
		RSensorDetectedObjectAttributes actObjWithAtts, actObjinMyList;
		for (int i = 0; i < detectedObjects_bySensor.size(); i++) {
			added = false;
			actObjWithAtts = detectedObjects_bySensor.get(i);
			for (int j = 0; j < objectList.size(); j++) {
				actObjinMyList = objectList.get(j);
				if (actObjWithAtts.dangerValue < actObjinMyList.dangerValue) {
					objectList.add(j, actObjWithAtts);
					added = true;
					break;
				}
			}
			if (!added) {
				objectList.add(actObjWithAtts);
			}
		}
		// Maximized the list size
		int maxDetectableObjsNum = radar.getMaxDetectableObjsNum();
		if (maxDetectableObjsNum < objectList.size()) {
			for (int i = maxDetectableObjsNum; i < objectList.size(); i++) {
				objectList.remove(i);
			}
		}
	}

	public static void send_Radar_Sensor_Signals(RSensor radar, VirtualFunctionBus virtFuncBus) {
		filterOut_LessDangerousObjects(radar);
		RSensorDetectedObjectAttributes actRadarObject;
		for (int i = 0; i < objectList.size(); i++) {
			actRadarObject = objectList.get(i);
			// System.out.println(i + " " + actRadarObject.parentWorldObject.getType() + " " + actRadarObject.dangerValue);
			virtFuncBus.sendSignal(new Signal(SignalDatabase.RADAR_LONGITUDINAL_RELATIVE_VELOCITY, (int) actRadarObject.longitudinalRelative_Velcity));
			virtFuncBus.sendSignal(new Signal(SignalDatabase.RADAR_LONGITUDINAL_DISTANCE_FROM_EGO, (int) actRadarObject.longitudinalDistance_From_EGO));
			virtFuncBus.sendSignal(new Signal(SignalDatabase.RADAR_LATERAL_RELATIVE_VELOCITY, (int) actRadarObject.longitudinalRelative_Velcity));
			virtFuncBus.sendSignal(new Signal(SignalDatabase.RADAR_LATERAL_DISTANCE_FROM_EGO, (int) actRadarObject.lateralDistance_From_EGO));
			virtFuncBus.sendSignal(new Signal(SignalDatabase.OBJECT_SIZE, actRadarObject.parentWorldObject.getRadius()));
		}
	}
}
