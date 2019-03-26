import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.Timer;

public class Logic extends JPanel {
	// prompt for number + button	
	// instructions + start
	// get ready (1 sec)
	// music for 200 ms
	// screen (hide after 500 ms or after the button is clicked)

	public File file;
	public FileWriter writer;
	
	private JPanel promptPanel, instructionsPanel, getReadyPanel;
	private FixationPanel fixationPanel;
	private Timer getReadyTimer, musicTimer, fixationTimer, trialTimer, blockTimer;
	private Music left, right, bLeft, bRight, current;
	private int[] blockSequence = new int[6];
	private JPanel self = this;
	private String[] INSTRUCTIONS = {"", "", "", "", "", ""};
	private JLabel instructionsLabel;
	private boolean distractors;
	// 0 - control, 1 - exo, 2 - endo
	private int soundType;
	private ImageIcon image;
	private int trialCounter = 0;
	
	private long start, stop;
	

	public Logic() {
		setLayout(new BorderLayout());	
		createPromptPanel();
		createInstructionsPanel();
		createFixationTimer();
		createFixationPanel();
		createGetReadyPanel();
		createGetReadyTimer();
		createTrialTimer();
		createBlockTimer();
		createMusicTimer();
		initializeMusic();
		initializeSequence();
		current = bLeft;
		add(promptPanel);
	}

	private void createPromptPanel() {
		promptPanel = new JPanel(new FlowLayout());
		JLabel promptLabel = new JLabel("Enter the participant number");
		JTextField promptField = new JTextField(20);
		JButton promptButton = new JButton("Submit");
		promptButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				//Make file for the participant
				int ptNumber = Integer.parseInt(promptField.getText());
				createNewFile(ptNumber); 
				
				for (int i = 0; i < blockSequence.length; i++) {
					int blockID = blockSequence[i];
					setInstructions(INSTRUCTIONS[blockID]);
					distractors = (blockID % 2 != 0); 
					soundType = blockID / 2;
					displayPanel(instructionsPanel);	
				}
			}		
		});
		promptPanel.add(promptLabel);
		promptPanel.add(promptField); 
		promptPanel.add(promptButton);
	}

	private void createNewFile(int ptNumber) {
		//Path path = FileSystems.getDefault().getPath(".").toAbsolutePath();
		String fileName = "./" + ptNumber + ".txt";
		System.out.println(fileName);
		file = new File(fileName); 
		try {
			if (file.createNewFile())
			{
				//write down the date 
				 
				//Initialize file writer
				writer = new FileWriter(file);
//				writer.write("Test data");
//				writer.close();
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void createInstructionsPanel() {
		instructionsPanel = new JPanel(new FlowLayout());
		instructionsLabel = new JLabel();
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {		
				startBlock();
				blockTimer.start();
			}		
		});
		instructionsPanel.add(instructionsLabel);
		instructionsPanel.add(startButton);
	}

	private void createFixationPanel() {
		fixationPanel = new FixationPanel(fixationTimer);
	}
	
	private void setInstructions(String currentInstruction) {
		instructionsLabel.setText(currentInstruction);
	}
	
	private void createGetReadyPanel() {
		getReadyPanel = new JPanel(new BorderLayout());
		JLabel getReadyLabel = new JLabel("<html>Get ready!</html>");
		getReadyPanel.add(getReadyLabel);
	}

	private void createGetReadyTimer() {
		getReadyTimer = new Timer(1000, event -> {
			removeAll();
			add(fixationPanel);			
			revalidate();
			repaint();
			
			current.play(true);
			musicTimer.start();
		});
		getReadyTimer.setRepeats(false);
	}
	
	private void createMusicTimer() {
		musicTimer = new Timer(200, event -> {
			// ADD DISTRACTORS IF NEEDED (CREATE A METHOD INSIDE FIXATIONPANEL CLASS)
			revalidate();
			repaint();	
			
			// fixationpanel starts listening
			fixationPanel.startListening();
			fixationTimer.start();
			System.out.println("now!");

		});
		musicTimer.setRepeats(false);
	}
	
	private void createFixationTimer() {
		fixationTimer = new Timer(500, event -> {
			// if the key is pressed while it is listening, record into
			// the boolean variable keyPressed - not here
			
			// in FIXATIONPANEL, in keyPressed() 
			// if specific keys were pressed WHILE LISTENING - record 
			System.out.println("timeout!");
//STOP LISTENING! 
			// at trigger, STOP listening;  
			// if !keyPressed, record the results
			// otherwise, do nothing
		});
		fixationTimer.setRepeats(false);
	}

	private void createTrialTimer() {	
		// Triggers every 1000 + 200 + 500 ms
		trialTimer = new Timer(1800, e ->  {
			//Will start new block every 4 trials
			if (++trialCounter % 4 == 0) {
				trialTimer.stop();
			}
			else {
				stop = System.nanoTime();
				long reacTime = stop - start;
				System.out.println("Trial time: " + (stop - start));
				
				try {
					
					writer.write("RT: " + reacTime + "\n");
					writer.flush();
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				start = stop;
				startTrial();
			}
		});
	}
	
	private void createBlockTimer() {	
		// Triggers every 1000 + 200 + 500 ms
		//50,000ms - length of block (50 sec)
		blockTimer = new Timer(7000, e ->  {
			displayPanel(instructionsPanel);
		});
		blockTimer.setRepeats(false);
	}
	
	private void displayPanel(JPanel newPanel) {
		removeAll();
		add(newPanel);			
		revalidate();
		repaint();
	}

	private void initializeMusic() {
		//endogenous condition
		File fLeft = new File("Left.wav");
		File fRight = new File("Right.wav");

		//exogenous condition
		File fbLeft = new File("bLeft.wav");
		File fbRight = new File("bRight.wav");

		//endo cond audio
		left = new Music(fLeft);
		right = new Music(fRight);

		//exo cond audio
		bLeft = new Music(fbLeft);
		bRight = new Music(fbRight);
	}

	private void initializeSequence() {
		/*
		 * 0 - silence
		 * 1 - endogenous
		 * 2 - exogenous
		 
		 */	
		Random rand = new Random();
		for (int i = 0; i < 3; i++) {
			blockSequence[i] = i;
		}
		// mix up - swaps 15 times
		for (int i = 0; i < 15; i++) {
			int a = rand.nextInt(3);
			int b = rand.nextInt(3);
			int temp = blockSequence[a];
			blockSequence[a] = blockSequence[b];
			blockSequence[b] = temp;
		}
	}

	private void startBlock() {
		start = System.nanoTime();	
		trialTimer.start();
		startTrial();
	}
	
	private void startTrial() {
		// DETERMINE CONGRUENCY HERE!
		System.out.println("new trial");
		displayPanel(getReadyPanel);	
		getReadyTimer.start();		
	}
}
