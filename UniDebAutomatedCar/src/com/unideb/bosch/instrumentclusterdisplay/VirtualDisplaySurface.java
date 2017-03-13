package com.unideb.bosch.instrumentclusterdisplay;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.JPanel;

import com.unideb.bosch.automatedcar.AutomatedCar;

public class VirtualDisplaySurface extends JPanel {

	private static final long serialVersionUID = 1;
	private BufferedImage background, needle, r, n, d, p, rightindex, leftindex, headlight;
	private int actual_KMH_Needle_Angle = 0;
	private int actual_RPM_Needle_Angle = 0;
	private Rectangle backgroundRectangle, needleRectangle_KMH, needleRectangle_RPM;
	private Rectangle r_Rectangle, n_Rectangle, d_Rectangle, p_Rectangle;
	private Rectangle rightIndex_Rectangle, leftIndex_Rectangle, headlight_Rectangle;
	public boolean show_R = true, show_N = true, show_D = true, show_P = true, show_RIndex = true, show_LIndex = true,
			show_Headlight = true;

	public VirtualDisplaySurface(AutomatedCar car) {
		try {
			background = ImageIO.read(new File("./ic_res/ic_backg.png"));
			needle = ImageIO.read(new File("./ic_res/needle.png"));
			r = ImageIO.read(new File("./ic_res/r.png"));
			n = ImageIO.read(new File("./ic_res/n.png"));
			d = ImageIO.read(new File("./ic_res/d.png"));
			p = ImageIO.read(new File("./ic_res/p.png"));
			rightindex = ImageIO.read(new File("./ic_res/rightindex.png"));
			leftindex = ImageIO.read(new File("./ic_res/leftindex.png"));
			headlight = ImageIO.read(new File("./ic_res/headl.png"));
		} catch (IOException ex) {
			System.err.println(ex.getMessage() + " Error in virtual display! ImageIO.read");
		}
		backgroundRectangle = new Rectangle(0, 0, background.getWidth(), background.getHeight());
		needleRectangle_KMH = new Rectangle(0, 0, needle.getWidth(), needle.getHeight());
		needleRectangle_RPM = new Rectangle(0, 0, needle.getWidth(), needle.getHeight());
		r_Rectangle = new Rectangle(0, 0, r.getWidth(), r.getHeight());
		n_Rectangle = new Rectangle(0, 0, n.getWidth(), n.getHeight());
		d_Rectangle = new Rectangle(0, 0, d.getWidth(), d.getHeight());
		p_Rectangle = new Rectangle(0, 0, p.getWidth(), p.getHeight());
		rightIndex_Rectangle = new Rectangle(0, 0, rightindex.getWidth(), rightindex.getHeight());
		leftIndex_Rectangle = new Rectangle(0, 0, leftindex.getWidth(), leftindex.getHeight());
		headlight_Rectangle = new Rectangle(0, 0, headlight.getWidth(), headlight.getHeight());
		this.setVisible(true);
	}

