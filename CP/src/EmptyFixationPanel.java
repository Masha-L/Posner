import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Random;
import javax.swing.JPanel;

import javax.imageio.ImageIO;
import javax.swing.ImageIcon;
import javax.swing.JLabel;

public class EmptyFixationPanel extends JPanel{
	private static int screenHeight = 700;
	private static int screenWidth = 1250;	
	private Random rand;
	
	public EmptyFixationPanel() {
		createPanel();
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
}
