package com.unideb.bosch.frontviewcamera;

import com.unideb.bosch.automatedcar.framework.Signal;

/**
 * @author Nandi
 * This class makes it possible to compare RoadSign objects by distance and then we can simply arrange them in a list
 * to get to 5 closest road sign with it's signal.
 *
 */
public class RoadSign implements Comparable<Float> {
	
	private final float distance;
	private final Signal signal;
	
	public RoadSign(float distance, Signal signal) {
		super();
		this.distance = distance;
		this.signal = signal;
	}

	public float getDistance() {
		return distance;
	}

	public Signal getSignal() {
		return signal;
	}

	@Override
	public int compareTo(Float o) {
		if(o > this.distance){
			return -1;
		}
		if(o < this.distance){
			return 1;
		}
		return 0;
	}

}
