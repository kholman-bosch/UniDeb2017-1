package com.unideb.bosch.automatedcar;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.unideb.bosch.automatedcar.framework.WorldObject;

public class VirtualWorldRenderer extends JPanel {

	private static final long serialVersionUID = 1;
	private static BufferedImage background, scaledBackground;
	private Rectangle backgroundRectangle;
	private static int previousWindowWidth = 0, previousWindowHeight = 0;
	private static float actualGraphics_Scale = 1f;
	// Debug
	public static boolean showDebugWorldData = true;
	public static boolean showRadarSensorDebugData = false;
	public static boolean showCameraDebugData = false;

	private static Font defaultFontBOLD = new Font("default", Font.BOLD, 12);

	public VirtualWorldRenderer(int windowWidth, int windowHeight) {
		try {
			background = ImageIO.read(new File("./world/road_1.png"));
		} catch (IOException ex) {
			System.err.println(ex.getMessage() + " Error in VirtualWorldRenderer! ImageIO.read");
		}
		this.setBackground(Color.gray);
		this.windowResizeLogic(windowWidth, windowHeight);
	}

	private void doDrawing(Graphics g) {
		Graphics2D globalMatrix = (Graphics2D) g.create();
		this.backgroundRectangle.setLocation(0, 0);
		TexturePaint backgroundPaint = new TexturePaint(scaledBackground, backgroundRectangle);
		globalMatrix.setPaint(backgroundPaint);
		globalMatrix.fillRect(0, 0, scaledBackground.getWidth(), scaledBackground.getHeight());
		for (int i = 0; i < VirtualWorld.getCars().size(); i++) {
			VirtualWorld.getCars().get(i).drawCar(globalMatrix, actualGraphics_Scale);
		}
		if (showDebugWorldData) {
			this.drawWorldObjectsDebugData(globalMatrix);
			drawKeybindingsInfo(g);
		}
		if (showRadarSensorDebugData) {
			this.drawRadarSensorDebugData(globalMatrix);
		}
		if (showCameraDebugData) {
			this.drawCameraDebugData(globalMatrix);
		}
	}

	public void windowResizeLogic(int windowWidth, int windowHeight) {
		if (windowWidth <= 20 || windowHeight <= 20) {
			return;
		}
		// scale the background image to fit the window but only scale if needed
		if (previousWindowWidth != windowWidth || previousWindowHeight != windowHeight) {
			actualGraphics_Scale = Math.min(windowWidth / (float) background.getWidth(), windowHeight / (float) background.getHeight());
			scaledBackground = VirtualWorld.scale(background, (int) (background.getWidth() * actualGraphics_Scale), (int) (background.getHeight() * actualGraphics_Scale));
			this.backgroundRectangle = new Rectangle(0, 0, scaledBackground.getWidth(), scaledBackground.getHeight());
			previousWindowWidth = windowWidth;
			previousWindowHeight = windowHeight;
			System.out.println("ResizeScaledBackground!");
		}
	}

	@Override
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		doDrawing(g);
	}

	public float getGraphicsScale() {
		return actualGraphics_Scale;
	}

	private void drawKeybindingsInfo(Graphics g) {
		g.setColor(Color.black);
		g.setFont(defaultFontBOLD);
		g.drawString("[F1] World debug info", 0, 20);
		g.drawString("[F2] Radar Sensor debug info", 0, 35);
		g.drawString("[F3] Camera debug info", 0, 50);
		g.drawString("Click inside one of the ICs to control a car.", 200, 20);
		g.drawString("Gear switch keys: D,N,R,P.  Car control keys: ↑,↓,→,←", 200, 35);
		g.drawString("Left click to add car, right click to remove.", 200, 50);
		g.drawString("Toggle ACC key: A.  ACC speed control keys: NUM+,NUM-", 550, 20);
		g.drawString("(when hitting A don't give gas to the car or the ACC will suspend itself)", 550, 35);
		g.drawString("Toggle TSR key: T", 550, 50);
	}

	private void drawWorldObjectsDebugData(Graphics2D g) {
		g.setColor(Color.black);
		g.setFont(defaultFontBOLD);
		int size = WorldObjectParser.getWorldObjects().size();
		g.setStroke(new BasicStroke(2));
		for (int i = 0; i < size; i++) {
			WorldObject worldObj = WorldObjectParser.getWorldObjects().get(i);
			if (worldObj.getType().contains("lane")) {
				continue;
			}
			g.setColor(Color.DARK_GRAY);
			int textX = (int) (worldObj.getX() * actualGraphics_Scale);
			int textY = (int) (worldObj.getY() * actualGraphics_Scale);
			int diameterHalf = (int) ((worldObj.getRadius() / 2) * actualGraphics_Scale) * 2;
			int diameterScaled = (int) (worldObj.getRadius() * actualGraphics_Scale) * 2;
			g.drawOval(textX - diameterHalf, textY - diameterHalf, diameterScaled, diameterScaled);
			g.setColor(Color.BLACK);
			g.drawString(worldObj.getType() + " ORI: " + Math.toDegrees(worldObj.getOrientation()), textX, textY);
			// draw orientation redgreen lines:
			float rotation = worldObj.getOrientation();
			float s = (float) Math.sin(rotation);
			float c = (float) Math.cos(rotation);
			int sx = worldObj.getX();
			int sy = worldObj.getY();
			int ex = (int) (sx + (50 * s));
			int ey = (int) (sy + (50 * c));
			int eex = (int) (sx + (100 * s));
			int eey = (int) (sy + (100 * c));
			sx = (int) (sx * getGraphicsScale());
			sy = (int) (sy * getGraphicsScale());
			ex = (int) (ex * getGraphicsScale());
			ey = (int) (ey * getGraphicsScale());
			eex = (int) (eex * getGraphicsScale());
			eey = (int) (eey * getGraphicsScale());
			// red part of the direction indicator line (this is where the line starts)
			g.setColor(Color.RED);
			g.drawLine(sx, sy, ex, ey);
			// green part of the indicator line to see the actual direction
			g.setColor(Color.GREEN);
			g.drawLine(ex, ey, eex, eey);
		}
	}

	private void drawRadarSensorDebugData(Graphics2D g) {
		// radar sensor debug
		ArrayList<AutomatedCar> cars = VirtualWorld.getCars();
		for (int i = 0; i < cars.size(); i++) {
			AutomatedCar actCar = cars.get(i);
			if (actCar.getRadarSensor() != null) {
				actCar.getRadarSensor().draw_DebugData(g);
			}
		}
	}

	private void drawCameraDebugData(Graphics2D g) {
		// camera debug
		ArrayList<AutomatedCar> cars = VirtualWorld.getCars();
		for (int i = 0; i < cars.size(); i++) {
			AutomatedCar actCar = cars.get(i);
			if (actCar.getFrontViewCamera() != null) {
				actCar.getFrontViewCamera().draw_DebugData(g);
				actCar.getFrontViewCamera().connectCameraWithRoadSigns(g);
			}
		}
	}

	public static int getWordlWidth() {
		return background.getWidth();
	}

	public static int getWordlHeight() {
		return background.getHeight();
	}
}
