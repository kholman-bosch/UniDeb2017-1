package com.unideb.bosch.automatedcar;

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
	private static boolean showDebugWorldData = true;
	private static Font serifFontBOLD = new Font("Serif", Font.BOLD, 12);

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
		VirtualWorld.getCars().get(0).drawCar(globalMatrix, actualGraphics_Scale);
		if (showDebugWorldData) {
			this.drawWorldObjectsDebugData(globalMatrix);
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

	private void drawWorldObjectsDebugData(Graphics2D g) {
		// worldobject texts
		g.setColor(Color.black);
		g.setFont(serifFontBOLD);
		int size = WorldObjectParser.getInstance().getWorldObjects().size();
		for (int i = 0; i < size; i++) {
			WorldObject worldObj = WorldObjectParser.getInstance().getWorldObjects().get(i);
			if (worldObj.getType().contains("road_2")) {
				continue;
			}
			g.drawString(worldObj.getType(), (int) (worldObj.getX() * actualGraphics_Scale), (int) (worldObj.getY() * actualGraphics_Scale));
		}
		// radar sensor debug
		ArrayList<AutomatedCar> cars = VirtualWorld.getCars();
		for (int i = 0; i < cars.size(); i++) {
			AutomatedCar actCar = cars.get(i);
			if (actCar.getRadarSensor() != null) {
				actCar.getRadarSensor().draw_DebugData(g);
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
