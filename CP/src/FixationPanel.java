import java.awt.FlowLayout;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FixationPanel extends JPanel implements KeyListener {

	private boolean isListening;
	private Timer fixationTimer;

	public FixationPanel( Timer timer) {
		isListening = false;
		this.fixationTimer = timer;
		setLayout(new FlowLayout());
		File file = new File ("fix.png");
		Image image = null;
		try {
			image = ImageIO.read(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
		ImageIcon icon = new ImageIcon(image);
		JLabel fixation = new JLabel();
		fixation.setIcon(icon);
		add(fixation);
		addKeyListener(this);
	}

	public void startListening() {
		isListening = true;
		setFocusable(true);
		requestFocusInWindow();
	}


	@Override
	public void keyTyped(KeyEvent e) {
		
	}


	@Override
	public void keyPressed(KeyEvent e) {
		if(isListening) {
			fixationTimer.stop();

			if(e.getID() == 401)
			{
				isListening = false;
				System.out.print("right!");
			}
		}
		//record answer
		
		//Following key detection is working: Just need to write to file, see next method
		//Right arrow pressed
		if (e.getKeyCode()==39)
	    {
	         System.out.println("Right\n");
	    }

		//Left arrow pressed
	    else if (e.getKeyCode()==37)
	    {
	         System.out.println("Left\n");
	    }
		
		//Invalid key pressed, record null
	    else {
	         System.out.println("Invalid key\n");
	    }

	}
	
	/*
	 * Record left, right, or null --> Just shell. Need to get writer/file into this class 
	 * Not sure if we need this if we just want to record accurate or not
	 */
//	private void recordKey(String result) {
//		try {
//			
////			writer.write("RT: " + result + "\n");
////			writer.flush();
//			
//		} catch (IOException e1) {
//			// TODO Auto-generated catch block
//			e1.printStackTrace();
//		}
//	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
