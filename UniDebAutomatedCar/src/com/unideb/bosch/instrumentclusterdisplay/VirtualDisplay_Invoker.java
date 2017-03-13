package com.unideb.bosch.instrumentclusterdisplay;

import com.unideb.bosch.automatedcar.AutomatedCar;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

public class VirtualDisplay_Invoker extends SystemComponent {

	private VirtualDisplay vd;

	public VirtualDisplay_Invoker(AutomatedCar car) {
		this.vd = new VirtualDisplay(car);
	}

	@Override
	public void cyclic() {
		this.vd.invalidate();
		this.vd.validate();
		this.vd.repaint();
	}

	@Override
	public void receiveSignal(Signal s) {
		//
	}
}
