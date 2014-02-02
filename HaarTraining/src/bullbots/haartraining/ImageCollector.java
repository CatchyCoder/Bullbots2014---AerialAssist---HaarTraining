package bullbots.haartraining;

import java.awt.Font;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;
import org.opencv.imgproc.Moments;

public class ImageCollector extends JFrame {
	
	private static final long serialVersionUID = 1L;
	
	private final JLabel message = new JLabel("Loading Camera...");
	private final JLabel img = new JLabel();
	private final JLabel mode = new JLabel();
	private final JLabel imgCount = new JLabel();
	
	public static FileManager fileManager = new FileManager();
	
	private Mat image, image2, image3;
	private VideoCapture vc;
	
	private int imageCount = 0;
	private final int DELAY = 250;
	
	int xRes = 640;
    int yRes = 480;
	
	public static boolean isRunning = false;
	public static boolean lookingForBall = false;
    
    public ImageCollector() {
    	initialize();
    	
        while(true) {
        	processImage();
        	updateGUI();
        }
    }
    
    public void initialize() {
    	// Loading the library
        System.out.println("Welcome to OpenCV " + Core.VERSION);
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        
        // Creating the image and settings it size (Image is blank)
    	image = new Mat(yRes,xRes,CvType.CV_8SC1);
    	image2 = new Mat(yRes,xRes,CvType.CV_8SC1);
    	image3 = new Mat(yRes,xRes,CvType.CV_8SC1);
        
        initFrame();
        
        // Getting access to the camera and turning it on
        vc = new VideoCapture(0);
        
        try {
        	Thread.sleep(1100);
        }
        catch(Exception e) {}
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
		
		img.setBounds(spacing, 0, xRes, yRes);
		img.setVisible(true);
		imgCount.setBounds(0, 0, spacing, 100);
		imgCount.setFont(new Font("Arial", Font.BOLD, 18));
		imgCount.setVisible(true);
		mode.setBounds(0, 100, spacing, 100);
		mode.setFont(new Font("Arial", Font.BOLD, 18));
		mode.setVisible(true);
		add(img);
		add(imgCount);
		add(mode);
	}
    
    private void processImage() {
        // Takes a picture
        vc.read(image);
        
        //System.out.print("X: "+vc.get(Highgui.CV_CAP_PROP_FRAME_WIDTH ));
        //System.out.println("\tY: "+vc.get(Highgui.CV_CAP_PROP_FRAME_HEIGHT ));
        
        if(isRunning) {
        	if(!lookingForBall) {
        		// Writing to the info file, then writing storing the image
        		fileManager.writeNegImage("images/nonball/image" + imageCount + ".jpg");
        		Highgui.imwrite("images/nonball/negative_image" + imageCount + ".jpg", image);
        		imageCount++;
        		
                try{
                	Thread.sleep(DELAY);
                }
                catch(Exception e){}
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
        		if(found){
        			Imgproc.drawContours(image, contours, i, new Scalar(0,255,0));
        			Moments mu = Imgproc.moments(contours.get(i),false);
        			Point mc = new Point(mu.get_m10()/mu.get_m00(), mu.get_m01()/mu.get_m00());
        			Core.circle(image, mc, 4, new Scalar(0,255,0),-1,8,0);
        			
        			// Finding rect of circle
        			Rect boundingRect = Imgproc.boundingRect(contours.get(i));
        			Core.rectangle(image, new Point(boundingRect.x, boundingRect.y), new Point(boundingRect.x + boundingRect.width, boundingRect.y + boundingRect.height), new Scalar(255, 255, 100));
        			
        			// Writing to the info file, then writing storing the image
            		fileManager.writePosImage("images/ball/image" + imageCount + ".jpg", 1, boundingRect);
            		Highgui.imwrite("images/ball/positive_image" + imageCount + ".jpg", image);
            		imageCount++;
            		
            		try{
                    	Thread.sleep(DELAY);
                    }
                    catch(Exception e){}
        		}
        	}
        	
        }
    }
	
	public void updateGUI() {
		img.setIcon(new ImageIcon(convMatToBuff(image)));
		imgCount.setText("Images Taken: " + imageCount);
		mode.setText("Looking for ball: " + lookingForBall);
		repaint();
		img.repaint();
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
