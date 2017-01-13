package com.unideb.bosch.automatedcar.framework;

/**
 * This class represent a single signal on the bus.
 * Signals can be handled by implementing the ISystemComponent interface.
 * 
 * In a real environment signals are encapsulated by frames (terminology depends on the network)
 * and a frame can contain several signals. This way the throughput of the network is utilized
 * more efficiently.
 * 
 * For the simulation environment let's assume that each frame contains a single message,
 * so we do not need to bother extracting signal data from frames. 
 *
 * Students must not modify this interface!
 */

public class Signal {
	
	// Signal Identifier, a component can decide based on this value
	// whether the content of the signal shall be processed or not.
	private int ID;
	
	// Signal value, the meaning of the value is provided by the CommMatrix
	private long Data;
	
	// Constructor for Signal
	public Signal(int _id, long _data) {
		this.ID = _id;
		this.Data = _data;
	}
	
	// Getter for Signal ID
	public int getID() {
		return ID;
	}
	
	// Getter for Signal Value
	public long getData() {
		return Data;
	}	
}
