package ClientSide;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OutputStream;
import java.net.ConnectException;
import java.net.InetAddress;
import java.net.Socket;

import javax.swing.AbstractAction;
import javax.swing.ActionMap;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollBar;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.KeyStroke;

import Utils.FileChecking;
import Utils.Message;
import Utils.Sounds;

public class Client {
	
	private ObjectInputStream objIn;
	private ObjectOutputStream objOut;
	private Socket socket = null;
	private Sender sender;
	private Receiver receiver;
	
	private JFrame frame;
	private JPanel mainPanel;
	private JPanel southPanel;
	private JPanel northPanel;
	private JPanel labelReconnectPanel;
	private JLabel userLabel;
	private JTextField userTF;
	private JTextArea chatTextArea;
	private JTextField inputTextField;
	private JButton sendButton;
	private JButton reconnectButton;
	private JScrollPane scroll;
	private boolean isReconnecting;
	
	private int serverPort;
	private String serverIP;
	
	public Client(String serverIP, int serverPort) { // TODO take care of connection exceptions and error cases
		boolean hasSoundFiles = FileChecking.soundFilesExist();
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		
		initGUI(hasSoundFiles);
		
		Sounds.playSound("startup");
	}

	public void runClient() {
		if(socket == null) {
			try {
				printOnApp("Connecting to the server...");
				connectToServer();
				runHelpers();	
			} catch(IOException e) {
				JOptionPane.showMessageDialog(null, "Error connecting to the server");
				System.out.println("Error contacting the server");
				printOnApp("Couldn't connect to the server");
			}	
		}
	}
	
	public void runHelpers() {
		sender = new Sender(objOut);
		
		receiver = new Receiver(this, objIn);
		receiver.start();
	}
	
	public void restartHelpers() throws IOException {
		sender = new Sender(objOut);
		
		if(receiver != null) {
			receiver.closeObjIn();
		}
		receiver = new Receiver(this, objIn);
		receiver.start();
	}
	
	public void printOnApp(String msg) {
		chatTextArea.setText(chatTextArea.getText() + msg + '\n');
		scrollDown();
	}
	
//	private void sendMessages() throws IOException {
//		Scanner s = new Scanner(System.in);
//		while(sendMore) {	
////			BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//			String str = s.nextLine();
//			sender.send(str);
//		}
//		s.close();
//	}
	
	private void connectToServer() throws IOException {	
		InetAddress address = InetAddress.getByName(serverIP);
		System.out.println("Address = " + address + ", Port = " + serverPort);
		
		socket = new Socket(address, serverPort);
		System.out.println("Socket = " + socket);
		
		objIn = new ObjectInputStream(socket.getInputStream());
		objOut = new ObjectOutputStream(socket.getOutputStream());
	}
	
	private void promptAndAskFiles() {
		Object[] options = {"Continue", "Cancel"};
		int answer_download = JOptionPane.showOptionDialog(frame,
			    "The client will now download some necessary files. If you cancel, the program will close.",
			    "Download Authorization",
			    JOptionPane.YES_NO_OPTION,
			    JOptionPane.QUESTION_MESSAGE,
			    null,
			    options,
			    options[1]);
		
		if(answer_download == 1) {
			System.exit(2);
		}
		
		askSoundsToServer();
	}
	
