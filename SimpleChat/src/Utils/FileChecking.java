package Utils;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.swing.JOptionPane;

public class FileChecking {
	
	public static boolean soundFilesExist() {
		return soundFilesDirectoryExists() && allSoundFilesExist();
	}
	
	public static boolean soundFilesDirectoryExists() { //Checks if there is a directory for the app in appdata. If not, creates it.
		String appDataPath = System.getenv("APPDATA");
		appDataPath = appDataPath + "/tittiesChat";
		File directory = new File(appDataPath);
		if(!directory.exists()) {
			boolean b = directory.mkdir();
			if(!b) {
				JOptionPane.showMessageDialog(null, "Error creating directory in AppData");
				System.out.println("Error creating directory in AppData");
				System.exit(2);
			}
			return false;
		} else {
			return true;
		}
	}
	
	public static boolean allSoundFilesExist() {
		try (Stream<Path> walk = Files.walk(Paths.get(System.getenv("APPDATA") + "/tittiesChat"))) {

			List<String> result = walk.map(x -> x.toString())
					.filter(fi -> fi.endsWith(".wav")).collect(Collectors.toList());
		
			return result.size() >= 2;

		} catch (IOException e) {
			e.printStackTrace();
			return false;
		}
	}
	
	public static void main(String[] args) {
//		try(ObjectOutputStream oos = new ObjectOutputStream(Files.newOutputStream(Paths.get(soundFile.getPath())))) {
//			oos.writeObject(soundFile);
//		} catch (IOException e) {
//			// TODO Auto-generated catch block
//			e.printStackTrace();
//		}
	}
	
}
