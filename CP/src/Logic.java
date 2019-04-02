import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Font;
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

	public File file;
	public FileWriter writer;

	private JPanel promptPanel, instructionsPanel, getReadyPanel;

	private FixationPanel fixationPanel;

	private Timer getReadyTimer, musicTimer, fixationTimer, trialTimer, blockTimer;

	private Music left, right, bLeft, bRight, current;

	private int[] blockSequence = new int[3];

	private String[] INSTRUCTIONS = {"", "", "", "", "", ""};
	private JLabel instructionsLabel;
	private int trialCounter = 0;
	private int currentBlock = 0;
	private int blockID = 0;
	private long start, stop;

	private boolean distractors = true;
	// 0 - control, 1 - exo, 2 - endo
	private int soundType;
	//0 - left, 1 - right
	private int direction;
	//true - if congruent, false if not 
	private boolean congruent;

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
				displayPanel(instructionsPanel);
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
				writer.write("trialCounter currentBlock condition congruent accuracy reaction time\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createInstructionsPanel() {
		instructionsPanel = new JPanel(new FlowLayout());
		instructionsLabel = new JLabel();
		instructionsLabel.setHorizontalAlignment(JLabel.CENTER);
		instructionsLabel.setVerticalAlignment(JLabel.CENTER);
		JButton startButton = new JButton("Start");
		startButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(currentBlock < 6) {
					if(currentBlock==2) {
						blockID++;
					}

					if(currentBlock == 4) {
						blockID++;
					}

					setConditions();
					startBlock();
					blockTimer.start();
				}

			}		
		});
		instructionsPanel.add(instructionsLabel);
		instructionsPanel.add(startButton);
	}

	private void createFixationPanel() {
		fixationPanel = new FixationPanel(fixationTimer, writer);
	}

	private void setInstructions(String currentInstruction) {
		instructionsLabel.setText(currentInstruction);
	}

	private void createGetReadyPanel() {
		getReadyPanel = new JPanel(new BorderLayout());
		JLabel getReadyLabel = new JLabel("Get ready!");
		getReadyLabel.setHorizontalAlignment(JLabel.CENTER);
		getReadyLabel.setVerticalAlignment(JLabel.CENTER);
		getReadyLabel.setFont(new Font(getReadyLabel.getFont().getFontName(), Font.BOLD, 35));
		getReadyPanel.add(getReadyLabel, BorderLayout.CENTER);
	}

	private void createGetReadyTimer() {
		getReadyTimer = new Timer(1000, event -> {
			removeAll();
			add(fixationPanel);			
			revalidate();
			repaint();

			if(blockSequence[blockID]!=0) {
				current.play(true);
			}

			musicTimer.start();
		});
		getReadyTimer.setRepeats(false);
	}

	private void createMusicTimer() {
		musicTimer = new Timer(200, event -> {
			fixationPanel.addStar(direction);
			//Add distractors
			if(distractors) {
				fixationPanel.addDistractors();
			}

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
			fixationPanel.deleteDistractors();
			// in FIXATIONPANEL, in keyPressed() 
			// if specific keys were pressed WHILE LISTENING - record 
			System.out.println("timeout!");
			//STOP LISTENING! 
			// at trigger, STOP listening;  
			// if !keyPressed, record the results
			// otherwise, do nothing
			recordTimeOut();

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
				startTrial();
			}
		});
	}

	private void recordTimeOut() {
		try {

			writer.write(0 + " " + 500 + "\n");
			writer.flush();

		} catch (IOException e1) {
			e1.printStackTrace();
		}
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

	protected void setConditions() {
		//determine distractors 
		determineDistractors();
		//set proper instructions 
		setInstructions("hey ho");
	}

	private void determineDirection() {
		Random r = new Random();
		direction = r.nextInt(2);
	}

	private void determineDistractors() {
		if(currentBlock%2==0)
			distractors = false;
		else 
			distractors = true;
		currentBlock++;
	}

	private void eightyTwentyMusic() {
		Random r = new Random();
		int val = r.nextInt(100);

		if(val>=20) {
			if(direction==0) //left
				current = (blockSequence[blockID] == 1) ? bLeft: left;
			else
				current = (blockSequence[blockID] == 1) ? bRight: right;
			congruent = true;
		}
		else {
			if(direction==0)
				current = (blockSequence[blockID] == 1) ? bRight: right;
			else
				current = (blockSequence[blockID] == 1) ? bLeft: left;
			congruent = false;
		}	
	}

	private void startBlock() {
		start = System.nanoTime();	
		trialTimer.start();
		startTrial();
	}

	private void startTrial() {
		fixationPanel.removeStar();
		//remove the star
		//choose stimuli first! 
		determineDirection(); 
		//determine congruency 
		eightyTwentyMusic();
		recordData();
		displayPanel(getReadyPanel);	
		getReadyTimer.start();		
	}

	private void recordData() {
		try {
			writer.write(trialCounter + " " + currentBlock + " " + blockSequence[blockID] 
					+ " " + congruent + " ");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
