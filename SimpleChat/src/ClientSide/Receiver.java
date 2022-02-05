package ClientSide;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.SocketException;

import Utils.Message;
import Utils.Sounds;

public class Receiver extends Thread {
	
	private Client client;
	private ObjectInputStream in;
	
	public Receiver(Client client, ObjectInputStream in) {
		this.client = client;
		this.in = in;
	}
	
	public void run() {
		try {
			receiveMessages();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	
	private void receiveMessages() throws IOException {
		try {
			while(true) {
				Message msg = (Message)in.readObject();
				if(msg.getFunction() == Message.MSG) {
					if(!client.frameIsActive()) {
						Sounds.playSound("notification");
					}
					client.printOnApp(msg.getSender() + ": " + msg.getMessage());
				}
			}
		} catch(SocketException e) {
			client.getSocket().close();
			client.setText("An error occurred with the connection to the server");
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
	}
}