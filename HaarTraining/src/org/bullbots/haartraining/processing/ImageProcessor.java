package org.bullbots.haartraining.processing;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

import org.bullbots.haartraining.Camera;
import org.bullbots.haartraining.page.MainPage;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class ImageProcessor {

	public static Camera camera;
	private Mat rawImage, processedImage, graphicImage;
	private MainPage mainPage;
	private FileManager fileManager = new FileManager();
	
	private int posCount = 0, negCount = 0;
	private final long DELAY = 200;
	
	public ImageProcessor(MainPage mainPage, int index) {
		// Loading OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Loading camera
		camera = new Camera(index);
		
		// Preparing images
		rawImage = new Mat(480, 640, CvType.CV_8SC1);
		processedImage = new Mat(480, 640, CvType.CV_8SC1);
		graphicImage = new Mat(480, 640, CvType.CV_8SC1);
		
		this.mainPage = mainPage;
	}
	
	private void processImage() {
		processOld();
		
		/*
        // Taking a picture, saving it to image
        camera.read(image);
        
        if(mainPage.isPositiveSearch()) processPositive();
        else processNegative();
        */
    }
	
	public void capturePosImage() {
		System.out.println("capturePosImage()");
		
		// Saving the raw image to the 'positive' folder
		Highgui.imwrite("images/positive/positive_image_" + posCount + ".jpg", rawImage);
		posCount++;
	}
	
	public void captureNegImage() {
		System.out.println("captureNegImage()");
		
		// Saving the raw image to the 'negative' folder
		Highgui.imwrite("images/negative/negative_image_" + negCount + ".jpg", rawImage);
		negCount++;
	}
		
	private void processOld() {
		// Takes a picture
        camera.read(rawImage);
        
    	if(false) {
    		// Writing to info file HERE
    		
            sleep(DELAY);
    	}
    	else {
    		// applies a blur
            Imgproc.GaussianBlur(rawImage, processedImage, new Size(7, 7), 1.1, 1.1);
            
            // Converts the color
            Imgproc.cvtColor(processedImage, processedImage, Imgproc.COLOR_BGR2HSV_FULL);
            
            // Filters the image to look for red
            int rotation = 128 - 255; // = (127) -- Hue
            Core.add(processedImage, new Scalar(rotation, 0, 0), processedImage);
            Core.inRange(processedImage, new Scalar(114, 114, 114), new Scalar(142, 255, 255), processedImage);
            
            // Finding contours
            ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
            Mat hierarchy=new Mat();
    		Imgproc.findContours(processedImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
    		
    		boolean found = false;
    		int i;
    		for(i = 0; i < contours.size(); i++){
    			if(Imgproc.contourArea(contours.get(i)) > 100){
    				found = true;
    				break;
    			}
    		}
    		if(found) {
    			// Finding rect of circle
    			Rect boundingRect = Imgproc.boundingRect(contours.get(i));
    			
    			// Wrote file HERE
        		
        		// After the image is saved, it is drawn on below....
    			
        		// Drawing contours
        		Imgproc.drawContours(graphicImage, contours, i, new Scalar(255, 0,0));
    			Moments mu = Imgproc.moments(contours.get(i),false);
    			Point mc = new Point(mu.get_m10()/mu.get_m00(), mu.get_m01()/mu.get_m00());
    			
    			// Drawing circle in the center of the object detected
    			Core.circle(graphicImage, mc, 4, new Scalar(255, 0, 0),-1,8,0);
        		
    			// Drawing a rectangle
    			Core.rectangle(graphicImage, new Point(boundingRect.x, boundingRect.y), new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height), new Scalar(255, 0, 0));
        		
    			// Wrote OTHER image HERE
    			
        		sleep(DELAY);
    		}
    	}
	}
	
	private void processPositive() {
		System.out.println("Processing positive");
		// Looking for the object...
		
		rawImage = applyFiltering(rawImage);
        
        // Finding contours
        ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy=new Mat();
		Imgproc.findContours(rawImage, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
		
		contours = getAreasLargerThan(contours, 300);
		
		if(contours.size() > 0) {
			// Finding the largest object
			MatOfPoint object = getLargestObject(contours);
			
			// Finding rect of circle
			Rect boundingRect = Imgproc.boundingRect(object);
			
			// Writing to the info file, then storing the image
    		if(mainPage.isCollectingData()) {
    			fileManager.writePosImagePath("positive/positive_image" + posCount + ".jpg", 1, boundingRect);
    			Highgui.imwrite("images/positive/positive_image" + posCount + ".jpg", rawImage);
    		}
    		
    		// After the image is saved, it is drawn on
    		// Drawing contours
    		Imgproc.drawContours(processedImage, contours, contours.indexOf(object), new Scalar(120, 0, 0));
			Moments mu = Imgproc.moments(object ,false);
			Point mc = new Point(mu.get_m10()/mu.get_m00(), mu.get_m01()/mu.get_m00());
			Core.circle(processedImage, mc, 4, new Scalar(120, 0, 0),-1,8,0);
    		
			// Drawing a rectangle
			Core.rectangle(processedImage, new Point(boundingRect.x, boundingRect.y), new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height), new Scalar(255, 255, 100));
    		
			if(mainPage.isCollectingData()) {
				// Writing another image of the positive image that was drawn on
    			Highgui.imwrite("images/positive_view/img" + posCount + ".jpg", rawImage);
    			
    			posCount++;
        		sleep(DELAY);
			}
		}
	}
	
	private void processNegative() {
		System.out.println("Processing negative");
		if(!mainPage.isCollectingData()) return;
		
		// Writing to the data file, then storing the image in a folder
		fileManager.writeNegImagePath("negative/negative_image" + negCount + ".jpg");
		Highgui.imwrite("images/negative/negative_image" + negCount + ".jpg", rawImage);
		negCount++;
		
        sleep(DELAY);
	}
	
	private Mat applyFiltering(Mat image) {		
		// Apply a filter
        Imgproc.GaussianBlur(image, image, new Size(7, 7), 1.1, 1.1);
        
        // Converts the color
        Imgproc.cvtColor(image, image, Imgproc.COLOR_BGR2HSV_FULL);
        
        // Filters the image to look for red
        int rotation = 128 - 255;
        Core.add(image, new Scalar(rotation, 0, 0), image);
        Core.inRange(image, new Scalar(114, 114, 114), new Scalar(142, 255, 255), image);
        //Core.inRange(image, new Scalar(0, 60, 0), new Scalar(255, 255, 255), image);
        
        return image;
	}
	
	private ArrayList<MatOfPoint> getAreasLargerThan(ArrayList<MatOfPoint> list, double size) {
		ArrayList<MatOfPoint> newList = list;
		for(int i = 0; i < newList.size(); i++) {
			if(Imgproc.contourArea(newList.get(i)) < size) {
				newList.remove(i);
			}
		}
		return newList;
	}
	
	private MatOfPoint getLargestObject(ArrayList<MatOfPoint> contours) {
		double max = -1; // area could turn out to be zero
		MatOfPoint object = null;
		for(int i = 0; i < contours.size(); i++) {
			double area = Imgproc.contourArea(contours.get(i));
			if(area > max) {
				max = area;
				object = contours.get(i);
			}
		}
		return object;
	}
	
	private BufferedImage convMat2Buff(Mat mat) {
		// Code for converting Mat to BufferedImage
		int type = BufferedImage.TYPE_BYTE_GRAY;
		if(mat.channels() > 1) {
			Mat mat2 = new Mat();
			Imgproc.cvtColor(mat,  mat2, Imgproc.COLOR_BGR2RGB);
			type = BufferedImage.TYPE_3BYTE_BGR;
			mat = mat2;
		}
		byte[] b = new byte[mat.channels() * mat.cols() * mat.rows()];
		mat.get(0, 0, b); // Get all the pixels
		BufferedImage bImage = new BufferedImage(mat.cols(), mat.rows(), type);
		bImage.getRaster().setDataElements(0, 0, mat.cols(), mat.rows(), b);
		return bImage;
	}
	
	private void sleep(long sleepTime) {
		try {
			Thread.sleep(sleepTime);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public BufferedImage getRawImage() {
		camera.read(rawImage);
		return convMat2Buff(rawImage);
	}
	
	public BufferedImage getProcessedImage() {
		processImage();
		return convMat2Buff(processedImage);
	}
	
	public BufferedImage getGraphicImage() {
		processImage();
		return convMat2Buff(graphicImage);
	}
	
	public int getNegCount() {
		return negCount;
	}
	
	public int getPosCount() {
		return posCount;
	}
}
