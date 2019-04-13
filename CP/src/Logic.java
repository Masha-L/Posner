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
import javax.swing.JTextArea;

public class Logic extends JPanel {

	public File file;
	public FileWriter writer;

	private JPanel promptPanel, instructionsPanel, getReadyPanel;

	private FixationPanel fixationPanel;

	private Timer getReadyTimer, musicTimer, fixationTimer, trialTimer, blockTimer;

	private Music left, right, bLeft, bRight, current;

	private int[] blockSequence = new int[3];

	private String[] INSTRUCTIONS = {"<html>SILENT EASY: Focus your eyes on the cross in the middle of the screen. \n"
			+ "A star will appear in either the left or right box.  Please press the left arrow\n"
			+ " key if the star appears in the box to the left of the fixation cross and press\n"
			+ " the right arrow key if the star appears in the box to the right of the fixation\n"
			+ " cross. You will have half a second to respond once the star appears on the screen.\n"
			+ " Please try to remain as still as possible and leave your headphones on. \n</html>", 
			"<html>SILENT HARD: Focus your eyes on the cross in the middle of the screen. A star will appear in either\n"
			+ " the left or right box, and you will see four objects, (heart, diamond, club, and spade)\n"
			+ " appear at random positions around the screen. You should still press the arrow key\n"
			+ " that corresponds to the box where the star appears. Please press the left arrow key\n"
			+ " if the star appears in the box to the left of the fixation cross and press the\n"
			+ " right arrow key if the star appears in the box to the right of the fixation cross.\n"
			+ " You will have half a second to respond once the star appears on the screen.\n</html>",
			"<html>EXO EASY: Focus your eyes on the cross in the middle of the screen. You will hear a beep in either\n"
			+ " the left or right headphone. A star will appear in either the left or right box.\n"
			+ " Please press the left arrow key if the star appears in the box to the left of the\n"
			+ " fixation cross and press the right arrow key if the star appears in the box to the\n"
			+ " right of the fixation cross. You will have half a second to respond once the star\n"
			+ " appears on the screen.\n</html>",
			"<html>EXO HARD: Focus your eyes on the cross in the middle of the screen. You will hear a beep in\n"
			+ " either the left or right headphone. A star will appear in either the left or\n"
			+ " right box, and you will see four objects, (heart, diamond, club, and spade) appear\n"
			+ " at random positions around the screen. You should still press the arrow key that\n"
			+ " corresponds to the box where the star appears. You will have half a second to respond\n"
			+ " once the star appears on the screen.\n</html>",
			"<html>ENDO EASY: Focus your eyes on the cross in the middle of the screen. This time, you will hear either\n"
			+ " left or right in your headphones. A star will appear in either the left or right box.\n"
			+ "  Please press the left arrow key if the star appears in the box to the left of the fixation\n"
			+ " cross and press the right arrow key if the star appears in the box to the right of the\n"
			+ " fixation cross. You will have half a second to respond once the star appears on the screen.\n</html>",
			"<html>ENDO HARD: Focus your eyes on the cross in the middle of the screen. This time, you will hear either left or\n"
			+ " right in your headphones. A star will appear in either the left or right box, and you will\n"
			+ " see four objects, (heart, diamond, club, and spade) appear at random positions around the screen.\n"
			+ " You should still press the arrow key that corresponds to the box where the star appears.\n"
			+ "You will have half a second to respond once the star appears on the screen.\n</html>"};
	private JTextArea instructionsLabel;
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
		initializeSequence();
		createInstructionsPanel();
		createFixationTimer();
		createFixationPanel();
		createGetReadyPanel();
		createGetReadyTimer();
		createTrialTimer();
		createBlockTimer();
		createMusicTimer();
		initializeMusic();
		
		
		 //I think this needs to be done before
		//the first panel so we can know which instructions to put and set them
		
		
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
				setConditions();
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
				fixationPanel.initializeWriter(writer);
				writer.write("trialCounter currentBlock condition congruent accuracy reaction time\n");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	private void createInstructionsPanel() {
		instructionsPanel = new JPanel(new FlowLayout());
		instructionsLabel = new JTextArea();
		instructionsLabel.setLineWrap(true);
		instructionsLabel.setWrapStyleWord(true);
		instructionsLabel.setFont(new Font(instructionsLabel.getFont().getFontName(), Font.BOLD, 25));
//		instructionsLabel.setBounds(20, 20, 400, 400);
		instructionsLabel.setSize(700, 400);
		
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
					startBlock();
					blockTimer.start();
				}
				if(currentBlock!=0)
					setInstructions();

			}		
		});
		setInstructions();
		instructionsPanel.add(instructionsLabel);
		instructionsLabel.setBounds(20, 20, 400, 400);
		instructionsPanel.add(startButton);
	}

	private void createFixationPanel() {
		fixationPanel = new FixationPanel(fixationTimer);
	}

	private void setInstructions() {
		instructionsLabel.setText(setConditions());
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

	protected String setConditions() {
		//determine distractors 
		determineDistractors();
		//set proper instructions 
		if(distractors == false) {
			//Easy Silent
			if(blockSequence[blockID] == 0) {
				return INSTRUCTIONS[0];
			}
			//Easy Exo
			else if(blockSequence[blockID] == 1) {
				return INSTRUCTIONS[2];
			}
			//Easy Endo
			else {
				return INSTRUCTIONS[4];
			}
		}
		
		//Hard Silent
		if(blockSequence[blockID] == 0) {
			return INSTRUCTIONS[1];
		}
		//Hard Exo
		else if(blockSequence[blockID] == 1) {
			return INSTRUCTIONS[3];
		}
		//Hard Endo
		else {
			return INSTRUCTIONS[5];
		}

//		setInstructions("hey ho");
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
