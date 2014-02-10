package old;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

public class InputHandler implements KeyListener {

	@Override
	public void keyPressed(KeyEvent event) {}
	
	@Override
	public void keyReleased(KeyEvent event) {
		int key = event.getKeyCode();
		
		if(key == KeyEvent.VK_ESCAPE) {
			ImageCollector.fileManager.closeFiles();
			System.exit(0);
		}
		else if(key == KeyEvent.VK_ENTER) ImageCollector.isRunning = !ImageCollector.isRunning;
		else if(key == KeyEvent.VK_B) ImageCollector.lookingForBall = !ImageCollector.lookingForBall;
	}

	@Override
	public void keyTyped(KeyEvent arg0) {}

}
