package org.bullbots.haartraining.processing;

import java.io.File;
import java.util.Formatter;

import org.opencv.core.Rect;

public class FileManager {
	
	private File negInfo, posInfo;
	private Formatter negFormatter, posFormatter;
	
	public static final String RES_FOLDER_PATH = "C:/HaarTraining";
	
	public FileManager() {
		createResFolders();
		
		
		try {
			negInfo = new File(RES_FOLDER_PATH + "/negative_info.dat");
			posInfo = new File(RES_FOLDER_PATH + "/positive_info.dat");
			negFormatter = new Formatter(negInfo);
			posFormatter = new Formatter(posInfo);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		
		
	}
	
	public void createResFolders() {
		File path1 = new File(RES_FOLDER_PATH + "/images/positive");
		File path2 = new File(RES_FOLDER_PATH + "/images/negative");
		
		// If the path does not exist, and it could not be created, display an error message
		if(!path1.exists() && !path1.mkdirs()) System.out.println("ERROR: Could not make \"" + path1.getPath() + "\"");
		if(!path2.exists() && !path2.mkdirs()) System.out.println("ERROR: Could not make \"" + path2.getPath() + "\"");
	}
	
	public void writeNegImagePath(String path) {
		try {
			negFormatter.format("%s" + System.lineSeparator(), path);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("File \"" + negInfo.getAbsolutePath() + "\" could not be written to.");
		}
	}
	
	public void writePosImagePath(String path, Rect rect) {
		try {
			posFormatter.format("%s %s %s %s %s %s" + System.lineSeparator(), path, 1, rect.x, rect.y, rect.width, rect.height);
		}
		catch(Exception e) {
			e.printStackTrace();
			System.err.println("File \"" + posInfo.getAbsolutePath() + "\" could not be written to.");
		}
	}
	
	public void closeFiles() {		
		negFormatter.close();
		posFormatter.close();
	}
}
