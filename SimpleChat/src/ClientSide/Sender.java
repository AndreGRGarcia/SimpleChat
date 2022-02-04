package ClientSide;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

public class Sender {
	
	private ObjectOutputStream out;
	
	public Sender(ObjectOutputStream out) {
		this.out = out;
	}
		
	public void send(Serializable obj) throws IOException {
		out.writeObject(obj);
	}
	
	public void closeConnection() {
		try {
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	

	
}
