import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.net.URI;
import java.net.URL;

import javax.swing.JFrame;
import javax.swing.JOptionPane;


public class VersionCheck extends Thread {
	private static JFrame frame;
	private static String currversion;
	public VersionCheck(JFrame frame, String currversion) {
		VersionCheck.frame=frame;
		VersionCheck.currversion=currversion;
	}
	

	@Override
	public void run() {
		try {
			URL version= new URL("http://....../replacerver.txt");
			BufferedReader in = new BufferedReader(new InputStreamReader(version.openStream()));
			String newver= in.readLine();
			URI downloadpage=new URI(in.readLine());
			in.close();
			if (newver!=null && !newver.equals(currversion)) {
				if(JOptionPane.showConfirmDialog(frame, "New version available! ("+newver+") Do you want to check the dowload page in browser?", "New version", JOptionPane.YES_NO_OPTION, JOptionPane.INFORMATION_MESSAGE)==JOptionPane.YES_OPTION) {
					java.awt.Desktop.getDesktop().browse(downloadpage);
				}
			}
		} catch (FileNotFoundException e) {
			System.out.println("No update info found");
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

}
