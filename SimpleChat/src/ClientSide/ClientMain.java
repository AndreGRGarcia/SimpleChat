package ClientSide;

public class ClientMain {
	
	public static void main(String[] args) {
		Client c1 = new Client("127.0.0.1", 44444);
		c1.runClient();
	}
	
}
