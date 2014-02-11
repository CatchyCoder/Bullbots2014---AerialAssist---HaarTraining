package org.bullbots.haartraining;

import org.bullbots.haartraining.page.MainPage;

import userinterface.window.Window;

import org.opencv.core.Core;

public class HaarTraining {
	
	public static Window window = new Window(800, 480);
	
	
	
	public HaarTraining() {
		window.setPage(new MainPage(window, 0, 0, window.getSize().width, window.getSize().height, ""));
		window.setVisible(true);
	}
}
