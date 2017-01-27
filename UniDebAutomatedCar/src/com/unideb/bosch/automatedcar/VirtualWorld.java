package com.unideb.bosch.automatedcar;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.unideb.bosch.automatedcar.AutomatedCar;
import com.unideb.bosch.automatedcar.framework.WorldObject;

public final class VirtualWorld {
	
	private static final int cyclePeriod = 100;
	
	private static WorldObjectParser world = WorldObjectParser.getInstance();
	private static AutomatedCar car = new AutomatedCar();
	
    private static JFrame frame = new JFrame("UniDeb Automated Car Project");
	
	
	private static void refreshFrame() {
	    frame.invalidate();
	    frame.validate();
	    frame.repaint();
	}

	public static void main(String[] args) {
			
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JPanel()
		{
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				
			    for (WorldObject object : world.getWorldObjects()) {
					// Paint objects
				    g.setColor(Color.black);
				    g.drawRect(object.getX(), object.getY(), 25, 25);
				    BufferedImage image;
					try {
						image = ImageIO.read(new File("c:/UniDebRepo/UniDebAutomatedCar/world/"+object.getImageFile()));						
						g.drawImage(image, object.getX(), object.getY(), null);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			    // Paint car
			    g.setColor(Color.black);
			    g.fillRect(car.getX(), car.getY(), 8, 8);
 
			}
		}
		);
	    
	    frame.validate();
	    frame.setSize(world.getWidth(), world.getHeight());
	    frame.setVisible(true);
	        
	    while(true)
	    {   	
	    	try {
	    		car.drive();
		    	refreshFrame();
				Thread.sleep(cyclePeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
	    }
	}
}
