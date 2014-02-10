package org.bullbots.haartraining.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.bullbots.haartraining.ImageProcessor;

import userinterface.item.ButtonItem;
import userinterface.item.GraphicalItem;
import userinterface.item.InteractiveItem;
import userinterface.page.Page;
import userinterface.window.Window;

public class MainPage extends Page implements Runnable {
	
	private ImageProcessor processor = new ImageProcessor();
	
	@Override
	public void keyPressed(KeyEvent event, int key) {
		System.out.println("test");
	}

	private static final long serialVersionUID = 1L;
	
	private ButtonItem exit = new ButtonItem(this, 0, 0, "Exit", new Font("Arial", Font.PLAIN, 24), Color.WHITE, Color.RED);
	private GraphicalItem image = new GraphicalItem(this, 100, 0);

	public MainPage(Window window, int x, int y, int width, int height, String resourcePath) {
		super(window, x, y, width, height, resourcePath);
		//this.setBackground(Color.DARK_GRAY);
		
		image.getComponent().setBounds(0, 100, 640, 480);
		this.addItem(image);
		new Thread(this).start();
	}
	
	@Override
	public void handleMousePress(InteractiveItem item) {
		if(item.equals(exit)) System.exit(0);
	}
	
	@Override
	public void handleKeyPress(InteractiveItem item, int key) {
		System.out.println("KEY");
		if(key == KeyEvent.VK_ESCAPE) System.exit(0);
	}
	
	@Override
	public void run() {
		while(true) {
			((JLabel) image.getComponent()).setIcon(new ImageIcon(processor.getCleanImage()));
		}
	}
}
