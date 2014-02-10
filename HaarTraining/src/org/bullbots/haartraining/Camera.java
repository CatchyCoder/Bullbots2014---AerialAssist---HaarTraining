package org.bullbots.haartraining;

import org.opencv.core.Mat;
import org.opencv.highgui.VideoCapture;

public class Camera {
	
	private static VideoCapture VC;
	
	public Camera(int index) {
		VC = new VideoCapture(index);
		Runtime.getRuntime().addShutdownHook(new Shutdownhook());
		
		// Giving a small amount of time to initailize the camera
		try {
			Thread.sleep(250);
		}
		catch(Exception e) {
			e.printStackTrace();;
		}
	}
	
	private static class Shutdownhook extends Thread {
		public void run() {
			System.out.println("Shutting down Camera...");
			VC.release();
			System.out.println("Camera was successfully closed.");
		}
	}
	
	public void read(Mat image) {
		VC.read(image);
	}
}
