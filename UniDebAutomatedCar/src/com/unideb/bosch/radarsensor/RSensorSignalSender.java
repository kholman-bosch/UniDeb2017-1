package com.unideb.bosch.radarsensor;

import java.util.ArrayList;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.framework.WorldObject;
import com.unideb.bosch.instrumentclusterdisplay.SignalDatabase;

public class RSensorSignalSender {

	private static int maximum_DetectableDangerousObjects = 5;
	private static ArrayList<RSensorDetectedObjectAttributes> objectList = new ArrayList<RSensorDetectedObjectAttributes>();

	private RSensorSignalSender() {
	}

	// this method creates a list of most dangerous objects
	private static void filterOut_LessDangerousObjects(RSensor radar) {
		objectList.clear();
		ArrayList<RSensorDetectedObjectAttributes> localList = radar.get_Detected_WorldObjects();
		int sensorX = radar.getCar().getRadarSensor_X();
		int sensorY = radar.getCar().getRadarSensor_Y();

		for (int i = 0; i < localList.size(); i++) {
			WorldObject actObj = localList.get(i).parentWorldObject;
			RSensorDetectedObjectAttributes actObjWithAtts = localList.get(i);
			int actObj_X_PosWithPreditcion = actObj.getX() + (int) actObjWithAtts.longitudinalRelative_Velcity;
			int actObj_Y_PosWithPreditcion = actObj.getY() + (int) actObjWithAtts.lateralRelative_Velcity;
			float dx = (sensorX - actObj_X_PosWithPreditcion) * (sensorX - actObj_X_PosWithPreditcion);
			float dy = (sensorY - actObj_Y_PosWithPreditcion) * (sensorY - actObj_Y_PosWithPreditcion);
			float distance = (float) Math.sqrt((double) (dx + dy));
			actObjWithAtts.dangerValue = (int) distance;
		}

		boolean added = false;
		RSensorDetectedObjectAttributes actObjWithAtts, actObjinMyList;
		for (int i = 0; i < localList.size(); i++) {
			added = false;
			actObjWithAtts = localList.get(i);
			for (int j = 0; j < objectList.size(); j++) {
				actObjinMyList = objectList.get(j);
				if (actObjWithAtts.dangerValue < actObjinMyList.dangerValue) {
					if (j == 0) {
						objectList.add(j, actObjWithAtts);
					} else {
						objectList.add(j , actObjWithAtts);
					}
					added = true;
					break;
				}
			}
			if (!added) {
				objectList.add(actObjWithAtts);
			}
		}
		//Maximezd the list size
		if (maximum_DetectableDangerousObjects < objectList.size()) {
			for (int i = maximum_DetectableDangerousObjects; i < objectList.size(); i++) {
				objectList.remove(i);
			}
		}
	}

	public static void send_Radar_Sensor_Signals(RSensor radar) {
		filterOut_LessDangerousObjects(radar);
		RSensorDetectedObjectAttributes actRadarObject;
		for (int i = 0; i < objectList.size(); i++) {
			actRadarObject = objectList.get(i);
			System.out.println(i + " " + actRadarObject.parentWorldObject.getType() + " " + actRadarObject.dangerValue);
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LONGITUDINAL_RELATIVE_VELOCITY, (int) actRadarObject.longitudinalRelative_Velcity));
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LONGITUDINAL_DISTANCE_FROM_EGO, (int) actRadarObject.longitudinalDistance_From_EGO));
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LATERAL_RELATIVE_VELOCITY, (int) actRadarObject.longitudinalRelative_Velcity));
			VirtualFunctionBus.sendSignal(new Signal(SignalDatabase.LATERAL_DISTANCE_FROM_EGO, (int) actRadarObject.lateralDistance_From_EGO));
		}
	}
}
