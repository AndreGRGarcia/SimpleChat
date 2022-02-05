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
	private JPanel panel;
	private JPanel southP;
	private JPanel northP;
	private JLabel userL;
	private JTextField userTF;
	private JTextArea ta;
	private JTextField tf;
	private JButton butt;
	private JScrollPane scroll;
	
	private int serverPort;
	private String serverIP;
	
	public Client(String serverIP, int serverPort) {
		boolean hasSoundFiles = FileChecking.soundFilesExist();
		this.serverIP = serverIP;
		this.serverPort = serverPort;
		
		initGUI(hasSoundFiles);
		
		Sounds.playSound("startup");
	}

	public void runClient() {
		try {
			if(socket == null)
				connectToServer();
			
			sender = new Sender(objOut);
			
			receiver = new Receiver(this, objIn);
			receiver.start();
			
			//sendMessages();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public void printOnApp(String msg) {
		ta.setText(ta.getText() + msg + '\n');
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
		System.out.println("Address = " + address);
		
		try{	
			socket = new Socket(address, serverPort);
			System.out.println("Socket = " + socket);
		} catch(ConnectException e) {
			JOptionPane.showMessageDialog(null, "Error contacting the server");
			System.out.println("Error contacting the server");
			System.exit(2);
		}
		
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
		panel = new JPanel();
		southP = new JPanel();
		ta = new JTextArea();
		tf = new JTextField();
		northP = new JPanel();
		scroll = new JScrollPane(ta);
		String smiley = new String(Character.toChars(0x1F603));
		userL = new JLabel("Username: " + smiley);
		userL.setFont(new Font("Symbola", Font.BOLD, 20));
		userTF = new JTextField();
		
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
				if(!tf.getText().isEmpty()) {
					try {
						sender.send(new Message(Message.MSG, userTF.getText(), tf.getText()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tf.setText("");
				}
	        }
	    });
		
		ta.setEditable(false);
		ta.setLineWrap(true);
		
		butt = new JButton("Send");
		butt.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent arg0) {
				if(!tf.getText().isEmpty()) {
					try {
						sender.send(new Message(Message.MSG, userTF.getText(), tf.getText()));
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					tf.setText("");
				}
			}
    		
    	});
		
		tf.setEditable(true);
		
		southP.setLayout(new BorderLayout());
		southP.add(tf, BorderLayout.CENTER);
		southP.add(butt, BorderLayout.EAST);
		
		userTF.setEditable(true);
		
		northP.setLayout(new BorderLayout());
		northP.add(userL, BorderLayout.NORTH);
		northP.add(userTF, BorderLayout.CENTER);
		
		panel.setLayout(new BorderLayout());
		panel.add(scroll, BorderLayout.CENTER);
		panel.add(southP, BorderLayout.SOUTH);
		panel.add(northP, BorderLayout.NORTH);
		
		frame.setSize(new Dimension(500, 500));
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(true);
		frame.getContentPane().add(panel);
		
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
	    		
	        try {
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
	        	
	        	
	        } catch(IOException e) {
	        	e.printStackTrace();
	        }
			
			
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
		ta.setText(str);
	}
	
	public Socket getSocket() {
		return socket;
	}
		
}