package org.bullbots.haartraining;

import org.bullbots.haartraining.page.MainPage;
import org.bullbots.haartraining.page.NewMainPage;

import userinterface.window.Window;

import org.opencv.core.Core;

public class HaarTraining {
	
	public static Window window = new Window(1000, 480);
	public static String FONT_STYLE = "Times New Roman";
	
	public HaarTraining() {
		window.setPage(new NewMainPage(window, 0, 0, window.getSize().width, window.getSize().height));
		window.setVisible(true);
	}
}
