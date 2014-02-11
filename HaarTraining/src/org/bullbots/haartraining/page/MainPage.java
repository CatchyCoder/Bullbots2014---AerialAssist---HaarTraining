package org.bullbots.haartraining.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.bullbots.haartraining.HaarTraining;
import org.bullbots.haartraining.ImageProcessor;

import userinterface.item.ButtonItem;
import userinterface.item.GraphicalItem;
import userinterface.item.InteractiveItem;
import userinterface.page.Page;
import userinterface.window.Window;

public class MainPage extends Page implements Runnable {
	
	private ImageProcessor processor = new ImageProcessor(0);
	
	private ButtonItem exit = new ButtonItem(this, 0, 0, "Exit", new Font("Arial", Font.PLAIN, 24), Color.WHITE, Color.RED);
	private GraphicalItem image = new GraphicalItem(this, 0, 0); // position is not set in constructor
	
	private boolean positiveSearch = false;
	
	public MainPage(Window window, int x, int y, int width, int height, String resourcePath) {
		super(window, x, y, width, height, resourcePath);
		this.setBackground(Color.DARK_GRAY);
		
		BufferedImage img =  processor.getCleanImage();
		image.getComponent().setBounds(HaarTraining.window.getSize().width - img.getWidth(), 0, img.getWidth(), img.getHeight());
		this.addItem(image);
		new Thread(this).start();
	}
	
	@Override
	public void handleMousePress(InteractiveItem item) {
		if(item.equals(exit)) System.exit(0);
	}
	
	@Override
	public void run() {
		while(true) {
			((JLabel) image.getComponent()).setIcon(new ImageIcon(processor.getCleanImage()));
		}
	}
}
