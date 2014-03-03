package org.bullbots.haartraining.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.bullbots.haartraining.HaarTraining;
import org.bullbots.haartraining.processing.ImageProcessor;

import userinterface.item.ButtonItem;
import userinterface.item.InteractiveItem;
import userinterface.item.StateButtonItem;
import userinterface.item.TextItem;
import userinterface.page.Page;
import userinterface.window.Window;

public class MainPage extends Page implements Runnable {
	
	private ImageProcessor processor = new ImageProcessor(this, 0);
	
	private ButtonItem exit = new ButtonItem(this, 0, 0, "Exit", new Font("Arial", Font.PLAIN, 24), Color.WHITE, Color.RED);
	private TextItem image = new TextItem(this, 0, 0, "", new Font("", 0, 0), Color.WHITE);
	
	private String[] paths = {"positive.png", "negative.png"};
	private String[] hoverPaths = {"switch.png", "switch.png"};
	private StateButtonItem switchMode = new StateButtonItem(this, 0, 50, paths, hoverPaths);
	
	private TextItem negCount = new TextItem(this, 0, 100, "Negative Images: 0", new Font("Arial", Font.PLAIN, 24), Color.WHITE);
	private TextItem posCount = new TextItem(this, 0, 150, "Positive Images: 0", new Font("Arial", Font.PLAIN, 24), Color.WHITE);
	
	private boolean isRunning = false;
	private boolean collectingData = false;
	
	public MainPage(Window window, int x, int y, int width, int height) {
		super(window, x, y, width, height, "/mainpage/");
		this.setBackground(Color.DARK_GRAY);
		
		BufferedImage img =  processor.getRawImage();
		image.getComponent().setBounds(HaarTraining.window.getSize().width - img.getWidth(), 0, img.getWidth(), img.getHeight());
		this.addItem(image);
		
		// Need camera feed to be processed on a separate thread,
		// that way user input can still be processed.
		isRunning = true;
		new Thread(this).start();
	}
	
	@Override
	public void handleMousePress(InteractiveItem item) {
		if(item.equals(exit)) {
			isRunning = false;
			System.exit(0);
		}
	}
	
	@Override
	public void handleKeyPress(KeyEvent event) {
		if(event.getKeyCode() == KeyEvent.VK_SPACE) collectingData = !collectingData; 
	}
	
	@Override
	public void run() {
		while(isRunning) {
			// Constantly updating camera feed
			JLabel imageComponent = ((JLabel) image.getComponent());
			if(isPositiveSearch()) imageComponent.setIcon(new ImageIcon(processor.getProcessedImage()));
			else imageComponent.setIcon(new ImageIcon(processor.getRawImage()));
			
			// Updating values
			negCount.setText("Negative Images: " + processor.getNegImages().size());
			posCount.setText("Positive Images: " + processor.getPosImages().size());
		}
	}
	
	public boolean isPositiveSearch() {
		return switchMode.getState() ==  0;
	}
	
	public boolean isCollectingData() {
		return collectingData;
	}
}