	private void doDrawing(Graphics g) {
		if (actual_KMH_Needle_Angle > 200) {
			actual_KMH_Needle_Angle = 0;
		}
		set_Actual_KMH_Needle_Angle(++actual_KMH_Needle_Angle);
		set_Actual_RPM_Needle_Angle(actual_KMH_Needle_Angle);
		Graphics2D gMatrix_KMH = (Graphics2D) g.create();
		Graphics2D gMatrix_RPM = (Graphics2D) g.create();
		Graphics2D gMatrix_Icons = (Graphics2D) g.create();
		backgroundRectangle.setLocation(0, 0);
		// g2d.rotate(Math.toRadians(11), 0, 0);
		TexturePaint backgroundPaint = new TexturePaint(background, backgroundRectangle);
		gMatrix_KMH.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
		gMatrix_KMH.setPaint(backgroundPaint);
		gMatrix_KMH.fillRect(0, 0, background.getWidth(), background.getHeight());
		//
		int needleLocationX_KMH = 206 - 65;
		int needleLocationY_KMH = 100 - 10;
		int needleMidPoint_KMH_X = 209;
		int needleMidPoint_KMH_Y = 98;
		needleRectangle_KMH.setLocation(needleLocationX_KMH, needleLocationY_KMH);
		TexturePaint needlePaint_KMH = new TexturePaint(needle, needleRectangle_KMH);
		gMatrix_KMH.setPaint(needlePaint_KMH);
		gMatrix_KMH.rotate(Math.toRadians(actual_KMH_Needle_Angle), needleMidPoint_KMH_X, needleMidPoint_KMH_Y);
		gMatrix_KMH.fillRect(needleLocationX_KMH, needleLocationY_KMH, needle.getWidth(), needle.getHeight());
		//
		int needleLocationX_RPM = 340;
		int needleLocationY_RPM = 100 - 5;
		int needleMidPoint_RPM_X = 408;
		int needleMidPoint_RPM_Y = 103;
		needleRectangle_RPM.setLocation(needleLocationX_RPM, needleLocationY_RPM);
		TexturePaint needlePaint = new TexturePaint(needle, needleRectangle_RPM);
		gMatrix_RPM.setPaint(needlePaint);
		gMatrix_RPM.rotate(Math.toRadians(actual_RPM_Needle_Angle), needleMidPoint_RPM_X, needleMidPoint_RPM_Y);
		gMatrix_RPM.fillRect(needleLocationX_RPM, needleLocationY_RPM, needle.getWidth(), needle.getHeight());
		//
		if (this.show_R) {
			int iconLoc_X = 39;
			int iconLoc_Y = 89;
			r_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint r_Paint = new TexturePaint(r, r_Rectangle);
			gMatrix_Icons.setPaint(r_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, r.getWidth(), r.getHeight());
		}
		if (this.show_N) {
			int iconLoc_X = 39;
			int iconLoc_Y = 139;
			n_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint n_Paint = new TexturePaint(n, n_Rectangle);
			gMatrix_Icons.setPaint(n_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, n.getWidth(), n.getHeight());
		}
		if (this.show_D) {
			int iconLoc_X = 39;
			int iconLoc_Y = 189;
			d_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint d_Paint = new TexturePaint(d, d_Rectangle);
			gMatrix_Icons.setPaint(d_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, d.getWidth(), d.getHeight());
		}
		if (this.show_P) {
			int iconLoc_X = 39;
			int iconLoc_Y = 39;
			p_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint p_Paint = new TexturePaint(p, p_Rectangle);
			gMatrix_Icons.setPaint(p_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, p.getWidth(), p.getHeight());
		}
		if (this.show_RIndex) {
			int iconLoc_X = 352;
			int iconLoc_Y = 198;
			rightIndex_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint rightIndex_Paint = new TexturePaint(rightindex, rightIndex_Rectangle);
			gMatrix_Icons.setPaint(rightIndex_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, rightindex.getWidth(), rightindex.getHeight());
		}
		if (this.show_LIndex) {
			int iconLoc_X = 292;
			int iconLoc_Y = 198;
			leftIndex_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint leftIndex_Paint = new TexturePaint(leftindex, leftIndex_Rectangle);
			gMatrix_Icons.setPaint(leftIndex_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, leftindex.getWidth(), leftindex.getHeight());
		}
		if (this.show_Headlight) {
			int iconLoc_X = 205;
			int iconLoc_Y = 200;
			headlight_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint headLight_Paint = new TexturePaint(headlight, headlight_Rectangle);
			gMatrix_Icons.setPaint(headLight_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, headlight.getWidth(), headlight.getHeight());
		}
		//
		gMatrix_KMH.dispose();
		gMatrix_RPM.dispose();
		gMatrix_Icons.dispose();
		g.dispose();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		doDrawing(g);
	}

	public void set_Actual_KMH_Needle_Angle(int angle) {
		angle = truncateAngle(angle, 245);
		this.actual_KMH_Needle_Angle = angle;
	}

	public void set_Actual_RPM_Needle_Angle(int angle) {
		angle = truncateAngle(angle, 255);
		this.actual_RPM_Needle_Angle = angle;
	}

	public void set_Actual_R(boolean state) {
		this.show_R = state;
	}

	public void set_Actual_N(boolean state) {
		this.show_N = state;
	}

	public void set_Actual_D(boolean state) {
		this.show_D = state;
	}

	public void set_Actual_P(boolean state) {
		this.show_P = state;
	}
	
	public void set_Actual_RightIndex(boolean state) {
		this.show_RIndex = state;
	}
	
	public void set_Actual_LeftIndex(boolean state) {
		this.show_LIndex = state;
	}
	
	public void set_Actual_Headlights(boolean state) {
		this.show_Headlight = state;
	}

	private int truncateAngle(int angle, int max) {
		if (angle < 0) {
			angle = 0;
		}
		if (angle > max) {
			angle = max;
		}
		return angle;
	}
}
