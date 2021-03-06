package org.bullbots.haartraining.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import org.bullbots.haartraining.Core;
import org.bullbots.haartraining.HaarTraining;
import org.bullbots.haartraining.item.ImageItem;
import org.bullbots.haartraining.processing.ImageProcessor;
import org.opencv.core.Mat;
import org.opencv.core.MatOfRect;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.highgui.Highgui;
import org.opencv.objdetect.CascadeClassifier;

import userinterface.item.ButtonItem;
import userinterface.item.InteractiveItem;
import userinterface.item.StateButtonItem;
import userinterface.item.TextItem;
import userinterface.page.Page;
import userinterface.window.Window;

public class NewMainPage extends Page implements Runnable {
	
	private String[] paths = {"positive.png", "negative.png"};
	private String[] hoverPaths = {"switch.png", "switch.png"};
	private StateButtonItem captureMode = new StateButtonItem(this, 0, 50, paths, hoverPaths);
	
	private ButtonItem exit = new ButtonItem(this, 0, 0, "Exit", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 32), Color.WHITE, Color.RED);
	private ImageProcessor processor = new ImageProcessor(null, 0);
	private ImageItem image = new ImageItem(this, 200, 0);
	private ButtonItem captureImage = new ButtonItem(this, 0, 100, "Take Picture\n(Or Press <enter>)", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 20), Color.WHITE, Color.GREEN);
	private ButtonItem finish = new ButtonItem(this, 0, 300, "Finish and Refine", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 20), Color.WHITE, Color.ORANGE);
	private TextItem posCount = new TextItem(this, 0, 150, "Positive Images: 0", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 20), Color.WHITE);
	private TextItem negCount = new TextItem(this, 0, 200, "Negative Images: 0", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 20), Color.WHITE);
	
	private boolean isRunning = false;
	
	
	CascadeClassifier c = new CascadeClassifier("testthing.xml");
	
	
	
	
	public NewMainPage(Window window, int x, int y, int width, int height) {
		super(window, x, y, width, height, "/mainpage/");
		this.setBackground(Color.DARK_GRAY);
		
		isRunning = true;
		new Thread(this).start();
	}
	
	@Override
	public void handleMousePress(InteractiveItem item) {
		if(item.equals(exit)) {
			isRunning = false;
			System.exit(0);
		}
		else if(item.equals(captureImage)) takePicture();
		else if(item.equals(finish)) {
			processor.printStuff();
			HaarTraining.window.setPage(new RefinePage(HaarTraining.window, 0, 0, this.getWidth(), this.getHeight(), processor));
		}
	}
	
	@Override
	public void handleKeyPress(KeyEvent event) {
		int key = event.getKeyCode();
		
		if(key == KeyEvent.VK_ENTER) takePicture();
	}
	
	private void takePicture() {
		// If positive
		if(captureMode.getState() == 0) processor.capturePosImage();
		else processor.captureNegImage();
	}

	@Override
	public void run() {
		// Resizing the image component
		BufferedImage img = processor.getRawImage();
		image.getComponent().setBounds(HaarTraining.window.getSize().width - img.getWidth(), 0, img.getWidth(), img.getHeight());
		
		// Updating the camera feed
		while(isRunning) {
			JLabel imageComponent = ((JLabel) image.getComponent());
			imageComponent.setIcon(new ImageIcon(processor.getRawImage()));
			
			posCount.setText("Positive Images: " + processor.getPosImages().size());
			negCount.setText("Negative Images: " + processor.getNegImages().size());
		}
	}
}
