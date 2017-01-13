package com.unideb.bosch.automatedcar.framework;
import com.unideb.bosch.automatedcar.framework.Signal;

/**
 * This interface provides the necessary methods for system components.
 * Every component has to realize this interface to be able to connect to
 * the car!
 *
 * Students must not modify this interface!
 */

public interface ISystemComponent {
	
	// Provide implementation for component calculation
	public void cyclic();
	
	// Use this, to receive message from other components
	public void receiveSignal(Signal s);
}
