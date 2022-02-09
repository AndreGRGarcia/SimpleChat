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
		receiveMessages();
	}
	
	
	private void receiveMessages() {
		try {
			while(true) {
				Message msg = (Message)in.readObject();
				if(msg.getFunction() == Message.MSG) {
					if(!client.frameIsActive()) {
						Sounds.playSound("notification");
					}
					client.printOnApp(msg.getSender() + ": " + msg.getMessage(), false);
				}
			}
		} catch(SocketException e) {
			try {
				client.getSocket().close();
			} catch (Exception e2) {
				e.printStackTrace();
			}
			//client.setText("An error occurred with the connection to the server");
			client.printOnApp("\nAn error occurred with the connection to the server", true);
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			
		}
		System.out.println("Receiver finished");
	}
	
	public void closeObjIn() throws IOException {
		in.close();
	}
	
}