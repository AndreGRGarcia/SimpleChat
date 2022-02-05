package ServerSide;

import java.io.File;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.Socket;
import java.net.SocketException;

import Utils.Message;

public class SSReceiver extends Thread {
	
	private Server server;
	private SSSender sender;
	private Socket socket;
	private ObjectInputStream in;
	private int id;
	private String filesLocation;
	
	public SSReceiver(Server server, Socket socket, int id, String filesLocation) {
		this.server = server;
		this.socket = socket;
		this.id = id;
		this.filesLocation = filesLocation;
	}
	
	private void doConnection() throws IOException {
		//in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		in = new ObjectInputStream(socket.getInputStream());
	}

	public void run() {
		try {
			doConnection();
			notifyClients();
			doReceiverThings();
		} catch (IOException e) {
			try {
				sender.closeConnection();
				socket.close();
			} catch (IOException e1) {
				// TODO Auto-generated catch block
				e1.printStackTrace();
			}
		}
	}
	
	private void notifyClients() {
		//sender.send(obj); //TODO
	}

	private void doReceiverThings() throws IOException {
		try{	
			for(;;) {
				System.out.println("SSReceiver waiting for messages");
				Message msg = (Message)in.readObject();
				System.out.println("SSReceiver " + getIdd() + ": " + msg.getFunction());
				if(msg.getFunction() == Message.SEND_SOUND_FILES) {
					System.out.println("Message was send files");
					sendSoundFiles();
				} else if(msg.getFunction() == Message.MSG) {
					if(msg.getMessage().equals("FIM")) {
						closeConnections();
						break;
					}
					for(SSSender s: server.getList()) {
						s.send(msg);
					}
				}
			}
		} catch(SocketException se) {
			closeConnections();
		} catch(IOException ioe) {
			closeConnections();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void sendSoundFiles() {
		try {
			
			File dir = new File(filesLocation);
			String[] fileList = dir.list();
			
			for(String fileName: fileList) {
				File fileToSend = new File(filesLocation + File.separator + fileName);
			    
			    sender.sendSoundFile(fileToSend);
				
				System.out.println("Waiting to get ok");
				in.readObject();	
			}

		} catch (IOException e) {
			server.writeOnScreen("Error with connection to client");
			System.exit(1);
		} catch (ClassNotFoundException e) {
			server.writeOnScreen("Error on receiving message from client");
			System.exit(1);
		}
		
	}

	private void closeConnections() {
		try {
			in.close();
			SSSender sen = null;
			for(SSSender s: server.getList()) {
				if(s.getIdd() == getIdd()) {
					s.closeConnection();
					sen = s;
					break;
				}
			}
			if(sen != null) server.getList().remove(sen);
			socket.close();
			server.writeOnScreen("Client " + getIdd() + " disconnected with success");
		} catch(IOException e) {
			server.writeOnScreen("Something went wrong with closing connection with client " + getIdd());
		}
	}

	public int getIdd() {
		return id;
	}
	
	public void setSender(SSSender sender) {
		this.sender = sender;
	}
	
}