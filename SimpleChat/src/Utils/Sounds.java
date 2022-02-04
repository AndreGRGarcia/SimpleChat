package Utils;

import java.io.File;

import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;

public class Sounds {
	
	public static synchronized void playSound(final String url) {
		try {
			AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(new File(System.getenv("APPDATA") + "/tittiesChat/" + url + ".wav").getAbsoluteFile());
			Clip clip = AudioSystem.getClip();
			clip.open(audioInputStream);
			clip.start();
			
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
}
