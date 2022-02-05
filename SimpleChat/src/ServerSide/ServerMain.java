package ServerSide;

public class ServerMain {
	
	public static void main(String[] args) {
		String filesLocation = "Sounds";
		Server server = new Server(filesLocation, 44444);
		server.startServing();
	}
	
}
