package com.unideb.bosch.frontviewcamera;

/**
 * @author Nandi
 * This class makes it possible to compare RoadSign objects by distance and then we can simply arrange them in a list
 * to get to 5 closest road sign with it's signal.
 *
 */
public class RoadSign implements Comparable<RoadSign> {
	
	public static final int ROAD_SIGN_SPEED_5 = 0;
	public static final int ROAD_SIGN_SPEED_10 = 1;
	public static final int ROAD_SIGN_SPEED_20 = 2;
	public static final int ROAD_SIGN_SPEED_30 = 3;
	public static final int ROAD_SIGN_SPEED_40 = 4;
	public static final int ROAD_SIGN_SPEED_50 = 5;
	public static final int ROAD_SIGN_SPEED_60 = 6;
	public static final int ROAD_SIGN_SPEED_70 = 7;
	public static final int ROAD_SIGN_SPEED_80 = 8;
	public static final int ROAD_SIGN_SPEED_90 = 9;
	public static final int ROAD_SIGN_SPEED_100 = 10;
	public static final int ROAD_SIGN_SPEED_110 = 11;
	public static final int ROAD_SIGN_SPEED_120 = 12;
	public static final int ROAD_SIGN_SPEED_130 = 13;
	public static final int ROAD_SIGN_DIRECTION_LEFTONLY = 14;
	public static final int ROAD_SIGN_DIRECTION_RIGHTONLY = 15;
//	public static final int ROAD_SIGN_DIRECTION_ = 16;
//	public static final int ROAD_SIGN_DIRECTION_ = 17;
	public static final int ROAD_SIGN_DIRECTION_ROUNDABOUT = 18;
//	public static final int ROAD_SIGN_DIRECTION_ = 19;
//	public static final int ROAD_SIGN_DIRECTION_ = 20;
//	public static final int ROAD_SIGN_DIRECTION_ = 21;
//	public static final int ROAD_SIGN_DIRECTION_ = 22;
//	public static final int ROAD_SIGN_ = 23;
	public static final int ROAD_SIGN_PRIORITY_STOP = 24;
	public static final int ROAD_SIGN_PRIORITY_MAINROAD = 25;
//	public static final int ROAD_SIGN_PRIORITY_ = 26;
	
	private double distance;
	private float longitudinalEGO;
	private float lateralEGO;
	private long trafficSignMeaing;
	
	public RoadSign(double distance, float longitudinalEGO, float lateralEGO, long trafficSignMeaing) {
		super();
		this.distance = distance;
		this.longitudinalEGO = longitudinalEGO;
		this.lateralEGO = lateralEGO;
		this.trafficSignMeaing = trafficSignMeaing;
	}

	public double getDistance() {
		return distance;
	}

	public float getLongitudinalEGO() {
		return longitudinalEGO;
	}

	public float getLateralEGO() {
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