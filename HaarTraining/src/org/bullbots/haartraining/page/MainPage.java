package org.bullbots.haartraining.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.bullbots.haartraining.HaarTraining;
import org.bullbots.haartraining.processing.ImageProcessor;

import userinterface.item.ButtonItem;
import userinterface.item.GraphicalItem;
import userinterface.item.InteractiveItem;
import userinterface.item.TextItem;
import userinterface.page.Page;
import userinterface.window.Window;

public class MainPage extends Page implements Runnable {
	
	private ImageProcessor processor = new ImageProcessor(this, 0);
	
	private ButtonItem exit = new ButtonItem(this, 0, 0, "Exit", new Font("Arial", Font.PLAIN, 24), Color.WHITE, Color.RED);
	private GraphicalItem image = new GraphicalItem(this, 0, 0); // position is not set in constructor
	private ButtonItem switchMode = new ButtonItem(this, 0, 50, "Switch", new Font("Arial", Font.PLAIN, 24), Color.WHITE, Color.ORANGE);
	
	private TextItem negCount = new TextItem(this, 0, 100, "Negative Images: 0", new Font("Arial", Font.PLAIN, 24));
	private TextItem posCount = new TextItem(this, 0, 150, "Positive Images: 0", new Font("Arial", Font.PLAIN, 24));
	
	private boolean positiveSearch = true;
	private boolean isRunning = false;
	private boolean collectingData = false;
	
	public MainPage(Window window, int x, int y, int width, int height, String resourcePath) {
		super(window, x, y, width, height, resourcePath);
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
		else if(item.equals(switchMode)) positiveSearch = !positiveSearch;
	}
	
	@Override
	public void run() {
		while(isRunning) {
			// Constantly updating camera feed
			JLabel imageComponent = ((JLabel) image.getComponent());
			if(positiveSearch) imageComponent.setIcon(new ImageIcon(processor.getProcessedImage()));
			else imageComponent.setIcon(new ImageIcon(processor.getDrawnImage()));
			
			negCount.getComponent().setText("Negative Images: " + processor.getNegCount());
			posCount.getComponent().setText("Positive Images: " + processor.getPosCount());
			
			// Updating the size of the components
			negCount.setSizeAndLoc(negCount.getComponent());
			posCount.setSizeAndLoc(posCount.getComponent());
		}
	}
	
	public boolean isPositiveSearch() {
		return positiveSearch;
	}
	
	public boolean isCollectingData() {
		return collectingData;
	}
}
