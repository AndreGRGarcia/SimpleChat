package Main;

import ClientSide.Client;
import ServerSide.Server;

public class Main {
	
	public static void main(String[] args) {
		new Server("E:\\E_Documents\\E_Sounds\\TittiesChat sounds\\sounds").startServing();
		new Client().runClient();
		new Client().runClient();
		
	}
	
}
