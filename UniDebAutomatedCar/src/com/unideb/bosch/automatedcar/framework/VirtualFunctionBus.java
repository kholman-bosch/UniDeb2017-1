package com.unideb.bosch.automatedcar.framework;

import java.util.ArrayList;

/**
 * This is the singleton class for the Virtual Function Bus. Components are only allowed to collect sensory data exclusively using the VFB. The VFB stores the input and output signals, inputs only have setters, while outputs only have getters
 * respectively.
 *
 * Students must not modify this class!
 *
 */
public class VirtualFunctionBus {

	public VirtualFunctionBus() {
	};

	private ArrayList<ISystemComponent> components = new ArrayList<ISystemComponent>();

	public void registerComponent(ISystemComponent comp) {
		this.components.add(comp);
		// System.out.println("System component " + comp.toString() + " is registered on the virtual function bus");
	}

	public void sendSignal(Signal s) {
		// System.out.println("Broadcast signal " + s.toString());
		// Broadcast the signal to all system components
		for (ISystemComponent comp : components) {
			comp.receiveSignal(s);
		}
	}

	public void cyclic() {
		// Once the virtual function bus has started components are called cyclically
		for (ISystemComponent comp : components) {
			// System.out.println("Calling cyclic function of " + comp.toString());
			comp.cyclic();
		}
	}
}
