package ServerSide;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.net.Socket;

public class SSSender {
	
	private Server server;
	public Socket socket;
	private ObjectOutputStream out;
	private int id;
	
	public SSSender(Server server, Socket socket, int id) {
		this.server = server;
		this.socket = socket;
		this.id = id;
		doConnection();
	}
	
	
	private void doConnection(){
		try {
			out = new ObjectOutputStream(socket.getOutputStream());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void closeConnection() {
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void send(Serializable obj) {
		try {
			out.writeObject(obj);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public void sendSoundFile(File fileToSend) {
		try {
			System.out.println("Sending ok with file length");
		    send(new Message(Message.OK, (int)fileToSend.length(), fileToSend.getName()));
	        
	        byte[] bytes = new byte[16 * 1024];
	        InputStream inT = new FileInputStream(fileToSend);
			OutputStream outT = socket.getOutputStream();
			
			System.out.println("Sending file");
			int count;
			while ((count = inT.read(bytes)) > 0) {
				System.out.println("Sending bytes");
				outT.write(bytes, 0, count);
			}
			inT.close();
		} catch (IOException e) {
			server.writeOnScreen("Error sending sound file");
			System.exit(1);
		} 
	}
	
	
	public int getIdd() {
		return id;
	}
}