package ServerSide;

import java.awt.Dimension;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Scanner;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

public class Server {
	
	public int port;
	private ArrayList<SSSender> list = new ArrayList<>();
	private boolean letMoreJoin = true;
	
	private JFrame mainFrame;
	private JTextArea logsTextArea;
	private JScrollPane scroll;
	private String filesLocation;
	
	public Server(String filesLocation, int port) {
		this.filesLocation = filesLocation;
		this.port = port;
		initGUI();
	}
	
	public void startServing() {
		try {
			ServerSocket s = new ServerSocket(port);
			writeOnScreen("Launched ServerSocket: " + s);
			int id = 1;
			new Thread() { // listener for server shutdown via console
				
				public void run() {
					Scanner s = new Scanner(System.in);
					for(;;) {
						String str = s.nextLine();
						if(str.equals("close")) {
							for(SSSender sen: getList()) {
								try {
									sen.socket.close();
								} catch (IOException e) {
									writeOnScreen("Couldn't close all sockets");
									continue;
								}
							}
							
							s.close();
							System.exit(0);
						}
					}
				}
				
			}.start();
			try {
				while(letMoreJoin) {
					Socket socket = s.accept();
					SSReceiver receiver = new SSReceiver(this, socket, id, filesLocation);
					receiver.start();
					SSSender sender = new SSSender(this, socket, id);
					receiver.setSender(sender);
					getList().add(sender);
					writeOnScreen("Connection accepted with client " + id + ": " + socket);
					id++;
				}
				s.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		
		} catch(IOException e) {
			JOptionPane.showMessageDialog(mainFrame, "There was an error opening the server. (Most likely the selected port is already in use)");
			System.exit(3);
		}
	}
		
	
	private void initGUI() {
		mainFrame = new JFrame();
		logsTextArea = new JTextArea();
		scroll = new JScrollPane(logsTextArea);
		
		logsTextArea.setEditable(false);
		logsTextArea.setLineWrap(true);
		
		mainFrame.setSize(new Dimension(500, 200));
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setResizable(true);
		mainFrame.getContentPane().add(scroll);
		
		mainFrame.setVisible(true);
	}
	
	public void writeOnScreen(String str) {
		logsTextArea.setText(logsTextArea.getText() + str + '\n');
		scrollDown();
	}
	
	public void scrollDown() {
		 JScrollBar sb = scroll.getVerticalScrollBar();
		 sb.setValue( sb.getMaximum() );
	}
	
//	private void openPort() {
//		if (UPnP.isUPnPAvailable()) { //is UPnP available?
//            if (UPnP.isMappedTCP(PORT)) { //is the port already mapped?
//            	writeOnScreen("UPnP port forwarding not enabled: port is already mapped");
//            } else if (UPnP.openPortTCP(PORT)) { //try to map port
//            	writeOnScreen("UPnP port forwarding enabled");
//            } else {
//            	writeOnScreen("UPnP port forwarding failed");
//            }
//        } else {
//            writeOnScreen("UPnP is not available");
//        }
//	}
//	
//	
//	private static final int SUCCESS = 0;
//	private static final int FAILURE_CLOSING = 1;
//	private static final int PORT_NOT_MAPPED = 2;
//	private static final int UPNP_UNAVAILABLE = 3;
//	private int closePort() {
//		if (UPnP.isUPnPAvailable()) { //is UPnP available?
//            if (UPnP.isMappedTCP(PORT)) { //is the port already mapped?
//            	if(UPnP.closePortTCP(PORT)) {
//            		writeOnScreen("Port was closed successfully");
//            		return SUCCESS;
//            	} else {
//            		writeOnScreen("Could not close port");
//            		return FAILURE_CLOSING;
//            	}
//            } else {
//            	writeOnScreen("Port is already closed");
//                return PORT_NOT_MAPPED;
//            }
//        } else {
//        	writeOnScreen("UPnP is not available");
//            return UPNP_UNAVAILABLE;
//        }
//	}
	
	public synchronized ArrayList<SSSender> getList() {
		return list;
	}
}