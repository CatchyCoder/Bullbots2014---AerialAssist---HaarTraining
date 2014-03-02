package org.bullbots.haartraining.processing;

import java.io.File;
import java.util.Formatter;

import org.opencv.core.Rect;

public class FileManager {

	private File negInfo, posInfo;
	private Formatter negFormatter, posFormatter;
	
	private final String RES_FOLDER_PATH = "C:/HaarTraining";
	
	public FileManager() {
		try {
			negInfo = new File("images/negative_info.dat");
			posInfo = new File("images/positive_info.dat");
			negFormatter = new Formatter(negInfo);
			posFormatter = new Formatter(posInfo);
		}
		catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public void createResFolders() {
		boolean created = new File(RES_FOLDER_PATH + "/images/positive").mkdirs();
		boolean created2 = new File(RES_FOLDER_PATH + "/images/negative").mkdirs();
		if(!created || !created2) { // Failed to create directory and/or parent directories
			System.err.println("ERROR: Failed to create resource folder(s).");
		}
		else System.out.println("Created Folders");
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
	
	public void writePosImagePath(String path, int objects, Rect rect) {
		try {
			posFormatter.format("%s %s %s %s %s %s" + System.lineSeparator(), path, objects, rect.x, rect.y, rect.width, rect.height);
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
