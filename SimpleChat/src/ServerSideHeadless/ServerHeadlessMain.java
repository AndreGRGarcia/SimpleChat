package ServerSideHeadless;

public class ServerHeadlessMain {
	
	public static void main(String[] args) {
		String filesLocation = "Sounds";
		ServerHeadless server = new ServerHeadless(filesLocation, 44444);
		server.startServing();
	}
	
}
