package com.unideb.bosch.instrumentclusterdisplay;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;

import javax.swing.JFrame;

import com.unideb.bosch.automatedcar.AutomatedCar;

public class VirtualDisplay extends JFrame {

	private static final long serialVersionUID = 1;
	private VirtualDisplaySurface surface;
	private Insets windowDecorationDims;

	public VirtualDisplay(AutomatedCar car) {
		this.surface = new VirtualDisplaySurface(car);
		windowDecorationDims = getInsets();
		if (car == null) {
			this.setTitle("Instrument Cluster Dummy Mode");
		} else {
			this.setTitle("Instrument Cluster for car: " + car.hashCode());
		}
		this.setLayout(new GridLayout());
		this.add(this.surface);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
	}

	@Override
	public Dimension getPreferredSize() {
		windowDecorationDims = getInsets();
		return new Dimension(768, 256 + windowDecorationDims.top);
	}

	public void set_Actual_KMH_Needle_Angle(int angle) {
		this.surface.set_Actual_KMH_Needle_Angle(angle);
	}

	public void set_Actual_RPM_Needle_Angle(int angle) {
		this.surface.set_Actual_RPM_Needle_Angle(angle);
	}

	public void set_Actual_R(boolean state) {
		this.surface.set_Actual_R(state);
	}

	public void set_Actual_N(boolean state) {
		this.surface.set_Actual_N(state);
	}

	public void set_Actual_D(boolean state) {
		this.surface.set_Actual_D(state);
	}

	public void set_Actual_P(boolean state) {
		this.surface.set_Actual_P(state);
	}

	public void set_Actual_RightIndex(boolean state) {
		this.surface.set_Actual_RightIndex(state);
	}

	public void set_Actual_LeftIndex(boolean state) {
		this.surface.set_Actual_LeftIndex(state);
	}

	public void set_Actual_Headlights(boolean state) {
		this.surface.set_Actual_Headlights(state);
	}

	public void set_Actual_SteeringWheel_Angle(int angle) {
		this.surface.set_Actual_SteeringWHeel_Angle(angle);
	}

	public void set_TSR_Status(boolean state) {
		this.surface.set_TSR_Status(state);
	}

	public void set_TSR_ActualSpeedLimit(int speedlimit) {
		this.surface.set_TSR_ActualSpeedLimit(speedlimit);
	}

	public void set_TSR_StopSign(boolean state) {
		this.surface.set_TSR_StopSign(state);
	}

	public void set_TSR_NoSpeedLimit(boolean state) {
		this.surface.set_TSR_NoSpeedLimit(state);
	}

	public void set_TSR_SixtyInCity(boolean state) {
		this.surface.set_TSR_SixtyInCity(state);
	}

	public void set_TSR_Yield(boolean state) {
		this.surface.set_TSR_Yield(state);
	}
	
	public void set_ACC_Status(boolean state) {
		this.surface.set_ACC_Status(state);
	}
}
