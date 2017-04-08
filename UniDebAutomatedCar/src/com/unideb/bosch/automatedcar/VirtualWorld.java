package com.unideb.bosch.automatedcar;

import java.awt.Color;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;

public final class VirtualWorld {

	// Parameters
	private static final int visualisationCyclePeriod = 16; // ms 16ms means 60Hz
	private static final int logicCyclePeriod = 100; // ms 100ms means 10Hz
	private static WorldObjectParser world = WorldObjectParser.getInstance();
	private static ArrayList<AutomatedCar> cars = new ArrayList<AutomatedCar>();
	private static VirtualWorldJFrame frame;
	private static VirtualWorldRenderer renderer;
	// Render
	private static float graphics_Interpolation = 0f;
	private static int windowDecorationOffsetOn_Y = 0;

	static {
		// Order matters because the frame should exist when the HMI set the
		// KeyListener.
		frame = new VirtualWorldJFrame();
		cars.add(new AutomatedCar());
	}

	private static void refreshFrame() {
		renderer.invalidate();
		renderer.validate();
		renderer.repaint();
	}

	public static void main(String[] args) {
		int windowWidth = 800;
		int windowHieght = 600;
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(windowWidth, windowHieght);
		renderer = new VirtualWorldRenderer(windowWidth, windowHieght);
		frame.add(renderer);
		frame.validate();
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setVisible(true);
		windowDecorationOffsetOn_Y = frame.getInsets().top; // dirty fix for the window decoration problem
		loop();
	}

	private static void loop() {
		double preferredTickRate = logicCyclePeriod;
		double currentTime = getNanotimeInMS();
		double time_Accumulator = 0d;
		while (true) {
			double newTime = getNanotimeInMS();
			double frameTime = newTime - currentTime;
			currentTime = newTime;
			time_Accumulator += frameTime;
			while (time_Accumulator >= preferredTickRate) {
				driveCars();
				time_Accumulator -= preferredTickRate;
			}
			graphics_Interpolation = (float) (time_Accumulator / preferredTickRate);
			renderer.windowResizeLogic(frame.getWidth(), frame.getHeight() - windowDecorationOffsetOn_Y);
			refreshFrame();
			try {
				Thread.sleep(visualisationCyclePeriod); // still using the unstable Thread.sleep. If good precision was needed we should spinsleep and burn cpu time
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private static void driveCars() {
		for (int i = 0; i < cars.size(); i++) {
			cars.get(i).drive();
		}
	}

	public static ArrayList<AutomatedCar> getCars() {
		return cars;
	}

	public static float getGraphicsScale() {
		return renderer.getGraphicsScale();
	}

	public static float getGraphicsInterpolationValue() {
		return graphics_Interpolation;
	}

	private static double getNanotimeInMS() {
		return (System.nanoTime() / 1000000d);
	}

	public static void addKeyListenerToFrame(KeyListener keyListener) {
		frame.addKeyListener(keyListener);
	}

	public static BufferedImage scale(BufferedImage imageToScale, int newWidth, int newHeight) {
		if (newWidth <= 0) {
			newWidth = 1;
		}
		if (newHeight <= 0) {
			newHeight = 1;
		}
		// a simple nearestneighbour scaler since the built in java versions were slow
		BufferedImage imgBuffer = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_RGB);
		int baseImageWidth = imageToScale.getWidth();
		int baseImageHeight = imageToScale.getHeight();
		int x, y;
		for (x = 0; x < newWidth; x++) {
			for (y = 0; y < newHeight; y++) {
				int col = imageToScale.getRGB(x * baseImageWidth / newWidth, y * baseImageHeight / newHeight);
				imgBuffer.setRGB(x, y, col);
			}
		}
		return imgBuffer;
	}

	public static BufferedImage scale_WithAlpha(BufferedImage imageToScale, int newWidth, int newHeight) {
		if (newWidth <= 0) {
			newWidth = 1;
		}
		if (newHeight <= 0) {
			newHeight = 1;
		}
		// a simple nearestneighbour scaler with alpha since the built in java versions were slow
		BufferedImage imgBuffer = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
		int baseImageWidth = imageToScale.getWidth();
		int baseImageHeight = imageToScale.getHeight();
		int x, y;
		Color color;
		for (x = 0; x < newWidth; x++) {
			for (y = 0; y < newHeight; y++) {
				int col = imageToScale.getRGB(x * baseImageWidth / newWidth, y * baseImageHeight / newHeight);
				color = new Color(col, true);
				imgBuffer.setRGB(x, y, color.getRGB());
			}
		}
		return imgBuffer;
	}

	public static int getWorldWidth() {
		return renderer.getWordlWidth();
	}

	public static int getWorldHeight() {
		return renderer.getWordlHeight();
	}
}