package com.unideb.bosch.automatedcar;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
	
    public static Image resizeImage(Image image, int width, int height, boolean max) {
      if (width < 0 && height > 0) {
        return resizeImageBy(image, height, false);
      } else if (width > 0 && height < 0) {
        return resizeImageBy(image, width, true);
      } else if (width < 0 && height < 0) {
        return image;
      }
      int currentHeight = image.getHeight(null);
      int currentWidth = image.getWidth(null);
      int expectedWidth = (height * currentWidth) / currentHeight;
      int size = height;
      if (max && expectedWidth > width) {
        size = width;
      } else if (!max && expectedWidth < width) {
        size = width;
      }
      return resizeImageBy(image, size, (size == width));
    }

    public static Image resizeImageBy(Image image, int size, boolean setWidth) {
      if (setWidth) {
        return image.getScaledInstance(size, -1, Image.SCALE_FAST);
      } else {
        return image.getScaledInstance(-1, size, Image.SCALE_FAST);
      }
    }
    
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
				// Use a buffered image as our canvas
				BufferedImage worldImage = new BufferedImage(world.getWidth(), world.getHeight(), BufferedImage.TYPE_INT_RGB);
				Graphics2D worldDisplay = worldImage.createGraphics();
				// Draw the background
				worldDisplay.drawImage(backgroundImage, 0, 0, null);
				// Draw the car
				AffineTransformOp scale = new AffineTransformOp(AffineTransform.getScaleInstance(2, 2), AffineTransformOp.TYPE_BILINEAR);
				worldDisplay.drawImage(scale.filter(carImage, null), car.getX(), car.getY(), null);
				
				// Resize the display to fit the window
				g.drawImage(resizeImage(worldImage, frame.getWidth(), frame.getHeight(), true), 0, 0, this);
			}
		}
		);
	    
	    frame.validate();
	    frame.setSize(800, 600);
	    frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
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
