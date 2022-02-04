package ServerSide;

import java.io.Serializable;

public class Message implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 4389646670157873152L;
	
	public static final int OK = 0;
	public static final int DENIED = 1;
	public static final int MSG = 2;
	public static final int SEND_SOUND_FILES = 3;
	public static final int CLIENT_CONNECTED = 4;
	
	
	private int function;
	private String author;
	private String message;
	private int byteNum;
	
	public Message() {}
	
	public Message(int func) {
		function = func;
	}
	
	public Message(int func, int byteNum) {
		function = func;
		this.byteNum = byteNum;
	}
	
	public Message(int func, int byteNum, String fileName) {
		function = func;
		this.byteNum = byteNum;
		this.message = fileName;
	}
	
	public Message(int func, String author, String message) {
		function = func;
		this.author = author;
		this.message = message;
	}
	
	public Message(int func, String sender) {}

	public int getFunction() {
		return function;
	}

	public void setFunction(int function) {
		this.function = function;
	}

	public String getSender() {
		return author;
	}

	public void setSender(String sender) {
		this.author = sender;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public int getByteNum() {
		return byteNum;
	}

	public void setByteNum(int byteNum) {
		this.byteNum = byteNum;
	}
	
		
	
}
