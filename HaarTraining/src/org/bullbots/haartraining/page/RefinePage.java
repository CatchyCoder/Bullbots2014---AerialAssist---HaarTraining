package org.bullbots.haartraining.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;

import org.bullbots.haartraining.HaarTraining;
import org.bullbots.haartraining.processing.FileManager;

import userinterface.item.ButtonItem;
import userinterface.item.TextItem;
import userinterface.page.Page;
import userinterface.window.Window;

public class RefinePage extends Page {
	
	private FileManager fileManager = new FileManager();
	
	private TextItem image = new TextItem(this, 200, 0, "", new Font("", 0, 0), Color.WHITE);
	
	private ButtonItem crop = new ButtonItem(this, 0, 0, "Crop", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE, Color.ORANGE);
	private ButtonItem next = new ButtonItem(this, 0, 50, "Next", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE, Color.ORANGE);
	private ButtonItem remove = new ButtonItem(this, 0, 100, "Remove", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE, Color.ORANGE);
	
	public RefinePage(Window window, int x, int y, int width, int height) {
		super(window, x, y, width, height, "");
		this.setBackground(Color.DARK_GRAY);
		
		// Resizing the image component and displaying the first image
		BufferedImage img = null; // TODO: Read the image here...
		//image.getComponent().setBounds(HaarTraining.window.getSize().width - img.getWidth(), 0, img.getWidth(), img.getHeight());
		
		// Making folders
		fileManager.createResFolders();
	}

	public void showNextImage() {
		
	}
}
