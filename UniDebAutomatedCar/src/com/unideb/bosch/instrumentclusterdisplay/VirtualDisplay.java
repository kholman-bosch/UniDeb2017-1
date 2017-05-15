package com.unideb.bosch.instrumentclusterdisplay;

import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.KeyListener;

import javax.swing.JFrame;

import com.unideb.bosch.acc.AdaptiveCruiseControlState;
import com.unideb.bosch.automatedcar.AutomatedCar;
import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;

public class VirtualDisplay extends JFrame {

	private static final long serialVersionUID = 1;
	private VirtualDisplaySurface surface;
	private InstrumentClusterLogic icl;
	private Insets windowDecorationDims;

	public VirtualDisplay(AutomatedCar car, VirtualFunctionBus virtFuncBus) {
		this.surface = new VirtualDisplaySurface(car);
		windowDecorationDims = getInsets();
		if (car == null) {
			this.setTitle("Instrument Cluster Dummy Mode");
		} else {
			this.setTitle("Instrument Cluster for car: " + car.hashCode());
		}
		this.icl = new InstrumentClusterLogic(virtFuncBus);
		this.setLayout(new GridLayout());
		this.add(this.surface);
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
	}

	public void addKeyListenerToFrame(KeyListener keyListener) {
		this.addKeyListener(keyListener);
	}

	public void update() {
		this.surface.set_Actual_KMH_Needle_Angle(this.icl.getVehicleSpeed());
		this.surface.set_Actual_RPM_Needle_Angle(this.icl.getMotorRPM());
		this.surface.set_Actual_N(this.icl.getGearPos_N_Status());
		this.surface.set_Actual_R(this.icl.getGearPos_R_Status());
		this.surface.set_Actual_D(this.icl.getGearPos_D_Status());
		this.surface.set_Actual_P(this.icl.getGearPos_P_Status());
		this.surface.set_Actual_RightIndex(this.icl.getRightTurnSignalStatus());
		this.surface.set_Actual_LeftIndex(this.icl.getLeftTurnSignalStatus());
		this.surface.set_Actual_Headlights(this.icl.getHeadlightStatus());
		this.surface.set_Actual_SteeringWHeel_Angle(this.icl.getSteeringWheelAngle());
		this.surface.set_TSR_Status(this.icl.is_TSR_Active());
		this.surface.set_ACC_Status(this.icl.getAccState());
		this.surface.set_TSR_ActualSpeedLimit(this.icl.get_TSR_ActualSpeedLimit());
		this.surface.set_TSR_NoSpeedLimit(this.icl.get_TSR_NoSpeedLimit());
		this.surface.set_TSR_SixtyInCity(this.icl.get_TSR_SixtyInCity());
		this.surface.set_TSR_StopSign(this.icl.get_TSR_StopSign());
		this.surface.set_TSR_Yield(this.icl.get_TSR_Yield());
		this.surface.set_CCS(this.icl.get_CruseControlSpeed());
		this.surface.set_SD(this.icl.get_SafeDistance());
		this.surface.invalidate();
		this.surface.validate();
		this.surface.repaint();
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

	public void set_CCS(int speed) {
		this.surface.set_CCS(speed);
	}

	public void set_SD(float distance) {
		this.surface.set_SD(distance);
	}

	public void set_ACC_Status(AdaptiveCruiseControlState state) {
		this.surface.set_ACC_Status(state);
	}
}
