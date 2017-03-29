package com.unideb.bosch.automatedcar;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.JPanel;
import org.apache.logging.log4j.core.pattern.AbstractStyleNameConverter.Yellow;
import com.unideb.bosch.automatedcar.framework.WorldObject;

public final class VirtualWorld {

	// Parameters
	private static final int cyclePeriod = 100;
	private static final String backgroundImagePath = "./world/road_1.png";
	private static WorldObjectParser world = WorldObjectParser.getInstance();
	private static AutomatedCar car;
	private static JFrame frame;
	private static BufferedImage backgroundImage;
	private static BufferedImage carImage;
	// debug
	private static int worldScaledWidth = 0;
	private static int worldScaledHeight = 0;
	private static boolean showDebugWorldData = true;
	private static Font serifFontBOLD = new Font("Serif", Font.BOLD, 12);

	static {
		// Order matters because the frame should exist when the HMI set the
		// KeyListener.
		frame = new JFrame("UniDeb Automated Car Project");
		car = new AutomatedCar();
	}

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

	public static void addKeyListenerToFrame(KeyListener keyListener) {
		frame.addKeyListener(keyListener);
	}

	public static Image resizeImageBy(Image image, int size, boolean setWidth) {
		if (setWidth) {
			Image scaledImage = image.getScaledInstance(size, -1, Image.SCALE_FAST);
			worldScaledWidth = scaledImage.getWidth(null);
			worldScaledHeight = scaledImage.getHeight(null);
			return scaledImage;
		} else {
			Image scaledImage = image.getScaledInstance(-1, size, Image.SCALE_FAST);
			worldScaledWidth = scaledImage.getWidth(null);
			worldScaledHeight = scaledImage.getHeight(null);
			return scaledImage;
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
			backgroundImage = ImageIO.read(new File(backgroundImagePath));
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new JPanel() {
			private static final long serialVersionUID = 1L;

			public void paintComponent(Graphics g) {
				// Use a buffered image as our canvas
				BufferedImage worldImage = new BufferedImage(world.getWidth(), world.getHeight(),
						BufferedImage.TYPE_INT_RGB);
				Graphics2D worldDisplay = worldImage.createGraphics();
				// Draw the background
				worldDisplay.drawImage(backgroundImage, 0, 0, null);
				// Draw the car
				// AffineTransformOp scale = new
				// AffineTransformOp(AffineTransform.getScaleInstance(2, 2),
				// AffineTransformOp.TYPE_BILINEAR);
				car.drawCar(worldDisplay);
				// worldDisplay.drawImage(scale.filter(carImage, null),
				// car.getX(), car.getY(), null);
				g.drawImage(resizeImage(worldImage, frame.getWidth(), frame.getHeight(), true), 0, 0, this);
				// Resize the display to fit the window
				// draw world debug data
				if (showDebugWorldData) {
					drawWorldObjectsDebugData(worldImage.getWidth(), worldImage.getHeight(), g);
				}
			}
		});

		frame.validate();
		frame.setSize(800, 600);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);

		while (true) {
			try {
				car.drive();
				refreshFrame();
				Thread.sleep(cyclePeriod);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	public static void drawWorldObjectsDebugData(int worldImageW, int worldImageH, Graphics g) {
		if (showDebugWorldData) {
			float scaleX = worldScaledWidth / (float) backgroundImage.getWidth();
			float scaleY = worldScaledHeight / (float) backgroundImage.getHeight();
			g.setFont(serifFontBOLD);
			int size = WorldObjectParser.getInstance().getWorldObjects().size();
			for (int i = 0; i < size; i++) {
				WorldObject worldObj = WorldObjectParser.getInstance().getWorldObjects().get(i);
				if (worldObj.getType().contains("road_2")) {
					continue;
				}
				g.drawString(worldObj.getType(), (int) (worldObj.getX() * scaleX), (int) (worldObj.getY() * scaleY));
			}
		}
	}
}
