import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.awt.Image;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.Timer;

public class FixationPanel extends JPanel implements KeyListener {

	private boolean isListening;
	private Timer fixationTimer;
	
	private static int screenHeight = 700;
	private static int screenWidth = 1250;	
	private Random rand;
	private JLabel starLabelLeft, starLabelRight, heartLabel, 
	spadeLabel, clubLabel, diamondLabel;
	private Dimension heartSize, spadeSize, clubSize, diamondSize;
	private boolean isAdded = false;

	
	public FixationPanel( Timer timer) {
		isListening = false;
		this.fixationTimer = timer;
//		setLayout(new FlowLayout());
//		setLayout(null);
//		File file = new File ("fix.png");
//		Image image = null;
//		try {
//			image = ImageIO.read(file);
//		} catch (IOException e) {
//			e.printStackTrace();
//		}
//		ImageIcon icon = new ImageIcon(image);
//		JLabel fixation = new JLabel();
//		fixation.setIcon(icon);
//		fixation.setBounds(0, 0, screenWidth, screenHeight);
//		add(fixation);
	    //Add fixation cross and boxes
		createPanel();
		createStarLeft();
		createStarRight();
		createDistractors();
		addKeyListener(this);
	}
	
	/*
	 * L Box X: 252
	 * R Box X: 877
	 */
	private int getXCoor(Dimension size) {
		//Initialize invalid value
		int x = 870;
		while(x > 252-size.width && x < 877) {
//			System.out.println("IN X LOOP");
			x = rand.nextInt(screenWidth-size.width);
		}
		return x;
	}
	
	/*
	 * Box Y: 290
	 * Box Size: 120
	 */
	private int getYCoor(Dimension size) {
		//Initialize value to be too large
		int y = 400;
		while(y > 290 - size.height && y < 290 + 120) {
//			System.out.println("IN Y LOOP");
			y = rand.nextInt(screenHeight-size.height);
		}
		return y;
	}
	
	private void createPanel() {
		setLayout(null);
		setBackground(new Color(126,126,126));
		rand = new Random();
		
		//Fixation Cross
		JLabel label = new JLabel("+");
		label.setBounds((screenWidth - 50)/2, setY(50), 50, 50);
		label.setFont(new Font("Serif", Font.PLAIN, 50));
		add(label);
		
		//Boxes
		BufferedImage lBox = null;
		BufferedImage rBox = null;
		try {
			lBox = ImageIO.read(new File("Box.png"));
			rBox = ImageIO.read(new File("Box.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		JLabel lBoxLabel = new JLabel(new ImageIcon(lBox));
		JLabel rBoxLabel = new JLabel(new ImageIcon(rBox));
		Dimension lBoxSize = lBoxLabel.getPreferredSize();
		Dimension rBoxSize = lBoxLabel.getPreferredSize();
		lBoxLabel.setBounds((int)(screenWidth*0.25) - 60, setY(lBoxSize.height), lBoxSize.width, lBoxSize.height);
		rBoxLabel.setBounds((int)(screenWidth*0.75) - 60, setY(rBoxSize.height), rBoxSize.width, rBoxSize.height);
		
		add(lBoxLabel);
		add(rBoxLabel);
	}
	
	// Centers Y position of objects
	private int setY(int objHeight) {
		return (int)(screenHeight-objHeight)/2;
	}
	
	/*
	 * Places the target visual stimulus in the left box
	 */
	private void createStarLeft() {
		BufferedImage star = null;
		try {
			star = ImageIO.read(new File("Target_Visual_Stimulus.PNG"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		starLabelLeft = new JLabel(new ImageIcon(star));
		Dimension size = starLabelLeft.getPreferredSize();
		starLabelLeft.setBounds((int)(screenWidth*0.25) - 60 + size.width/2-9, setY(size.height), size.width, size.height);	
	}
	
	/*
	 * Places the target visual stimulus in the right box
	 */
	private void createStarRight() {
		BufferedImage star = null;
		try {
			star = ImageIO.read(new File("Target_Visual_Stimulus.PNG"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		starLabelRight = new JLabel(new ImageIcon(star));
		Dimension size = starLabelRight.getPreferredSize();
		starLabelRight.setBounds((int)(screenWidth*0.75) - 60 + size.width/2-9, setY(size.height), size.width, size.height);
	}
	
	private void createDistractors() {
		BufferedImage heart = null;
		BufferedImage spade = null;
		BufferedImage club = null;
		BufferedImage diamond = null;
		try {
			heart = ImageIO.read(new File("Heart.png"));
			spade = ImageIO.read(new File("Spade.png"));
			club = ImageIO.read(new File("Club.png"));
			diamond = ImageIO.read(new File("Diamond.png"));
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		//Heart
		heartLabel = new JLabel(new ImageIcon(heart));
		heartSize = heartLabel.getPreferredSize();
		
		//Spade
		spadeLabel = new JLabel(new ImageIcon(spade));
		//Kept getting cut off - so set minimum size (increased by 15 from size of image)
		spadeSize = new Dimension();
		spadeSize.setSize(85, 100);
		
		//Club
		clubLabel = new JLabel(new ImageIcon(club));
		clubSize = clubLabel.getPreferredSize();
		
		//Diamond
		diamondLabel = new JLabel(new ImageIcon(diamond));
		//Kept getting cut off - so set minimum size (increased by 15 from size of image)
		diamondSize = new Dimension();
		diamondSize.setSize(85, 99);
	}
	
	//0 = left, 1 = right
	public void addStar(int side) {
		if(side == 0) {
			add(starLabelLeft);
		}
		else {
			add(starLabelRight);
		}
	}
	
	public void addDistractors() {
		if(!isAdded) {
			add(heartLabel);
			add(spadeLabel);
			add(clubLabel);
			add(diamondLabel);
			isAdded = true;
		}
		randLabelPos();
		makeDistVisible();
	}
	
	private void makeDistVisible() {
		heartLabel.setVisible(true);
		spadeLabel.setVisible(true);
		clubLabel.setVisible(true);
		diamondLabel.setVisible(true);
	}
	private void randLabelPos() {
		heartLabel.setBounds(getXCoor(heartSize), getYCoor(heartSize), heartSize.height, heartSize.width);
		spadeLabel.setBounds(getXCoor(spadeSize), getYCoor(spadeSize), spadeSize.height, spadeSize.width);
		clubLabel.setBounds(getXCoor(clubSize), getYCoor(clubSize), clubSize.height, clubSize.width);
		diamondLabel.setBounds(getXCoor(diamondSize), getYCoor(diamondSize), diamondSize.height, diamondSize.width);
	}
	
	public void deleteDistractors() {
		heartLabel.setVisible(false);
		spadeLabel.setVisible(false);
		clubLabel.setVisible(false);
		diamondLabel.setVisible(false);
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
			
			if(e.getID() == 401)
			{
				fixationTimer.stop();
				isListening = false;
				deleteDistractors();
				System.out.print("right!");
			}
		}
		//record answer
		
		//Following key detection is working: Just need to write to file, see next method
		//Right arrow pressed
		if (e.getKeyCode()==39)
	    {
			fixationTimer.stop();
			isListening = false;
			deleteDistractors();
	        System.out.println("Right\n");
	    }

		//Left arrow pressed
	    else if (e.getKeyCode()==37)
	    {
			fixationTimer.stop();
			isListening = false;
			deleteDistractors();
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
