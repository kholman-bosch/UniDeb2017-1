package com.unideb.bosch.automatedcar;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;

public class VirtualWorldMouseListener implements MouseListener {

	@Override
	public void mouseClicked(MouseEvent e) {
		int mx = e.getX();
		int my = e.getY();
		int scaledMX = (int) (mx / VirtualWorld.getGraphicsScale());
		int scaledMY = (int) (my / VirtualWorld.getGraphicsScale());
		switch (e.getButton()) {
		case 0:
		case 1:
			if (VirtualWorld.getCars().size() < 4) {
				VirtualWorld.addNewCar(scaledMX, scaledMY);
			} else {
				System.out.println("Cannot add more than 4 cars!");
			}
			break;
		case 2:
		case 3:
			VirtualWorld.markCar_ForRemove(scaledMX, scaledMY);
			break;
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// TODO Auto-generated method stub

	}

	@Override
	public void mouseExited(MouseEvent e) {
		// TODO Auto-generated method stub

	}

}
