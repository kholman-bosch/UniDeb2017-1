package com.unideb.bosch.acc;

public enum AdaptiveCruiseControlState {
	ACTIVE("Active"), DISABLED("Disabled"), STOPANDGO("Stop & Go"), SUSPENDED("Suspended");

	private final String value;

	AdaptiveCruiseControlState(String v) {
		value = v;
	}

	public String value() {
		return value;
	}
}
