package old;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.bullbots.haartraining.Camera;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfRect;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;
import org.opencv.objdetect.CascadeClassifier;

public class ImageCollector extends JFrame {
	
	/*
	 * TODO:
	 * * Clean up the code
	 * * Clean up the GUI
	 */
	
	private static final long serialVersionUID = 1L;
	
	private final JLabel imgComp = new JLabel();
	private final JLabel mode = new JLabel();
	private final JLabel imgCount = new JLabel();
	
	public static FileManagerOld fileManager = new FileManagerOld();
	
	private Mat image, image2, image3;
	private Camera camera;
	
	private int imageCount = 0;
	private final int DELAY = 250;
	
	int xRes = 640;
    int yRes = 480;
	
	public static boolean isRunning = false;
	public static boolean lookingForBall = false;
    
    public ImageCollector() {
    	initialize();
    	
        while(true) {
        	//processImage();
        	stuff();
        	updateGUI();
        }
    }
    
    public void initialize() {
    	// Loading the library
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        camera = new Camera(0);
        
        // Creating the image and settings it size (Image is blank)
    	image = new Mat(yRes,xRes,CvType.CV_8SC1);
    	image2 = new Mat(yRes,xRes,CvType.CV_8SC1);
    	image3 = new Mat(yRes,xRes,CvType.CV_8SC1);
        
        initFrame();
    }
    
    private void initFrame() {
    	int spacing = 200;
		setSize(xRes + spacing, yRes);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setLocationRelativeTo(null);
		setUndecorated(true);
		setLayout(null);
		setResizable(false);
		setVisible(true);
		addKeyListener(new InputHandler());
		
		imgComp.setBounds(spacing, 0, xRes, yRes);
		imgComp.setVisible(true);
		imgCount.setBounds(0, 0, spacing, 100);
		imgCount.setFont(new Font("Arial", Font.BOLD, 18));
		imgCount.setVisible(true);
		mode.setBounds(0, 100, spacing, 100);
		mode.setFont(new Font("Arial", Font.BOLD, 18));
		mode.setVisible(true);
		add(imgComp);
		add(imgCount);
		add(mode);
	}
    
    private void stuff() {
	    CascadeClassifier faceDetector = new CascadeClassifier("final.xml");
	    //faceDetector.load("testthing.xml");
	    System.out.println(faceDetector.empty());
	    camera.read(image);

	    // Detect faces in the image.
	    // MatOfRect is a special container class for Rect.
	    MatOfRect faceDetections = new MatOfRect();
	    faceDetector.detectMultiScale(image, faceDetections);

	    System.out.println(String.format("Detected %s things", faceDetections.toArray().length));

	    // Draw a bounding box around each face.
	    for (Rect rect : faceDetections.toArray()) {
	        Core.rectangle(image, new Point(rect.x, rect.y), new Point(rect.x + rect.width, rect.y + rect.height), new Scalar(0, 255, 0));
	    }

	    // Save the visualized detection.
	    String filename = "testpic.png";
	    Highgui.imwrite(filename, image);
    }
    
    private void processImage() {
        // Takes a picture
        camera.read(image);
        
        //System.out.print("X: "+vc.get(Highgui.CV_CAP_PROP_FRAME_WIDTH ));
        //System.out.println("\tY: "+vc.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT ));
        
        if(isRunning) {
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
        	
        }
    }
    
    private void sleep(long sleepTime) {
    	try {
    		Thread.sleep(sleepTime);
    	}
    	catch(Exception e) {
    		e.printStackTrace();
    	}
    }
	
	public void updateGUI() {
		imgComp.setIcon(new ImageIcon(convMatToBuff(image)));
		imgCount.setText("Images Taken: " + imageCount);
		mode.setText("Looking for ball: " + lookingForBall);
		repaint();
		imgComp.repaint();
		imgCount.repaint();
	}
	
	public BufferedImage convMatToBuff(Mat mat) {
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
}
