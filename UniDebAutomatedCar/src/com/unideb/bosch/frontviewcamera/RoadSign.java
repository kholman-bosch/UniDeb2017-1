package com.unideb.bosch.frontviewcamera;

/**
 * @author Nandi
 * This class makes it possible to compare RoadSign objects by distance and then we can simply arrange them in a list
 * to get to 5 closest road sign with it's signal.
 *
 */
public class RoadSign implements Comparable<RoadSign> {
	
	private final float distance;
	private final long longitudinalEGO;
	private final long lateralEGO;
	private final long trafficSignMeaing;
	
	
	

	public RoadSign(float distance, long longitudinalEGO, long lateralEGO, long trafficSignMeaing) {
		super();
		this.distance = distance;
		this.longitudinalEGO = longitudinalEGO;
		this.lateralEGO = lateralEGO;
		this.trafficSignMeaing = trafficSignMeaing;
	}


	

	public float getDistance() {
		return distance;
	}




	public long getLongitudinalEGO() {
		return longitudinalEGO;
	}




	public long getLateralEGO() {
		return lateralEGO;
	}




	public long getTrafficSignMeaing() {
		return trafficSignMeaing;
	}




	@Override
	public int compareTo(RoadSign o) {
		if(o.getDistance() > this.distance){
			return -1;
		}
		if(o.getDistance() < this.distance){
			return 1;
		}
		return 0;
	}

}
