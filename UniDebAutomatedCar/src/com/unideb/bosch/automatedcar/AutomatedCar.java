package com.unideb.bosch.automatedcar;

import java.awt.Color;
import java.awt.Graphics;

import javax.swing.JPanel;

import com.unideb.bosch.automatedcar.framework.VirtualFunctionBus;
import com.unideb.bosch.automatedcar.vehicleparts.Driver;
import com.unideb.bosch.automatedcar.vehicleparts.PowertrainSystem;

public final class AutomatedCar extends JPanel{

	private static final long serialVersionUID = 1L;
	private int x = 100;
	private int y = 100;
	private int angle = 0;
	
	public AutomatedCar() {
		// Compose our car from brand new system components
		new PowertrainSystem(this);
		// Place a driver into our car
		new Driver();
	}

	public void setPosition(int positionX, int positionY, int ang) {
		x = positionX;
		y = positionY;
		angle = ang;
	}
	
	public void paintComponent(Graphics g) {
	    g.setColor(Color.black);
	    g.drawRect(x, y, 8, 8);
	  }
	
	
	public void drive() {	
		VirtualFunctionBus.cyclic();
	}
}
