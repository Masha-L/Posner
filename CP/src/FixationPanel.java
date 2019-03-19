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

	}

	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub

	}

}
