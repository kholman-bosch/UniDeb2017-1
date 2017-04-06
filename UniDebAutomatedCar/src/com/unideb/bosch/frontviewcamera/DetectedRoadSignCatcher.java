package com.unideb.bosch.frontviewcamera;

import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

public class DetectedRoadSignCatcher extends SystemComponent {

	@Override
	public void cyclic() {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void receiveSignal(Signal s) {
		switch( s.getID() ){
		case 16:
			System.out.println("ROAD SIGN DETECTED! " + s.getData());
			break;
		default:
			break;
		}
	}

}
