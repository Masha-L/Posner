import javax.swing.JFrame;

public class Application {
	public static void main(String[] args) {
		JFrame frame = new JFrame("norm");
		frame.setSize(1250, 700);
		frame.setResizable(false);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.add(new Logic());
		frame.setVisible(true);
	}
}
