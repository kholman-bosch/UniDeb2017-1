package com.unideb.bosch.automatedcar;

import javax.swing.JFrame;
import com.unideb.bosch.automatedcar.AutomatedCar;

public final class VirtualWorld {
	
	private static final int cyclePeriod = 100;
	
	private static AutomatedCar car = new AutomatedCar();
	
    private static JFrame frame = new JFrame("UniDeb Automated Car Project");
	
	
	private static void refreshFrame() {
	    frame.invalidate();
	    frame.validate();
	    frame.repaint();
	}

	public static void main(String[] args) {
			
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(car);
	    frame.setSize(300, 200);
	    frame.setVisible(true);
	        
	    while(true)
	    {   	
	    	try {
	    		car.drive();
		    	refreshFrame();
				Thread.sleep(cyclePeriod);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
	    }
	}
}
