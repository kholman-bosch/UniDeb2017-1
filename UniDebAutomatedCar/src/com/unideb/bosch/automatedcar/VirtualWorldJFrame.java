package com.unideb.bosch.automatedcar;

import java.awt.GridLayout;

import javax.swing.JFrame;

public class VirtualWorldJFrame extends JFrame {

	private static final long serialVersionUID = 1;

	public VirtualWorldJFrame() {
		this.setTitle("UniDeb Automated Car Project");
		this.setLayout(new GridLayout());
		this.setResizable(true);
		this.pack();
		this.setVisible(true);
	}
}