# SimpleChat
A chat application made with java and the swing framework.

To run this code:
1- Run the main method that is on the Server.java file to start the server (a window with some text should appear). Don't forget to pass the path of the directory with the needed sound files to the server object.
2- After you can run the main on the Client.java file, passing the IP or url of the server as the Client object argument. You can run multiple instances of the client.

I first made this project so I would understand how Sockets worked and how to use them with threads to make a working application. Later I wanted to find out how to send files through the socket, so the client asks the server to download 2 sound files, a startup sound and a notification sound. The location of the files is the only argument that the server object takes.
