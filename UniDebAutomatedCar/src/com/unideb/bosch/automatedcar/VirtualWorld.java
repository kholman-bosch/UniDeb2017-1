package com.unideb.bosch.automatedcar;

import java.awt.Graphics;
import java.awt.geom.AffineTransform;
import java.awt.image.AffineTransformOp;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;

import com.unideb.bosch.automatedcar.AutomatedCar;

public final class VirtualWorld {
	
	// Parameters
	private static final int cyclePeriod = 100;
	private static final String backgroundImagePath = "./world/road_1.png";
	private static final String carImagePath = "./world/bosch1.png";
	
	private static WorldObjectParser world = WorldObjectParser.getInstance();
	private static AutomatedCar car = new AutomatedCar();
	
    private static JFrame frame = new JFrame("UniDeb Automated Car Project");
    private static BufferedImage backgroundImage;
    private static BufferedImage carImage;
	
	
	private static void refreshFrame() {
	    frame.invalidate();
	    frame.validate();
	    frame.repaint();
	}

	public static void main(String[] args) {

		try {
			// Load background image
			backgroundImage =  ImageIO.read(new File(backgroundImagePath));
			// Load car image
			carImage =  ImageIO.read(new File(carImagePath));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JPanel()
		{
			private static final long serialVersionUID = 1L;
			public void paintComponent(Graphics g) {
				
				// Display map	
				// The image has a constant offset compared to the input XML
				g.drawImage(backgroundImage, -100, -100, null);
			
			    // Display car
				AffineTransformOp scale = new AffineTransformOp(AffineTransform.getScaleInstance(2, 2), AffineTransformOp.TYPE_BILINEAR);
			    g.drawImage(scale.filter(carImage, null), car.getX(), car.getY(), null);
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
