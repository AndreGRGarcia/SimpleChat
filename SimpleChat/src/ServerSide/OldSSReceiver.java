package ServerSide;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;

public class OldSSReceiver extends Thread {
	
	private Server server;
	private SSSender sender;
	private Socket socket;
	private ObjectInputStream in;
	private int id;
	private String filesLocation;
	
	public OldSSReceiver(Server server, Socket socket, int id, String filesLocation) {
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
			
			ArrayList<File> files = new ArrayList<>();
			File dir = new File(filesLocation);
			String[] fileList = dir.list();
			
		    File myFile = new File ("E:\\E_Documents\\E_Sounds\\TittiesChat sounds\\sounds\\startup.wav");
		    
		    System.out.println("Sending ok with file length");
		    System.out.println("Di length is: " + (int)myFile.length());
		    sender.send(new Message(Message.OK, (int)myFile.length()));
	        
	        byte[] bytes = new byte[16 * 1024];
	        InputStream inT = new FileInputStream(myFile);
			OutputStream outT = socket.getOutputStream();
			
			System.out.println("Sending file");
			int count;
			while ((count = inT.read(bytes)) > 0) {
				System.out.println("Sending bytes");
				outT.write(bytes, 0, count);
			}
			
			System.out.println("Waiting to get ok");
			in.readObject();
			
			myFile = new File ("E:\\E_Documents\\E_Sounds\\TittiesChat sounds\\sounds\\notification.wav");
			
			System.out.println("Sending ok with file length");
		    sender.send(new Message(Message.OK, (int)myFile.length()));
		    
		    bytes = new byte[16 * 1024];
		    inT = new FileInputStream(myFile);
		    
		    System.out.println("Sending file");
			while ((count = inT.read(bytes)) > 0) {
				System.out.println("Sending bytes");
				outT.write(bytes, 0, count);
			}
			
			System.out.println("Waiting to get ok");
			in.readObject();
		    
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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