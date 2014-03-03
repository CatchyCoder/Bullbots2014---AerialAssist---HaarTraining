package org.bullbots.haartraining.item;

import java.awt.Component;
import java.awt.Point;
import java.awt.event.MouseEvent;

import javax.swing.JLabel;

import org.opencv.core.Rect;

import userinterface.item.InteractiveItem;
import userinterface.page.Page;

public class ImageItem extends InteractiveItem {
	
	private JLabel IMAGE = new JLabel();
	
	Point point1, point2;
	
	public ImageItem(Page page, int x, int y) {
		super(page, x, y);
		
		page.addItem(this);
	}
	
	@Override
	public JLabel getComponent() {
		return IMAGE;
	}
	
	@Override
	public void mousePressed(MouseEvent event) {
		point1 = new Point(event.getX(), event.getY());
		
		// Resetting point2
		point2 = point1;
	}
	
	@Override
	public void mouseDragged(MouseEvent event) {
		point2 = new Point(event.getX(), event.getY());
	}
	
	@Override
	public void mouseReleased(MouseEvent event) {
		point2 = new Point(event.getX(), event.getY());
	}
	
	public Rect getRect() {
		int width, height;
		// Calculating width and height
		if(point1.x < point2.x) width = point2.x - point1.x;
		else width = point1.x - point2.x;
		if(point1.y < point2.y) height = point2.y - point1.y;
		else height = point1.y - point2.y;
		
		return new Rect(point1.x, point1.y, width, height);
	}
}
