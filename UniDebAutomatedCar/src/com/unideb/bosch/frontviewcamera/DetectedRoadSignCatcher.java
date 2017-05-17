package com.unideb.bosch.frontviewcamera;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

/**
 * Debug class for camera signals.
 * */
public class DetectedRoadSignCatcher extends SystemComponent {

	private boolean roadSignDetected = false;
	private boolean latArrived = false;
	private boolean lonArrived = false;
	
	private long latEGO;
	private long lonEGO;
	private long roadSignMeaning;
	
	public DetectedRoadSignCatcher(VirtualFunctionBus virtFuncBus){
		super(virtFuncBus);
	}
	
	@Override
	public void cyclic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSignal(Signal s) {
		if( !roadSignDetected ){
			// getting the first signal of a detection.
			switch( s.getID() ){
			case 16:
				//System.out.println("ROAD SIGN DETECTED!");
				roadSignMeaning = (long)s.getData();
				roadSignDetected = true;
				break;
			default:
				break;
			}	
		} else {
			// road sign already detected, wait for the lat/lon signals.
			switch( s.getID() ){
			case 17:
				lonEGO = (long) s.getData();
				lonArrived = true;
				break;
			case 18:
				latEGO = (long) s.getData();
				latArrived = true;
				break;
			}
		}
		
		if( roadSignDetected && lonArrived && latArrived ){
			roadSignDetected = false;
			lonArrived = false;
			latArrived = false;
			
			//System.out.println("Road sign detected correctly! Meaning: " + this.roadSignMeaning + " longitudinalEGO: " + lonEGO + " latitudinalEGO: " + latEGO);
		}
		
	}

}