	private void initGUI(boolean hasSoundFiles) {
		if(!hasSoundFiles) {
			promptAndAskFiles();
		}
		
		frame = new JFrame();
		frame.setBounds(700, 300, 800, 600);
		mainPanel = new JPanel();
		southPanel = new JPanel();
		chatTextArea = new JTextArea();
		inputTextField = new JTextField();
		northPanel = new JPanel();
		scroll = new JScrollPane(chatTextArea);
		String smiley = new String(Character.toChars(0x1F603));
		userLabel = new JLabel("Username: " + smiley);
		userLabel.setFont(new Font("Symbola", Font.BOLD, 20));
		userTF = new JTextField();
		labelReconnectPanel = new JPanel();
		
		JPanel contentPane = (JPanel) frame.getContentPane();
	    int condition = JComponent.WHEN_IN_FOCUSED_WINDOW;
	    InputMap inputMap = contentPane.getInputMap(condition);
	    ActionMap actionMap = contentPane.getActionMap();
		String enter = "enter";
	    inputMap.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), enter);
	    actionMap.put(enter, new AbstractAction() {

	        /**
			 * 
			 */
			private static final long serialVersionUID = 3531215686146911120L;

			@Override
	        public void actionPerformed(ActionEvent arg0) {
				if(!inputTextField.getText().isEmpty()) {
					try {
						sender.send(new Message(Message.MSG, userTF.getText(), inputTextField.getText()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					inputTextField.setText("");
				}
	        }
	    });
		
		chatTextArea.setEditable(false);
		chatTextArea.setLineWrap(true);
		
		sendButton = new JButton("Send");
		sendButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!inputTextField.getText().isEmpty()) {
					try {
						sender.send(new Message(Message.MSG, userTF.getText(), inputTextField.getText()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					inputTextField.setText("");
				}
			}
    		
    	});
		
		reconnectButton = new JButton("Reconnect");
		reconnectButton.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(isReconnecting) {
					return;
				}
				isReconnecting = true;
				printOnApp("Attempting to reconnect...");
				System.out.println("Reconnecting...");
				
				new Thread() {
					@Override
					public void run() {
						try {
							connectToServer();
							restartHelpers();
							printOnApp("Reconnected!\n");
						} catch (Exception e) {
							e.printStackTrace();
							JOptionPane.showMessageDialog(null, "Error reconnecting to the server");
							System.out.println("Error reconnecting to the server");
							printOnApp("Couldn't reconnect to the server, check if the server is online, or if you have the right ip address");
						}
						isReconnecting = false;
					}
				}.start();
			}
    		
    	});
		
		inputTextField.setEditable(true);
		
		southPanel.setLayout(new BorderLayout());
		southPanel.add(inputTextField, BorderLayout.CENTER);
		southPanel.add(sendButton, BorderLayout.EAST);
		
		userTF.setEditable(true);
		
		labelReconnectPanel.setLayout(new BorderLayout());
		labelReconnectPanel.add(userLabel, BorderLayout.CENTER);
		labelReconnectPanel.add(reconnectButton, BorderLayout.EAST);
		
		northPanel.setLayout(new BorderLayout());
		northPanel.add(labelReconnectPanel, BorderLayout.NORTH);
		northPanel.add(userTF, BorderLayout.CENTER);
		
		mainPanel.setLayout(new BorderLayout());
		mainPanel.add(scroll, BorderLayout.CENTER);
		mainPanel.add(southPanel, BorderLayout.SOUTH);
		mainPanel.add(northPanel, BorderLayout.NORTH);
		
		frame.setSize(new Dimension(500, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().add(mainPanel);
		
		frame.setVisible(true);
	}
	
	private void askSoundsToServer() {
		try {
			connectToServer(); //Open socket connection
			
			// Prepare to receive the first file
			InputStream inT = null;
			OutputStream outT = null;
			
			try {
				inT = socket.getInputStream();
			} catch (IOException ex) {
				System.out.println("Can't get socket input stream.");
				System.exit(1);
			}
			
			// Ask for files and receive ok
	    	System.out.println("Asking for the sound files");
	    	objOut.writeObject(new Message(Message.SEND_SOUND_FILES)); //Ask for sound files
	    	
	    	System.out.println("Waiting for ok");
	    	Message ok = (Message)objIn.readObject();
	    	while(ok.getFunction() != Message.OK && ok.getFunction() != Message.DENIED) {
	    		ok = (Message)objIn.readObject();
	    	}
	    	if(ok.getFunction() == Message.DENIED) {
	    		JOptionPane.showMessageDialog(null, "Error getting all files from server");
				System.out.println("Error getting all files from server");
				System.exit(2);
	    	}
	    	
	    	// Receive first file
	    	int length = ok.getByteNum();
	    	System.out.println("length=" + length);
	    	System.out.println("Received ok, gonna receive the file");
	    	
			File myFile = new File(System.getenv("APPDATA") + "/tittiesChat/" + ok.getMessage());
			
			try {
				outT = new FileOutputStream(myFile);
			} catch (FileNotFoundException ex) {
				System.out.println("File not found.");
			}
	    		
        	byte[] bytes = new byte[16*1024];
        	int count, total = 0;
        	while (total < length) {
        		count = inT.read(bytes);
        		total += count;
        		outT.write(bytes, 0, count);
        	}
        	
        	// Received the file, now preparing the next before asking for it
        	
        	
        	// After preparing, send ok to ask for it and receive ok with byte number
        	System.out.println("Received the file, sending ok");
        	objOut.writeObject(new Message(Message.OK));
        	
        	System.out.println("Waiting for ok");
	    	ok = (Message)objIn.readObject();
	    	while(ok.getFunction() != Message.OK && ok.getFunction() != Message.DENIED) {
	    		ok = (Message)objIn.readObject();
	    	}
	    	if(ok.getFunction() == Message.DENIED) {
	    		JOptionPane.showMessageDialog(null, "Error getting all files from server");
				System.out.println("Error getting all files from server");
				System.exit(2);
	    	}
	    	
	    	myFile = new File(System.getenv("APPDATA") + "/tittiesChat/" + ok.getMessage());
        	try {
        		outT = new FileOutputStream(myFile);
        	} catch (FileNotFoundException ex) {
        		System.out.println("File not found. ");
        	}
        	bytes = new byte[16 * 1024];
	    	
	    	// Receiving the second file
	    	length = ok.getByteNum();
	    	System.out.println("length=" + length);
	    	System.out.println("Received ok, gonna receive the file");
        	total = 0;
        	while (total < length) {
        		count = inT.read(bytes);
        		total += count;
        		outT.write(bytes, 0, count);
        	}
        	
        	// received both files, sending ok and moving on
        	System.out.println("Received the file, sending ok");
        	objOut.writeObject(new Message(Message.OK));
	        	

			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public boolean frameIsActive() {
		return frame.isActive();
	}
	
	public void scrollDown() {
		 JScrollBar sb = scroll.getVerticalScrollBar();
		 sb.setValue( sb.getMaximum() );
	}
	
	public void setText(String str) {
		chatTextArea.setText(str);
	}
	
	public Socket getSocket() {
		return socket;
	}
		
}