package com.unideb.bosch.instrumentclusterdisplay;

import com.unideb.bosch.automatedcar.AutomatedCar;
import com.unideb.bosch.automatedcar.framework.Signal;
import com.unideb.bosch.automatedcar.framework.SystemComponent;

public class VirtualDisplay_Invoker extends SystemComponent {

	private VirtualDisplay vd;
	private InstrumentClusterLogic icl;

	public VirtualDisplay_Invoker(AutomatedCar car, InstrumentClusterLogic icl_f) {
		this.vd = new VirtualDisplay(car);
		this.icl = icl_f;
	}

	@Override
	public void cyclic() {
		this.vd.set_Actual_KMH_Needle_Angle(this.icl.getVehicleSpeed());
		this.vd.set_Actual_RPM_Needle_Angle(this.icl.getMotorRPM());
		this.vd.set_Actual_N(this.icl.getGearPos_N_Status());
		this.vd.set_Actual_R(this.icl.getGearPos_R_Status());
		this.vd.set_Actual_D(this.icl.getGearPos_D_Status());
		this.vd.set_Actual_P(this.icl.getGearPos_P_Status());
		this.vd.set_Actual_RightIndex(this.icl.getRightTurnSignalStatus());
		this.vd.set_Actual_LeftIndex(this.icl.getLeftTurnSignalStatus());
		this.vd.set_Actual_Headlights(this.icl.getHeadlightStatus());
		this.vd.set_Actual_SteeringWheel_Angle(this.icl.getSteeringWheelAngle());
		this.vd.set_TSR_Status(this.icl.is_TSR_Active());
		this.vd.set_ACC_Status(this.icl.is_ACC_Active());
		this.vd.set_TSR_ActualSpeedLimit(this.icl.get_TSR_ActualSpeedLimit());
		this.vd.set_TSR_NoSpeedLimit(this.icl.get_TSR_NoSpeedLimit());
		this.vd.set_TSR_SixtyInCity(this.icl.get_TSR_SixtyInCity());
		this.vd.set_TSR_StopSign(this.icl.get_TSR_StopSign());
		this.vd.set_TSR_Yield(this.icl.get_TSR_Yield());
		this.vd.invalidate();
		this.vd.validate();
		this.vd.repaint();
	}

	@Override
	public void receiveSignal(Signal s) {
		//
	}
}
