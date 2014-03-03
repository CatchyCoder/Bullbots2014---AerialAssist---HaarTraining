package org.bullbots.haartraining.page;

import java.awt.Color;
import java.awt.Font;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;

import javax.swing.ImageIcon;

import org.bullbots.haartraining.HaarTraining;
import org.bullbots.haartraining.item.ImageItem;
import org.bullbots.haartraining.processing.FileManager;
import org.bullbots.haartraining.processing.ImageProcessor;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import userinterface.item.ButtonItem;
import userinterface.item.InteractiveItem;
import userinterface.item.TextItem;
import userinterface.page.Page;
import userinterface.window.Window;

public class RefinePage extends Page {
	
	private FileManager fileManager = new FileManager();
	private ImageProcessor processor;
	
	private ImageItem image = new ImageItem(this, 200, 0);
	
	private ButtonItem previous = new ButtonItem(this, 0, 0, "Previous", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE, Color.ORANGE);
	private ButtonItem next = new ButtonItem(this, 100, 0, "Next", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE, Color.ORANGE);
	private ButtonItem crop = new ButtonItem(this, 0, 50, "Crop", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE, Color.ORANGE);
	private ButtonItem remove = new ButtonItem(this, 0, 100, "Remove", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE, Color.ORANGE);
	private ButtonItem finish = new ButtonItem(this, 0, 250, "Finish", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.ORANGE, Color.GREEN);
	
	private TextItem currentImageNumber = new TextItem(this, 0, 200, "", new Font(HaarTraining.FONT_STYLE, Font.BOLD, 24), Color.WHITE);
	
	private int currentImage = 0;
	
	private boolean isCropping = false;
	
	public RefinePage(Window window, int x, int y, int width, int height, ImageProcessor processor) {
		super(window, x, y, width, height, "");
		this.setBackground(Color.DARK_GRAY);
		
		this.processor = processor;
		
		// Displaying the first image
		updateImageNumber();
		updateImage();
	}
	
	@Override
	public void handleKeyPress(KeyEvent event) {
		int key = event.getKeyCode();
		
		if(key == KeyEvent.VK_RIGHT) showNextImage();
		else if(key == KeyEvent.VK_LEFT) showPreviousImage();
	}
	
	@Override
	public void handleMousePress(InteractiveItem item) {
		if(item.equals(next)) showNextImage();
		else if(item.equals(previous)) showPreviousImage();
		else if(item.equals(remove)) {
			// Have to have at least one positive image
			if(processor.getPosImages().size() >= 2) {
				// Removing the image from the collection
				processor.getPosImages().remove(currentImage);
				
				// Keeping the image index in range
				if(currentImage >= processor.getPosImages().size()) currentImage--;
				
				// Updating the image with the new image in that index
				updateImageNumber();
				updateImage();
			}
		}
		else if(item.equals(crop)) {
			if(isCropping) {
				// Cropping the image
				Mat mat = processor.getPosImages().get(currentImage);
				System.out.println(image.getRect());
				Mat croppedMat = new Mat(mat, image.getRect());
				
				// Switching the old image with the new image
				processor.getPosImages().set(currentImage, croppedMat);
				
				// Updating GUI
				updateImage();
			}
			
			isCropping = !isCropping;
			if(isCropping) this.setBackground(new Color(0, 100, 0));
			else this.setBackground(Color.DARK_GRAY);
		}
		else if(item.equals(finish)) {
			System.out.println("did it");
			// Saving images
			String PATH = FileManager.RES_FOLDER_PATH;
			for(Mat image : processor.getPosImages()) {
				fileManager.writePosImagePath("images/positive/" + processor.getPosImages().indexOf(image) + ".jpg", 
						new Rect(0, 0, image.width(), image.height()));
				Highgui.imwrite(PATH + "/images/positive/" + processor.getPosImages().indexOf(image) + ".jpg", image);
			}
			
			for(Mat image : processor.getNegImages()) {
				fileManager.writeNegImagePath("images/negative/" + processor.getNegImages().indexOf(image) + ".jpg");
				Highgui.imwrite(PATH + "/images/negative/" + processor.getNegImages().indexOf(image) + ".jpg", image);
			}
			
			fileManager.closeFiles();
		}
	}
	
	public void showNextImage() {
		// Making sure the index is in range
		currentImage++;
		if(currentImage >= processor.getPosImages().size()) currentImage = 0;
		
		updateImageNumber();
		updateImage();
	}
	
	public void showPreviousImage() {
		// Making sure the index is in range
		currentImage--;
		if(currentImage < 0) currentImage = processor.getPosImages().size() - 1;
		
		updateImageNumber();
		updateImage();
	}
	
	public void updateImageNumber() {
		currentImageNumber.setText("Current Image: " + (currentImage + 1) + " of " + processor.getPosImages().size());
	}
	
	public void updateImage() {
		ImageIcon icon = new ImageIcon(processor.convMat2Buff(processor.getPosImages().get(currentImage)));
		image.getComponent().setBounds(HaarTraining.window.getSize().width - icon.getIconWidth(), 0, icon.getIconWidth(), icon.getIconHeight());
		image.getComponent().setIcon(icon);
	}
}
