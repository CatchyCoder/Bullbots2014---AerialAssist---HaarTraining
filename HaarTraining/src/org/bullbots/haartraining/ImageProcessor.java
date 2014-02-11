package org.bullbots.haartraining;

import java.awt.image.BufferedImage;
import java.util.ArrayList;

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
	
	private Mat image, dirtyImage;
	
	public ImageProcessor(int index) {
		// Loading OpenCV library
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		
		// Loading camera
		camera = new Camera(index);
		
		// Preparing images
		image = new Mat(480, 640, CvType.CV_8SC1);
		dirtyImage = new Mat(480, 640, CvType.CV_8SC1);
	}
	
	private void processImage() {
        // Takes a picture
        camera.read(image);
        //dirtyImage = image;
        
        /*if(isRunning) {
        	if(!lookingForBall) {
        		// Writing to the info file, then storing the image
        		fileManager.writeNegImagePath("negative/negative_image" + imageCount + ".jpg");
        		Highgui.imwrite("images/negative/negative_image" + imageCount + ".jpg", image);
        		imageCount++;
        		
                sleep(DELAY);
        	}
        	else {
        		// Apply a filter
                Imgproc.GaussianBlur(image, image, new Size(7, 7), 1.1, 1.1);
                
                // Converts the color
                Imgproc.cvtColor(image, image2, Imgproc.COLOR_BGR2HSV_FULL); // Will this work?
                
                // Filters the image to look for red
                int rotation = 128 - 255;
                Core.add(image2, new Scalar(rotation, 0, 0), image2);
                Core.inRange(image2, new Scalar(114, 114, 114), new Scalar(142, 255, 255), image3);
                
                // Writing to an image file
                //Highgui.imwrite(".png",image3);	
                
                // Finding contours
                ArrayList<MatOfPoint> contours = new ArrayList<MatOfPoint>();
                Mat hierarchy=new Mat();
        		Imgproc.findContours(image3, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE, new Point(0, 0));
        		
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
        			
        			// Writing to the info file, then storing the image
            		fileManager.writePosImagePath("positive/positive_image" + imageCount + ".jpg", 1, boundingRect);
            		Highgui.imwrite("images/positive/positive_image" + imageCount + ".jpg", image);
            		
            		// After the image is saved, it is drawn on
            		// Drawing contours
            		Imgproc.drawContours(image, contours, i, new Scalar(0,255,0));
        			Moments mu = Imgproc.moments(contours.get(i),false);
        			Point mc = new Point(mu.get_m10()/mu.get_m00(), mu.get_m01()/mu.get_m00());
        			Core.circle(image, mc, 4, new Scalar(0,255,0),-1,8,0);
            		
        			// Drawing a rectangle
        			Core.rectangle(image, new Point(boundingRect.x, boundingRect.y), new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height), new Scalar(255, 255, 100));
            		
        			// Writing another image of the positive image that was drawn on
        			Highgui.imwrite("images/positive_view/img" + imageCount + ".jpg", image);
        			
            		imageCount++;
            		sleep(DELAY);
        		}
        	}
        	
        }*/
    }
	
	public BufferedImage convMat2Buff(Mat mat) {
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
	
	public BufferedImage getCleanImage() {
		processImage();
		return convMat2Buff(image);
	}
	
	public BufferedImage getDirtyImage() {
		processImage();
		return convMat2Buff(dirtyImage);
	}
}
