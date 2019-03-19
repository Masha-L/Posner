import java.io.File;
import java.io.IOException;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.FloatControl;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;

/**
 * The Class to work with .wav music files
 * @author Mashka
 *
 */
public class Music implements LineListener {

	private boolean released = false;
	private Clip music;
	private FloatControl volumeCtrl;
	private boolean isPlaying = false;

	/**
	 * Constructor - processes the file and sets the volume
	 * @param f - the name of the file.
	 */
	public Music(File f) {
		try {

			AudioInputStream stream = AudioSystem.getAudioInputStream(f);
			music = AudioSystem.getClip();
			music.open(stream);
			music.addLineListener(this);
			volumeCtrl = (FloatControl) music.getControl(FloatControl.Type.MASTER_GAIN);
			released = true;

		} catch (IOException | UnsupportedAudioFileException | LineUnavailableException exc) {
			System.out.println("error :(");
			released = false;
		}
	}

	/**
	 * This methods starts the music.
	 * @param isPlaying - helps to figure out if the music is currently playing.
	 */
	public void play(boolean isPlaying) {
		
		if (released) {
			if (isPlaying) {
				music.stop();
				music.setFramePosition(0);
				music.start();
				this.isPlaying = true;
			} 
			else if (isPlaying == false) {
				music.setFramePosition(0);
				music.start();
				isPlaying = true;
			}
		}
	}

	@Override
	public void update(LineEvent event) {
		if (event.getType() == LineEvent.Type.STOP) {
			isPlaying = false;
			synchronized (music) {
				music.notify();
			}
		}
	}
}