package com.unideb.bosch.instrumentclusterdisplay;

import java.awt.Color;
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
	private BufferedImage background, needle, r, n, d, p, rightindex, leftindex, headlight, steeringWheel;
	private BufferedImage activespeedlimit_icon, nospeedlimit_icon, sixtyincity_icon, stop_icon, yield_icon, tsr_icon, acc_icon;
	private int actual_KMH_Needle_Angle = 0;
	private int actual_RPM_Needle_Angle = 0;
	private int steeringWheel_Angle = 0;
	private Rectangle backgroundRectangle, needleRectangle_KMH, needleRectangle_RPM;
	private Rectangle r_Rectangle, n_Rectangle, d_Rectangle, p_Rectangle;
	private Rectangle rightIndex_Rectangle, leftIndex_Rectangle, headlight_Rectangle, steeringWheel_Rectangle;
	private Rectangle activespeedlimit_Rectangle, nospeedlimit_Rectangle, sixtyincity_Rectangle, stop_Rectangle, yield_Rectangle, acc_Rectangle, tsr_Rectangle;
	private boolean show_R = true, show_N = true, show_D = true, show_P = true, show_RIndex = true, show_LIndex = true, show_Headlight = true;
	private boolean show_nospeedlimit = true, show_sixtyincity = true, show_stop = true, show_yield = true, show_tsr = true, show_acc = true;
	private int show_activespeedlimit = 50;

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
			steeringWheel = ImageIO.read(new File("./ic_res/steeringwheel.png"));
			activespeedlimit_icon = ImageIO.read(new File("./ic_res/activespeedlimit.png"));
			nospeedlimit_icon = ImageIO.read(new File("./ic_res/nospeedlimit.png"));
			sixtyincity_icon = ImageIO.read(new File("./ic_res/sixtyincity.png"));
			stop_icon = ImageIO.read(new File("./ic_res/stop.png"));
			yield_icon = ImageIO.read(new File("./ic_res/yield.png"));
			acc_icon = ImageIO.read(new File("./ic_res/accon.png"));
			tsr_icon = ImageIO.read(new File("./ic_res/tsron.png"));
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
		steeringWheel_Rectangle = new Rectangle(0, 0, steeringWheel.getWidth(), steeringWheel.getHeight());
		activespeedlimit_Rectangle = new Rectangle(0, 0, activespeedlimit_icon.getWidth(), activespeedlimit_icon.getHeight());
		nospeedlimit_Rectangle = new Rectangle(0, 0, nospeedlimit_icon.getWidth(), nospeedlimit_icon.getHeight());
		sixtyincity_Rectangle = new Rectangle(0, 0, sixtyincity_icon.getWidth(), sixtyincity_icon.getHeight());
		stop_Rectangle = new Rectangle(0, 0, stop_icon.getWidth(), stop_icon.getHeight());
		yield_Rectangle = new Rectangle(0, 0, yield_icon.getWidth(), yield_icon.getHeight());
		acc_Rectangle = new Rectangle(0, 0, acc_icon.getWidth(), acc_icon.getHeight());
		tsr_Rectangle = new Rectangle(0, 0, tsr_icon.getWidth(), tsr_icon.getHeight());
		this.setBackground(Color.black);
		this.setVisible(true);
	}

	private void doDrawing(Graphics g) {
		this.setBackground(Color.black);
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
		if (this.show_acc) {
			int iconLoc_X = 7;
			int iconLoc_Y = 230;
			acc_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint acc_Paint = new TexturePaint(acc_icon, acc_Rectangle);
			gMatrix_Icons.setPaint(acc_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, acc_icon.getWidth(), acc_icon.getHeight());
		}
		if (this.show_tsr) {
			int iconLoc_X = 285;
			int iconLoc_Y = 175;
			tsr_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint tsr_Paint = new TexturePaint(tsr_icon, tsr_Rectangle);
			gMatrix_Icons.setPaint(tsr_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, tsr_icon.getWidth(), tsr_icon.getHeight());

			if (this.show_activespeedlimit != 0) {
				iconLoc_X = 179;
				iconLoc_Y = 123;
				activespeedlimit_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
				TexturePaint activespeedlimit_Paint = new TexturePaint(activespeedlimit_icon, activespeedlimit_Rectangle);
				gMatrix_Icons.setPaint(activespeedlimit_Paint);
				gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, activespeedlimit_icon.getWidth(), activespeedlimit_icon.getHeight());
			}
			if (this.show_nospeedlimit) {
				iconLoc_X = 304;
				iconLoc_Y = 203;
				nospeedlimit_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
				TexturePaint nospeedlimit_Paint = new TexturePaint(nospeedlimit_icon, nospeedlimit_Rectangle);
				gMatrix_Icons.setPaint(nospeedlimit_Paint);
				gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, nospeedlimit_icon.getWidth(), nospeedlimit_icon.getHeight());
			}
			if (this.show_sixtyincity) {
				iconLoc_X = 453;
				iconLoc_Y = 203;
				sixtyincity_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
				TexturePaint sixtyincity_Paint = new TexturePaint(sixtyincity_icon, sixtyincity_Rectangle);
				gMatrix_Icons.setPaint(sixtyincity_Paint);
				gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, sixtyincity_icon.getWidth(), sixtyincity_icon.getHeight());
			}
			if (this.show_stop) {
				iconLoc_X = 356;
				iconLoc_Y = 203;
				stop_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
				TexturePaint stop_Paint = new TexturePaint(stop_icon, stop_Rectangle);
				gMatrix_Icons.setPaint(stop_Paint);
				gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, stop_icon.getWidth(), stop_icon.getHeight());
			}
			if (this.show_yield) {
				iconLoc_X = 405;
				iconLoc_Y = 203;
				yield_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
				TexturePaint yield_Paint = new TexturePaint(yield_icon, yield_Rectangle);
				gMatrix_Icons.setPaint(yield_Paint);
				gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, yield_icon.getWidth(), yield_icon.getHeight());
			}
		}
		if (this.show_R) {
			int iconLoc_X = 39 - 30;
			int iconLoc_Y = 89 - 33;
			r_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint r_Paint = new TexturePaint(r, r_Rectangle);
			gMatrix_Icons.setPaint(r_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, r.getWidth(), r.getHeight());
		}
		if (this.show_N) {
			int iconLoc_X = 39 - 30;
			int iconLoc_Y = 139 - 33;
			n_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint n_Paint = new TexturePaint(n, n_Rectangle);
			gMatrix_Icons.setPaint(n_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, n.getWidth(), n.getHeight());
		}
		if (this.show_D) {
			int iconLoc_X = 39 - 30;
			int iconLoc_Y = 189 - 33;
			d_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint d_Paint = new TexturePaint(d, d_Rectangle);
			gMatrix_Icons.setPaint(d_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, d.getWidth(), d.getHeight());
		}
		if (this.show_P) {
			int iconLoc_X = 39 - 30;
			int iconLoc_Y = 39 - 33;
			p_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint p_Paint = new TexturePaint(p, p_Rectangle);
			gMatrix_Icons.setPaint(p_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, p.getWidth(), p.getHeight());
		}
		if (this.show_RIndex) {
			int iconLoc_X = 352 - 130;
			int iconLoc_Y = 198;
			rightIndex_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint rightIndex_Paint = new TexturePaint(rightindex, rightIndex_Rectangle);
			gMatrix_Icons.setPaint(rightIndex_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, rightindex.getWidth(), rightindex.getHeight());
		}
		if (this.show_LIndex) {
			int iconLoc_X = 292 - 130;
			int iconLoc_Y = 198;
			leftIndex_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint leftIndex_Paint = new TexturePaint(leftindex, leftIndex_Rectangle);
			gMatrix_Icons.setPaint(leftIndex_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, leftindex.getWidth(), leftindex.getHeight());
		}
		if (this.show_Headlight) {
			int iconLoc_X = 205 - 130;
			int iconLoc_Y = 200;
			headlight_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
			TexturePaint headLight_Paint = new TexturePaint(headlight, headlight_Rectangle);
			gMatrix_Icons.setPaint(headLight_Paint);
			gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, headlight.getWidth(), headlight.getHeight());
		}
		int iconLoc_X = 528;
		int iconLoc_Y = 20;
		steeringWheel_Rectangle.setLocation(iconLoc_X, iconLoc_Y);
		TexturePaint steeringWheel_Paint = new TexturePaint(steeringWheel, steeringWheel_Rectangle);
		gMatrix_Icons.setPaint(steeringWheel_Paint);
		int wheelMidPoint_X = 640;
		int wheelMidPoint_Y = 128;
		gMatrix_Icons.rotate(-Math.toRadians(steeringWheel_Angle), wheelMidPoint_X, wheelMidPoint_Y);
		gMatrix_Icons.fillRect(iconLoc_X, iconLoc_Y, steeringWheel.getWidth(), steeringWheel.getHeight());
		//
		gMatrix_KMH.dispose();
		gMatrix_RPM.dispose();
		gMatrix_Icons.dispose();
		g.dispose();
	}

	@Override
	public void paintComponent(Graphics g) {
		// super.paintComponent(g);
		doDrawing(g);
	}

	public void set_Actual_KMH_Needle_Angle(int kmh) {
		float actKMH_needleIndicator_Angle = (kmh / 120f) * 245;
		this.actual_KMH_Needle_Angle = (int) actKMH_needleIndicator_Angle;
	}

	public void set_Actual_RPM_Needle_Angle(int rpm) {
		float actRPM_needleIndicator_Angle = (rpm / 9000f) * 255;
		this.actual_RPM_Needle_Angle = (int) actRPM_needleIndicator_Angle;
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

	public void set_Actual_SteeringWHeel_Angle(int angle) {
		this.steeringWheel_Angle = angle;
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
